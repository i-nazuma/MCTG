package app.model;

import lombok.Getter;

public class BattleRound {
    @Getter
    private int id;
    @Getter
    private Card cardA;
    @Getter
    private Card cardB;
    @Getter
    private Card winnerCard;

    public BattleRound(int id, Card cardA, Card cardB, Card winnerCard) {
        this.id = id;
        this.cardA = cardA;
        this.cardB = cardB;
        this.winnerCard = winnerCard;
    }
}