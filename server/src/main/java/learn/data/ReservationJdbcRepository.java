package learn.data;

import learn.data.mappers.ReservationMapper;
import learn.models.Reservation;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReservationJdbcRepository implements ReservationRepository {

    private final JdbcClient jdbcClient;

    public ReservationJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Reservation> findByTableId(int tableId) {
        final String sql = """
                select reservation_id, player_name, patron_email, session_id, table_id
                from reservation
                where table_id = ?
                order by reservation_id;
                """;
        return jdbcClient.sql(sql)
                .param(tableId)
                .query(new ReservationMapper())
                .list();
    }

    @Override
    public Reservation findByTableIdAndSessionId(int tableId, String sessionId) {
        final String sql = """
                select reservation_id, player_name, patron_email, session_id, table_id
                from reservation
                where table_id = ? and session_id = ?
                limit 1;
                """;
        return jdbcClient.sql(sql)
                .param(tableId)
                .param(sessionId)
                .query(new ReservationMapper())
                .optional().orElse(null);
    }

    @Override
    public Reservation add(Reservation reservation) {
        final String sql = """
                insert into reservation (player_name, patron_email, session_id, table_id)
                values (?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcClient.sql(sql)
                .param(reservation.getPlayerName())
                .param(reservation.getPatronEmail())
                .param(reservation.getSessionId())
                .param(reservation.getTableId())
                .update(keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        reservation.setReservationId(keyHolder.getKey().intValue());
        return reservation;
    }

    @Override
    public boolean deleteByTableIdAndSessionId(int tableId, String sessionId) {
        return jdbcClient.sql("delete from reservation where table_id = ? and session_id = ?;")
                .param(tableId)
                .param(sessionId)
                .update() > 0;
    }
}
