package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import exception.ResponseException;

import jakarta.websocket.*;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebSocketFacade(int port, ServerMessageObserver serverMessageObserver) throws ResponseException {
        try {
            String url = "ws://localhost:" + port;
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @OnMessage
                public void onMessage(String message){
                    try {
//                        System.out.println("REACHED THE MESSAGE HANDLER");
                        ServerMessage serverMessage = createMessageSerializer().fromJson(message, ServerMessage.class);
                        serverMessageObserver.notify(serverMessage);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            });
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String authToken,int gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws ResponseException {
        try {
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,authToken,gameId,move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leaveGame(String authToken,int gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void resign(String authToken,int gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public static Gson createMessageSerializer() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(ServerMessage.class,
                (JsonDeserializer<ServerMessage>) (el, type, ctx) -> {

                    JsonObject obj = el.getAsJsonObject();
                    String typeName = obj.get("serverMessageType").getAsString();

                    return switch (ServerMessage.ServerMessageType.valueOf(typeName)) {
                        case NOTIFICATION ->
                                ctx.deserialize(el, NotificationMessage.class);
                        case ERROR ->
                                ctx.deserialize(el, ErrorMessage.class);
                        case LOAD_GAME ->
                                ctx.deserialize(el, LoadGameMessage.class);
                    };
                });

        return builder.create();
    }

}