package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import io.javalin.*;
import io.javalin.http.Context;
import server.handlers.*;
import service.ClearService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO, memoryGameDAO);
        ClearService clearService = new ClearService(memoryUserDAO,memoryAuthDAO, memoryGameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .get("/error", this::throwException)
                .exception(Exception.class, this::exceptionHandler)
                .post("/user", new registerHandler(userService))
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
        }
        context.json(body);
    }

}
