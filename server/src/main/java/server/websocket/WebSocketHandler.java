package server.websocket;

import chess.ChessBoard;
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

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (UserGameCommand) command);
                case MAKE_MOVE -> makeMove(session, username, command);
                case LEAVE -> leaveGame(session, username, (UserGameCommand) command);
                case RESIGN -> resign(session, username, (UserGameCommand) command);
            }
       } catch (Exception ex) {
            connections.notifyRootUser(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + ex.getMessage()));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, UserGameCommand userGameCommand)
            throws IOException, DatabaseException {
        connections.add(userGameCommand.getGameID(), session);

        // This lowkey might not be the best way of doing it
        // Not sure if the gameDAO will already be updated with the correct information at this point...
        // In which case it will always display the observing message

        String message;
        if (username.equals(gameDAO.getGameData(userGameCommand.getGameID()).whiteUsername())){
            message = String.format("%s has joined the game as WHITE", username);
        } else if(username.equals(gameDAO.getGameData(userGameCommand.getGameID()).blackUsername())){
            message = String.format("%s has joined the game as BLACK", username);
        } else {
            message = String.format("%s is now observing the game", username);
        }

        // Different message if the user is joining as an observer
        NotificationMessage notificationMessage =
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcast(session,userGameCommand.getGameID(),notificationMessage);
        connections.notifyRootUser(session,new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                gameDAO.getGameData(userGameCommand.getGameID()).game()));
    }

    private void makeMove(Session session, String username, MakeMoveCommand makeMoveCommand)
            throws IOException, DatabaseException {

        GameData thisGameData = gameDAO.getGameData(makeMoveCommand.getGameID());
        ChessGame chessGame = thisGameData.game();
        ChessBoard board = chessGame.getBoard();
        try {
            // Cannot make a move on a game that is finished
            if (chessGame.getGameState()){
                throw new InvalidMoveException("Game is finished");
            }

            // Check if the user is attempting to make a move for the opponent somehow
            if (board.getPiece(makeMoveCommand.getMove().getStartPosition()).getTeamColor() != chessGame.getTeamTurn()){
                throw new InvalidMoveException("cannot move opponent's piece");
            }

            // Check if the user is attempting to move one of their opponent's pieces
            if (username.equals(thisGameData.whiteUsername())){
                if (board.getPiece(makeMoveCommand.getMove().getStartPosition()).getTeamColor() != ChessGame.TeamColor.WHITE){
                    throw new InvalidMoveException("Cannot move an opponent's piece");
                }
            } else if (username.equals(thisGameData.blackUsername())) {
                if (board.getPiece(makeMoveCommand.getMove().getStartPosition()).getTeamColor() != ChessGame.TeamColor.BLACK){
                    throw new InvalidMoveException("Cannot move an opponent's piece");
                }
            } else {
                // If they don't match either username, they're an observer and shouldn't be able to make a move
                throw new InvalidMoveException("Observer cannot move pieces");
            }

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

        } catch (Exception ex) {
            connections.notifyRootUser(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + ex.getMessage()));
        }

    }

    private void leaveGame(Session session, String username, UserGameCommand userGameCommand)
            throws DatabaseException, IOException{
        // Remove the root client from the game
        connections.remove(userGameCommand.getGameID(), session);

        // Update the game in the database
        // Remove the white or black username from the GameData, unless they're an observer
        GameData original = gameDAO.getGameData(userGameCommand.getGameID());
        GameData updated = new GameData(original.gameID(),
                original.whiteUsername(), original.blackUsername(), original.gameName(), original.game());
        if (username.equals(original.whiteUsername())){
            updated = new GameData(original.gameID(),
                    null, original.blackUsername(), original.gameName(), original.game());
        } else if (username.equals(original.blackUsername())) {
            updated = new GameData(original.gameID(),
                    original.whiteUsername(), null, original.gameName(), original.game());
        }

        gameDAO.replaceGame(userGameCommand.getGameID(), updated);

        var message = String.format("%s has left the game", username);
        NotificationMessage leaveMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcast(session, userGameCommand.getGameID(), leaveMessage);
    }

    private void resign(Session session, String username, UserGameCommand userGameCommand)
            throws IOException{
        try {
            GameData thisGameData = gameDAO.getGameData(userGameCommand.getGameID());

            // Check to make sure the user is one of the players
            if(!username.equals(thisGameData.whiteUsername()) && !username.equals(thisGameData.blackUsername())){
                throw new ResponseException(ResponseException.Code.ClientError,"Observer cannot resign from a game");
            }

            ChessGame chessGame = thisGameData.game();

            //Check to make sure the game isn't finished already
            if (chessGame.getGameState()){
                throw new ResponseException(ResponseException.Code.ClientError,"Game is already finished");
            }

            // Mark the game as finished
            chessGame.setGameState(true);

            // This might be unnecessary
            // Could maybe just pass in "thisGameData" to replaceGame and it will update
            GameData updated = new GameData(thisGameData.gameID(),
                    thisGameData.whiteUsername(), thisGameData.blackUsername(), thisGameData.gameName(),chessGame);
            gameDAO.replaceGame(userGameCommand.getGameID(), updated);

            String message = String.format("%s resigned from the game", username);
            NotificationMessage resignMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
            connections.broadcast(null, userGameCommand.getGameID(), resignMessage);
        } catch (Exception ex){
            connections.notifyRootUser(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + ex.getMessage()));
        }
    }
}