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

    public Result<BarOwner> authenticate (BarOwner proposedLoginCreds){
        Result<BarOwner> result = new Result<>();
        BarOwner fromDb = barOwnerRepository.findByEmail(proposedLoginCreds.getEmail());

        if(fromDb == null){
            result.setType(ResultType.INVALID);
            result.addErrorMessage("Invalid email and/or password");
            return result;
        }

        if(!(fromDb.getPassword().equalsIgnoreCase(proposedLoginCreds.getPassword()))){
            result.setType(ResultType.INVALID);
            result.addErrorMessage("Invalid email and/or password");
            return result;
        }

        result.setPayload(fromDb);
        return result;
    }


}
