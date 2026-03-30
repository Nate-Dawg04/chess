package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.*;
import results.*;
import server.Server;
import server.ServerFacade;
import service.Service;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear(new ClearRequest());
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerPositive() throws Exception {
        var authData = facade.register(new RegisterRequest("player1",
                "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(ResponseException.class,
                () -> facade.register(new RegisterRequest("player1","pw","e")));
    }

    @Test
    void loginPositive() throws Exception{
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        var loginResult = facade.login(new LoginRequest("testPlayer","testPassword"));
        assertEquals("testPlayer", loginResult.username());
    }

    @Test
    void loginNegative() throws Exception{
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("wrong","wrong")));
    }

    @Test
    void logoutPositive() throws Exception{
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        String authToken = facade.login(new LoginRequest("testPlayer","testPassword")).authToken();
        assertEquals(new LogoutResult(), facade.logout(new LogoutRequest(authToken)));
    }

    @Test
    void logoutNegative() throws Exception {
        assertThrows(ResponseException.class, () -> facade.logout(new LogoutRequest(Service.generateAuthString())));
    }

    @Test
    void listGamesPositive() throws Exception {
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        String authToken = facade.login(new LoginRequest("testPlayer","testPassword")).authToken();
        facade.createGame(new CreateGameRequest(authToken,"Game1"));
        var listGamesResult = facade.listGames(new ListGamesRequest(authToken));
        assertFalse(listGamesResult.games().isEmpty());
    }

    @Test
    void listGamesNegative() throws Exception {
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        String authToken = facade.login(new LoginRequest("testPlayer","testPassword")).authToken();
        facade.createGame(new CreateGameRequest(authToken,"Game1"));
        assertThrows(ResponseException.class,
                () -> facade.listGames(new ListGamesRequest(Service.generateAuthString())));
    }

    @Test
    void createGamePositive() throws Exception {
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        String authToken = facade.login(new LoginRequest("testPlayer","testPassword")).authToken();
        var createGameResult = facade.createGame(new CreateGameRequest(authToken,"Game1"));
        assertTrue(createGameResult.gameID() > 0);
    }

    @Test
    void createGameNegative() throws Exception {
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        facade.login(new LoginRequest("testPlayer","testPassword"));
        assertThrows(ResponseException.class,
                () -> facade.createGame(new CreateGameRequest(Service.generateAuthString(),"Game1")));
    }

    @Test
    void joinGamePositive() throws Exception{
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        String authToken = facade.login(new LoginRequest("testPlayer","testPassword")).authToken();
        int gameID = facade.createGame(new CreateGameRequest(authToken,"JoinGame Game")).gameID();
        var joinGameResult = facade.joinGame(new JoinGameRequest(authToken,"WHITE",gameID));
        assertEquals(new JoinGameResult(),joinGameResult);
    }

    @Test
    void joinGameNegative() throws Exception{
        facade.register(new RegisterRequest("testPlayer","testPassword","testEmail"));
        String authToken = facade.login(new LoginRequest("testPlayer","testPassword")).authToken();
        facade.createGame(new CreateGameRequest(authToken,"JoinGame Game")).gameID();
        assertThrows(ResponseException.class, () -> facade.joinGame(new JoinGameRequest(authToken,"WHITE",10000)));
    }

}
