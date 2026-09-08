package learn.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.models.BarOwner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
public class AuthorizationHelper {

    private final String secret;
    private final ObjectMapper objectMapper = new ObjectMapper();
    /*uses env variable for auth secret*/
    public AuthorizationHelper(@Value("${auth.secret}") String secret) {
        this.secret = secret;
    }

   /*creating tokens on successful login*/
    public String makeToken(BarOwner owner) {
        try {
            String userJson = objectMapper.writeValueAsString(BarOwnerNoPassword.fromBarOwner(owner));
            int signature = Objects.hash(userJson + secret);
            return userJson + "|" + signature;
        } catch (Exception ex) {

            throw new IllegalStateException("Could not create auth token", ex);
        }
    }

    /*authenticating user signature from header after we recieve a request */
    public AuthResult getBarOwnerFromHeaders(Map<String, String> headers) {
        AuthResult result = new AuthResult();

        String token = headers == null ? null : headers.get("authorization");
        if (token == null || token.isBlank()) {
            result.setResponseEntity(unauthorized("Missing Authorization header."));
            return result;
        }
        /*uses lastIndexOf array method just in case something is wrong with the token*/
        int pipe = token.lastIndexOf('|');
        if (pipe < 0) {
            result.setResponseEntity(unauthorized("bad auth token."));
            return result;
        }
        /* gives us serialized object*/
        String userJson = token.substring(0, pipe);
        int claimedSignature;
        try {/*gives us signature of claimed token*/
            claimedSignature = Integer.parseInt(token.substring(pipe + 1));
        } catch (NumberFormatException ex) {
            result.setResponseEntity(unauthorized("bad auth token."));
            return result;
        }

        int recomputedSignature = Objects.hash(userJson + secret);
        if (recomputedSignature != claimedSignature) {
            result.setResponseEntity(unauthorized(" Fraudulent  authorization header."));
            return result;
        }
        /*for issue where sent json was invalid in some way. */
        try {
            BarOwnerNoPassword payload = objectMapper.readValue(userJson, BarOwnerNoPassword.class);
            result.setBarOwner(payload.toBarOwner());
        } catch (Exception ex) {
            result.setResponseEntity(unauthorized("Invalid user in auth token."));
        }
        return result;
    }
        /*for responseEntity Object*/
    private static ResponseEntity<Object> unauthorized(String message) {
        return new ResponseEntity<>(List.of(message), HttpStatus.UNAUTHORIZED);
    }
}
