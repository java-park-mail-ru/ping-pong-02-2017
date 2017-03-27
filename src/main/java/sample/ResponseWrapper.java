package sample;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sergey on 28.03.17.
 */
public class ResponseWrapper<T> {
    private List<String> errors;
    private T data;

    @JsonCreator
    public ResponseWrapper(@JsonProperty("errors") List<String> errors,
                           @JsonProperty("data") T data) {
        this.errors = errors;
        this.data = data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public T getData() {
        return data;
    }
}
