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
}
