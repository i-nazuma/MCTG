package app.controller;

import app.model.Card;
import app.model.User;
import app.model.UserProfile;
import app.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import server.Request;
import server.Response;

import java.util.ArrayList;
import java.util.List;

public class UserController extends Controller {
    private UserService userService;

    public UserController(UserService userService) {
        super();
        this.userService = userService;
    }

    // GET /user
    public Response getUser(Request request) {

        String username;

        if(request.getAuthorization() != null) {
            username = request.getAuthorization().split(" ")[1].split("-")[0];
        }else{
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ message: \"Authorization Token is missing! \" }"
            );
        }
        if(!request.getPathname().equals("/users/" + username)){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"add your own username to the end of the URL to get your user data! Example: users/janedoe \" }"
            );
        }

        if (this.userService.checkIfUserExists(username)){
            User user = null;
            UserProfile additionalData = null;

                user = this.userService.getUserByUsername(username);
                additionalData = this.userService.getUserProfile(user.getId());

                if(user.getToken().equals(request.getAuthorization())){
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ Username: " + username + ": Name: \"" + additionalData.getName() + "\" Bio: \"" + additionalData.getBio() + "\" Image: \"" + additionalData.getImage() + "\""
                    );
                }else return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ message: \"You are not allowed to view other users data!\" }"
                );
        }else{
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"User not found\" }"
            );
        }
    }

    // POST /user
    public Response register(Request request) {
        User user = null;
        try {
            // request.getBody() => "{ \"id\": 4, \"name\": \"kienboec\", ... }
            user = this.getObjectMapper().readValue(request.getBody(), User.class);
            if(this.userService.checkIfUserExists(user.getUsername())) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ message: \"username \"" + user.getUsername() + "\" already taken. Try again!\" }"
                );
            }else{
                this.userService.addUser(user);
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ message: \"Successfully registered!\" }"
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

    //POST /sessions
    public Response login(Request request) {
        User user = null;
        try {
            user = this.getObjectMapper().readValue(request.getBody(), User.class);
            if(!this.userService.checkIfUserExists(user.getUsername())){
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"User does not exist yet!\" }"
                );
            } else if(this.userService.checkIfLoggedIn(user.getUsername())) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"" + user.getUsername() + " is logged in already!\" }"
                );
            }

            boolean correctCredentials = this.userService.checkCredentials( user.getUsername(),user.getPassword() );

            if(correctCredentials){
                this.userService.loginUser(user.getUsername());
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"User logged in successfully!\" }"
                );
            }else{
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"Wrong credentials entered, Login failed.\" }"
                );
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public Response showScoreBoard(Request request) {
        ArrayList<User> allUsers = this.userService.getUsersRankedByElo();
        StringBuilder html = new StringBuilder(
                "<table><tr>" +
                "<th>Username</th>" +
                "<th>Total Battles</th>" +
                "<th>Won/Lost/Draw</th>" +
                "<th>ELO</th>" +
                "</tr>");
        for(User u : allUsers){
            String row = "<tr><td>"+ u.getUsername() +"</td><td>"+ u.getTotal_battles()
                    +"</td><td>"+ u.getWon_battles() +"/"+ u.getLost_battles() +"/"+ u.getDraw_battles() +"</td><td>" + u.getElo() +
                    "</td></tr>";
            html.append(row);
        }
        html.append("</table>");
        return new Response(
                HttpStatus.OK,
                ContentType.HTML,
                html.toString()
        );
    }

    public Response showStats(Request request) {
        User user = null;
        String username = request.getAuthorization().split(" ")[1].split("-")[0];
        if(this.userService.checkIfUserExists(username)){
            if(this.userService.checkIfLoggedIn(username)){
                user = this.userService.getUserByUsername(username);
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"Here are the battle statistics for " + user.getUsername() + ": " +
                                "\" \"totalbattles\" : \""+ user.getTotal_battles() +"\" \"wonbattles\" : \""+ user.getWon_battles() +
                                "\" \"lostbattles\" : \""+ user.getLost_battles() +"\" \"draws\" : \""+ user.getDraw_battles() + "\" }"
                );
            }else{
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"Log in first to view your stats!\" }"
                );
            }
        }
        return null;
    }

    public Response editUser(Request request) {

        String username;

        if(request.getAuthorization() != null) {
            username = request.getAuthorization().split(" ")[1].split("-")[0];
        }else{
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ message: \"Authorization Token is missing! \" }"
            );
        }
        if(!request.getPathname().equals("/users/" + username)){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"add your own username to the end of the URL to edit your user data! Example: users/janedoe \" }"
            );
        }

        if (this.userService.checkIfUserExists(username)){
            User user = null;
            UserProfile userProfile = null;
            try {
                user = this.userService.getUserByUsername(username);
                if(user.getToken().equals(request.getAuthorization())){
                    userProfile = this.getObjectMapper().readValue(request.getBody(), UserProfile.class);
                    this.userService.updateUser(username, userProfile.getName(), userProfile.getBio(), userProfile.getImage());
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ message: \"User edited successfully!\" }"
                    );
                }else{
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"You are not allowed to edit other users data!\" }"
                    );
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"message\" : \"Internal Server Error\" }"
                );
            }
        }else{
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"User not found\" }"
            );
        }
    }
}
