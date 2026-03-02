package dataaccess;

import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    void deleteAllAuthData();
    String getAuth(String authToken) throws UnauthorizedException;
    void deleteAuth(String authToken);
}
