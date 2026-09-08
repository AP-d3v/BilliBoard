package learn.data;

import learn.data.mappers.BarOwnerMapper;
import learn.models.BarOwner;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class BarOwnerJdbcRepository implements BarOwnerRepository {

    private final JdbcClient jdbcClient;

    public BarOwnerJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public BarOwner findByEmail(String email) {
        final String sql = """
                select bar_owner_id, email, first_name, last_name, password
                from bar_owner
                where email = ?;
                """;
        return jdbcClient.sql(sql)
                .param(email)
                .query(new BarOwnerMapper())
                .optional().orElse(null);
    }


    @Override
    public BarOwner add(BarOwner barOwner) {
        final String sql = """
                insert into bar_owner (email, first_name, last_name, password)
                values (?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcClient.sql(sql)
                .param(barOwner.getEmail())
                .param(barOwner.getFirstname())
                .param(barOwner.getLastName())
                .param(barOwner.getPassword())
                .update(keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        barOwner.setBarOwnerId(keyHolder.getKey().intValue());
        return barOwner;
    }
}
