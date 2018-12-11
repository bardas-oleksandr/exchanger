package ua.pb.exception;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ValidationException extends RuntimeException {

    @Autowired
    private Gson gson;

    public ValidationException(String message) {
        super(message);
    }

    public List<String> getErrorCodeMessages() {
        return gson.fromJson(getMessage(), ArrayList.class);
    }
}
