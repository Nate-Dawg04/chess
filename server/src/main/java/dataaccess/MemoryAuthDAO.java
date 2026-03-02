package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, ArrayList<String>> allAuthData = new HashMap<>();

    public void createAuth(AuthData authData) {
        if (allAuthData.get(authData.username()) != null){
            allAuthData.get(authData.username()).add(authData.authToken());
        } else {
            ArrayList<String> newAuthDataList = new ArrayList<>(1);
            newAuthDataList.add(authData.authToken());
            allAuthData.put(authData.username(), newAuthDataList);
        }
    }

    public void deleteAllAuthData(){
        allAuthData.clear();
    }
}
