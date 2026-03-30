package server;

import com.google.gson.Gson;
import exception.ResponseException;
import results.*;
import requests.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException{
        var request = buildRequest("POST","/user",registerRequest,null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException{
        var request = buildRequest("POST","/session",loginRequest,null);
        var response = sendRequest(request);
        return handleResponse(response,LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws ResponseException{
        var request = buildRequest("DELETE","/session",null, logoutRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response,LogoutResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ResponseException{
        var request = buildRequest("GET","/game",null, listGamesRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response,ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException{
        CreateGameRequestBody createGameRequestBody = new CreateGameRequestBody(createGameRequest.gameName());
        var request = buildRequest("POST","/game",createGameRequestBody, createGameRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response,CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws ResponseException{
        JoinGameRequestBody joinGameRequestBody =
                new JoinGameRequestBody(joinGameRequest.playerColor(),joinGameRequest.gameID());
        var request = buildRequest("PUT","/game",joinGameRequestBody, joinGameRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, JoinGameResult.class);
    }

    public ClearResult clear(ClearRequest clearRequest) throws ResponseException{
        var request = buildRequest("DELETE","/db",null,null);
        var response = sendRequest(request);
        return handleResponse(response,ClearResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
