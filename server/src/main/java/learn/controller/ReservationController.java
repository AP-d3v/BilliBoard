package learn.controller;

import learn.domain.Result;
import learn.domain.ResultType;
import learn.domain.ReservationService;
import learn.models.Reservation;
import learn.models.ReservationInLine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/tables/{tableId}/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    //returns the dto version of reservation
    @GetMapping
    public List<ReservationInLine> line(@PathVariable int tableId) {
        List<ReservationInLine> line = new ArrayList<>();
        for (Reservation reservation : service.findByTableId(tableId)) {
            line.add(ReservationInLine.fromReservation(reservation));
        }
        return line;
    }

    @PostMapping
    public ResponseEntity<Object> join(@PathVariable int tableId, @RequestBody Reservation reservation) {
        reservation.setTableId(tableId);
        Result<Reservation> result = service.join(reservation);
        if (!result.isSuccess()) {
            HttpStatus status = result.getType() == ResultType.NOT_FOUND
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result.getErrorMessages(), status);
        }
        // the response has the session id with it
        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }


    @DeleteMapping
    public ResponseEntity<Object> leave(@PathVariable int tableId, @RequestBody Reservation reservation) {
        boolean left = service.leave(tableId, reservation.getSessionId());
        if (left) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(List.of("You're not in line for this table."), HttpStatus.NOT_FOUND);
    }
}
