package learn.models;

/*Dto object for person in line*/
public class ReservationInLine {

    private String playerName;

    public ReservationInLine() {
    }

    public static ReservationInLine fromReservation(Reservation reservation) {
        ReservationInLine view = new ReservationInLine();
        view.playerName = reservation.getPlayerName();
        return view;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
