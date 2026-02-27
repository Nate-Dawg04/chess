package server;

import dataaccess.MemoryUserDAO;
import io.javalin.*;
import server.handlers.*;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserService service = new UserService(new MemoryUserDAO());
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new registerHandler(service));

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
