package app.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter
    @Setter
    @JsonAlias({"Username"})
    private String username;
    @Getter
    @Setter
    @JsonAlias({"Password"})
    private String password;
    @Getter
    private int id;
    @Getter
    private String token;
    @Getter
    private int coins;
    @Getter
    private int total_battles;
    @Getter
    private int won_battles;
    @Getter
    private int lost_battles;
    @Getter
    private int draw_battles;
    @Getter
    private int elo;
    // Jackson needs the default constructor
    public User() {}

    public User(int id, String username, String token, int coins, int total_battles, int won_battles, int lost_battles, int draw_battles, int elo) {
        this.username = username;
        this.id = id;
        this.token = token;
        this.coins = coins;
        this.total_battles = total_battles;
        this.won_battles = won_battles;
        this.lost_battles = lost_battles;
        this.draw_battles = draw_battles;
        this.elo = elo;
    }
}
