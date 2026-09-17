package learn.data;

import learn.models.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository {

    List<Reservation> findByTableId(int tableId);

    Reservation findByTableIdAndSessionId(int tableId, String sessionId);

    Reservation findPlayingByTableId(int tableId);

    List<Reservation> findExpiredConfirms(LocalDateTime cutoff);

    Reservation add(Reservation reservation);

    boolean updateStatus(int reservationId, String status);

    boolean setConfirmWindow(int reservationId, LocalDateTime when, String nudgedBySession);

    boolean clearConfirmWindow(int reservationId);

    boolean deleteById(int reservationId);

    boolean deleteByTableIdAndSessionId(int tableId, String sessionId);
}
