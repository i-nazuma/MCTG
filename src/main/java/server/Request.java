package server;

import http.Method;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Request {
    private Method method;
    private String pathname;
    private String params;
    private String authorization;
    private String contentType;
    private Integer contentLength;
    private String body = "";
}
