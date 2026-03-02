package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedException;
import server.requests.CreateGameRequest;
import server.requests.ListGamesRequest;
import server.results.CreateGameResult;
import server.results.ListGamesResult;

import java.util.ArrayList;

public class GameService extends Service{
    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException {
        try {
            authDAO.getAuth(listGamesRequest.authToken());
        } catch (UnauthorizedException ex){
            throw new UnauthorizedException(ex.getMessage());
        }
        return new ListGamesResult(gameDAO.getAllGames());
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException, BadRequestException {
        if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()){
            throw new BadRequestException("bad request");
        }
        try {
            authDAO.getAuth(createGameRequest.authToken());
        } catch (UnauthorizedException ex){
            throw new UnauthorizedException(ex.getMessage());
        }
        int gameID = gameDAO.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws BadRequestException,UnauthorizedException, AlreadyTakenException {

    }

}
