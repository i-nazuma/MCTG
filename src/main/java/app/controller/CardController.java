package app.controller;

import app.model.Card;
import app.model.User;
import app.service.CardService;
import app.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import http.ContentType;
import http.HttpStatus;
import server.Request;
import server.Response;

import java.util.ArrayList;

public class CardController extends Controller{
    private CardService cardService;
    private UserService userService;

    public CardController(CardService cardService, UserService userService) {
        super();
        this.cardService = cardService;
        this.userService = userService;
    }


    // POST /packages (admin only)
    public Response createPackages(Request request) {
        ArrayList<Card> cardList = null;
        try {
            if(request.getAuthorization().equals("Basic admin-mtcgToken")) {
                // request.getBody() => "{ \"id\": 4, \"name\": \"kienboec\", ... }
                cardList = this.getObjectMapper().readValue(request.getBody(), new TypeReference<ArrayList<Card>>() {
                });
                if (this.cardService.createPackage(cardList)) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ message: \"Package saved successfully!\" }"
                    );
                } else {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ message: \"Error: could not upload Package." +
                                    " Try again and maybe check the containing Cards.\" }"
                    );
                }
            }else{
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ message: \"Error: Only admins can create Packages.\" }"
                );
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    // POST /transactions/packages
    public Response acquirePackages(Request request) {
        User user = null;
        String username = request.getAuthorization().split(" ")[1].split("-")[0];
        if(this.userService.checkIfUserExists(username)) {

            user = this.userService.getUserByToken(request.getAuthorization());

            if(user.getCoins() < 5) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ message: \"You need 5 coins to buy a package. Your current balance is " + user.getCoins() + " coins.\" }"
                );
            }else{
                if (this.cardService.assignPackages(user.getId())) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ message: \"Package acquired successfully! Your current balance is " + user.getCoins() + " coins.\" }"
                    );
                } else {
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.JSON,
                            "{ \"message\" : \"All packages are sold out, sorry about that. Try again later.\" }"
                    );
                }
            }

        }else{
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Error: User does not exist, please create an account first.\" }"
            );
        }
    }

    public Response showCards(Request request) {
        return null;
    }

    public Response showDeck(Request request) {
        return null;
    }

    public Response configureDeck(Request request) {
        return null;
    }
}
