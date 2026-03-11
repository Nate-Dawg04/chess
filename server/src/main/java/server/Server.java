package server;

import com.google.gson.Gson;
import dataaccess.databaseDAOs.SQLAuthDAO;
import dataaccess.databaseDAOs.SQLGameDAO;
import dataaccess.databaseDAOs.SQLUserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedException;
import dataaccess.memoryDAOs.MemoryAuthDAO;
import dataaccess.memoryDAOs.MemoryGameDAO;
import dataaccess.memoryDAOs.MemoryUserDAO;
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
        SQLUserDAO sqlUserDAO = null;
        SQLAuthDAO sqlAuthDAO = null;
        SQLGameDAO sqlGameDAO = null;
        try {
            sqlUserDAO = new SQLUserDAO();
        } catch (Exception ex) {
            System.out.println("\"Unable to start server: %s%n\", ex.getMessage()");
        }
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(sqlUserDAO, memoryAuthDAO, memoryGameDAO);
        GameService gameService = new GameService(sqlUserDAO, memoryAuthDAO, memoryGameDAO);
        ClearService clearService = new ClearService(sqlUserDAO, memoryAuthDAO, memoryGameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .get("/error", this::throwException)
                .exception(Exception.class, this::exceptionHandler)
                .post("/user", new RegisterHandler(userService))
                .post("/session", new LoginHandler(userService))
                .delete("/session", new LogoutHandler(userService))
                .get("/game", new ListGamesHandler(gameService))
                .post("/game", new CreateGameHandler(gameService))
                .put("/game", new JoinGameHandler(gameService))
                .delete("/db", new ClearHandler(clearService));
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
        // handles different Exceptions, provides correct status code
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
