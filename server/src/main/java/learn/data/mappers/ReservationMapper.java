package learn.data.mappers;

import learn.models.Reservation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReservationMapper implements RowMapper<Reservation> {

    @Override
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setPlayerName(rs.getString("player_name"));
        reservation.setPatronEmail(rs.getString("patron_email"));
        reservation.setSessionId(rs.getString("session_id"));
        reservation.setTableId(rs.getInt("table_id"));
        reservation.setStatus(rs.getString("status"));
        reservation.setConfirmRequestedAt(rs.getObject("confirm_requested_at", LocalDateTime.class));
        reservation.setNudgedBySession(rs.getString("nudged_by_session"));
        reservation.setOnesignalSubscriptionId(rs.getString("onesignal_subscription_id"));
        return reservation;
    }
}
