// crowd-funding-api/src/main/java/learn/fund/domain/Result.java
package learn.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Result<T> {

    private ResultType type = ResultType.SUCCESS;
    private T payload;
    private ArrayList<String> messages = new ArrayList<>();

    public boolean isSuccess() {
        return type == ResultType.SUCCESS;
    }

    public List<String> getErrorMessages() {
        return new ArrayList<>(messages);
    }

    public void addErrorMessage(String message) {
        messages.add(message);
        this.type = ResultType.INVALID;
    }

    public void setNotFound() {
        this.type = ResultType.NOT_FOUND;
    }

    public ResultType getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    // snip
}