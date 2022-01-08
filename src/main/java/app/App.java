package app;

import app.controller.CardController;
import app.controller.UserController;
import app.service.CardService;
import app.service.UserService;
import http.ContentType;
import http.HttpStatus;
import http.Method;
import server.Request;
import server.Response;
import server.ServerApp;

public class App implements ServerApp {
    private final UserController userController;
    private final CardController cardController;

    public App() {
        this.userController = new UserController(new UserService());
        this.cardController = new CardController(new CardService(), new UserService());
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getPathname().contains("/users") && request.getMethod() == Method.GET) {
            return this.userController.getUserByUsername(request);

        } else if (request.getPathname().equals("/users") && request.getMethod() == Method.POST) {
            return this.userController.register(request);

        } else if(request.getPathname().equals("/sessions") && request.getMethod() == Method.POST){
            return this.userController.login(request);

        } else if(request.getPathname().equals("/packages") && request.getMethod() == Method.POST){
            return this.cardController.createPackages(request);

        } else if(request.getPathname().equals("/transactions/packages") && request.getMethod() == Method.POST){
            return this.cardController.acquirePackages(request);

        } else if(request.getPathname().equals("/cards") && request.getMethod() == Method.GET){
            return this.cardController.showCards(request);

        } else if(request.getPathname().equals("/deck") && request.getMethod() == Method.GET){
            return this.cardController.showDeck(request);

        } else if(request.getPathname().equals("/deck") && request.getMethod() == Method.PUT){
            return this.cardController.configureDeck(request);

        } else if(request.getPathname().equals("/stats") && request.getMethod() == Method.GET){
            return this.userController.showStats(request);

        } else if(request.getPathname().equals("/score") && request.getMethod() == Method.GET){
            return this.userController.showScoreBoard(request);

        } else if(request.getPathname().equals("/battles") && request.getMethod() == Method.POST) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"Not implemented yet.\" }"
            );
        } else if(request.getPathname().equals("/tradings") && request.getMethod() == Method.GET) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"Not implemented yet.\" }"
            );
        } else if(request.getPathname().equals("/tradings") && request.getMethod() == Method.POST) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"Not implemented yet.\" }"
            );
        }

        return new Response(
            HttpStatus.BAD_REQUEST,
            ContentType.JSON,
            "[]"
        );
    }
}
