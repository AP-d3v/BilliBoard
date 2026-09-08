package learn.data.mappers;

import learn.models.BarOwner;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BarOwnerMapper implements RowMapper<BarOwner> {


    @Override
    public BarOwner mapRow(ResultSet rs, int rowNum) throws SQLException {
        BarOwner barOwner = new BarOwner();
        barOwner.setBarOwnerId(rs.getInt("bar_owner_id"));
        barOwner.setEmail(rs.getString("email"));
        barOwner.setFirstname(rs.getString("first_name"));
        barOwner.setLastName(rs.getString("last_name"));
        barOwner.setPassword(rs.getString("password"));
        return barOwner;
    }
}
