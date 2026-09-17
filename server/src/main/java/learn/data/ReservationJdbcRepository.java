package learn.data;

import learn.data.mappers.ReservationMapper;
import learn.models.Reservation;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReservationJdbcRepository implements ReservationRepository {

    private static final String COLUMNS =
            "reservation_id, player_name, patron_email, session_id, table_id, status, "
                    + "confirm_requested_at, nudged_by_session, onesignal_subscription_id";

    private final JdbcClient jdbcClient;

    public ReservationJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Reservation> findByTableId(int tableId) {
        final String sql = "select " + COLUMNS
                + " from reservation where table_id = ? order by reservation_id;";
        return jdbcClient.sql(sql).param(tableId).query(new ReservationMapper()).list();
    }

    @Override
    public Reservation findByTableIdAndSessionId(int tableId, String sessionId) {
        final String sql = "select " + COLUMNS
                + " from reservation where table_id = ? and session_id = ? limit 1;";
        return jdbcClient.sql(sql)
                .param(tableId).param(sessionId)
                .query(new ReservationMapper())
                .optional().orElse(null);
    }

    @Override
    public Reservation findPlayingByTableId(int tableId) {
        final String sql = "select " + COLUMNS
                + " from reservation where table_id = ? and status = 'PLAYING' limit 1;";
        return jdbcClient.sql(sql)
                .param(tableId)
                .query(new ReservationMapper())
                .optional().orElse(null);
    }

    @Override
    public List<Reservation> findExpiredConfirms(LocalDateTime cutoff) {
        final String sql = "select " + COLUMNS
                + " from reservation"
                + " where confirm_requested_at is not null and confirm_requested_at < ?;";
        return jdbcClient.sql(sql).param(cutoff).query(new ReservationMapper()).list();
    }

    @Override
    public Reservation add(Reservation reservation) {
        final String sql = """
                insert into reservation
                    (player_name, patron_email, session_id, table_id, status, onesignal_subscription_id)
                values (?, ?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcClient.sql(sql)
                .param(reservation.getPlayerName())
                .param(reservation.getPatronEmail())
                .param(reservation.getSessionId())
                .param(reservation.getTableId())
                .param(reservation.getStatus() == null ? "WAITING" : reservation.getStatus())
                .param(reservation.getOnesignalSubscriptionId())
                .update(keyHolder);

        if (rows <= 0) {
            return null;
        }
        reservation.setReservationId(keyHolder.getKey().intValue());
        return reservation;
    }

    @Override
    public boolean updateStatus(int reservationId, String status) {
        return jdbcClient.sql("update reservation set status = ? where reservation_id = ?;")
                .param(status).param(reservationId)
                .update() > 0;
    }

    @Override
    public boolean setConfirmWindow(int reservationId, LocalDateTime when, String nudgedBySession) {
        return jdbcClient.sql(
                        "update reservation set confirm_requested_at = ?, nudged_by_session = ? where reservation_id = ?;")
                .param(when).param(nudgedBySession).param(reservationId)
                .update() > 0;
    }

    @Override
    public boolean clearConfirmWindow(int reservationId) {
        return jdbcClient.sql(
                        "update reservation set confirm_requested_at = null, nudged_by_session = null where reservation_id = ?;")
                .param(reservationId)
                .update() > 0;
    }

    @Override
    public boolean deleteById(int reservationId) {
        return jdbcClient.sql("delete from reservation where reservation_id = ?;")
                .param(reservationId)
                .update() > 0;
    }

    @Override
    public boolean deleteByTableIdAndSessionId(int tableId, String sessionId) {
        return jdbcClient.sql("delete from reservation where table_id = ? and session_id = ?;")
                .param(tableId).param(sessionId)
                .update() > 0;
    }
}
