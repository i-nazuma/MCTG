package app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Battle {
    @Setter
    @Getter
    private int id;
    @Setter
    @Getter
    private boolean isFinished;
    @Setter
    @Getter
    private User playerA;
    @Setter
    @Getter
    private User playerB;
    @Setter
    @Getter
    private User winningPlayer;
    @Setter
    @Getter
    private  ArrayList<BattleRound> battlesRounds;

    public Battle(int id, boolean isFinished, User playerA, User playerB, User winningPlayer) {
        this.id = id;
        this.isFinished = isFinished;
        this.playerA = playerA;
        this.playerB = playerB;
        this.winningPlayer = winningPlayer;
        this.battlesRounds = new ArrayList<>();
    }

    public void addRound(BattleRound battleRound) {
        this.battlesRounds.add(battleRound);
    }
}
