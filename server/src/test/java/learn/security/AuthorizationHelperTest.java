package learn.security;

import learn.models.BarOwner;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Plain unit test (no Spring, no DB) -- covers the four outcomes the auth
 * notes part 2 §5 explicitly say to test:
 *   1. no Authorization header
 *   2. header present but tampered with
 *   3. header present but the user JSON is malformed
 *   4. the successful round-trip
 */
class AuthorizationHelperTest {

    private final AuthorizationHelper helper = new AuthorizationHelper("test-secret");

    private BarOwner sampleOwner() {
        return new BarOwner(1, "aprescott@dev10.com", "Alasco", "Prescott", "-1424436561");
    }

    @Test
    void makeTokenThenVerifyRoundTrips() {
        String token = helper.makeToken(sampleOwner());

        AuthResult result = helper.getBarOwnerFromHeaders(Map.of("authorization", token));

        assertTrue(result.isSuccess());
        assertEquals(1, result.getBarOwner().getBarOwnerId());
        assertEquals("aprescott@dev10.com", result.getBarOwner().getEmail());
        assertEquals("Alasco", result.getBarOwner().getFirstname());
        // the password never travels in the token
        assertNull(result.getBarOwner().getPassword());
    }

    @Test
    void missingHeaderIsUnauthorized() {
        AuthResult result = helper.getBarOwnerFromHeaders(Map.of());
        assertFalse(result.isSuccess());
        assertEquals(401, result.getResponseEntity().getStatusCode().value());
    }

    @Test
    void tamperedClaimsAreRejected() {
        String token = helper.makeToken(sampleOwner());
        // change the id in the JSON but keep the original signature
        String tampered = token.replace("\"barOwnerId\":1", "\"barOwnerId\":2");
        assertNotEquals(token, tampered);

        AuthResult result = helper.getBarOwnerFromHeaders(Map.of("authorization", tampered));

        assertFalse(result.isSuccess());
        assertEquals(401, result.getResponseEntity().getStatusCode().value());
    }

    @Test
    void malformedTokenIsRejected() {
        assertFalse(helper.getBarOwnerFromHeaders(Map.of("authorization", "not-a-real-token")).isSuccess());
        assertFalse(helper.getBarOwnerFromHeaders(Map.of("authorization", "{bad json}|123")).isSuccess());
    }

    @Test
    void differentSecretCannotVerify() {
        String token = helper.makeToken(sampleOwner());
        AuthorizationHelper otherServer = new AuthorizationHelper("a-different-secret");
        assertFalse(otherServer.getBarOwnerFromHeaders(Map.of("authorization", token)).isSuccess());
    }
}
