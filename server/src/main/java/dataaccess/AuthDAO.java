package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;
    void deleteAllAuthData() throws DataAccessException;
    String getAuth(String authToken) throws UnauthorizedException, DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
