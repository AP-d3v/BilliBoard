package learn.domain;

import learn.data.ReservationRepository;
import learn.models.BilliardTable;
import learn.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class ReservationServiceTest {

    ReservationRepository repository;
    BilliardTableService billiardTableService;
    ReservationService service;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationRepository.class);
        billiardTableService = mock(BilliardTableService.class);
        service = new ReservationService(repository, billiardTableService);


        when(billiardTableService.findById(anyInt()))
                .thenReturn(new BilliardTable(1, 4, LocalTime.of(23, 0), 1));
        when(repository.findByTableIdAndSessionId(anyInt(), anyString())).thenReturn(null);
    }

    @Test
    void shouldJoinAndGetAServerGeneratedSession() {
        Reservation toJoin = new Reservation(0, "Sam", "sam@example.com", "", 1);
        when(repository.add(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setReservationId(3);
            return r;
        });

        Result<Reservation> result = service.join(toJoin);

        assertTrue(result.isSuccess());
        assertEquals(3, result.getPayload().getReservationId());
        assertNotNull(result.getPayload().getSessionId());
        assertFalse(result.getPayload().getSessionId().isBlank());
    }

    @Test
    void shouldKeepAnExistingSessionId() {
        Reservation toJoin = new Reservation(0, "Sam", "sam@example.com", "my-existing-session", 1);
        when(repository.add(any())).thenAnswer(inv -> inv.getArgument(0));

        Result<Reservation> result = service.join(toJoin);

        assertTrue(result.isSuccess());
        assertEquals("my-existing-session", result.getPayload().getSessionId());
    }

    @Test
    void shouldNotJoinWithBlankName() {
        Result<Reservation> result = service.join(new Reservation(0, " ", "sam@example.com", "", 1));
        assertFalse(result.isSuccess());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldNotJoinWithBlankEmail() {
        Result<Reservation> result = service.join(new Reservation(0, "Sam", "", "", 1));
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotJoinTableThatDoesNotExist() {
        when(billiardTableService.findById(anyInt())).thenReturn(null);

        Result<Reservation> result = service.join(new Reservation(0, "Sam", "sam@example.com", "", 99));

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldNotJoinSameTableWithASessionAlreadyInLine() {
        when(repository.findByTableIdAndSessionId(1, "my-session"))
                .thenReturn(new Reservation(1, "Sam", "sam@example.com", "my-session", 1));


        Result<Reservation> result = service.join(new Reservation(0, "Sam", "different@example.com", "my-session", 1));

        assertFalse(result.isSuccess());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldJoinADifferentTableWithTheSameSession() {
        // makes sure the user can still join other tables
        when(repository.findByTableIdAndSessionId(2, "my-session")).thenReturn(null);
        when(repository.add(any())).thenAnswer(inv -> inv.getArgument(0));

        Result<Reservation> result = service.join(new Reservation(0, "Sam", "sam@example.com", "my-session", 2));

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldLeave() {
        when(repository.deleteByTableIdAndSessionId(1, "s")).thenReturn(true);
        assertTrue(service.leave(1, "s"));
    }

    @Test
    void shouldNotLeaveWithoutSession() {
        assertFalse(service.leave(1, ""));
        assertFalse(service.leave(1, null));
        verify(repository, never()).deleteByTableIdAndSessionId(anyInt(), any());
    }
}
