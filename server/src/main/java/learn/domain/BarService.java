package learn.domain;

import learn.data.BarRepository;
import learn.models.Bar;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarService {

    private final BarRepository repository;

    public BarService(BarRepository repository) {
        this.repository = repository;
    }

    public List<Bar> findByBarOwnerId(int barOwnerId) {
        return repository.findByBarOwnerId(barOwnerId);
    }

    public Bar findById(int barId) {
        return repository.findById(barId);
    }

    public Result<Bar> add(Bar bar) {
        Result<Bar> result = validate(bar);
        if (!result.isSuccess()) {
            return result;
        }
        if (bar.getBarId() != 0) {
            result.addErrorMessage("A new bar cannot already have an id.");
            return result;
        }
        result.setPayload(repository.add(bar));
        return result;
    }

    public Result<Bar> update(Bar bar) {
        Result<Bar> result = validate(bar);
        if (!result.isSuccess()) {
            return result;
        }
        if (bar.getBarId() <= 0) {
            result.addErrorMessage("Bar id is required to update.");
            return result;
        }
        if (!repository.update(bar)) {
            result.addErrorMessage("Bar id " + bar.getBarId() + " was not found.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(bar);
        return result;
    }

    public boolean deleteById(int barId) {
        return repository.deleteById(barId);
    }

    private Result<Bar> validate(Bar bar) {
        Result<Bar> result = new Result<>();
        if (bar == null) {
            result.addErrorMessage("Bar is required.");
            return result;
        }
        if (bar.getBarName() == null || bar.getBarName().isBlank()) {
            result.addErrorMessage("Bar name is required.");
        }
        if (bar.getAddress() == null || bar.getAddress().isBlank()) {
            result.addErrorMessage("Address is required.");
        }
        if (bar.getBarOwnerId() <= 0) {
            result.addErrorMessage("A bar must belong to a bar owner.");
        }
        return result;
    }
}
