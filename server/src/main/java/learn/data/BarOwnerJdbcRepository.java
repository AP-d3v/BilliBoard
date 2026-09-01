package learn.data;

import learn.data.mappers.BarOwnerMapper;
import learn.models.BarOwner;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class BarOwnerJdbcRepository implements BarOwnerRepository{
    private final JdbcClient jdbcClient;

    public BarOwnerJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public BarOwner findByEmail(String email){
        final String sql = """
                select bar_owner_id, email, first_name,last_name,password
                from bar_owner
                where email = ?;
                """;
        return jdbcClient.sql(sql)
                .param(email)
                .query( new BarOwnerMapper())
                .optional().orElse(null);
    }
}
