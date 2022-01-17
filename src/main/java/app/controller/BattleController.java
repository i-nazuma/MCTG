package app.controller;

import app.model.Card;
import app.model.User;
import app.service.BattleService;
import app.service.CardService;
import app.service.UserService;
import http.ContentType;
import http.HttpStatus;
import server.Request;
import server.Response;

import java.util.ArrayList;

public class BattleController extends Controller{
    private BattleService battleService;
    private CardService cardService;
    private UserService userService;

    public BattleController(BattleService battleService, CardService cardService, UserService userService) {
        super();
        this.battleService = battleService;
        this.cardService = cardService;
        this.userService = userService;
    }

    //POST /battles
    public Response battle(Request request) {
        ArrayList<Card> deck =  new ArrayList<>();
        String username = request.getAuthorization().split(" ")[1].split("-")[0];
        if(this.userService.checkIfUserExists(username)) {
            User user = this.userService.getUserByUsername(username);
            deck = this.cardService.getDeckForUser(user.getId());
            if(deck == null || deck.size() != 4){
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ message: \"You need 4 cards in your deck to battle! Configure your deck and try again.\" }"
                );
            }
            if (!this.battleService.checkIfUserAlreadyInQueue(user.getId())){
                return new Response(
                        HttpStatus.OK,
                        ContentType.HTML,
                        this.battleService.requestBattle(user.getId())); //sends back Log written in HTML;
            }else{
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ message: \"You cannot battle against yourself!.\" }"
                );
            }
        }
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Error: User does not exist, please create an account first.\" }"
            );
    }
}
