package service;

import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedException;
import model.GameData;
import server.requests.CreateGameRequest;
import server.requests.JoinGameRequest;
import server.requests.ListGamesRequest;
import server.results.CreateGameResult;
import server.results.JoinGameResult;
import server.results.ListGamesResult;

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
        try {
            authDAO.getAuth(joinGameRequest.authToken());
        } catch (UnauthorizedException ex){
            throw new UnauthorizedException(ex.getMessage());
        }
        if (joinGameRequest.playerColor() == null
                || joinGameRequest.gameID() == 0
                || (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK"))
        ) {
            throw new BadRequestException("bad request");
        }
        GameData gameData = gameDAO.getGameData(joinGameRequest.gameID());
        // Get the players username
        String username = authDAO.getAuth(joinGameRequest.authToken());
        GameData newGameData;
        if (joinGameRequest.playerColor().equals("WHITE")){
            if (gameData.whiteUsername() != null){
                throw new AlreadyTakenException("already taken");
            }
            newGameData = new GameData(gameData.gameID(),username, gameData.blackUsername(),
                    gameData.gameName(),gameData.game());
        } else {
            if (gameData.blackUsername() != null){
                throw new AlreadyTakenException("already taken");
            }
            newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username,
                    gameData.gameName(),gameData.game());
        }
        gameDAO.replaceGame(gameData.gameID(),newGameData);
        return new JoinGameResult();
    }

}
