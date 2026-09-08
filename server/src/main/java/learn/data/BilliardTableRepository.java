package learn.data;

import learn.models.BilliardTable;

import java.util.List;

public interface BilliardTableRepository {

    List<BilliardTable> findByBarId(int barId);
}
