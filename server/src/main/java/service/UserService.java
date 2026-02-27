package service;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.UserDAO;
import dataaccess.model.UserData;
import server.requests.*;
import server.results.*;

public class UserService {

    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException {
        // Check to see if username is already taken
        if (dataAccess.getUser(registerRequest.username()) != null){
            throw new AlreadyTakenException("Username already taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

    // 1. Verify the input
    // 1.5 Validate the passed in authToken
    // 2. Check to make sure the requested username isn't already taken
    // 3. Create a new User model object: User u = new User (...)
    // 4. Insert new User into the database by calling UserDao.createUser (u)
    // 5. Login the user (create a new AuthToken model object, insert it into the database)
    // 6. Create a RegisterResult and return

    }

//  public LoginResult login(LoginRequest loginRequest) {}
//  public void logout(LogoutRequest logoutRequest) {}




//    private void validateUsername(int id) throws DataAccessException {
//        if (id <= 0) {
//            throw new DataAccessException(ResponseException.Code.ClientError, "Error: invalid pet ID");
//        }
//    }
}
