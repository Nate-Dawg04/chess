package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseException;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import server.requests.RegisterRequest;
import server.results.RegisterResult;
import service.*;

public class RegisterHandler implements Handler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) throws AlreadyTakenException, BadRequestException, DatabaseException {
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(context.body(), RegisterRequest.class);
        RegisterResult registerResult;
        try {
            registerResult = userService.register(registerRequest);
        } catch (AlreadyTakenException ex) {
            throw new AlreadyTakenException(ex.getMessage());
        } catch (BadRequestException ex) {
            throw new BadRequestException(ex.getMessage());
        }
        context.status(200);
        context.json(gson.toJson(registerResult));
    }
}
