package ch.epfl.javass.jass;
import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class PackedCard {
    public static final int INVALID = 0b111111;
    public static final int RANK_SIZE = 4;
    public static final int COLOR_SIZE = 2;
    public static final int RANK_MAX = 8;
    private PackedCard() {}
    
    /**
     * 
     * @param pkCard
     *      La forme empaquetée de la carte
     * @return 
     *      Un booléen (vrai si pkCard représente une carte du jeu, faux sinon)
     */
    public static boolean isValid(int pkCard) {
        int rangValue = Bits32.extract(pkCard, 0, RANK_SIZE);
        int unusedValue = pkCard >>> RANK_SIZE + COLOR_SIZE;
        
        return (rangValue >= 0 && rangValue <= 8 && unusedValue == 0 && pkCard != INVALID);
    }
    
    /**
     * 
     * @param c
     *      La couleur de la carte
     * @param r
     *      Le rang de la carte
     * @return 
     *      La carte empaquetée de couleur et rang donné
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), RANK_SIZE, c.ordinal(), COLOR_SIZE);
    }
    
    /**
     * 
     * @param pkCard
     *      La forme empaquetée de la carte
     * @return 
     *      La couleur de la carte
     */
    public static Card.Color color(int pkCard){
        assert isValid(pkCard);
        int cardColor = Bits32.extract(pkCard, RANK_SIZE, COLOR_SIZE);
        return Color.ALL.get(cardColor);
        
        
    }
    
    /**
     * 
     * @param pkCard
     *      La forme empaquetée de la carte
     * @return 
     *      Le rang de la carte
     */
    public static Card.Rank rank(int pkCard){
        assert isValid(pkCard);
        int cardRank = Bits32.extract(pkCard, 0, RANK_SIZE);
        return Rank.ALL.get(cardRank);
    }    
    
    /**
     * 
     * @param trump
     *      L'atout
     * @param pkCardL
     *      La forme empaquetée de la carte comparée
     * @param pkCardR
     *      La forme empaquetée de la carte qui compare l'autre carte
     * @return un booléen (vrai si la carte comparée est supérieure à l'autre carte en paramètre)
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        Card.Color cardLColor = color(pkCardL);
        Card.Rank cardLRank = rank(pkCardL);
        Card.Color cardRColor = color(pkCardR);
        Card.Rank cardRRank = rank(pkCardR);
        
        if(cardLColor.equals(trump) && cardRColor.equals(trump)) {
            return cardLRank.trumpOrdinal() > cardRRank.trumpOrdinal();
        }else if(cardLColor.equals(trump)) {
           return true; 
        }else if(cardRColor.equals(trump)){
            return false;
        }else if(cardRColor.equals(cardLColor)) {
            return cardLRank.ordinal() > cardRRank.ordinal(); 
        }else {
            return false;
        }
    }
    
    /**
     * 
     * @param trump
     *      La couleur de l'atout
     * @param pkCard
     *      La forme empaquetée de la carte
     * @return 
     *      Le nombre de points remportés par la carte, sachant que "trump" est l'atout
     */
    public static int points(Card.Color trump, int pkCard) {
        if(rank(pkCard).equals(Card.Rank.JACK) || rank(pkCard).equals(Card.Rank.NINE)) {
            if(color(pkCard).equals(trump)) {
                switch(rank(pkCard)) {
                case NINE: return 14;
                case JACK: return 20;
                default: return -1;
                }
            }else {
                switch(rank(pkCard)) {
                case NINE: return 0;
                case JACK: return 2;
                default: return -1;
                }
            }
        }else {
            switch(rank(pkCard)) {
            case SIX:
            case SEVEN:
            case EIGHT: return 0;
            case TEN: return 10;
            case QUEEN: return 3;
            case KING: return 4;
            case ACE: return 11;
            default: return -1;
            }
        }
    }
    
    /**
     * 
     * @param pkCard
     *      La forme empaquetée de la carte
     * @return
     *      Une représentation textuelle de la carte (avec sa couleur et son rang)
     */
    public static String toString(int pkCard) {
        return color(pkCard).toString()+rank(pkCard).toString();
    }
}
