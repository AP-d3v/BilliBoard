package learn.data;

import learn.TestHelper;
import learn.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.time.LocalDateTime;
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
    void shouldFindEveryReservationForATableInOrder() {
        List<Reservation> all = repo.findByTableId(1);
        assertEquals(2, all.size());
        assertEquals(TestHelper.reservation1, all.get(0));
        assertEquals("Grace", all.get(1).getPlayerName());
    }

    @Test
    void shouldReturnEmptyLineForTableWithNobody() {
        assertTrue(repo.findByTableId(2).isEmpty());
    }

    @Test
    void shouldFindThePlayingPlayer() {
        assertEquals("Ada", repo.findPlayingByTableId(1).getPlayerName());
        assertNull(repo.findPlayingByTableId(2));
    }

    @Test
    void shouldFindBySession() {
        assertNotNull(repo.findByTableIdAndSessionId(1, "seed-session-ada"));
        assertNull(repo.findByTableIdAndSessionId(2, "seed-session-ada"));
    }

    @Test
    void shouldAddAsWaiting() {
        Reservation toAdd = new Reservation(0, "Sam", "sam@example.com", "session-sam", 1);
        toAdd.setOnesignalSubscriptionId("sub-sam");
        Reservation added = repo.add(toAdd);
        assertNotNull(added);
        assertTrue(added.getReservationId() > 2);

        Reservation found = repo.findByTableIdAndSessionId(1, "session-sam");
        assertEquals("WAITING", found.getStatus());
        assertEquals("sub-sam", found.getOnesignalSubscriptionId());
    }


    @Test
    void shouldSetAndClearConfirmWindow() {
        assertTrue(repo.setConfirmWindow(1, LocalDateTime.now(), "seed-session-grace"));
        Reservation ada = repo.findByTableIdAndSessionId(1, "seed-session-ada");
        assertNotNull(ada.getConfirmRequestedAt());
        assertEquals("seed-session-grace", ada.getNudgedBySession());

        assertTrue(repo.clearConfirmWindow(1));
        ada = repo.findByTableIdAndSessionId(1, "seed-session-ada");
        assertNull(ada.getConfirmRequestedAt());
        assertNull(ada.getNudgedBySession());
    }

    @Test
    void shouldFindExpiredConfirms() {
        assertTrue(repo.findExpiredConfirms(LocalDateTime.now()).isEmpty());

        repo.setConfirmWindow(1, LocalDateTime.now().minusMinutes(5), "seed-session-grace");
        List<Reservation> expired = repo.findExpiredConfirms(LocalDateTime.now().minusMinutes(2));
        assertEquals(1, expired.size());
        assertEquals("Ada", expired.get(0).getPlayerName());
    }

    @Test
    void shouldDeleteById() {
        assertTrue(repo.deleteById(2));
        assertEquals(1, repo.findByTableId(1).size());
        assertFalse(repo.deleteById(2));
    }

    @Test
    void shouldDeleteByTableAndSession() {
        assertTrue(repo.deleteByTableIdAndSessionId(1, "seed-session-ada"));
        assertEquals(1, repo.findByTableId(1).size());
    }
}
