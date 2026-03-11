package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseException;
import model.UserData;

public interface UserDAO {
     UserData getUser(String username) throws DatabaseException;
     void createUser(UserData user) throws DatabaseException;
     void deleteAllUsers() throws DatabaseException;
}
