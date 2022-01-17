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
        ArrayList<Card> cardList = new ArrayList<>();
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
        ArrayList<Card> cardList = new ArrayList<>();
        StringBuilder allCardsJSON = new StringBuilder("{");
        if(request.getAuthorization() == null){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"No Authorization token given.\" }"
            );
        }
        String username = request.getAuthorization().split(" ")[1].split("-")[0];
        if(this.userService.checkIfUserExists(username)) {
            User user = this.userService.getUserByUsername(username);
            cardList = this.cardService.getAllCardsForUser(user.getId());
            if(cardList == null){
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ message: \"You have no cards! Acquire a package and try again.\" }"
                );
            }
            return getJSONResponse(cardList, allCardsJSON);
        }else{
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Error: User does not exist, please create an account first.\" }"
            );
        }
    }

    public Response showDeck(Request request) {

        if(request.getParams().equals("format=plain")){
            return showDeckPlain(request);
        }

        ArrayList<Card> cardList =  new ArrayList<>();
        StringBuilder allCardsJSON = new StringBuilder("{");
        String username = request.getAuthorization().split(" ")[1].split("-")[0];
        if(this.userService.checkIfUserExists(username)) {
            User user = this.userService.getUserByUsername(username);
            cardList = this.cardService.getDeckForUser(user.getId());
            if(cardList == null){
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ message: \"You have no cards in your deck! Configure your deck and try again.\" }"
                );
            }
            return getJSONResponse(cardList, allCardsJSON);
        }else{
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Error: User does not exist, please create an account first.\" }"
            );
        }
    }

    private Response getJSONResponse(ArrayList<Card> cardList, StringBuilder allCardsJSON) {
        int i = 1;
        for (Card c : cardList) {
            String cardString = "                                                              " +
                "Card " + i + ": Name: \"" + c.getName() + "\" Damage: \"" + c.getDamage() + "\" Id: \"" + c.getToken() + "\"";
            i++;
            allCardsJSON.append(cardString);
        }
        allCardsJSON.append("}");
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                allCardsJSON.toString()
        );
    }

    public Response showDeckPlain(Request request) {
        ArrayList<Card> cardList =  new ArrayList<>();
        StringBuilder allCardsHTML = new StringBuilder(
                "<table><tr>" +
                        "<th>Card Number</th>" +
                        "<th>Card Name</th>" +
                        "<th>Damage</th>" +
                        "<th>ID</th>" +
                        "</tr>");
        String username = request.getAuthorization().split(" ")[1].split("-")[0];
        if(this.userService.checkIfUserExists(username)) {
            User user = this.userService.getUserByUsername(username);
            cardList = this.cardService.getDeckForUser(user.getId());
            if(cardList == null){
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ message: \"You have no cards in your deck! Configure your deck and try again.\" }"
                );
            }
            int i = 1;
            for (Card c : cardList) {
                String cardString = "<tr><td>" + i + "</td><td>" + c.getName() + "</td><td>" + c.getDamage() + "</td><td>" + c.getToken() + "</td></tr>";
                i++;
                allCardsHTML.append(cardString);
            }
            allCardsHTML.append("</table>");
            return new Response(
                    HttpStatus.OK,
                    ContentType.HTML,
                    allCardsHTML.toString()
            );
        }else{
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Error: User does not exist, please create an account first.\" }"
            );
        }
    }

    public Response configureDeck(Request request) {
        String username = request.getAuthorization().split(" ")[1].split("-")[0];
        ArrayList<String> cardIdList = new ArrayList<>();
        try{
            if(this.userService.checkIfUserExists(username)) {
                User user = this.userService.getUserByUsername(username);
                cardIdList = this.getObjectMapper().readValue(request.getBody(), new TypeReference<ArrayList<String>>(){});
                if(cardIdList.size() != 4){
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.JSON,
                            "{ message: \"Error: You need to assign exactly 4 Cards to your Deck.\" }"
                    );
                }else{
                    int i = 1;
                    for (String ID : cardIdList) {
                        if(this.cardService.checkIfCardBelongsToUser(ID, user.getId())){
                            if(this.cardService.checkIfCardIsAlreadyInDeck(ID)){
                                return new Response(
                                        HttpStatus.BAD_REQUEST,
                                        ContentType.JSON,
                                        "{ message: \"Error: Card " + i + " is already in your Deck.\" }"
                                );
                            }else{
                                if(!this.cardService.insertCardInDeck(ID)){
                                    return new Response(
                                            HttpStatus.INTERNAL_SERVER_ERROR,
                                            ContentType.JSON,
                                            "{ \"message\" : \"Internal Server Error\" }"
                                    );
                                }
                            }
                        }else{
                            return new Response(
                                    HttpStatus.BAD_REQUEST,
                                    ContentType.JSON,
                                    "{ message: \"Error: Incorrect Card ID at Card " + i + " (does not belong to you or does not exist).\" }"
                            );
                        }
                        i++;
                    }
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ message: \"Deck configured successfully!\" }"
                    );
                }
            }else{
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ message: \"Error: User does not exist, please create an account first.\" }"
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
}
