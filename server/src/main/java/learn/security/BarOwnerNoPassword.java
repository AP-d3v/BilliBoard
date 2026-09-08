package learn.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import learn.models.BarOwner;

/* Annotation tells Jackson to ignore any unrecognized json fields and not fail the program*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class BarOwnerNoPassword {

    private int barOwnerId;
    private String email;
    private String firstname;
    private String lastName;

    public BarOwnerNoPassword() {
    }

    public static BarOwnerNoPassword fromBarOwner(BarOwner owner) {
        BarOwnerNoPassword dto = new BarOwnerNoPassword();
        dto.barOwnerId = owner.getBarOwnerId();
        dto.email = owner.getEmail();
        dto.firstname = owner.getFirstname();
        dto.lastName = owner.getLastName();
        return dto;
    }

    public BarOwner toBarOwner() {
        BarOwner owner = new BarOwner();
        owner.setBarOwnerId(barOwnerId);
        owner.setEmail(email);
        owner.setFirstname(firstname);
        owner.setLastName(lastName);
        return owner;
    }

    public int getBarOwnerId() {
        return barOwnerId;
    }

    public void setBarOwnerId(int barOwnerId) {
        this.barOwnerId = barOwnerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
