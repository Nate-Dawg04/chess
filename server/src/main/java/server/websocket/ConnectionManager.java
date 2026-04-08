package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>());
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(Session excludeSession, int gameID, ServerMessage serverMessage) throws IOException {
        Gson gson = new Gson();
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(gson.toJson(serverMessage));
                }
            }
        }
    }

    public void notifyRootUser(Session session, ServerMessage serverMessage) throws IOException{
        Gson gson = new Gson();
        if (session.isOpen()){
            session.getRemote().sendString(gson.toJson(serverMessage));
        }
    }
}
