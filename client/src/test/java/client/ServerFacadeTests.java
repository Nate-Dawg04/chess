package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.ClearRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import server.Server;
import server.ServerFacade;

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
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("player1","pw","e")));
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
    void logoutPositive(){

    }

    @Test
    void logoutNegative(){

    }

}
