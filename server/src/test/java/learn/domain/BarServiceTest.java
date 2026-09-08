package learn.domain;

import learn.data.BarRepository;
import learn.models.Bar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class BarServiceTest {

    BarRepository repository;
    BarService service;

    @BeforeEach
    void setUp() {
        repository = mock(BarRepository.class);
        service = new BarService(repository);
    }

    @Test
    void shouldAddValidBar() {
        Bar toAdd = new Bar(0, "New Bar", "1 Ball Lane", 1);
        Bar saved = new Bar(5, "New Bar", "1 Ball Lane", 1);
        when(repository.add(toAdd)).thenReturn(saved);

        Result<Bar> result = service.add(toAdd);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getPayload().getBarId());
    }

    @Test
    void shouldNotAddBlankName() {
        Result<Bar> result = service.add(new Bar(0, " ", "1 Ball Lane", 1));

        assertFalse(result.isSuccess());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldNotAddWithoutOwner() {
        Result<Bar> result = service.add(new Bar(0, "New Bar", "1 Ball Lane", 0));
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotAddWhenIdAlreadySet() {
        Result<Bar> result = service.add(new Bar(9, "New Bar", "1 Ball Lane", 1));
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldUpdateExistingBar() {
        Bar bar = new Bar(3, "Renamed", "2 Rack Rd", 1);
        when(repository.update(bar)).thenReturn(true);

        Result<Bar> result = service.update(bar);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateMissingBar() {
        Bar bar = new Bar(404, "Ghost", "Nowhere", 1);
        when(repository.update(bar)).thenReturn(false);

        Result<Bar> result = service.update(bar);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldDelete() {
        when(repository.deleteById(3)).thenReturn(true);
        assertTrue(service.deleteById(3));
    }
}
