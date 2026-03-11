package service;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedException;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.requests.*;
import server.results.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerPositive() {
        UserService userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO(),new MemoryGameDAO());
        RegisterRequest registerRequest = new RegisterRequest("Nathan","Nathan","Nathan");
        try {
            assertEquals(RegisterResult.class,
                    userService.register(registerRequest).getClass());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void registerNegative() {
        UserService userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO(),new MemoryGameDAO());
        RegisterRequest registerRequest = new RegisterRequest("","","");
        assertThrows(BadRequestException.class,() -> userService.register(registerRequest));
    }

    @Test
    void loginPositive() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO,new MemoryGameDAO());
        UserData user = new UserData("Nathan", BCrypt.hashpw("Right Password", BCrypt.gensalt()),"Nathan");
        memoryUserDAO.createUser(user);
        memoryAuthDAO.createAuth(new AuthData(userService.generateAuthString(), user.username()));
        LoginRequest loginRequest = new LoginRequest("Nathan","Right Password");
        try {
            assertEquals(LoginResult.class,userService.login(loginRequest).getClass());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void loginNegative() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserService userService = new UserService(memoryUserDAO,new MemoryAuthDAO(),new MemoryGameDAO());
        UserData user = new UserData("Nathan",BCrypt.hashpw("Nathan", BCrypt.gensalt()),"Nathan");
        memoryUserDAO.createUser(user);
        LoginRequest loginRequest = new LoginRequest("Nathan","Wrong Password");
        assertThrows(UnauthorizedException.class,() -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void logoutPositive() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(memoryUserDAO,memoryAuthDAO,new MemoryGameDAO());
        UserData user = new UserData("Nathan","Right Password","Nathan");
        memoryUserDAO.createUser(user);
        String authToken = userService.generateAuthString();
        memoryAuthDAO.createAuth(new AuthData(authToken, user.username()));
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        try {
            assertEquals(LogoutResult.class,userService.logout(logoutRequest).getClass());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void logoutNegative() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserService userService = new UserService(memoryUserDAO,new MemoryAuthDAO(),new MemoryGameDAO());
        UserData user = new UserData("Nathan","Nathan","Nathan");
        memoryUserDAO.createUser(user);
        LogoutRequest logoutRequest = new LogoutRequest(userService.generateAuthString());
        assertThrows(UnauthorizedException.class,() -> {
            userService.logout(logoutRequest);
        });
    }
}