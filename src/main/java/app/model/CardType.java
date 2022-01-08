package app.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public enum CardType {
    @JsonAlias("Goblin")
    GOBLIN,

    @JsonAlias("Elf")
    ELF,

    @JsonAlias("Kraken")
    KRAKEN,

    @JsonAlias("Wizard")
    WIZARD,

    @JsonAlias("Dragon")
    DRAGON,

    @JsonAlias("Knight")
    KNIGHT,

    @JsonAlias("Ork")
    ORK,

    @JsonAlias("Troll")
    TROLL,

    @JsonAlias("Spell")
    SPELL
}