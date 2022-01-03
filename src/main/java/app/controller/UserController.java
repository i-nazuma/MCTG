package app.controller;

import app.model.User;
import app.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import server.Request;
import server.Response;

import java.util.List;

public class UserController extends Controller {
    private UserService userService;

    public UserController(UserService userService) {
        super();
        this.userService = userService;
    }

    // GET /user
    public Response getUser() {
        try {
            List userData = this.userService.getUser();
            // "[ { \"id\": 1, \"city\": \"Vienna\", \"temperature\": 9.0 }, { ... }, { ... } ]"
            String userDataJSON = this.getObjectMapper().writeValueAsString(userData);

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                userDataJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // POST /user
    public Response addUser(Request request) {
        User user = null;
        try {

            // request.getBody() => "{ \"id\": 4, \"city\": \"Graz\", ... }
            user = this.getObjectMapper().readValue(request.getBody(), User.class);
            this.userService.addUser(user);

            return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ message: \"Success\" }"
            );
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
