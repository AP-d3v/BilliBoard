package learn.data;

import learn.data.mappers.BarMapper;
import learn.models.Bar;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BarJdbcRepository implements BarRepository {

    private final JdbcClient jdbcClient;

    public BarJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Bar> findByBarOwnerId(int barOwnerId) {
        final String sql = """
                select bar_id, bar_name, address, bar_owner_id
                from bar
                where bar_owner_id = ?;
                """;
        return jdbcClient.sql(sql)
                .param(barOwnerId)
                .query(new BarMapper())
                .list();
    }

    @Override
    public Bar findById(int barId) {
        final String sql = """
                select bar_id, bar_name, address, bar_owner_id
                from bar
                where bar_id = ?;
                """;
        return jdbcClient.sql(sql)
                .param(barId)
                .query(new BarMapper())
                .optional().orElse(null);
    }

    @Override
    public Bar add(Bar bar) {
        final String sql = """
                insert into bar (bar_name, address, bar_owner_id)
                values (?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcClient.sql(sql)
                .param(bar.getBarName())
                .param(bar.getAddress())
                .param(bar.getBarOwnerId())
                .update(keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        bar.setBarId(keyHolder.getKey().intValue());
        return bar;
    }

    @Override
    public boolean update(Bar bar) {
        final String sql = """
                update bar set
                    bar_name = ?,
                    address = ?
                where bar_id = ?;
                """;
        return jdbcClient.sql(sql)
                .param(bar.getBarName())
                .param(bar.getAddress())
                .param(bar.getBarId())
                .update() > 0;
    }

    @Override
    public boolean deleteById(int barId) {

        jdbcClient.sql("delete from billiard_table where bar_id = ?;")
                .param(barId)
                .update();
        return jdbcClient.sql("delete from bar where bar_id = ?;")
                .param(barId)
                .update() > 0;
    }
}
