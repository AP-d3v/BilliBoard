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

    @MockitoBean // New annotation
    BarOwnerJdbcRepository repository;

    @Autowired
    BarOwnerService service;

    @Test
    void authenticateHappyPath() {

        BarOwner fromDb = TestHelper.barOwner1;
        BarOwner proposedLogin = new BarOwner(0,"aprescott@dev10.com","Alasco","Prescott","abc123");
        when(repository.findByEmail(any())).thenReturn(TestHelper.barOwner1);

        Result<BarOwner> result = service.authenticate(proposedLogin);
        assertTrue(result.isSuccess());
        assertTrue(result.getPayload().equals(fromDb));



    }

    @Test
    void authenticateEmailNotFound(){
        BarOwner proposedLogin = new BarOwner(0,"fake@dev10.com","Alasco","Prescott","abc123");
        when(repository.findByEmail("fake@dev10.com")).thenReturn(null);
        Result<BarOwner> result = service.authenticate(proposedLogin);
        assertFalse(result.isSuccess());


    }

    @Test
    void authenticatePasswordInvalid(){
        BarOwner fromDb = TestHelper.barOwner1;
        BarOwner proposedLogin = new BarOwner(0,"aprescott@dev10.com",
                "Alasco","Prescott","wrongPass");
        when(repository.findByEmail(any())).thenReturn(TestHelper.barOwner1);
        Result<BarOwner> 
        assertFalse();
    }
}