package server.requests;

public record CreateGameRequest(String authToken, String gameName) {
}
