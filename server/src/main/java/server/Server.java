package server;

import com.google.gson.Gson;
import dataaccess.sql.*;
import dataaccess.exceptions.*;
import io.javalin.*;
import io.javalin.http.Context;
import server.handlers.*;
import server.websocket.WebSocketHandler;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        SQLUserDAO sqlUserDAO = null;
        SQLAuthDAO sqlAuthDAO = null;
        SQLGameDAO sqlGameDAO = null;
        try {
            sqlUserDAO = new SQLUserDAO();
            sqlAuthDAO = new SQLAuthDAO();
            sqlGameDAO = new SQLGameDAO();
        } catch (Exception ex) {
            System.out.println("\"Unable to start server: %s%n\", ex.getMessage()");
        }
        UserService userService = new UserService(sqlUserDAO, sqlAuthDAO, sqlGameDAO);
        GameService gameService = new GameService(sqlUserDAO, sqlAuthDAO, sqlGameDAO);
        ClearService clearService = new ClearService(sqlUserDAO, sqlAuthDAO, sqlGameDAO);

        webSocketHandler = new WebSocketHandler(sqlUserDAO, sqlAuthDAO, sqlGameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .get("/error", this::throwException)
                .exception(Exception.class, this::exceptionHandler)
                .post("/user", new RegisterHandler(userService))
                .post("/session", new LoginHandler(userService))
                .delete("/session", new LogoutHandler(userService))
                .get("/game", new ListGamesHandler(gameService))
                .post("/game", new CreateGameHandler(gameService))
                .put("/game", new JoinGameHandler(gameService))
                .delete("/db", new ClearHandler(clearService))
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });
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
