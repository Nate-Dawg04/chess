package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DatabaseException;
    void deleteAllAuthData() throws DatabaseException;
    String getAuth(String authToken) throws UnauthorizedException, DatabaseException;
    void deleteAuth(String authToken) throws DatabaseException;
}
