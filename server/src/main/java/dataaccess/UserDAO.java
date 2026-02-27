package dataaccess;

import dataaccess.model.UserData;

public interface UserDAO {
     UserData getUser(String username);
}
