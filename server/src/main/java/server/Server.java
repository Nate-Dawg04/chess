package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedException;
import io.javalin.*;
import io.javalin.http.Context;
import server.handlers.*;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO, memoryGameDAO);
        GameService gameService = new GameService(memoryUserDAO,memoryAuthDAO, memoryGameDAO);
        ClearService clearService = new ClearService(memoryUserDAO,memoryAuthDAO, memoryGameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .get("/error", this::throwException)
                .exception(Exception.class, this::exceptionHandler)
                .post("/user", new registerHandler(userService))
                .post("/session", new loginHandler(userService))
                .delete("/session", new logoutHandler(userService))
                .get("/game", new listGamesHandler(gameService))
                .post("/game", new createGameHandler(gameService))
                .delete("/db",new clearHandler(clearService));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void throwException(Context context) {
        throw new RuntimeException("The server is on fire!");
    }

    private void exceptionHandler(Exception e, Context context) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        if (e.getClass() == AlreadyTakenException.class){
            context.status(403);
        } else if (e.getClass() == BadRequestException.class) {
            context.status(400);
        } else if (e.getClass() == UnauthorizedException.class) {
            context.status(401);
        } else {
            context.status(500);
        }
        context.json(body);
    }

}
