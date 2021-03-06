package app.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class Card {
    @Getter
    @JsonAlias({"Name"})
    private String name;
    @Getter
    @JsonAlias({"Damage"})
    private float damage;
    @Getter
    @JsonAlias({"Id"})
    private String token;
    @Getter
    @Setter
    private int packageID;
    @Getter
    @Setter
    private ElementType elementType;
    @Getter
    @Setter
    private CardType cardType;

    public Card() {}

    @JsonCreator
    public Card(@JsonProperty("Id")String token, @JsonProperty("Name") String name, @JsonProperty("Damage") float damage) {
        this.name = name;
        this.damage = damage;
        this.token = token;


        //this part is to read the element- and card type out of the name,
        // unfortunately java doesn't let methods pass objects as reference
        String[] words;
        {
            assert name != null;
            words = name.split("(?<!^)(?=[A-Z])"); //splits camelCase/PascalCase
        }
        if(words.length == 1) {
            this.elementType = ElementType.REGULAR;
            switch (words[0]) {
                case "Ork" -> this.cardType = CardType.ORK;
                case "Dragon" -> this.cardType = CardType.DRAGON;
                case "Troll" -> this.cardType = CardType.TROLL;
                case "Elf" -> this.cardType = CardType.ELF;
                case "Kraken" -> this.cardType = CardType.KRAKEN;
                case "Wizard" -> this.cardType = CardType.WIZARD;
                case "Knight" -> this.cardType = CardType.KNIGHT;
                case "Goblin" -> this.cardType = CardType.GOBLIN;
            }

        }else if(words.length == 2){
            switch (words[0]) {
                case "Fire" -> this.elementType = ElementType.FIRE;
                case "Water" -> this.elementType = ElementType.WATER;
                case "Regular" -> this.elementType = ElementType.REGULAR;
            }

            switch (words[1]) {
                case "Spell" -> this.cardType = CardType.SPELL;
                case "Ork" -> this.cardType = CardType.ORK;
                case "Dragon" -> this.cardType = CardType.DRAGON;
                case "Troll" -> this.cardType = CardType.TROLL;
                case "Elf" -> this.cardType = CardType.ELF;
                case "Kraken" -> this.cardType = CardType.KRAKEN;
                case "Wizard" -> this.cardType = CardType.WIZARD;
                case "Knight" -> this.cardType = CardType.KNIGHT;
                case "Goblin" -> this.cardType = CardType.GOBLIN;
            }
        }
    }
}
