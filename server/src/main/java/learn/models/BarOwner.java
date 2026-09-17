package learn.models;

import java.util.Objects;

public class BarOwner {

   private int barOwnerId;
   private String email;
   private String firstname;
   private String lastName;
   private String password;

    public BarOwner() {
    }

    public BarOwner(int barOwnerId, String email, String firstname, String lastName, String password) {
        this.barOwnerId = barOwnerId;
        this.email = email;
        this.firstname = firstname;
        this.lastName = lastName;
        this.password = password;
    }

    public int getBarOwnerId() {
        return barOwnerId;
    }

    public void setBarOwnerId(int barOwnerId) {
        this.barOwnerId = barOwnerId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BarOwner barOwner)) return false;
        return barOwnerId == barOwner.barOwnerId && Objects.equals(email, barOwner.email) && Objects.equals(firstname, barOwner.firstname) && Objects.equals(lastName, barOwner.lastName) && Objects.equals(password, barOwner.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barOwnerId, email, firstname, lastName, password);
    }
}
