package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    // Lowkey no idea if this should be a string or what...
    private final ChessGame game;

    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    // Change from string??
    public ChessGame getGame(){
        return this.game;
    }
}
