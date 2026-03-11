package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DatabaseException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private SQLAuthDAO sqlAuthDAO;
    private SQLUserDAO sqlUserDAO;

    @BeforeAll
    public static void createDatabase() throws DatabaseException {
        DatabaseManager.createDatabase();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        sqlAuthDAO = new SQLAuthDAO();
        sqlUserDAO = new SQLUserDAO();
    }

    @AfterEach
    void tearDown() throws DatabaseException {
        sqlAuthDAO.deleteAllAuthData();
        sqlUserDAO.deleteAllUsers();
    }

    @Test
    void createAuthPositive() throws DatabaseException, UnauthorizedException {
        // Can create an Auth for a user
        sqlUserDAO.createUser(new UserData("Test","pw","e"));
        String authToken = Service.generateAuthString();
        AuthData testAuthData = new AuthData(authToken,"Test");
        sqlAuthDAO.createAuth(testAuthData);
        assertEquals("Test",sqlAuthDAO.getAuth(authToken));
    }

    @Test
    void createAuthNegative() throws DatabaseException{
        // Attempting to create a duplicate auth throws an exception
        sqlUserDAO.createUser(new UserData("Duplicate","pw","e"));
        String authToken = Service.generateAuthString();
        AuthData testAuthData = new AuthData(authToken,"Duplicate");
        sqlAuthDAO.createAuth(testAuthData);
        assertThrows(DatabaseException.class, () -> sqlAuthDAO.createAuth(testAuthData));

    }

    @Test
    void getAuthPositive() throws DatabaseException, UnauthorizedException{
        // Can get multiple different auths

        sqlUserDAO.createUser(new UserData("Duplicate","pw","e"));
        //First Auth
        String authToken = Service.generateAuthString();
        sqlAuthDAO.createAuth(new AuthData(authToken,"Duplicate"));
        //Second Auth
        String authToken2 = Service.generateAuthString();
        sqlAuthDAO.createAuth(new AuthData(authToken2,"Duplicate"));

        //Get multiple Auths
        assertEquals(sqlAuthDAO.getAuth(authToken),sqlAuthDAO.getAuth(authToken2));
    }

    @Test
    void getAuthNegative() throws DatabaseException{
        // Attempting to get an invalid auth throws an exception
        sqlUserDAO.createUser(new UserData("Test","pw","e"));
        String authToken = Service.generateAuthString();
        sqlAuthDAO.createAuth(new AuthData(authToken,"Test"));

        assertThrows(UnauthorizedException.class,() -> sqlAuthDAO.getAuth(Service.generateAuthString()));
    }

    @Test
    void deleteAuthPositive() throws DatabaseException{
        // Deleting an auth removes it from the database, so attempting to get the auth
            //throws an exception
        sqlUserDAO.createUser(new UserData("Test","pw","e"));
        String authToken = Service.generateAuthString();
        sqlAuthDAO.createAuth(new AuthData(authToken,"Test"));
        sqlAuthDAO.deleteAuth(authToken);
        assertThrows(UnauthorizedException.class,() -> sqlAuthDAO.getAuth(authToken));
    }

    @Test
    void deleteAuthNegative() throws DatabaseException, UnauthorizedException{
        // Attempting to delete an auth that doesn't exist has no effect
        sqlUserDAO.createUser(new UserData("Test","pw","e"));
        String authToken = Service.generateAuthString();
        sqlAuthDAO.createAuth(new AuthData(authToken,"Test"));
        sqlAuthDAO.deleteAuth("");
        assertEquals("Test",sqlAuthDAO.getAuth(authToken));
    }

    @Test
    void deleteAllAuthDataPositive() throws DatabaseException {
        // Deleting all the auth data means getAuth returns null
        sqlUserDAO.createUser(new UserData("Test","password","email"));
        //First Auth
        String authToken = Service.generateAuthString();
        sqlAuthDAO.createAuth(new AuthData(authToken,"Test"));
        //Second Auth
        String authToken2 = Service.generateAuthString();
        sqlAuthDAO.createAuth(new AuthData(authToken2,"Test"));

        sqlAuthDAO.deleteAllAuthData();

        assertThrows(UnauthorizedException.class, () -> sqlAuthDAO.getAuth(authToken));
    }

}