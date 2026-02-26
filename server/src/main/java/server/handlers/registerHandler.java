package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import server.requests.RegisterRequest;
import server.results.RegisterResult;
import service.UserService;

public class registerHandler implements Handler {
    public void handle(Context context){
        String jsonResponse = context.body();
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(jsonResponse, RegisterRequest.class);
        RegisterResult registerResult = UserService.register(registerRequest);
        context.result(gson.toJson(registerResult));

    }
}
