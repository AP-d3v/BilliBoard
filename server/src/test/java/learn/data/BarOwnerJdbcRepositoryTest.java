package learn.data;

import learn.TestHelper;
import learn.models.BarOwner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BarOwnerJdbcRepositoryTest {

    @Autowired
    JdbcClient jdbcClient;

    @Autowired
    BarOwnerRepository repo;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByEmail() {
        BarOwner expected = TestHelper.barOwner1;
        BarOwner actual = repo.findByEmail("aprescott@dev10.com");

        assertEquals(expected, actual);
        assertEquals("aprescott@dev10.com", actual.getEmail());
    }

    @Test
    void shouldNotFindByEmail() {
        assertNull(repo.findByEmail("fakeemail@gmail.com"));
    }


    @Test
    void shouldAdd() {
        BarOwner toAdd = new BarOwner(0, "second@dev10.com", "Second", "Owner", "someHashString");

        BarOwner added = repo.add(toAdd);

        assertNotNull(added);
        assertTrue(added.getBarOwnerId() > 1);
        assertEquals(added, repo.findByEmail("second@dev10.com"));
    }
}
