package learn.models;

import java.util.Objects;

public class Bar {

    private int barId;
    private String barName;
    private String address;
    private int barOwnerId;

    public Bar() {
    }

    public Bar(int barId, String barName, String address, int barOwnerId) {
        this.barId = barId;
        this.barName = barName;
        this.address = address;
        this.barOwnerId = barOwnerId;
    }

    public int getBarId() {
        return barId;
    }

    public void setBarId(int barId) {
        this.barId = barId;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBarOwnerId() {
        return barOwnerId;
    }

    public void setBarOwnerId(int barOwnerId) {
        this.barOwnerId = barOwnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bar bar)) return false;
        return barId == bar.barId
                && barOwnerId == bar.barOwnerId
                && Objects.equals(barName, bar.barName)
                && Objects.equals(address, bar.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barId, barName, address, barOwnerId);
    }
}
