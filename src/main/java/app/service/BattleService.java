package app.service;

import app.model.Battle;
import app.model.BattleRound;
import app.model.Card;
import app.model.User;
import database.DatabaseService;
import utils.BattleHTML;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class BattleService {
    private static BattleService instance;
    private final UserService userService;
    private final CardService cardService;

    private BattleService() {
        userService = UserService.getInstance();
        cardService = CardService.getInstance();

    }

    public static synchronized BattleService getInstance() {
        if (BattleService.instance == null) {
            BattleService.instance = new BattleService();
        }
        return BattleService.instance;
    }

    public String requestBattle(int userID){
        int tmpID = 0;
        int tmpPlayerAID = 0;
        int tmpPlayerBID = 0;
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT id, player_a, player_b FROM battles WHERE player_a IS NULL OR player_b IS NULL LIMIT 1
                """ )
        ) {
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) { //open battle exists
                tmpID = resultSet.getInt(1);
                tmpPlayerAID = resultSet.getInt( 2 );
                tmpPlayerBID = resultSet.getInt( 3 );
            }else{ //create new one
                this.createBattle();
                requestBattle(userID); //now we try again
            }

            //now we have a battle, but we have to check if players are missing
            if(tmpPlayerAID == 0){
                this.addPlayerAToBattle(tmpID, userID);
                return "Player added to Queue, waiting for opponent...";
            }else if(tmpPlayerBID == 0){
                this.addPlayerBToBattle(tmpID, userID);
                return startBattle(tmpID, tmpPlayerAID, userID);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Internal Server Error";
    }

    private void createBattle() {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement
                ("INSERT INTO battles VALUES(DEFAULT)")
        ) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPlayerAToBattle(int battleID, int userID) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE battles SET player_a = ? WHERE id = ?
                 """)
        ) {
            stmt.setInt( 1, userID );
            stmt.setInt( 2, battleID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPlayerBToBattle(int battleID, int userID) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE battles SET player_b = ? WHERE id = ?
                 """)
        ) {
            stmt.setInt( 1, userID );
            stmt.setInt( 2, battleID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfUserAlreadyInQueue(int id){
        //check If user can battle, if userID is already in an
        //open battle slot, return false.
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT COUNT(id) FROM battles WHERE (player_a=? OR player_b=?) AND finished=false 
                 """)
        ) {
            stmt.setInt( 1, id );
            stmt.setInt( 2, id );
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                int tmp = resultSet.getInt(1);
                return tmp > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String startBattle(int battleID, int playerAID, int playerBID) {
        //here the actual battle takes place
        StringBuilder BattleLogHTML = new StringBuilder(); //my HTML BattleLog

        Battle battle = new Battle(battleID, false,
                this.userService.getUserByID(playerAID),
                this.userService.getUserByID(playerBID),
                null);

        BattleLogHTML.append(BattleHTML.head(battle));

        System.out.println("----------" + battle.getPlayerA().getUsername() + " VS " + battle.getPlayerB().getUsername() + "----------\s");
        ArrayList<Card> deckA = this.cardService.getDeckForUser(battle.getPlayerA().getId());
        ArrayList<Card> deckB = this.cardService.getDeckForUser(battle.getPlayerB().getId());

        Card cardA;
        Card cardB;
        Card winnerCard;

        while(!battle.isFinished() && battle.getBattlesRounds().size() < 100){
            //did a player win already?
            if(deckA.size() == 0){
                battle.setWinningPlayer(battle.getPlayerA());
                battle.setFinished(true);
            }else if(deckB.size() == 0){
                battle.setWinningPlayer(battle.getPlayerB());
                battle.setFinished(true);
            }else{
                cardA = deckA.get(new Random().nextInt(deckA.size()));
                cardB = deckB.get(new Random().nextInt(deckB.size()));
                winnerCard = null;
                int newRoundNumber = battle.getBattlesRounds().size() + 1;
                System.out.println("ROUND " + newRoundNumber + ":\s");
                if (cardA.defeats(cardB) || cardA.calculateEffectiveDamage(cardB) > cardB.calculateEffectiveDamage(cardA)) {
                    // Player A wins this round, and gets cardB
                    winnerCard = cardA;
                    deckB.remove(cardB);
                    deckA.add(cardB);
                } else if (cardB.defeats(cardA) || cardB.calculateEffectiveDamage(cardA) > cardA.calculateEffectiveDamage(cardB)) {
                    // Player B wins this round, and gets cardA
                    winnerCard = cardB;
                    deckA.remove(cardA);
                    deckB.add(cardA);
                }

                System.out.println("Deck A: " + deckA.size() + "\s CardA Dmg: " + cardA.calculateEffectiveDamage(cardB) + "\s");
                System.out.println("Deck B: " + deckB.size() + "\s CardB Dmg: " + cardB.calculateEffectiveDamage(cardA) + "\s");

                if (winnerCard != null) {
                    System.out.println("Winner: " + winnerCard.getName() + "  (" + winnerCard.getDamage() + " dmg)");
                }else{
                    //draw
                    System.out.println("It's a Draw.\s");
                }
                BattleLogHTML.append(BattleHTML.row(newRoundNumber, cardA, cardB,winnerCard));
                battle.addRound(new BattleRound(battle.getBattlesRounds().size()+1, cardA, cardB, winnerCard));
            }
        }

        //determining Winner
        if(battle.getWinningPlayer() == null){ //not a clear winner, both users have cards left
            if(deckA.size() == deckB.size()) { //it's a draw, both users have the same amount of cards left

                System.out.println("----------   BATTLE DRAW!   ----------\s");
                this.setDraw(battle);
                this.userService.updateStats(battle.getPlayerA(), 0);
                this.userService.updateStats(battle.getPlayerB(), 0);

            }else if(deckA.size() > deckB.size()){

                battle.setWinningPlayer(battle.getPlayerA());
            }else{
                battle.setWinningPlayer(battle.getPlayerB());
            }
        } //set Stats
        BattleLogHTML.append(BattleHTML.resultRow(battle));
        BattleLogHTML.append(BattleHTML.end());

        if(battle.getWinningPlayer() != null){
            this.setWinnerForBattle(battle.getWinningPlayer(), battle);
            this.userService.updateStats(battle.getWinningPlayer(), 1);
            System.out.println("----------   " + battle.getWinningPlayer().getUsername() + " WINS!   ----------\s");

            if(battle.getPlayerA().equals(battle.getWinningPlayer())){
                //Player A won
                this.userService.updateStats(battle.getPlayerB(), -1);

            }else if(battle.getPlayerB().equals(battle.getWinningPlayer())){
                //Player B won
                this.userService.updateStats(battle.getPlayerA(), -1);
            }
        }

        //reset decks
        this.cardService.resetDeck(battle.getPlayerA().getId());
        this.cardService.resetDeck(battle.getPlayerB().getId());

        return BattleLogHTML.toString();
    }

    private boolean setWinnerForBattle(User winner, Battle battle) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement
                ("UPDATE battles SET winner = ?, finished = TRUE WHERE id = ?")
        ) {
            stmt.setInt(2, battle.getId());

            if (winner != null) {  //update winner
                stmt.setInt(1, winner.getId());
            } else {    //draw
                stmt.setNull(1, java.sql.Types.NULL);
            }
            stmt.execute();
            return true; //successfully set
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean setDraw(Battle battle) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement
                ("UPDATE battles SET finished = TRUE WHERE id = ?")
        ) {
            stmt.setInt(1, battle.getId());
            stmt.execute();
            return true; //successfully set
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}