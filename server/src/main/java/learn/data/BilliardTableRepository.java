package learn.data;

import learn.models.BilliardTable;

import java.util.List;

public interface BilliardTableRepository {

    List<BilliardTable> findByBarId(int barId);

    BilliardTable findById(int tableId);

    BilliardTable add(BilliardTable table);

    boolean update(BilliardTable table);

    boolean deleteById(int tableId);
}
