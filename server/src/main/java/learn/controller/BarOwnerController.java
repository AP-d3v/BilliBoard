package learn.controller;

import learn.domain.BarOwnerService;
import learn.domain.Result;
import learn.domain.ResultType;
import learn.models.BarOwner;
import learn.security.AuthResult;
import learn.security.AuthorizationHelper;
import learn.security.BarOwnerNoPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class BarOwnerController {

    private final BarOwnerService service;
    private final AuthorizationHelper authorizationHelper;

    public BarOwnerController(BarOwnerService service, AuthorizationHelper authorizationHelper) {
        this.service = service;
        this.authorizationHelper = authorizationHelper;
    }


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody BarOwner proposedLogin) {
        Result<BarOwner> result = service.authenticate(proposedLogin);

        if (result.isSuccess()) {
            String token = authorizationHelper.makeToken(result.getPayload());
            return ResponseEntity.ok(Map.of("diyJwt", token));
        }
        if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(result.getErrorMessages(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.getErrorMessages(), HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody BarOwner newOwner) {
        Result<BarOwner> result = service.create(newOwner);

        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getErrorMessages(), HttpStatus.BAD_REQUEST);
        }
        String token = authorizationHelper.makeToken(result.getPayload());
        return new ResponseEntity<>(Map.of("diyJwt", token), HttpStatus.CREATED);
    }


    @GetMapping("/me")
    public ResponseEntity<Object> me(@RequestHeader Map<String, String> headers) {
        AuthResult auth = authorizationHelper.getBarOwnerFromHeaders(headers);
        if (!auth.isSuccess()) {
            return auth.getResponseEntity();
        }
        return ResponseEntity.ok(BarOwnerNoPassword.fromBarOwner(auth.getBarOwner()));
    }
}
