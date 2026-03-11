package service;

import dataaccess.memoryDAOs.MemoryAuthDAO;
import dataaccess.memoryDAOs.MemoryGameDAO;
import dataaccess.memoryDAOs.MemoryUserDAO;
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
        try {
            clearService.clear(new ClearRequest());
        } catch (Exception ex) {
            fail();
        }
        assertTrue(memoryGameDAO.getAllGames().isEmpty());
    }
}