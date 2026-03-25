package service;

import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import requests.ClearRequest;

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