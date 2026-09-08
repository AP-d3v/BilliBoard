package learn.data.mappers;

import learn.models.BilliardTable;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class BilliardTableMapper implements RowMapper<BilliardTable> {

    @Override
    public BilliardTable mapRow(ResultSet rs, int rowNum) throws SQLException {
        BilliardTable table = new BilliardTable();
        table.setTableId(rs.getInt("table_id"));
        table.setMaxPlayers(rs.getInt("max_players"));
        table.setClosingTime(rs.getObject("closing_time", LocalTime.class));
        table.setBarId(rs.getInt("bar_id"));
        return table;
    }
}
