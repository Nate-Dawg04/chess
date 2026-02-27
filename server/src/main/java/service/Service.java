package service;

import dataaccess.*;

import java.util.UUID;

public class Service {
    public final UserDAO userDAO;
    public final AuthDAO authDAO;
    public final GameDAO gameDAO;

    public Service(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public String generateAuthString(){
        return UUID.randomUUID().toString();
    }
}
