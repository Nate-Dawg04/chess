package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import server.requests.RegisterRequest;

public class registerHandler implements Handler {
    public void handle(Context context){
        String jsonResponse = context.body();
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(jsonResponse, RegisterRequest.class);



//        Pet pet = new Gson().fromJson(ctx.body(), Pet.class);
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        ctx.result(new Gson().toJson(pet));
    }
}
