package server;

import http.ContentType;
import http.HttpStatus;
import lombok.ToString;

@ToString
public class Response {
    private int status;
    private String message;
    private String contentType;
    private String content;

    public Response(HttpStatus httpStatus, ContentType contentType, String content) {
        this.status = httpStatus.code;
        this.message = httpStatus.message;
        this.contentType = contentType.type;
        this.content = content;
    }

    protected String get() {
        return "HTTP/1.1 " + this.status + " " + this.message + "\r\n" +
                "Content-Type: " + this.contentType + "\r\n" +
                "Content-Length: " + this.content.length() + "\r\n" +
                "\r\n" +
                this.content;
    }
}