package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ch.epfl.javass.Preconditions;


/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class Card {
    private final int pkCard;

    /**
     * 
     * @param pkCard
     *      Version empaquetée de la carte
     */
    private Card(int pkCard) {
        this.pkCard = pkCard;
    }

    /**
     * 
     * @param c
     *      Couleur de la carte
     * @param r
     *      Rang de la carte
     * @return La carte de couleur et de rang donné en paramètre
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * 
     * @param packed
     *      Version empaquetée de la carte
     * @return La carte dont packed est la version empaquetée
     */
    public static Card ofPacked(int packed) {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        return new Card(packed);
    }

    /**
     * 
     * @return 
     *      La version empaquetée de la carte
     */
    public int packed() {
        return this.pkCard;
    }

    /**
     * 
     * @return 
     *      La couleur de la carte
     */
    public Color color() {
        return PackedCard.color(pkCard);
    }

    /**
     * 
     * @return 
     *      Le rang de la carte
     */
    public Rank rank() {
        return PackedCard.rank(pkCard);
    }

    /**
     * 
     * @param trump
     *      La couleur de l'atout
     * @param that
     *      La carte à comparer
     * @return 
     *      Un booléen (vrai si la carte est supérieure à that, faux sinon)
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.pkCard, that.packed());
    }

    /**
     * 
     * @param trump
     *      La couleur de l'atout
     * @return Le nombre de points remportés par la carte
     */
    public int points(Color trump) {
        return PackedCard.points(trump, this.pkCard);
    }

    /**
     * Une méthode qui s'assure que le récepteur et le paramètre sont égaux
     * @param thatO
     *      L'autre carte comparée
     * @return 
     *      Un booléen (vrai si l'objet thatO est le même que la carte)
     */
    @Override
    public boolean equals(Object thatO) {
        return (thatO instanceof Card) && (this.hashCode() == ((Card) thatO).hashCode());
    }


    /**
     * 
     * @return 
     *      La forme empaquetée de la carte
     */
    @Override
    public int hashCode() {
        return this.packed();
    }

    /**
     * 
     * @return 
     *      Une représentation textuelle de la carte
     */
    @Override
    public String toString() {
        return PackedCard.toString(this.pkCard);
    }

    public enum Color {  
        SPADE(),
        HEART(),
        DIAMOND(),
        CLUB();

        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = 4;

        /**
         * 
         * @return  
         *      Une représentation textuelle des couleurs
         */
        @Override
        public String toString() {
            switch(this) {
            case SPADE: return "\u2660";
            case HEART: return "\u2665";
            case DIAMOND: return "\u2666";
            case CLUB: return "\u2663";
            default: return "";
            }
        }
    }

    public enum Rank {
        SIX(),
        SEVEN(),
        EIGHT(),
        NINE(),
        TEN(),
        JACK(),
        QUEEN(),
        KING(),
        ACE();

        public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = 9;

        /**
         * 
         * @return 
         *      La position de la carte quand sa couleur est atout
         */
        public int trumpOrdinal() {
            switch(this) {
            case SIX: return 0;
            case SEVEN: return 1;
            case EIGHT: return 2;
            case NINE: return 7;
            case TEN: return 3;
            case JACK: return 8;
            case QUEEN: return 4;
            case KING: return 5;
            case ACE: return 6;
            default: return -1;
            }
        }

        /**
         * 
         * @return 
         *      Une représentation textuelle des rangs
         */
        @Override
        public String toString() {
            switch(this) {
            case SIX: return "6";
            case SEVEN: return "7";
            case EIGHT: return "8";
            case NINE: return "9";
            case TEN: return "10";
            case JACK: return "J";
            case QUEEN: return "Q";
            case KING: return "K";
            case ACE: return "A";
            default: return "";
            }
        }
    }
}
