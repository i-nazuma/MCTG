package app.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public enum ElementType {
    @JsonAlias("Regular")
    REGULAR,

    @JsonAlias("Fire")
    FIRE,

    @JsonAlias("Water")
    WATER
}
