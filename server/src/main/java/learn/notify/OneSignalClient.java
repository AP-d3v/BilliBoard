package learn.notify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OneSignalClient {

    private final String appId;
    private final String restApiKey;
    private final String webUrl;
    private final RestClient restClient = RestClient.create();

    public OneSignalClient(@Value("${onesignal.app-id:}") String appId,
                           @Value("${onesignal.rest-api-key:}") String restApiKey,
                           @Value("${app.web-url:http://localhost:5173}") String webUrl) {
        this.appId = appId.trim();
        this.restApiKey = restApiKey.trim();
        this.webUrl = webUrl.trim();
    }

    public void push(String subscriptionId, String heading, String message) {
        Map<String, Object> body = baseBody(subscriptionId, heading, message);
        if (body == null) {
            return;
        }
        send(subscriptionId, body);
    }

    public void pushConfirmRequest(String subscriptionId, int tableId) {
        Map<String, Object> body = baseBody(subscriptionId,
                "Are you still playing?",
                "Someone is waiting for this table. Tap \"I'm done\" to give it up, "
                        + "or scan the Still Here QR to keep it. You have 2 minutes.");
        if (body == null) {
            return;
        }
        body.put("url", webUrl + "/still-here/" + tableId);
        body.put("web_buttons", List.of(Map.of(
                "id", "done",
                "text", "I'm done",
                "url", webUrl + "/done/" + tableId)));
        send(subscriptionId, body);
    }

    private Map<String, Object> baseBody(String subscriptionId, String heading, String message) {
        if (appId.isBlank() || restApiKey.isBlank()) {
            System.out.println("OneSignal push skipped: app id / key not configured");
            return null;
        }
        if (subscriptionId == null || subscriptionId.isBlank()) {
            System.out.println("OneSignal push skipped: this patron has no push subscription id");
            return null;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("target_channel", "push");
        body.put("include_subscription_ids", List.of(subscriptionId));
        body.put("headings", Map.of("en", heading));
        body.put("contents", Map.of("en", message));
        return body;
    }

    private void send(String subscriptionId, Map<String, Object> body) {
        try {
            String response = restClient.post()
                    .uri("https://api.onesignal.com/notifications")
                    .header("Authorization", "Key " + restApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            System.out.println("OneSignal push -> sub " + subscriptionId + " : " + response);
        } catch (Exception ex) {
            System.err.println("OneSignal push failed: " + ex.getMessage());
        }
    }
}
