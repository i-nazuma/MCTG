package app.service;

import app.model.Card;
import app.model.User;
import database.DatabaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CardService {
    public CardService(){

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
        ArrayList<Card> allCards = new ArrayList<>();
        return allCards;
    }

    public Card getCardByID(String ID){

        return null;
    }

    public ArrayList<Card> getDeckForUser(int user_id){
        ArrayList<Card> allCards = new ArrayList<>();
        return null;
    }

}
