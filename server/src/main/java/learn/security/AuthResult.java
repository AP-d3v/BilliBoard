package learn.security;

import learn.models.BarOwner;
import org.springframework.http.ResponseEntity;


public class AuthResult {

    private BarOwner barOwner;
    private ResponseEntity<Object> responseEntity;

    public boolean isSuccess() {
        return barOwner != null;
    }

    public BarOwner getBarOwner() {
        return barOwner;
    }

    public void setBarOwner(BarOwner barOwner) {
        this.barOwner = barOwner;
    }

    public ResponseEntity<Object> getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<Object> responseEntity) {
        this.responseEntity = responseEntity;
    }
}
