package ui;

import exception.ResponseException;
import server.ServerFacade;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }


}
