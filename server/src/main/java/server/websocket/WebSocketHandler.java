package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
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
import model.GameData;
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
        Session session = wsMessageContext.session;
        try {
            Gson gson = new Gson();

            // First make it a makeMoveCommand, so that the "move" field is accessible
            // If not, the "move" field should be null, and can cast it later into a UserGameCommand

            MakeMoveCommand command = gson.fromJson(wsMessageContext.message(), MakeMoveCommand.class);

            int gameId = command.getGameID();
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
                case CONNECT -> connect(session, username, (UserGameCommand) command);
                case MAKE_MOVE -> makeMove(session, username, command);
//                case LEAVE -> leaveGame(session, username, (UserGameCommand) command);
//                case RESIGN → resign(session, username, (UserGameCommand) command);
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

    private void makeMove(Session session, String username, MakeMoveCommand makeMoveCommand) throws IOException, DatabaseException {
//        Server verifies the validity of the move.
//        Game is updated to represent the move. Game is updated in the database.
//        Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
//        Server sends a Notification message to all other clients in that game informing them what move was made.
//        If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.

//        Gson gson = new Gson();
        GameData thisGameData = gameDAO.getGameData(makeMoveCommand.getGameID());
        ChessGame chessGame = thisGameData.game();
        try {
            // Make the move on the ChessGame (which checks it's validity, potentially throws InvalidMoveException)
            chessGame.makeMove(makeMoveCommand.getMove());

            // Create a new GameData object that's a copy of the original, except the ChessGame has been updated with the move
            GameData newGameData = new GameData(thisGameData.gameID(), thisGameData.whiteUsername(),
                    thisGameData.blackUsername(), thisGameData.gameName(), chessGame);

            // Update the game in the database
            gameDAO.replaceGame(makeMoveCommand.getGameID(), newGameData);

            // Send a LOAD_GAME message to ALL clients in the game with an updated game
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,chessGame);
            connections.broadcast(null, makeMoveCommand.getGameID(), loadGameMessage);

            // Send a Notification message to all other clients informing them what move was made
            var message = String.format("%s moved from %s to %s",
                    username,makeMoveCommand.getMove().getStartPosition(),makeMoveCommand.getMove().getEndPosition());
            NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(session, makeMoveCommand.getGameID(), notificationMessage);

            // Send a Notification message to ALL clients if the move results in check, checkmate, or stalemate
            String gameStateMessage = null;

            // Check if either user is in check
            if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)){
                gameStateMessage = String.format("%s is now in check!",thisGameData.whiteUsername());
            } else if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
                gameStateMessage = String.format("%s is now in check!",thisGameData.blackUsername());
            }

            // Check if either user is in checkmate
            if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)){
                gameStateMessage = String.format("%s is now in checkmate! Game over!",thisGameData.whiteUsername());
            }
            if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)){
                gameStateMessage = String.format("%s is now in checkmate! Game over!",thisGameData.blackUsername());
            }

            // Checks if either user is in stalemate
            if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE)){
                gameStateMessage = String.format("%s is now in stalemate! Game over!",thisGameData.whiteUsername());
            }
            if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK)){
                gameStateMessage = String.format("%s is now in stalemate! Game over!",thisGameData.blackUsername());
            }

            if (gameStateMessage != null){
                // Create the notification message to send out to all users (if any of the above conditions applied)
                NotificationMessage notificationMessage1 =
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,gameStateMessage);
                connections.broadcast(null, makeMoveCommand.getGameID(), notificationMessage1);
            }

        } catch (InvalidMoveException ex) {
            connections.notifyRootUser(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + ex.getMessage()));
        }

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