package learn.data;

import learn.models.Reservation;

import java.util.List;

public interface ReservationRepository {


    List<Reservation> findByTableId(int tableId);


    Reservation findByTableIdAndSessionId(int tableId, String sessionId);

    Reservation add(Reservation reservation);

    boolean deleteByTableIdAndSessionId(int tableId, String sessionId);
}
