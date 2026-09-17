package learn.data;

import learn.TestHelper;
import learn.models.BilliardTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.time.LocalTime;
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

    @Test
    void shouldFindById() {
        assertEquals(TestHelper.table1, repo.findById(1));
        assertNull(repo.findById(999));
    }

    @Test
    void shouldAdd() {
        BilliardTable toAdd = new BilliardTable(0, 8, LocalTime.of(2, 0), 1);

        BilliardTable added = repo.add(toAdd);

        assertNotNull(added);
        assertTrue(added.getTableId() > 3);
        assertEquals(added, repo.findById(added.getTableId()));
    }

    @Test
    void shouldUpdate() {
        BilliardTable table = new BilliardTable(1, 6, LocalTime.of(22, 30), 1);
        assertTrue(repo.update(table));
        assertEquals(table, repo.findById(1));
    }

    @Test
    void shouldNotUpdateMissing() {
        assertFalse(repo.update(new BilliardTable(999, 4, LocalTime.of(23, 0), 1)));
    }

    @Test
    void shouldDeleteById() {
        assertTrue(repo.deleteById(3));
        assertNull(repo.findById(3));
        assertFalse(repo.deleteById(3));
    }
}
