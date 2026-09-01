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
    void setup(){
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindByEmail() {
        //arrange
        BarOwner expected = TestHelper.barOwner1;
        //act
        BarOwner actual = repo.findByEmail("aprescott@dev10.com");

        //assert
        assertEquals(expected, actual);
        assertTrue(actual.getEmail().equals("aprescott@dev10.com"));
    }

    @Test
    void shouldNotFindByEmail(){
        BarOwner expected = TestHelper.barOwner1;
        BarOwner actual = repo.findByEmail("fakeemail@gmail.com");
        assertNull(actual);
    }
}