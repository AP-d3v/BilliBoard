package learn.models;

import java.util.List;

public class TableLine {

    private String currentPlayerName;
    private boolean confirmOpen;
    private List<ReservationInLine> waiting;

    public TableLine() {
    }

    public TableLine(String currentPlayerName, boolean confirmOpen, List<ReservationInLine> waiting) {
        this.currentPlayerName = currentPlayerName;
        this.confirmOpen = confirmOpen;
        this.waiting = waiting;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName;
    }

    public boolean isConfirmOpen() {
        return confirmOpen;
    }

    public void setConfirmOpen(boolean confirmOpen) {
        this.confirmOpen = confirmOpen;
    }

    public List<ReservationInLine> getWaiting() {
        return waiting;
    }

    public void setWaiting(List<ReservationInLine> waiting) {
        this.waiting = waiting;
    }
}
