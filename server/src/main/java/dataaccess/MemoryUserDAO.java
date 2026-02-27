package dataaccess;

import dataaccess.model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username){
        return users.get(username);
    }



}
