package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DatabaseException;
import dataaccess.exceptions.UnauthorizedException;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext wsMessageContext) throws Exception {
        //when a message is received, send it to all the users in that game?
        int gameId = -1;
        Session session = wsMessageContext.session;
        try {
            Gson gson = new Gson();
            // Serialization logic is a bit more complicated
            // If it's a makeMoveCommand, it needs to be deserialized to get the makeMove information
            // How can you tell if it's specifically a makeMoveCommand?
            UserGameCommand command = gson.fromJson(wsMessageContext.message(), UserGameCommand.class);

            gameId = command.getGameID();
            String authToken = command.getAuthToken();

            String username = authDAO.getAuth(authToken);

            // Check for a valid authToken and a valid gameID
            if (username == null){
                throw new UnauthorizedException("unauthorized");
            }
            if (gameDAO.getGameData(gameId) == null){
                throw new BadRequestException("invalid gameId");
            }

            // Add session? What does saveSession mean?
//            connections.add(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
//                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
//                case LEAVE -> leaveGame(session, username, command);
//                case RESIGN → resign(session, username, command);
            }
       } catch (Exception ex) {
            connections.notifyRootUser(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + ex.getMessage()));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, UserGameCommand userGameCommand) throws IOException, DatabaseException {
        connections.add(userGameCommand.getGameID(), session);
        var message = String.format("%s has connected", username);
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcast(session,userGameCommand.getGameID(),notificationMessage);
        connections.notifyRootUser(session,new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,gameDAO.getGameData(userGameCommand.getGameID()).game()));
    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(session, notification);
//    }
//
//    private void exit(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }
}