package learn.data.mappers;

import learn.models.Reservation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationMapper implements RowMapper<Reservation> {

    @Override
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setPlayerName(rs.getString("player_name"));
        reservation.setPatronEmail(rs.getString("patron_email"));
        reservation.setSessionId(rs.getString("session_id"));
        reservation.setTableId(rs.getInt("table_id"));
        return reservation;
    }
}
