package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import server.requests.JoinGameRequest;
import server.results.JoinGameResult;
import service.GameService;

public class JoinGameHandler implements Handler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context context) throws BadRequestException, UnauthorizedException,
            AlreadyTakenException, DataAccessException {
        Gson gson = new Gson();
        JoinGameRequest tempJoinGameRequest = gson.fromJson(context.body(), JoinGameRequest.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(context.header("authorization"),
                tempJoinGameRequest.playerColor(), tempJoinGameRequest.gameID());
        JoinGameResult joinGameResult;
        try {
            joinGameResult = gameService.joinGame(joinGameRequest);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        } catch (BadRequestException ex) {
            throw new BadRequestException(ex.getMessage());
        } catch (AlreadyTakenException ex) {
            throw new AlreadyTakenException(ex.getMessage());
        }
        context.status(200);
        context.json(gson.toJson(joinGameResult));
    }
}
