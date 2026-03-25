package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import requests.LoginRequest;
import results.LoginResult;
import service.UserService;

public class LoginHandler implements Handler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context context) throws BadRequestException, UnauthorizedException,
            DatabaseException {
        Gson gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(context.body(), LoginRequest.class);

        LoginResult loginResult;
        try {
            loginResult = userService.login(loginRequest);
        } catch (BadRequestException ex) {
            throw new BadRequestException(ex.getMessage());
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        } catch (DatabaseException ex) {
            throw new DatabaseException(ex.getMessage());
        }
        context.status(200);
        context.json(gson.toJson(loginResult));
    }
}
