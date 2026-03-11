package service;

import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.requests.CreateGameRequest;
import server.requests.JoinGameRequest;
import server.requests.ListGamesRequest;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void listGamesPositive() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        GameService gameService = new GameService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        UserData user = new UserData("Nathan","Nathan","Nathan");
        memoryUserDAO.createUser(user);
        String authToken = userService.generateAuthString();
        memoryAuthDAO.createAuth(new AuthData(authToken, user.username()));
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        try {
            gameService.createGame(new CreateGameRequest(authToken,"Game1"));
            gameService.createGame(new CreateGameRequest(authToken,"Game2"));
            gameService.createGame(new CreateGameRequest(authToken,"Game3"));
            assertEquals(3, gameService.listGames(listGamesRequest).games().size());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void listGamesNegative() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        GameService gameService = new GameService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        UserData user = new UserData("Nathan","Right Password","Nathan");
        memoryUserDAO.createUser(user);
        String authToken = userService.generateAuthString();
        memoryAuthDAO.createAuth(new AuthData(authToken, user.username()));
        ListGamesRequest listGamesRequest = new ListGamesRequest(userService.generateAuthString());
        assertThrows(UnauthorizedException.class,() -> gameService.listGames(listGamesRequest));
    }

    @Test
    void createGamePositive() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        GameService gameService = new GameService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);

        UserData user = new UserData("Nathan","Nathan","Nathan");
        memoryUserDAO.createUser(user);
        String authToken = userService.generateAuthString();
        memoryAuthDAO.createAuth(new AuthData(authToken, user.username()));
        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken,"Fun Game");
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken,"Fun Game");
        try {
            assertEquals(1, gameService.createGame(createGameRequest1).gameID());
            assertEquals(2, gameService.createGame(createGameRequest2).gameID());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void createGameNegative() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        GameService gameService = new GameService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);

        CreateGameRequest createGameRequest = new CreateGameRequest(userService.generateAuthString(),"Fun Game");

        assertThrows(UnauthorizedException.class,() -> gameService.createGame(createGameRequest));

    }

    @Test
    void joinGamePositive() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        memoryGameDAO.createGame("Game1");
        memoryGameDAO.createGame("Game2");
        String authToken = gameService.generateAuthString();
        memoryAuthDAO.createAuth(new AuthData(authToken,"Nathan"));
        int gameID = memoryGameDAO.createGame("Test Game");
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken,"WHITE",gameID);
        try {
            gameService.joinGame(joinGameRequest);
            assertEquals("Nathan", memoryGameDAO.getGameData(gameID).whiteUsername());
        } catch (Exception ex) {
            fail();
        }

    }

    @Test
    void joinGameNegative() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        memoryGameDAO.createGame("Game1");
        memoryGameDAO.createGame("Game2");
        String authToken = gameService.generateAuthString();
        memoryAuthDAO.createAuth(new AuthData(authToken,"Nathan"));
        int gameID = memoryGameDAO.createGame("Test Game");
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken,"WHITE",gameID);

        //Try to join a game with a spot that is already full
        try {
            gameService.joinGame(joinGameRequest);
        } catch (Exception ex) {
            fail();
        }
        assertThrows(AlreadyTakenException.class,() -> gameService.joinGame(joinGameRequest));
    }
}