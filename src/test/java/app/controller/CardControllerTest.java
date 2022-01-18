package app.controller;

import http.ContentType;
import http.HttpStatus;
import http.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Request;
import server.Response;

import static org.junit.jupiter.api.Assertions.*;

class CardControllerTest {
    CardController testCardController = new CardController(null, null);
    Request testRequest = new Request();


    @Test
    @DisplayName("Tests that only admins can create Packages")
    void createPackages() {

        testRequest.setMethod(Method.POST);
        testRequest.setPathname("/packages");
        testRequest.setParams("/packages");
        testRequest.setAuthorization("Basic user-mtcgToken");
        testRequest.setContentType("application/json");
        testRequest.setContentLength(0);
        testRequest.setBody("");

        Response testResponse = testCardController.createPackages(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ message: \"Error: Only admins can create Packages.\" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());
    }

    @Test
    @DisplayName("Tests if correct Response is returned when given no Authorization Header")
    void showCards() {
        testRequest.setMethod(Method.GET);
        testRequest.setPathname("/cards");
        testRequest.setParams("/cards");
        testRequest.setAuthorization("");
        testRequest.setContentType("application/json");
        testRequest.setContentLength(0);
        testRequest.setBody("");

        Response testResponse = testCardController.createPackages(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ message: \"Error: Only admins can create Packages.\" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());
    }
}