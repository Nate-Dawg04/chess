package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.DataAccessException;
import server.requests.ClearRequest;
import server.results.ClearResult;

public class ClearService extends Service{
    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }

    public ClearResult clear(ClearRequest clearRequest) throws DataAccessException {
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuthData();
        gameDAO.deleteAllGameData();
        return new ClearResult();
    }
}
