package dataaccess.databaseDAOs;

import chess.ChessGame;
import dataaccess.DatabaseManager;
import dataaccess.exceptions.DatabaseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {
    private SQLAuthDAO sqlAuthDAO;
    private SQLUserDAO sqlUserDAO;
    private SQLGameDAO sqlGameDAO;
    private int gameID;
    private int gameID2;
    private int gameID3;

    @BeforeAll
    public static void createDatabase() throws DatabaseException {
        DatabaseManager.createDatabase();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        sqlAuthDAO = new SQLAuthDAO();
        sqlUserDAO = new SQLUserDAO();
        sqlGameDAO = new SQLGameDAO();
        fillUpDatabase();
    }

    @AfterEach
    void tearDown() throws DatabaseException {
        sqlAuthDAO.deleteAllAuthData();
        sqlUserDAO.deleteAllUsers();
        sqlGameDAO.deleteAllGameData();
    }

    @Test
    void getAllGamesPositive() throws DatabaseException{
        // Should be 3 games in the database (from the fillUpDatabase method)
        assertEquals(3,sqlGameDAO.getAllGames().size());
    }

//    @Test
//    void getAllGamesNegative() throws DatabaseException {
//
//    }

    @Test
    void createGamePositive() throws DatabaseException{
        // Can successfully create a new game in the database
        int gameID = sqlGameDAO.createGame("New Game");
        assertEquals("New Game",sqlGameDAO.getGameData(gameID).gameName());
    }

    @Test
    void createGameNegative() {
        // Throws an error when the name for a game isn't passed in
        assertThrows(DatabaseException.class, () -> sqlGameDAO.createGame(null));
    }

    @Test
    void getGameDataPositive() throws DatabaseException{
        // Can get the gameData for multiple different games
        assertNotEquals(sqlGameDAO.getGameData(gameID),sqlGameDAO.getGameData(gameID2));
    }

    @Test
    void getGameDataNegative() throws DatabaseException{
        // Returns null for an invalid gameID
        assertNull(sqlGameDAO.getGameData(1000));
    }

    @Test
    void replaceGamePositive() throws DatabaseException{
        // Replacing one of the games with "Replacement Game" is successful
        sqlGameDAO.replaceGame(gameID,new GameData(3,null,null,
                "Replacement Game",new ChessGame()));
        assertEquals("Replacement Game",sqlGameDAO.getGameData(gameID).gameName());
    }

    @Test
    void replaceGameNegative() throws DatabaseException{
        // Trying to replace a game with a gameID that doesn't exist has no effect
        sqlGameDAO.replaceGame(500,new GameData(3,null,null,
                "Replacement Game",new ChessGame()));
        assertEquals("Game1",sqlGameDAO.getGameData(gameID).gameName());
        assertEquals("Game2",sqlGameDAO.getGameData(gameID2).gameName());
        assertEquals("Game3",sqlGameDAO.getGameData(gameID3).gameName());
    }

    @Test
    void deleteAllGameDataPositive() throws DatabaseException{
        // Trying to get a game after all have been deleted returns null
        sqlGameDAO.deleteAllGameData();
        assertNull(sqlGameDAO.getGameData(1));
    }

    private void fillUpDatabase() throws DatabaseException{
        sqlUserDAO.createUser(new UserData("Test1", "password1","email1"));
        sqlUserDAO.createUser(new UserData("Test2", "password2","email2"));
        sqlUserDAO.createUser(new UserData("Test3", "password3","email3"));
        sqlAuthDAO.createAuth(new AuthData(Service.generateAuthString(),"Test1"));
        sqlAuthDAO.createAuth(new AuthData(Service.generateAuthString(),"Test2"));
        sqlAuthDAO.createAuth(new AuthData(Service.generateAuthString(),"Test3"));
        gameID = sqlGameDAO.createGame("Game1");
        gameID2 = sqlGameDAO.createGame("Game2");
        gameID3 = sqlGameDAO.createGame("Game3");
    }
}