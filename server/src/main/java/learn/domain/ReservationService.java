package learn.domain;

import learn.data.ReservationRepository;
import learn.models.Reservation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final BilliardTableService billiardTableService;

    public ReservationService(ReservationRepository repository, BilliardTableService billiardTableService) {
        this.repository = repository;
        this.billiardTableService = billiardTableService;
    }

    public List<Reservation> findByTableId(int tableId) {
        return repository.findByTableId(tableId);
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
        // reuses the patron's session id if they have one, otherwise the server makes it
        if (patronSession.isBlank()) {
            reservation.setSessionId(UUID.randomUUID().toString());
        }

        result.setPayload(repository.add(reservation));
        return result;
    }

    public boolean leave(int tableId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }
        return repository.deleteByTableIdAndSessionId(tableId, sessionId);
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
