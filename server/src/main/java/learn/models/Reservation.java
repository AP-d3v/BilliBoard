package learn.models;

import java.util.Objects;

public class Reservation {

    private int reservationId;
    private String playerName;
    private String patronEmail;
    private String sessionId;
    private int tableId;

    public Reservation() {
    }

    public Reservation(int reservationId, String playerName, String patronEmail, String sessionId, int tableId) {
        this.reservationId = reservationId;
        this.playerName = playerName;
        this.patronEmail = patronEmail;
        this.sessionId = sessionId;
        this.tableId = tableId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPatronEmail() {
        return patronEmail;
    }

    public void setPatronEmail(String patronEmail) {
        this.patronEmail = patronEmail;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reservation that)) return false;
        return reservationId == that.reservationId
                && tableId == that.tableId
                && Objects.equals(playerName, that.playerName)
                && Objects.equals(patronEmail, that.patronEmail)
                && Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, playerName, patronEmail, sessionId, tableId);
    }
}
