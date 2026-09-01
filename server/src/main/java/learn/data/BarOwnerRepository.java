package learn.data;

import learn.models.BarOwner;

public interface BarOwnerRepository {

    BarOwner findByEmail(String email);
}
