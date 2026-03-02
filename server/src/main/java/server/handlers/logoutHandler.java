package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import server.requests.LogoutRequest;
import server.results.LogoutResult;
import service.UserService;

public class logoutHandler implements Handler {
    private final UserService userService;

    public logoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context context) throws UnauthorizedException {
        Gson gson = new Gson();
        LogoutRequest logoutRequest = new LogoutRequest(context.header("authorization"));
        LogoutResult logoutResult;
        try {
            logoutResult = userService.logout(logoutRequest);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        }
        context.status(200);
        context.json(gson.toJson(logoutResult));
    }
}
