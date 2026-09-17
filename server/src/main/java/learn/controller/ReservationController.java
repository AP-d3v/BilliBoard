package learn.controller;

import learn.domain.Result;
import learn.domain.ResultType;
import learn.domain.ReservationService;
import learn.models.Reservation;
import learn.models.TableLine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tables/{tableId}/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public TableLine line(@PathVariable int tableId) {
        return service.boardFor(tableId);
    }

    @PostMapping("/role")
    public Map<String, String> role(@PathVariable int tableId, @RequestBody Map<String, Object> body) {
        return Map.of("role", service.roleFor(tableId, (String) body.get("sessionId")));
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

    @PostMapping("/nudge")
    public ResponseEntity<Object> nudge(@PathVariable int tableId, @RequestBody Map<String, Object> body) {
        return toResponse(service.nudge(tableId, (String) body.get("sessionId")));
    }

    @PostMapping("/check-in")
    public ResponseEntity<Object> checkIn(@PathVariable int tableId, @RequestBody Map<String, Object> body) {
        return toResponse(service.checkIn(tableId, (String) body.get("sessionId")));
    }

    @PostMapping("/still-here")
    public ResponseEntity<Object> stillHere(@PathVariable int tableId, @RequestBody Map<String, Object> body) {
        return toResponse(service.stillHere(tableId, (String) body.get("sessionId")));
    }

    @PostMapping("/give-up")
    public ResponseEntity<Object> giveUp(@PathVariable int tableId, @RequestBody Map<String, Object> body) {
        return toResponse(service.giveUp(tableId, (String) body.get("sessionId")));
    }

    private ResponseEntity<Object> toResponse(Result<Void> result) {
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(result.getErrorMessages(), HttpStatus.BAD_REQUEST);
    }
}
