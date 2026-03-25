package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.DatabaseException;
import requests.ClearRequest;
import results.ClearResult;

public class ClearService extends Service{
    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }

    public ClearResult clear(ClearRequest clearRequest) throws DatabaseException {
        try {
            authDAO.deleteAllAuthData();
            gameDAO.deleteAllGameData();
            userDAO.deleteAllUsers();
            return new ClearResult();
        } catch (Exception ex){
            throw new DatabaseException(String.format("Unable to read data: %s%n", ex.getMessage()));
        }
    }
}
