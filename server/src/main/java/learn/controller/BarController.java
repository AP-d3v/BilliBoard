package learn.controller;

import learn.domain.BarService;
import learn.domain.BilliardTableService;
import learn.domain.Result;
import learn.domain.ResultType;
import learn.models.Bar;
import learn.models.BarOwner;
import learn.models.BilliardTable;
import learn.security.AuthResult;
import learn.security.AuthorizationHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bars")
public class BarController {

    private final BarService service;
    private final BilliardTableService billiardTableService;
    private final AuthorizationHelper authorizationHelper;

    public BarController(BarService service, BilliardTableService billiardTableService,
                         AuthorizationHelper authorizationHelper) {
        this.service = service;
        this.billiardTableService = billiardTableService;
        this.authorizationHelper = authorizationHelper;
    }

    @GetMapping
    public ResponseEntity<Object> findMyBars(@RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        List<Bar> bars = service.findByBarOwnerId(auth.getBarOwner().getBarOwnerId());
        return ResponseEntity.ok(bars);
    }

    @GetMapping("/{barId}")
    public ResponseEntity<Object> findById(@PathVariable int barId,
                                           @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        ResponseEntity<Object> ownershipProblem = checkOwnership(barId, auth.getBarOwner());
        if (ownershipProblem != null) {
            return ownershipProblem;
        }
        return ResponseEntity.ok(service.findById(barId));
    }

    @GetMapping("/{barId}/tables")
    public ResponseEntity<Object> findTablesForBar(@PathVariable int barId,
                                                   @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        // call back that makes sure the url in the http request and the owner actually belong to one another
        ResponseEntity<Object> ownershipProblem = checkOwnership(barId, auth.getBarOwner());
        if (ownershipProblem != null) {
            return ownershipProblem;
        }
        return ResponseEntity.ok(billiardTableService.findByBarId(barId));
    }

    @PostMapping("/{barId}/tables")
    public ResponseEntity<Object> addTable(@PathVariable int barId, @RequestBody BilliardTable table,
                                           @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        ResponseEntity<Object> ownershipProblem = checkOwnership(barId, auth.getBarOwner());
        if (ownershipProblem != null) {
            return ownershipProblem;
        }
        table.setTableId(0);
        table.setBarId(barId);

        Result<BilliardTable> result = billiardTableService.add(table);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getErrorMessages(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{barId}/tables/{tableId}")
    public ResponseEntity<Object> updateTable(@PathVariable int barId, @PathVariable int tableId,
                                              @RequestBody BilliardTable table,
                                              @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        ResponseEntity<Object> ownershipProblem = checkOwnership(barId, auth.getBarOwner());
        if (ownershipProblem != null) {
            return ownershipProblem;
        }
        //method to ensure table belongs to bar
        ResponseEntity<Object> tableProblem = checkTableAtBar(barId, tableId);
        if (tableProblem != null) {
            return tableProblem;
        }
        table.setTableId(tableId);
        table.setBarId(barId);

        Result<BilliardTable> result = billiardTableService.update(table);
        if (!result.isSuccess()) {
            HttpStatus status = result.getType() == ResultType.NOT_FOUND
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result.getErrorMessages(), status);
        }
        return ResponseEntity.ok(result.getPayload());
    }

    @DeleteMapping("/{barId}/tables/{tableId}")
    public ResponseEntity<Object> deleteTable(@PathVariable int barId, @PathVariable int tableId,
                                              @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        ResponseEntity<Object> ownershipProblem = checkOwnership(barId, auth.getBarOwner());
        if (ownershipProblem != null) {
            return ownershipProblem;
        }
        ResponseEntity<Object> tableProblem = checkTableAtBar(barId, tableId);
        if (tableProblem != null) {
            return tableProblem;
        }
        billiardTableService.deleteById(tableId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody Bar bar, @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        bar.setBarId(0);
        bar.setBarOwnerId(auth.getBarOwner().getBarOwnerId());

        Result<Bar> result = service.add(bar);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getErrorMessages(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{barId}")
    public ResponseEntity<Object> update(@PathVariable int barId, @RequestBody Bar bar,
                                         @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }

        ResponseEntity<Object> ownershipProblem = checkOwnership(barId, auth.getBarOwner());
        if (ownershipProblem != null) {
            return ownershipProblem;
        }
        /*set the bar id from the path variable and the barOwnerId from the request header*/
        bar.setBarId(barId);
        bar.setBarOwnerId(auth.getBarOwner().getBarOwnerId());

        Result<Bar> result = service.update(bar);
        if (!result.isSuccess()) {
            HttpStatus status = result.getType() == ResultType.NOT_FOUND
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result.getErrorMessages(), status);
        }
        return ResponseEntity.ok(result.getPayload());
    }

    @DeleteMapping("/{barId}")
    public ResponseEntity<Object> deleteById(@PathVariable int barId,
                                             @RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }

        ResponseEntity<Object> ownershipProblem = checkOwnership(barId, auth.getBarOwner());
        if (ownershipProblem != null) {
            return ownershipProblem;
        }

        service.deleteById(barId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private ResponseEntity<Object> checkOwnership(int barId, BarOwner owner) {
        Bar existing = service.findById(barId);
        if (existing == null) {
            return new ResponseEntity<>(List.of("Bar not found."), HttpStatus.NOT_FOUND);
        }
        if (existing.getBarOwnerId() != owner.getBarOwnerId()) {
            return new ResponseEntity<>(List.of("That bar belongs to someone else."), HttpStatus.FORBIDDEN);
        }
        return null;
    }

    // makes sure the table belongs to the bar
    private ResponseEntity<Object> checkTableAtBar(int barId, int tableId) {
        BilliardTable existing = billiardTableService.findById(tableId);
        if (existing == null || existing.getBarId() != barId) {
            return new ResponseEntity<>(List.of("Table not found at this bar."), HttpStatus.NOT_FOUND);
        }
        return null;
    }
}
