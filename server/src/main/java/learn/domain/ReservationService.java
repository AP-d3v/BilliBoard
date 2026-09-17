package learn.domain;

import learn.data.ReservationRepository;
import learn.models.Reservation;
import learn.models.ReservationInLine;
import learn.models.TableLine;
import learn.notify.OneSignalClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private static final String WAITING = "WAITING";
    private static final String PLAYING = "PLAYING";

    private final ReservationRepository repository;
    private final BilliardTableService billiardTableService;
    private final OneSignalClient oneSignalClient;

    public ReservationService(ReservationRepository repository,
                              BilliardTableService billiardTableService,
                              OneSignalClient oneSignalClient) {
        this.repository = repository;
        this.billiardTableService = billiardTableService;
        this.oneSignalClient = oneSignalClient;
    }

    public List<Reservation> findByTableId(int tableId) {
        return repository.findByTableId(tableId);
    }

    public TableLine boardFor(int tableId) {
        Reservation playing = repository.findPlayingByTableId(tableId);

        List<ReservationInLine> waiting = new ArrayList<>();
        for (Reservation r : repository.findByTableId(tableId)) {
            if (WAITING.equals(r.getStatus())) {
                waiting.add(ReservationInLine.fromReservation(r));
            }
        }

        boolean confirmOpen = playing != null && playing.getConfirmRequestedAt() != null;
        String currentName = playing == null ? null : playing.getPlayerName();
        return new TableLine(currentName, confirmOpen, waiting);
    }

    public String roleFor(int tableId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return "NONE";
        }
        Reservation mine = repository.findByTableIdAndSessionId(tableId, sessionId);
        if (mine == null) {
            return "NONE";
        }
        if (PLAYING.equals(mine.getStatus())) {
            return "CURRENT";
        }
        if (mine.getConfirmRequestedAt() != null) {
            return "CHECK_IN";
        }
        Reservation next = firstInLine(tableId);
        if (next != null && next.getReservationId() == mine.getReservationId()) {
            return "NEXT";
        }
        return "WAITING";
    }

    public Result<Reservation> join(Reservation reservation) {
        Result<Reservation> result = validate(reservation);
        if (!result.isSuccess()) {
            return result;
        }

        if (billiardTableService.findById(reservation.getTableId()) == null) {
            result.addErrorMessage("That table does not exist.", ResultType.NOT_FOUND);
            return result;
        }

        String patronSession = reservation.getSessionId() == null ? "" : reservation.getSessionId();
        if (!patronSession.isBlank()
                && repository.findByTableIdAndSessionId(reservation.getTableId(), patronSession) != null) {
            result.addErrorMessage("You're already in line for this table.");
            return result;
        }

        reservation.setReservationId(0);
        if (patronSession.isBlank()) {
            reservation.setSessionId(UUID.randomUUID().toString());
        }

        boolean tableIsEmpty = repository.findByTableId(reservation.getTableId()).isEmpty();
        reservation.setStatus(tableIsEmpty ? PLAYING : WAITING);

        Reservation saved = repository.add(reservation);

        oneSignalClient.push(saved.getOnesignalSubscriptionId(),
                "You're in line",
                "You joined the line for table " + saved.getTableId() + ".");

        result.setPayload(saved);
        return result;
    }

    public boolean leave(int tableId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }
        Reservation mine = repository.findByTableIdAndSessionId(tableId, sessionId);
        if (mine == null) {
            return false;
        }
        boolean wasCurrentPlayer = PLAYING.equals(mine.getStatus());

        Reservation nextBefore = firstInLine(tableId);
        boolean wasNextPlayer = nextBefore != null
                && nextBefore.getReservationId() == mine.getReservationId();

        boolean removed = repository.deleteById(mine.getReservationId());
        if (removed && wasCurrentPlayer) {
            promoteNextPlayer(tableId);
        } else if (removed && wasNextPlayer) {
            notifyNewNextPlayer(tableId);
        }
        return removed;
    }

    public Result<Void> nudge(int tableId, String sessionId) {
        Result<Void> result = new Result<>();

        if (sessionId == null || sessionId.isBlank()) {
            result.addErrorMessage("Join the line before nudging.");
            return result;
        }
        Reservation nudger = repository.findByTableIdAndSessionId(tableId, sessionId);
        if (nudger == null) {
            result.addErrorMessage("Join the line before nudging.");
            return result;
        }

        Reservation playing = repository.findPlayingByTableId(tableId);
        if (playing == null) {
            result.addErrorMessage("Nobody is the current player right now.");
            return result;
        }
        if (nudger.getReservationId() == playing.getReservationId()) {
            result.addErrorMessage("You are the current player.");
            return result;
        }
        if (playing.getConfirmRequestedAt() == null) {
            repository.setConfirmWindow(playing.getReservationId(), LocalDateTime.now(), sessionId);
            oneSignalClient.pushConfirmRequest(playing.getOnesignalSubscriptionId(), tableId);
        }
        return result;
    }

    public Result<Void> stillHere(int tableId, String sessionId) {
        Result<Void> result = new Result<>();
        Reservation mine = currentPlayerOrError(tableId, sessionId, result);
        if (mine == null) {
            return result;
        }
        boolean wasNudged = mine.getConfirmRequestedAt() != null;
        repository.clearConfirmWindow(mine.getReservationId());
        if (wasNudged) {
            Reservation next = firstInLine(tableId);
            if (next != null) {
                oneSignalClient.push(next.getOnesignalSubscriptionId(),
                        "Still in play",
                        "The current player confirmed they're still at table " + tableId
                                + ". You're still next in line.");
            }
        }
        return result;
    }

    public Result<Void> checkIn(int tableId, String sessionId) {
        Result<Void> result = new Result<>();
        if (sessionId == null || sessionId.isBlank()) {
            result.addErrorMessage("You don't have a check-in window right now.");
            return result;
        }
        Reservation mine = repository.findByTableIdAndSessionId(tableId, sessionId);
        if (mine == null || PLAYING.equals(mine.getStatus()) || mine.getConfirmRequestedAt() == null) {
            result.addErrorMessage("You don't have a check-in window right now.");
            return result;
        }
        repository.updateStatus(mine.getReservationId(), PLAYING);
        repository.clearConfirmWindow(mine.getReservationId());
        notifyNewNextPlayer(tableId);
        return result;
    }

    public Result<Void> giveUp(int tableId, String sessionId) {
        Result<Void> result = new Result<>();
        Reservation mine = currentPlayerOrError(tableId, sessionId, result);
        if (mine == null) {
            return result;
        }
        repository.deleteById(mine.getReservationId());
        promoteNextPlayer(tableId);
        return result;
    }

    @Scheduled(fixedDelayString = "${reservation.expire-check-ms:30000}")
    public void promoteExpiredTables() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(2);
        for (Reservation expired : repository.findExpiredConfirms(cutoff)) {
            String nudgerSession = expired.getNudgedBySession();
            repository.deleteById(expired.getReservationId());

            Reservation next = firstInLine(expired.getTableId());
            if (next == null) {
                continue;
            }
            if (nudgerSession != null && nudgerSession.equals(next.getSessionId())) {
                promoteToCurrent(next, expired.getTableId());
            } else {
                repository.setConfirmWindow(next.getReservationId(), LocalDateTime.now(), nudgerSession);
                oneSignalClient.push(next.getOnesignalSubscriptionId(),
                        "It's your turn",
                        "Check in at table " + next.getTableId()
                                + " within 2 minutes to become the current player.");
            }
        }
    }

    private Reservation firstInLine(int tableId) {
        for (Reservation r : repository.findByTableId(tableId)) {
            if (WAITING.equals(r.getStatus())) {
                return r;
            }
        }
        return null;
    }

    private void promoteNextPlayer(int tableId) {
        Reservation next = firstInLine(tableId);
        if (next != null) {
            promoteToCurrent(next, tableId);
        }
    }

    private void promoteToCurrent(Reservation player, int tableId) {
        repository.updateStatus(player.getReservationId(), PLAYING);
        repository.clearConfirmWindow(player.getReservationId());
        oneSignalClient.push(player.getOnesignalSubscriptionId(),
                "You're up",
                "The table is yours -- you're now the current player at table " + tableId + ".");
        notifyNewNextPlayer(tableId);
    }

    private void notifyNewNextPlayer(int tableId) {
        Reservation newNext = firstInLine(tableId);
        if (newNext != null) {
            oneSignalClient.push(newNext.getOnesignalSubscriptionId(),
                    "You're next in line",
                    "Head to table " + tableId
                            + " -- you can check in as soon as the current player is done.");
        }
    }

    private Reservation currentPlayerOrError(int tableId, String sessionId, Result<Void> result) {
        if (sessionId == null || sessionId.isBlank()) {
            result.addErrorMessage("You're not the current player.");
            return null;
        }
        Reservation mine = repository.findByTableIdAndSessionId(tableId, sessionId);
        if (mine == null || !PLAYING.equals(mine.getStatus())) {
            result.addErrorMessage("You're not the current player.");
            return null;
        }
        return mine;
    }

    private Result<Reservation> validate(Reservation reservation) {
        Result<Reservation> result = new Result<>();
        if (reservation == null) {
            result.addErrorMessage("Reservation is required.");
            return result;
        }
        if (reservation.getPlayerName() == null || reservation.getPlayerName().isBlank()) {
            result.addErrorMessage("Player name is required.");
        }
        if (reservation.getPatronEmail() == null || reservation.getPatronEmail().isBlank()) {
            result.addErrorMessage("Email is required.");
        }
        if (reservation.getTableId() <= 0) {
            result.addErrorMessage("A reservation must be for a table.");
        }
        return result;
    }
}
