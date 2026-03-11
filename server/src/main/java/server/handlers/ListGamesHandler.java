package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import server.requests.ListGamesRequest;
import server.results.ListGamesResult;
import service.GameService;

public class ListGamesHandler implements Handler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context context) throws UnauthorizedException , DataAccessException {
        Gson gson = new Gson();
        ListGamesRequest listGamesRequest = new ListGamesRequest(context.header("authorization"));
        ListGamesResult listGamesResult;
        try {
            listGamesResult = gameService.listGames(listGamesRequest);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        }
        context.status(200);
        context.json(gson.toJson(listGamesResult));
    }
}
