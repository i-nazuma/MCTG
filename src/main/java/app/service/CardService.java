package app.service;

import app.model.Card;
import database.DatabaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CardService {
    private static CardService instance;
    private CardService(){}

    public static synchronized CardService getInstance() {
        if (CardService.instance == null) {
            CardService.instance = new CardService();
        }
        return CardService.instance;
    }
    private int getHighestPackageNumber() { // only not sold ones
        try ( PreparedStatement statement = DatabaseService.getInstance().prepareStatement("""
                SELECT MAX(id)
                 FROM packages WHERE is_sold = false
                 """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() ) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getLowestPackageNumber() { // only not sold ones
        try ( PreparedStatement statement = DatabaseService.getInstance().prepareStatement("""
                SELECT MIN(id)
                 FROM packages WHERE is_sold = false
                 """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() ) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean createPackage(ArrayList<Card> cardList){
        this.createEmptyPackage();
        int newPackageNumber = this.getHighestPackageNumber();
        //assigning the empty package with cards
        for(Card card : cardList){

            try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                INSERT INTO cards (name, damage, element_type, card_type, package_id, token)
                 VALUES (?,?,?,?,?,?);
                """ )
            ) {
                stmt.setString( 1, card.getName());
                stmt.setFloat( 2, card.getDamage());
                //ordinal: Regular = 0, Fire = 1, Water = 2
                stmt.setInt( 3, card.getElementType().ordinal());
                //ordinal: Goblin = 0, Elf = 1, Kraken = 2, ...
                stmt.setInt( 4, card.getCardType().ordinal());
                stmt.setInt( 5, newPackageNumber);
                stmt.setString( 6, card.getToken());
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void createEmptyPackage() {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                INSERT INTO packages DEFAULT VALUES
                """ )
        ) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void subtractFiveCoins(int userID){
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE users SET coins = coins-5 WHERE id = ?
                """ )
        ) {
            stmt.setInt( 1, userID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean assignPackages(int userID){
        int oldestPackage = this.getLowestPackageNumber();
        if(oldestPackage == 0){
            return false; //all packages sold out
        }
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE cards SET user_id = ? WHERE package_id = ?
                """ )
        ) {
            stmt.setInt( 1, userID);
            stmt.setInt( 2, oldestPackage);
            stmt.execute();
            //cards were assigned successfully, now we can subtract the coins and set the package to sold
            this.subtractFiveCoins(userID);
            this.setPackageToSold(oldestPackage);

            return true; //package successfully assigned to user
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Error in assignPackages");
        return false;
    }

    private void setPackageToSold(int oldestPackage){
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE packages SET is_sold = true WHERE id = ?
                """ )
        ) {
            stmt.setInt( 1, oldestPackage);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Card> getAllCardsForUser(int user_id){

        if(!this.checkIfUserHasCards(user_id)){
            return null;
        }
        ArrayList<Card> allCards = new ArrayList<>();
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SElECT token, name, damage FROM cards WHERE user_id=?
                """ )
        ) {
            stmt.setInt(1, user_id);
            ResultSet resultSet = stmt.executeQuery();
            while( resultSet.next() ) {
                allCards.add(new Card(
                        resultSet.getString(1),
                        resultSet.getString( 2 ),
                        resultSet.getFloat( 3 )

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allCards;
    }

    public boolean checkIfCardBelongsToUser(String id, int user_id){
        int tmpID = 0;
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SElECT user_id FROM cards
                 WHERE token=?""" )
        ) {
            stmt.setString(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                tmpID = resultSet.getInt(1);
                return tmpID == user_id;
            }else { //card doesn't exist
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkIfCardIsAlreadyInDeck(String id){
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SElECT in_deck FROM cards
                 WHERE token = ?""" )
        ) {
            stmt.setString(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean insertCardInDeck(String id){
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE cards SET in_deck = true WHERE token = ?""" )
        ) {
            stmt.setString(1, id);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkIfUserHasCards(int user_id) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT COUNT(*) FROM cards WHERE user_id = ?
                 """)
        ) {
            stmt.setInt( 1, user_id);
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

    public boolean checkIfUserHasCardsInDeck(int user_id) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT COUNT(*) FROM cards WHERE user_id = ? AND in_deck = true
                 """)
        ) {
            stmt.setInt( 1, user_id);
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

    public ArrayList<Card> getDeckForUser(int user_id){

        if(!this.checkIfUserHasCardsInDeck(user_id)){
            return null;
        }

        ArrayList<Card> allCardsInDeck = new ArrayList<>();
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SElECT token, name, damage FROM cards
                 WHERE user_id=? AND in_deck=true""" )
        ) {
            stmt.setInt(1, user_id);
            ResultSet resultSet = stmt.executeQuery();

            while( resultSet.next() ) {
                allCardsInDeck.add(new Card(
                        resultSet.getString(1),
                        resultSet.getString( 2 ),
                        resultSet.getFloat( 3 )
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allCardsInDeck;
    }

}
