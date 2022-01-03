package app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

public class Controller {
    @Getter
    private ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
    }
}
