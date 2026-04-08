package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    // Lowkey no idea if this should be a string or what...
    private final String game;

    public LoadGameMessage(ServerMessageType type, String game) {
        super(type);
        this.game = game;
    }

    // Change from string??
    public String getGame(){
        return this.game;
    }
}
