package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
     UserData getUser(String username) throws DataAccessException;
     void createUser(UserData user) throws DataAccessException;
     void deleteAllUsers() throws DataAccessException;
}
