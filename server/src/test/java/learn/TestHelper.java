package learn;

import learn.models.Bar;
import learn.models.BarOwner;
import learn.models.BilliardTable;
import learn.models.Reservation;

import java.time.LocalTime;

public class TestHelper {


    public static final String HASH = "-1424436561";

    public static BarOwner barOwner1 =
            new BarOwner(1, "aprescott@dev10.com", "Alasco", "Prescott", HASH);


    public static Bar bar1 = new Bar(1, "Carmelos", "1234 Main St", 1);
    public static Bar bar2 = new Bar(2, "Paddy's Pub", "123 5th Ave", 1);

    public static BilliardTable table1 = new BilliardTable(1, 4, LocalTime.of(23, 0), 1);

    // first in line
    public static Reservation reservation1 =
            new Reservation(1, "Ada", "ada@example.com", "seed-session-ada", 1);
}
