package learn.data;

import learn.TestHelper;
import learn.models.BilliardTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BilliardTableJdbcRepositoryTest {

    @Autowired
    JdbcClient jdbcClient;

    @Autowired
    BilliardTableRepository repo;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindTablesForOneBar() {
        List<BilliardTable> tables = repo.findByBarId(1);
        assertEquals(2, tables.size());
        assertTrue(tables.contains(TestHelper.table1));

        assertEquals(1, repo.findByBarId(2).size());
    }

    @Test
    void shouldReturnEmptyForBarWithNoTables() {
        assertTrue(repo.findByBarId(999).isEmpty());
    }
}
