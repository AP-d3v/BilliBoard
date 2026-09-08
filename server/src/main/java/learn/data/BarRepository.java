package learn.data;

import learn.models.Bar;

import java.util.List;

public interface BarRepository {

    List<Bar> findByBarOwnerId(int barOwnerId);

    Bar findById(int barId);

    Bar add(Bar bar);

    boolean update(Bar bar);

    boolean deleteById(int barId);
}
