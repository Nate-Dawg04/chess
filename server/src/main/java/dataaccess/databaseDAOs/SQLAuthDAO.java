package dataaccess.databaseDAOs;

import dataaccess.AuthDAO;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public void deleteAllAuthData() {

    }

    @Override
    public String getAuth(String authToken) throws UnauthorizedException {
        return "";
    }

    @Override
    public void deleteAuth(String authToken) {

    }
}
