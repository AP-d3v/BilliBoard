package learn.domain;

import learn.data.BarOwnerRepository;
import learn.models.BarOwner;
import org.springframework.stereotype.Service;

@Service
public class BarOwnerService {

private final BarOwnerRepository barOwnerRepository;

    public BarOwnerService(BarOwnerRepository barOwnerRepository) {
        this.barOwnerRepository = barOwnerRepository;
    }

    public BarOwner authenticate (BarOwner proposedLoginCreds){
        return null;
    }


}
