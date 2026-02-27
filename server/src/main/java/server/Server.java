package server;

import dataaccess.*;
import io.javalin.*;
import server.handlers.*;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserService userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO(), new MemoryGameDAO());

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new registerHandler(userService));

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
