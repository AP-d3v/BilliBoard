package learn.data;

import learn.data.mappers.BilliardTableMapper;
import learn.models.BilliardTable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BilliardTableJdbcRepository implements BilliardTableRepository {

    private final JdbcClient jdbcClient;

    public BilliardTableJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<BilliardTable> findByBarId(int barId) {
        final String sql = """
                select table_id, max_players, closing_time, bar_id
                from billiard_table
                where bar_id = ?;
                """;
        return jdbcClient.sql(sql)
                .param(barId)
                .query(new BilliardTableMapper())
                .list();
    }
}
