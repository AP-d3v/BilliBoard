package learn.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {

    private int reservationId;
    private String playerName;
    private String patronEmail;
    private String sessionId;
    private int tableId;
    private String status;
    private LocalDateTime confirmRequestedAt;
    private String nudgedBySession;
    private String onesignalSubscriptionId;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getConfirmRequestedAt() {
        return confirmRequestedAt;
    }

    public void setConfirmRequestedAt(LocalDateTime confirmRequestedAt) {
        this.confirmRequestedAt = confirmRequestedAt;
    }

    public String getNudgedBySession() {
        return nudgedBySession;
    }

    public void setNudgedBySession(String nudgedBySession) {
        this.nudgedBySession = nudgedBySession;
    }

    public String getOnesignalSubscriptionId() {
        return onesignalSubscriptionId;
    }

    public void setOnesignalSubscriptionId(String onesignalSubscriptionId) {
        this.onesignalSubscriptionId = onesignalSubscriptionId;
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
