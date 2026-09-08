package learn.data.mappers;

import learn.models.Bar;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BarMapper implements RowMapper<Bar> {

    @Override
    public Bar mapRow(ResultSet rs, int rowNum) throws SQLException {
        Bar bar = new Bar();
        bar.setBarId(rs.getInt("bar_id"));
        bar.setBarName(rs.getString("bar_name"));
        bar.setAddress(rs.getString("address"));
        bar.setBarOwnerId(rs.getInt("bar_owner_id"));
        return bar;
    }
}
