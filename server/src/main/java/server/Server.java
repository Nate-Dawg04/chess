package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import server.handlers.*;
import service.Service;

public class Server {

    private final Javalin javalin;

    public Server() {
        Service service = new Service(new MemoryUserDAO(),new MemoryAuthDAO(), new MemoryGameDAO());
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
