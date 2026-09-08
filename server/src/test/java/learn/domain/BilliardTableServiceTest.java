package learn.domain;

import learn.data.BilliardTableRepository;
import learn.models.BilliardTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Plain unit test -- no Spring, no DB. Mockito stands in for the repository.
class BilliardTableServiceTest {

    BilliardTableRepository repository;
    BilliardTableService service;

    @BeforeEach
    void setUp() {
        repository = mock(BilliardTableRepository.class);
        service = new BilliardTableService(repository);
    }

    @Test
    void shouldAddValidTable() {
        BilliardTable toAdd = new BilliardTable(0, 4, LocalTime.of(23, 0), 1);
        BilliardTable saved = new BilliardTable(7, 4, LocalTime.of(23, 0), 1);
        when(repository.add(toAdd)).thenReturn(saved);

        Result<BilliardTable> result = service.add(toAdd);

        assertTrue(result.isSuccess());
        assertEquals(7, result.getPayload().getTableId());
    }

    @Test
    void shouldNotAddZeroMaxPlayers() {
        Result<BilliardTable> result = service.add(new BilliardTable(0, 0, LocalTime.of(23, 0), 1));

        assertFalse(result.isSuccess());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldNotAddWithoutClosingTime() {
        Result<BilliardTable> result = service.add(new BilliardTable(0, 4, null, 1));
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotAddWithoutBar() {
        Result<BilliardTable> result = service.add(new BilliardTable(0, 4, LocalTime.of(23, 0), 0));
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotAddWhenIdAlreadySet() {
        Result<BilliardTable> result = service.add(new BilliardTable(9, 4, LocalTime.of(23, 0), 1));
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldUpdateExistingTable() {
        BilliardTable table = new BilliardTable(3, 6, LocalTime.of(22, 30), 1);
        when(repository.update(table)).thenReturn(true);

        Result<BilliardTable> result = service.update(table);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateMissingTable() {
        BilliardTable table = new BilliardTable(404, 4, LocalTime.of(23, 0), 1);
        when(repository.update(table)).thenReturn(false);

        Result<BilliardTable> result = service.update(table);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldDelete() {
        when(repository.deleteById(3)).thenReturn(true);
        assertTrue(service.deleteById(3));
    }
}
