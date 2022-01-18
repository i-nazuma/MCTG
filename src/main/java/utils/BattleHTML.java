package utils;

import app.model.Battle;
import app.model.Card;
import lombok.Getter;
//this is my unique feature, it isn't the prettiest code, but it's a nice and simple frontend :D
public class BattleHTML {
    public static String head(Battle battle){
        StringBuilder string = new StringBuilder("<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>MCTG</title>" +
                "    <!-- Bootstrap CSS -->" +
                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">" +
                "    <style>" +
                "        .list-group, .center {" +
                "            margin: auto;" +
                "            width: 60%;" +
                "            padding-left: 50px;" +
                "            text-align: center;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "<div class=\"jumbotron\" class=\"center\">" +
                "    <h1 class=\"center\" class=\"display-4\">Battle</h1>" +
                "    <hr class=\"my-4\">" +
                "    <h4 class=\"center\">");
        string.append(battle.getPlayerA().getUsername()+" vs "+battle.getPlayerB().getUsername());
        string.append("</h4>" +
                "</div>" +
                "<div class=\"list-group\">" +
                "    <a href=\"#\" class=\"list-group-item list-group-item-primary\">Let the Battles begin!</a>");
        return string.toString();
    }

    public static String row(int roundNumber, Card cardA, Card cardB, Card winnerCard) {
        StringBuilder string = new StringBuilder("<a href=\"#\" class=\"list-group-item list-group-item-light\">--- ROUND " + roundNumber + " ---</a>\n<a href=\"#\" class=\"list-group-item list-group-item-action list-group-item-");
        if (winnerCard == null) { //round draw
            string.append("light\"><b>"+ cardA.getName() +"</b> with "+ cardA.getDamage() +" Damage against <b>"+ cardB.getName() +"</b> with "+ cardB.getDamage() +" Damage results in a Draw.</a>");

        } else if (winnerCard.equals(cardA)) { //round won
            string.append("success\"><b>"+ cardA.getName() +"</b> with "+ cardA.getDamage() +" Damage beats <b>"+ cardB.getName() +"</b> with "+ cardB.getDamage() +" Damage and wins the Round!</a>");

        } else if (winnerCard.equals(cardB)){ //round lost (for simplicity, this is all from User A's perspective)
            string.append("danger\"><b>"+ cardA.getName() +"</b> with "+ cardA.getDamage() +" Damage gets defeated by <b>"+ cardB.getName() +"</b> with "+ cardB.getDamage() +" Damage and loses the Round!</a>");
        }
        return string.toString();
    }

    public static String resultRow(Battle battle){
        if(battle.getWinningPlayer() != null){
            StringBuilder string = new StringBuilder("<a href=\"#\" class=\"list-group-item list-group-item-action list-group-item-warning\"><b>");
            string.append(battle.getWinningPlayer().getUsername());
            string.append(" won the Battle!</b></a> ");
            return string.toString();
        }else{ //draw
            return "<a href=\"#\" class=\"list-group-item list-group-item-action list-group-item-light\"><b>It's a Draw!</b></a>";
        }
    }

    public static String end(){
        return "</div></body></html>";

    }
}
