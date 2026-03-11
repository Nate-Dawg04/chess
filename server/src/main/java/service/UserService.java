package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import server.requests.*;
import server.results.*;

public class UserService extends Service {

    public UserService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO, authDAO, gameDAO);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException, DataAccessException {
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

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
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

    public LogoutResult logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException{
        try {
            authDAO.getAuth(logoutRequest.authToken());
        } catch (UnauthorizedException ex){
            throw new UnauthorizedException(ex.getMessage());
        }
        authDAO.deleteAuth(logoutRequest.authToken());
        return new LogoutResult();
    }
}
