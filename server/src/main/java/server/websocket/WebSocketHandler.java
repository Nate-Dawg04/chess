package server.websocket;

import com.google.gson.Gson;
import dataaccess.exceptions.UnauthorizedException;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        // How do I create a connection here?
        //add a session to the connection manager?
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
            UserGameCommand command = gson.fromJson(wsMessageContext.message(), UserGameCommand.class);

            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());

            // Add session?
            connections.add(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
//                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
//                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
//                case RESIGN → resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            connections.broadcast(session, gameId, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: unauthorized"));
        } catch (Exception ex) {
            connections.broadcast(session, gameId, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + ex.getMessage()));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        // How do I close the websocket connection?
        System.out.println("Websocket closed");
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