package app.model;

import lombok.Getter;

public class Battle {
    @Getter
    int id;
    @Getter
    Card cardA;
    @Getter
    Card cardB;
    @Getter
    Card winnerCard;
}
