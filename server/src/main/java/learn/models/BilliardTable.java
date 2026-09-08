package learn.models;

import java.time.LocalTime;
import java.util.Objects;

public class BilliardTable {

    private int tableId;
    private int maxPlayers;
    private LocalTime closingTime;
    private int barId;

    public BilliardTable() {
    }

    public BilliardTable(int tableId, int maxPlayers, LocalTime closingTime, int barId) {
        this.tableId = tableId;
        this.maxPlayers = maxPlayers;
        this.closingTime = closingTime;
        this.barId = barId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public int getBarId() {
        return barId;
    }

    public void setBarId(int barId) {
        this.barId = barId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BilliardTable that)) return false;
        return tableId == that.tableId
                && maxPlayers == that.maxPlayers
                && barId == that.barId
                && Objects.equals(closingTime, that.closingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId, maxPlayers, closingTime, barId);
    }
}
