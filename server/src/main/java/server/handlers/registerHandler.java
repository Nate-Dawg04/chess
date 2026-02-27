package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import server.requests.RegisterRequest;
import server.results.RegisterResult;
import service.UserService;

public class registerHandler implements Handler {
    private final UserService service;

    public registerHandler(UserService service) {
        this.service = service;
    }

    public void handle(Context context) throws AlreadyTakenException {
        String jsonResponse = context.body();
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(jsonResponse, RegisterRequest.class);
        RegisterResult registerResult = service.register(registerRequest);
        context.result(gson.toJson(registerResult));

    }
}
