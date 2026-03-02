package dataaccess;

import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, String> allAuthData = new HashMap<>();

    public void createAuth(AuthData authData) {
        allAuthData.put(authData.authToken(), authData.username());
    }

    public void getAuth(String authToken) throws UnauthorizedException {
        if (allAuthData.get(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }
    }

    public void deleteAllAuthData(){
        allAuthData.clear();
    }

    public void deleteAuth(String authToken){
        allAuthData.remove(authToken);
    }
}
