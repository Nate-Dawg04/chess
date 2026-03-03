package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import server.requests.CreateGameRequest;
import server.results.CreateGameResult;
import service.GameService;

public class CreateGameHandler implements Handler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context context) throws UnauthorizedException, BadRequestException {
        Gson gson = new Gson();
        CreateGameRequest tempCreateGameRequest = gson.fromJson(context.body(),CreateGameRequest.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(context.header("authorization"),
                                                                    tempCreateGameRequest.gameName());
        CreateGameResult createGameResult;
        try {
            createGameResult = gameService.createGame(createGameRequest);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        } catch (BadRequestException ex) {
            throw new BadRequestException(ex.getMessage());
        }
        context.status(200);
        context.json(gson.toJson(createGameResult));
    }
}
