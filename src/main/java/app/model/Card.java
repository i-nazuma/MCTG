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
        // unfortunately java doesn't let methods pass objects as reference, could be much cleaner...
        String[] words;
        {
            assert name != null;
            words = name.split("(?<!^)(?=[A-Z])"); //this regex splits camelCase/PascalCase
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
                default -> throw new IllegalArgumentException("Not a valid CardType! (" + words[0] + ")");
            }

        }else if(words.length == 2){
            switch (words[0]) {
                case "Fire" -> this.elementType = ElementType.FIRE;
                case "Water" -> this.elementType = ElementType.WATER;
                case "Regular" -> this.elementType = ElementType.REGULAR;
                default -> throw new IllegalArgumentException("Not a valid ElementType! (" + words[0] + ")");
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
                default -> throw new IllegalArgumentException("Not a valid CardType! (" + words[1] + ")");
            }
        }
    }

    //here we handle the specialties, where the actual Damage doesn't even matter
    public boolean defeats(Card opponentCard){
        //Monster VS Monster
        if(!CardType.SPELL.equals(this.getCardType()) && !CardType.SPELL.equals(opponentCard.getCardType())){
            //Dragons defeat Goblins
            if(CardType.DRAGON.equals(this.getCardType()) && CardType.GOBLIN.equals(opponentCard.getCardType())){
                return true;

            //Wizards defeat Orks
            }else if(CardType.WIZARD.equals(this.getCardType()) && CardType.ORK.equals(opponentCard.getCardType())){
                return true;

            //FireElves defeat Dragons
            }else if(ElementType.FIRE.equals(this.getElementType()) && CardType.ELF.equals(this.getCardType()) && CardType.DRAGON.equals(opponentCard.getCardType())){
                return true;
            }
        }
        //Monster VS Spell, only one specialty: Krakens defeat Spells
        if(CardType.KRAKEN.equals(this.getCardType()) && CardType.SPELL.equals(opponentCard.getCardType())){
            return true;
        }

        //Spell VS Monster, also only one specialty: WaterSpells defeat Knights
        if(CardType.SPELL.equals(this.getCardType()) && ElementType.WATER.equals(this.getElementType()) && CardType.KNIGHT.equals(opponentCard.getCardType())){
            return true;
        }

        return false; //no specialty involved
    }

    //in case Spells are involved, the damage effectiveness is handled here
    public float calculateEffectiveDamage(Card opponentCard){
        if(CardType.SPELL.equals(this.getCardType())){

            if //effective (200% damage
                    ((ElementType.WATER.equals(this.getElementType()) && ElementType.FIRE.equals(opponentCard.getElementType())) ||
                    (ElementType.FIRE.equals(this.getElementType()) && ElementType.REGULAR.equals(opponentCard.getElementType())) ||
                    (ElementType.REGULAR.equals(this.getElementType()) && ElementType.WATER.equals(opponentCard.getElementType())))
            {
                return 2 * this.getDamage();
            }

            if //not effective (50% damage
                    ((ElementType.FIRE.equals(this.getElementType()) && ElementType.WATER.equals(opponentCard.getElementType())) ||
                            (ElementType.REGULAR.equals(this.getElementType()) && ElementType.FIRE.equals(opponentCard.getElementType())) ||
                            (ElementType.WATER.equals(this.getElementType()) && ElementType.REGULAR.equals(opponentCard.getElementType())))
            {
                return this.getDamage() / 2;
            }
        }

        //no effect
        return this.getDamage();
    }
}
