package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import requests.ClearRequest;
import results.ClearResult;
import service.ClearService;

public class ClearHandler implements Handler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Gson gson = new Gson();
        ClearRequest clearRequest = gson.fromJson(context.body(), ClearRequest.class);
        ClearResult clearResult = clearService.clear((clearRequest));
        context.status(200);
        context.json(gson.toJson(clearResult));
    }
}
