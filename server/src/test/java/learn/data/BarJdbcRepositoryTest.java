package learn.data;

import learn.TestHelper;
import learn.models.Bar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BarJdbcRepositoryTest {

    @Autowired
    JdbcClient jdbcClient;

    @Autowired
    BarRepository repo;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByBarOwnerId() {
        List<Bar> bars = repo.findByBarOwnerId(1);
        assertEquals(2, bars.size());
        assertTrue(bars.contains(TestHelper.bar1));
    }

    @Test
    void shouldNotFindBarsForUnknownOwner() {
        assertTrue(repo.findByBarOwnerId(999).isEmpty());
    }

    @Test
    void shouldFindById() {
        assertEquals(TestHelper.bar1, repo.findById(1));
        assertNull(repo.findById(999));
    }

    @Test
    void shouldAdd() {
        Bar toAdd = new Bar(0, "8 Ball Hall", "3 Break St", 1);

        Bar added = repo.add(toAdd);

        assertNotNull(added);
        assertTrue(added.getBarId() > 2);
        assertEquals(added, repo.findById(added.getBarId()));
    }

    @Test
    void shouldUpdate() {
        Bar bar = new Bar(1, "Carmelos (renamed)", "1234 Main St", 1);
        assertTrue(repo.update(bar));
        assertEquals(bar, repo.findById(1));
    }

    @Test
    void shouldNotUpdateMissing() {
        assertFalse(repo.update(new Bar(999, "Nope", "Nowhere", 1)));
    }

    @Test
    void shouldDeleteById() {
        assertTrue(repo.deleteById(2));
        assertNull(repo.findById(2));
        assertFalse(repo.deleteById(2));
    }
}
