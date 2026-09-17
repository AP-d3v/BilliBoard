package learn.domain;

import learn.TestHelper;
import learn.data.BarOwnerJdbcRepository;
import learn.models.BarOwner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BarOwnerServiceTest {

    @MockitoBean
    BarOwnerJdbcRepository repository;

    @Autowired
    BarOwnerService service;



    @Test
    void authenticateHappyPath() {
        BarOwner proposedLogin = new BarOwner(0, "aprescott@dev10.com", "Alasco", "Prescott", "abc123");
        when(repository.findByEmail(any())).thenReturn(TestHelper.barOwner1);

        Result<BarOwner> result = service.authenticate(proposedLogin);

        assertTrue(result.isSuccess());
        assertEquals(TestHelper.barOwner1, result.getPayload());
    }

    @Test
    void authenticateEmailNotFound() {
        BarOwner proposedLogin = new BarOwner(0, "fake@dev10.com", "Alasco", "Prescott", "abc123");
        when(repository.findByEmail("fake@dev10.com")).thenReturn(null);

        Result<BarOwner> result = service.authenticate(proposedLogin);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void authenticateWrongPassword() {
        BarOwner proposedLogin = new BarOwner(0, "aprescott@dev10.com", "Alasco", "Prescott", "wrongPass");
        when(repository.findByEmail(any())).thenReturn(TestHelper.barOwner1);

        Result<BarOwner> result = service.authenticate(proposedLogin);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
    }



    @Test
    void createHappyPath() {
        BarOwner newOwner = new BarOwner(0, "new@dev10.com", "New", "Owner", "secret123");
        when(repository.findByEmail("new@dev10.com")).thenReturn(null);
        when(repository.add(any())).thenAnswer(inv -> {
            BarOwner o = inv.getArgument(0);
            o.setBarOwnerId(2);
            return o;
        });

        Result<BarOwner> result = service.create(newOwner);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getPayload().getBarOwnerId());
        // password was hashed, not stored raw
        assertNotEquals("secret123", result.getPayload().getPassword());
        assertEquals(BarOwnerService.hash("secret123"), result.getPayload().getPassword());
    }

    @Test
    void createRejectsShortPassword() {
        BarOwner newOwner = new BarOwner(0, "new@dev10.com", "New", "Owner", "123");
        Result<BarOwner> result = service.create(newOwner);
        assertFalse(result.isSuccess());
    }

    @Test
    void createRejectsBlankName() {
        BarOwner newOwner = new BarOwner(0, "new@dev10.com", " ", "Owner", "secret123");
        Result<BarOwner> result = service.create(newOwner);
        assertFalse(result.isSuccess());
    }

    @Test
    void createRejectsDuplicateEmail() {
        BarOwner newOwner = new BarOwner(0, "aprescott@dev10.com", "Al", "P", "secret123");
        when(repository.findByEmail("aprescott@dev10.com")).thenReturn(TestHelper.barOwner1);

        Result<BarOwner> result = service.create(newOwner);

        assertFalse(result.isSuccess());
    }
}
