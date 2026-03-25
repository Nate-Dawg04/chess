package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;

public class UserService extends Service {

    public UserService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException, DatabaseException {
        // Validate the input here (none of it null), throw BadRequestException if something is wrong
        if (registerRequest.username() == null
            || registerRequest.password() == null
            || registerRequest.email() == null
            || registerRequest.username().isEmpty()
            || registerRequest.password().isEmpty()
            || registerRequest.email().isEmpty()
        ){
            throw new BadRequestException("bad request");
        }
        if (userDAO.getUser(registerRequest.username()) != null){
            throw new AlreadyTakenException("already taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        userDAO.createUser(user);
        AuthData authData = new AuthData(generateAuthString(),user.username());
        authDAO.createAuth(authData);

        return new RegisterResult(user.username(),authData.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DatabaseException {
        if (loginRequest.username() == null
            || loginRequest.username().isEmpty()
            || loginRequest.password() == null
            || loginRequest.password().isEmpty()
        ){
            throw new BadRequestException("bad request");
        }

        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null
            || !BCrypt.checkpw(loginRequest.password(), userData.password())
        ){
            throw new UnauthorizedException("unauthorized");
        }

        AuthData authData = new AuthData(generateAuthString(),userData.username());
        authDAO.createAuth(authData);
        return new LoginResult(userData.username(),authData.authToken());
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws UnauthorizedException, DatabaseException {
        try {
            authDAO.getAuth(logoutRequest.authToken());
        } catch (UnauthorizedException ex){
            throw new UnauthorizedException(ex.getMessage());
        }
        authDAO.deleteAuth(logoutRequest.authToken());
        return new LogoutResult();
    }
}
