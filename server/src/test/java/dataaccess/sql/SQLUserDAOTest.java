package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DatabaseException;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    private SQLUserDAO sqlUserDAO;

    @BeforeAll
    public static void createDatabase() throws DatabaseException {
        DatabaseManager.createDatabase();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        sqlUserDAO = new SQLUserDAO();
    }

    @AfterEach
    void tearDown() throws DatabaseException {
        sqlUserDAO.deleteAllUsers();
    }

    @Test
    void getUserPositive() throws DatabaseException {
        // Tests getting a couple different users
            UserData testUser = new UserData("Nathan","password","email");
            sqlUserDAO.createUser(testUser);
            UserData testUser2 = new UserData("John","other password","email2");
            sqlUserDAO.createUser(testUser2);
            assertEquals(testUser.username(),sqlUserDAO.getUser("Nathan").username());
            assertEquals(testUser.email(),sqlUserDAO.getUser("Nathan").email());
            assertEquals(testUser2.username(),sqlUserDAO.getUser("John").username());
            assertEquals(testUser2.email(),sqlUserDAO.getUser("John").email());
    }

    @Test
    void getUserNegative() throws DatabaseException {
        // Tests that trying to get a user that doesn't exist returns null
        assertNull(sqlUserDAO.getUser(""));
    }

    @Test
    void createUserPositive() throws DatabaseException {
        //Tests that creating a user inserts the user into the database
        UserData testUser = new UserData("Test", "Super secret password", "email");
        sqlUserDAO.createUser(testUser);
        assertEquals(testUser.username(),sqlUserDAO.getUser("Test").username());
        assertEquals(testUser.email(),sqlUserDAO.getUser("Test").email());
    }

    @Test
    void createUserNegative() throws DatabaseException {
        // Tests if trying to create a user which already exists throws a DatabaseException
        UserData testUser = new UserData("Test", "Super secret password", "email");
        UserData testUser2 = new UserData("Test", "Super secret password", "email");
        sqlUserDAO.createUser(testUser);
        assertThrows(DatabaseException.class, () -> sqlUserDAO.createUser(testUser2));
    }

    @Test
    void deleteAllUsersPositive() throws DatabaseException {
        // Tests that the clear method works properly
        sqlUserDAO.createUser(new UserData("Test1", "Test1","Test1"));
        sqlUserDAO.createUser(new UserData("Test2", "Test2","Test2"));
        sqlUserDAO.createUser(new UserData("Test3", "Test3","Test3"));
        sqlUserDAO.deleteAllUsers();
        assertNull(sqlUserDAO.getUser("Test3"));
    }
}