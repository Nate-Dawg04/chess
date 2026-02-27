package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, String> allAuthData = new HashMap<>();

    public void createAuth(AuthData authData) {
        allAuthData.put(authData.authToken(), authData.username());
    }
}
