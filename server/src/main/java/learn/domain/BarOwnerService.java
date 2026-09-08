package learn.domain;

import learn.data.BarOwnerRepository;
import learn.models.BarOwner;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BarOwnerService {

    private final BarOwnerRepository barOwnerRepository;

    public BarOwnerService(BarOwnerRepository barOwnerRepository) {
        this.barOwnerRepository = barOwnerRepository;
    }


    public Result<BarOwner> authenticate(BarOwner proposedLogin) {
        Result<BarOwner> result = new Result<>();

        BarOwner fromDb = barOwnerRepository.findByEmail(proposedLogin.getEmail());

        if (fromDb == null) {
            result.addErrorMessage("Bar owner does not exist.", ResultType.NOT_FOUND);
            return result;
        }

        if (!fromDb.getPassword().equals(hash(proposedLogin.getPassword()))) {
            result.addErrorMessage("Incorrect password.", ResultType.INVALID);
            return result;
        }

        result.setPayload(fromDb);
        return result;
    }


    public Result<BarOwner> create(BarOwner newOwner) {
        Result<BarOwner> result = validate(newOwner);
        if (!result.isSuccess()) {
            return result;
        }

        if (barOwnerRepository.findByEmail(newOwner.getEmail()) != null) {
            result.addErrorMessage("That email is already registered.");
            return result;
        }

        //stores hashed password in database*/
        newOwner.setPassword(hash(newOwner.getPassword()));
        newOwner.setBarOwnerId(0);

        result.setPayload(barOwnerRepository.add(newOwner));
        return result;
    }

    private Result<BarOwner> validate(BarOwner owner) {
        Result<BarOwner> result = new Result<>();
        if (owner == null) {
            result.addErrorMessage("Bar owner is required.");
            return result;
        }
        if (isBlank(owner.getEmail())) {
            result.addErrorMessage("Email is required.");
        }
        if (isBlank(owner.getFirstname())) {
            result.addErrorMessage("First name is required.");
        }
        if (isBlank(owner.getLastName())) {
            result.addErrorMessage("Last name is required.");
        }
        if (isBlank(owner.getPassword()) || owner.getPassword().length() < 6) {
            result.addErrorMessage("Password must be at least 6 characters.");
        }
        return result;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }


    public static String hash(String rawPassword) {
        return String.valueOf(Objects.hash(rawPassword));
    }
}
