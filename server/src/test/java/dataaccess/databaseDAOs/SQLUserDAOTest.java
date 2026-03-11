package dataaccess.databaseDAOs;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    private static SQLUserDAO sqlUserDAO;

    @BeforeAll
    public static void createDatabase() {
        try {
            DatabaseManager.createDatabase();
            sqlUserDAO = new SQLUserDAO();
        } catch (Exception ex) {
            System.out.println("Error creating the database for the tests");
        }

    }

//    @BeforeEach
//    void setUp(){
//
//    }

    @AfterEach
    void tearDown() throws DataAccessException {

    }

    @Test
    void getUserPositive() {
    }

    @Test
    void getUserNegative() {
    }

    @Test
    void createUserPositive() {
    }

    @Test
    void createUserNegative() {
    }

    @Test
    void deleteAllUsersPositive() {
    }
}