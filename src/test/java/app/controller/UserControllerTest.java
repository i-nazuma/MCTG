package app.controller;

import http.ContentType;
import http.HttpStatus;
import http.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Request;
import server.Response;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController testUserController = new UserController(null);
    Request testRequest = new Request();


    @Test
    @DisplayName("Tests if getUser without an Authorization Header gets handled correctly")
    void getUserWithoutAuthorization() {
        testRequest.setMethod(Method.GET);
        testRequest.setPathname("/users");
        testRequest.setParams("/users");
        testRequest.setAuthorization(null);
        testRequest.setContentType("application/json");
        testRequest.setContentLength(0);
        testRequest.setBody("");

        Response testResponse = testUserController.getUser(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ message: \"Authorization Token is missing! \" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());
    }

    @Test
    @DisplayName("Tests if getUser with a wrong pathname gets handled correctly")
    void getUserWithBadPathname() {
        testRequest.setMethod(Method.GET);
        testRequest.setPathname("/users");
        testRequest.setParams("/users");
        testRequest.setAuthorization("Basic user-mtcgToken");
        testRequest.setContentType("application/json");
        testRequest.setContentLength(0);
        testRequest.setBody("");

        Response testResponse = testUserController.getUser(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ message: \"add your own username to the end of the URL to get your user data! Example: users/janedoe \" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());
    }

    @Test
    @DisplayName("Tests if editUser with no Authorization Header gets handled correctly")
    void editUserWithoutAuthorization() {
        testRequest.setMethod(Method.PUT);
        testRequest.setPathname("/users");
        testRequest.setParams("/users");
        testRequest.setAuthorization(null);
        testRequest.setContentType("application/json");
        testRequest.setContentLength(19);
        testRequest.setBody("{\"Name\": \"Testing\"}");

        Response testResponse = testUserController.getUser(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ message: \"Authorization Token is missing! \" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());

    }

    @Test
    @DisplayName("Tests if editUser with a wrong pathname gets handled correctly")
    void editUserWithBadPathname() {
        testRequest.setMethod(Method.PUT);
        testRequest.setPathname("/users");
        testRequest.setParams("/users");
        testRequest.setAuthorization("Basic user-mtcgToken");
        testRequest.setContentType("application/json");
        testRequest.setContentLength(0);
        testRequest.setBody("");

        Response testResponse = testUserController.getUser(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ message: \"add your own username to the end of the URL to get your user data! Example: users/janedoe \" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());
    }
}