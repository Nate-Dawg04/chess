package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(int status, String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = map.get("message").toString();
        return new ResponseException(fromHttpStatusCode(status), message);
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        if (httpStatusCode >= 400 && httpStatusCode < 500){
            return Code.ClientError;
        } else if (httpStatusCode >= 500 && httpStatusCode < 600) {
            return Code.ServerError;
        } else {
            throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        }
    }

}