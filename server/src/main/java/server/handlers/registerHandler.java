package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import server.requests.RegisterRequest;
import server.results.RegisterResult;
import service.*;

public class registerHandler implements Handler {
    private final UserService userService;

    public registerHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) throws AlreadyTakenException {
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(context.body(), RegisterRequest.class);
        RegisterResult registerResult;
        try {
            registerResult = userService.register(registerRequest);
        } catch (AlreadyTakenException ex) {
            throw new AlreadyTakenException("already taken");
        }
        context.status(200);
        context.json(gson.toJson(registerResult));
    }
}
