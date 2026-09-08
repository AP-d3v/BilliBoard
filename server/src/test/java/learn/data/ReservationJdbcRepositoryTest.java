package learn.data;

import learn.TestHelper;
import learn.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ReservationJdbcRepositoryTest {

    @Autowired
    JdbcClient jdbcClient;

    @Autowired
    ReservationRepository repo;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindLineForTableInOrder() {
        List<Reservation> line = repo.findByTableId(1);
        assertEquals(2, line.size());
        assertEquals(TestHelper.reservation1, line.get(0));
        assertEquals("Grace", line.get(1).getPlayerName());
    }

    @Test
    void shouldReturnEmptyLineForTableWithNobody() {
        assertTrue(repo.findByTableId(2).isEmpty());
    }

    @Test
    void shouldFindBySessionInThatTablesLine() {
        assertNotNull(repo.findByTableIdAndSessionId(1, "seed-session-ada"));
    }

    @Test
    void shouldNotFindWhenSessionIsInADifferentTablesLine() {
        // Ada's session is in table 1, not table 2
        assertNull(repo.findByTableIdAndSessionId(2, "seed-session-ada"));
    }

    @Test
    void shouldNotFindUnknownSession() {
        assertNull(repo.findByTableIdAndSessionId(1, "nope"));
    }

    @Test
    void shouldAdd() {
        Reservation toAdd = new Reservation(0, "Sam", "sam@example.com", "session-sam", 1);

        Reservation added = repo.add(toAdd);

        assertNotNull(added);
        assertTrue(added.getReservationId() > 2);
        assertEquals(3, repo.findByTableId(1).size());
    }

    @Test
    void shouldDeleteByTableAndSession() {
        assertTrue(repo.deleteByTableIdAndSessionId(1, "seed-session-ada"));
        assertEquals(1, repo.findByTableId(1).size());
        assertFalse(repo.deleteByTableIdAndSessionId(1, "seed-session-ada"));
    }
}
