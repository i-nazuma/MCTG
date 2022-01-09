package app.service;

import app.model.User;
import app.model.UserProfile;
import database.DatabaseService;

import java.sql.*;
import java.util.ArrayList;

public class UserService {

    public UserService() {

    }

    // GET /user/:id
    public User getUserByUsername(String username) {

        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT id, username, token, coins, total_battles, won_battles, lost_battles, elo
                 FROM users WHERE username=?
                 """)
        ) {
            stmt.setString( 1, username);
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                return new User(
                        resultSet.getInt(1),
                        resultSet.getString( 2 ),
                        resultSet.getString( 3 ),
                        resultSet.getInt( 4 ),
                        resultSet.getInt( 5 ),
                        resultSet.getInt( 6 ),
                        resultSet.getInt( 7 ),
                        resultSet.getInt( 8 )

                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // POST /user
    public void addUser(User user) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                INSERT INTO users (username, password, token) VALUES (?,?,?);
                """ )
        ) {
            stmt.setString(1, user.getUsername());
            stmt.setString( 2, user.getPassword());
            stmt.setString( 3, "Basic " + user.getUsername() + "-mtcgToken");

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfUserExists(String username) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT COUNT(username) FROM users WHERE username=?
                 """)
        ) {
            stmt.setString( 1, username );
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

    public boolean checkIfLoggedIn(String username) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT status
                 FROM users WHERE username=?
                 """)
        ) {
            stmt.setString( 1, username );
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                return (resultSet.getInt(1) == 1); //logged in = true, not logged in = false
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkCredentials(String username, String password) {
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT password
                 FROM users WHERE username=?
                 """)
        ) {
            stmt.setString( 1, username );
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                boolean passwordCorrect = resultSet.getString(1).equals(password);
                return passwordCorrect;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void loginUser(String username) {
            try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE users SET status=1 WHERE username=?
                """ )
            ) {
                stmt.setString(1, username);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public User getUserByToken(String token){
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SElECT id, username, coins, total_battles, won_battles, lost_battles, elo FROM users WHERE token=?
                """ )
        ) {
            stmt.setString(1, token);
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                    return new User(
                            resultSet.getInt(1),
                            resultSet.getString( 2 ),
                            token,
                            resultSet.getInt( 3 ),
                            resultSet.getInt( 4 ),
                            resultSet.getInt( 5 ),
                            resultSet.getInt( 6 ),
                            resultSet.getInt( 7 )
                    );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<User> getUsersRankedByElo() {
        ArrayList<User> allUsers = new ArrayList<>();
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SElECT id, username, token, coins, total_battles, won_battles, lost_battles, elo FROM users
                ORDER BY elo, username ASC""" )
        ) {
            ResultSet resultSet = stmt.executeQuery();
            while( resultSet.next() ) {
                allUsers.add(new User(
                        resultSet.getInt(1),
                        resultSet.getString( 2 ),
                        resultSet.getString( 3 ),
                        resultSet.getInt( 4 ),
                        resultSet.getInt( 5 ),
                        resultSet.getInt( 6 ),
                        resultSet.getInt( 7 ),
                        resultSet.getInt( 8 )
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allUsers;
    }

    public void updateUser(String username, String name, String bio, String image){
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                UPDATE users SET name=?, bio=?, image=? WHERE username=?
                """ )
        ) {stmt.setString(1, name);
            stmt.setString( 2, bio);
            stmt.setString( 3, image);
            stmt.setString( 4, username);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserProfile getUserProfile(int userID){
        try ( PreparedStatement stmt = DatabaseService.getInstance().prepareStatement("""
                SELECT name, bio, image
                 FROM users WHERE id=?
                 """)
        ) {
            stmt.setInt( 1, userID);
            ResultSet resultSet = stmt.executeQuery();
            if( resultSet.next() ) {
                return new UserProfile(
                        resultSet.getString(1),
                        resultSet.getString( 2 ),
                        resultSet.getString( 3 )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
