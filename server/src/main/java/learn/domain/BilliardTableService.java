package learn.domain;

import learn.data.BilliardTableRepository;
import learn.models.BilliardTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BilliardTableService {

    private final BilliardTableRepository repository;

    public BilliardTableService(BilliardTableRepository repository) {
        this.repository = repository;
    }

    public List<BilliardTable> findByBarId(int barId) {
        return repository.findByBarId(barId);
    }

    public BilliardTable findById(int tableId) {
        return repository.findById(tableId);
    }

    public Result<BilliardTable> add(BilliardTable table) {
        Result<BilliardTable> result = validate(table);
        if (!result.isSuccess()) {
            return result;
        }
        if (table.getTableId() != 0) {
            result.addErrorMessage("A new table cannot already have an id.");
            return result;
        }
        result.setPayload(repository.add(table));
        return result;
    }

    public Result<BilliardTable> update(BilliardTable table) {
        Result<BilliardTable> result = validate(table);
        if (!result.isSuccess()) {
            return result;
        }
        if (table.getTableId() <= 0) {
            result.addErrorMessage("Table id is needed to update.");
            return result;
        }
        if (!repository.update(table)) {
            result.addErrorMessage("Table id " + table.getTableId() + " does not exist cannot update.", ResultType.NOT_FOUND);
            return result;
        }
        result.setPayload(table);
        return result;
    }

    public boolean deleteById(int tableId) {
        return repository.deleteById(tableId);
    }

    private Result<BilliardTable> validate(BilliardTable table) {
        Result<BilliardTable> result = new Result<>();
        if (table == null) {
            result.addErrorMessage("Table is required.");
            return result;
        }
        if (table.getMaxPlayers() <= 0) {
            result.addErrorMessage("Max players must be greater than zero.");
        }
        if (table.getClosingTime() == null) {
            result.addErrorMessage("Closing time is required.");
        }
        if (table.getBarId() <= 0) {
            result.addErrorMessage("A table must belong to a bar.");
        }
        return result;
    }
}
