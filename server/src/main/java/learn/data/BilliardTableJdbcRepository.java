package learn.data;

import learn.data.mappers.BilliardTableMapper;
import learn.models.BilliardTable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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

    @Override
    public BilliardTable findById(int tableId) {
        final String sql = """
                select table_id, max_players, closing_time, bar_id
                from billiard_table
                where table_id = ?;
                """;
        return jdbcClient.sql(sql)
                .param(tableId)
                .query(new BilliardTableMapper())
                .optional().orElse(null);
    }

    @Override
    public BilliardTable add(BilliardTable table) {
        final String sql = """
                insert into billiard_table (max_players, closing_time, bar_id)
                values (?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcClient.sql(sql)
                .param(table.getMaxPlayers())
                .param(table.getClosingTime())
                .param(table.getBarId())
                .update(keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        table.setTableId(keyHolder.getKey().intValue());
        return table;
    }

    @Override
    public boolean update(BilliardTable table) {
        final String sql = """
                update billiard_table set
                    max_players = ?,
                    closing_time = ?
                where table_id = ?;
                """;
        return jdbcClient.sql(sql)
                .param(table.getMaxPlayers())
                .param(table.getClosingTime())
                .param(table.getTableId())
                .update() > 0;
    }

    @Override
    public boolean deleteById(int tableId) {
        return jdbcClient.sql("delete from billiard_table where table_id = ?;")
                .param(tableId)
                .update() > 0;
    }
}
