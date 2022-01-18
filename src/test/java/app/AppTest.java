package app;

import http.ContentType;
import http.HttpStatus;
import http.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Request;
import server.Response;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    App testApp = new App();
    Request testRequest = new Request();

    @Test
    @DisplayName("Tests if correct Response is given when \"localhost:port\" is called")
    void checkIfHomepageResponds() {
        testRequest.setMethod(Method.GET);
        testRequest.setPathname("/");
        testRequest.setParams("/");
        testRequest.setAuthorization(null);
        testRequest.setContentType(null);
        testRequest.setContentLength(null);
        testRequest.setBody("");

        Response testResponse = testApp.handleRequest(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Bad Request. (or the main page, welcome, in that case :)\" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());
    }

    @Test
    @DisplayName("Trading isn't implemented yet, but shouldn't let the program crash")
    void checkTradingResponse() {
        testRequest.setMethod(Method.GET);
        testRequest.setPathname("/tradings");
        testRequest.setParams("/");
        testRequest.setAuthorization(null);
        testRequest.setContentType(null);
        testRequest.setContentLength(null);
        testRequest.setBody("");

        Response testResponse = testApp.handleRequest(testRequest);
        Response expectedResponse = new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Not implemented yet.\" }"
        );
        assertEquals(testResponse.toString(), expectedResponse.toString());
    }
}