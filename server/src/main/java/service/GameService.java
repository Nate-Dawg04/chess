package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.UnauthorizedException;
import server.requests.ListGamesRequest;
import server.results.ListGamesResult;

import java.util.ArrayList;

public class GameService extends Service{
    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException {
        //Make sure authToken is valid
        //Return an arrayList of all the Games in a ListGamesResult object
            //The arrayList is an object in the ListGamesResult record
        try {
            authDAO.getAuth(listGamesRequest.authToken());
        } catch (UnauthorizedException ex){
            throw new UnauthorizedException(ex.getMessage());
        }
        return new ListGamesResult(gameDAO.getAllGames());
    }

}
