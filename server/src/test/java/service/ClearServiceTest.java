package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.requests.ClearRequest;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    @Test
    void clearPositive() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        ClearService clearService = new ClearService(memoryUserDAO,memoryAuthDAO,memoryGameDAO);
        memoryUserDAO.createUser(new UserData("Nathan","Nathan","Nathan"));
        memoryAuthDAO.createAuth(new AuthData(clearService.generateAuthString(), "Nathan"));
        memoryGameDAO.createGame("Fun Game");
        clearService.clear(new ClearRequest());
        assertTrue(memoryGameDAO.getAllGames().isEmpty());
    }
}