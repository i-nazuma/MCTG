import app.App;
import app.controller.Controller;
import app.model.Card;
import app.service.CardService;
import com.fasterxml.jackson.core.JsonProcessingException;


public class TestingMain extends Controller {

    public TestingMain() {
        CardService cardService = CardService.getInstance();
    }
    public void createCard(String requestBody){
        Card card = null;
        try{
            System.out.println(requestBody);
            card = this.getObjectMapper().readValue(requestBody, Card.class);
            String cardDataJSON = this.getObjectMapper().writeValueAsString(card);
            System.out.println(cardDataJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String token = ("Basic altenhof-mtcgToken");
        String username = token.split(" ")[1].split("-")[0];
        System.out.println(username);
    }
}
