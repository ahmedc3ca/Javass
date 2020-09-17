package ch.epfl.javass.jass;
import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class PackedCardSet {
    public static final long EMPTY = 0L;
    public static final long ALL_CARDS = 0b0000000111111111000000011111111100000001111111110000000111111111L;

    private static long[][] trumpAboveValues = {
            {0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_111111110L,    // 6
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_111111100L, // 7
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_111111000L, // 8
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_000100000L, // 9
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_111101000L, // 10
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_000000000L, // JACK
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_110101000L, // QUEEN
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_100101000L, // KING
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_000101000L  // ACE
            }, // ♠
            {0b0000000_000000000_0000000_000000000_0000000_111111110_0000000_000000000L,    // 6
                0b0000000_000000000_0000000_000000000_0000000_111111100_0000000_000000000L, // 7
                0b0000000_000000000_0000000_000000000_0000000_111111000_0000000_000000000L, // 8
                0b0000000_000000000_0000000_000000000_0000000_000100000_0000000_000000000L, // 9
                0b0000000_000000000_0000000_000000000_0000000_111101000_0000000_000000000L, // 10
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_000000000L, // JACK
                0b0000000_000000000_0000000_000000000_0000000_110101000_0000000_000000000L, // QUEEN
                0b0000000_000000000_0000000_000000000_0000000_100101000_0000000_000000000L, // KING
                0b0000000_000000000_0000000_000000000_0000000_000101000_0000000_000000000L  // ACE
            }, // ♥
            {0b0000000_000000000_0000000_111111110_0000000_000000000_0000000_000000000L,    // 6
                0b0000000_000000000_0000000_111111100_0000000_000000000_0000000_000000000L, // 7
                0b0000000_000000000_0000000_111111000_0000000_000000000_0000000_000000000L, // 8
                0b0000000_000000000_0000000_000100000_0000000_000000000_0000000_000000000L, // 9
                0b0000000_000000000_0000000_111101000_0000000_000000000_0000000_000000000L, // 10
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_000000000L, // JACK
                0b0000000_000000000_0000000_110101000_0000000_000000000_0000000_000000000L, // QUEEN
                0b0000000_000000000_0000000_100101000_0000000_000000000_0000000_000000000L, // KING
                0b0000000_000000000_0000000_000101000_0000000_000000000_0000000_000000000L  // ACE
            }, // ♦
            {0b0000000_111111110_0000000_000000000_0000000_000000000_0000000_000000000L,    // 6
                0b0000000_111111100_0000000_000000000_0000000_000000000_0000000_000000000L, // 7
                0b0000000_111111000_0000000_000000000_0000000_000000000_0000000_000000000L, // 8
                0b0000000_000100000_0000000_000000000_0000000_000000000_0000000_000000000L, // 9
                0b0000000_111101000_0000000_000000000_0000000_000000000_0000000_000000000L, // 10
                0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_000000000L, // JACK
                0b0000000_110101000_0000000_000000000_0000000_000000000_0000000_000000000L, // QUEEN
                0b0000000_100101000_0000000_000000000_0000000_000000000_0000000_000000000L, // KING
                0b0000000_000101000_0000000_000000000_0000000_000000000_0000000_000000000L  // ACE
            } // ♣
    };

    private static long[] subsetOfColorValues = {
            0b0000000_000000000_0000000_000000000_0000000_000000000_0000000_111111111L, // ♠
            0b0000000_000000000_0000000_000000000_0000000_111111111_0000000_000000000L, // ♥
            0b0000000_000000000_0000000_111111111_0000000_000000000_0000000_000000000L, // ♦
            0b0000000_111111111_0000000_000000000_0000000_000000000_0000000_000000000L  // ♣
    };

    
    private PackedCardSet(){}
    
    /**
     *
     * @param pkCardSet
     *      L'ensemble des cartes sous forme empaquetée
     * @return
     *      Un booléen (vrai si les bits inutilisées sont égaux à zéro, faux sinon)
     */
    public static boolean isValid(long pkCardSet) {
        boolean isUnusedBits1 = Bits64.extract(pkCardSet, 9, 7) == 0L;
        boolean isUnusedBits2 = Bits64.extract(pkCardSet, 25, 7) == 0L;
        boolean isUnusedBits3 = Bits64.extract(pkCardSet, 41, 7) == 0L;
        boolean isUnusedBits4 = Bits64.extract(pkCardSet, 57, 7) == 0L;

        return isUnusedBits1 && isUnusedBits2 && isUnusedBits3 && isUnusedBits4;
    }


    /**
     * 
     * @param pkCard
     *      La carte sous forme empaquetée
     * @return
     *      L'ensemble de cartes strictement plus fortes que la carte empaquetée donnée, sachant qu'il s'agit d'une carte d'atout
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);

        return trumpAboveValues[Bits32.extract(pkCard, 4, 2)][Bits32.extract(pkCard, 0, 4)];
    }


    /**
     * 
     * @param pkCard
     *      La carte sous forme empaquetée
     * @return
     *      L'ensemble de cartes empaqueté contenant uniquement la carte empaquetée donnée
     */
    public static long singleton(int pkCard) {
        assert(PackedCard.isValid(pkCard));

        return Bits64.mask(pkCard, 1);
    }


    /**
     * 
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @return
     *      Un booléen (vrai si l'ensemble des cartes est vide, faux sinon)
     */
    public static boolean isEmpty(long pkCardSet) {
        assert(isValid(pkCardSet));

        return pkCardSet == EMPTY;
    }


    /**
     * 
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @return
     *      La taille de l'ensemble de cartes empaqueté donné
     */
    public static int size(long pkCardSet) {
        assert(isValid(pkCardSet));

        return Long.bitCount(pkCardSet);
    }
    /**
     * 
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @param index
     *      L'index du carte qu'on cherche dans l'ensemble pkCardSet
     * @return
     *      La version empaquetée de la carte à l'index donné de l'ensemble de cartes empaqueté donné
     *      
     */
    public static int get(long pkCardSet, int index) {
        assert(isValid(pkCardSet) && index < size(pkCardSet) && index >= 0);

        for(int i = 0; i < index; ++i) {
            pkCardSet = pkCardSet & ~Long.lowestOneBit(pkCardSet);
        }
        int indexInLong = Long.numberOfTrailingZeros(pkCardSet);
        return indexInLong;
    }


    /**
     * @brief 
     *      Ajoute la carte donnée dans l'ensemble de cartes en paramètre
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @param pkCard
     *      La carte sous forme empaquetée
     * @return
     *      L'ensemble de cartes pkcardSet avec en plus la carte pkCard
     *      
     */
    public static long add(long pkCardSet, int pkCard) {
        assert(isValid(pkCardSet));

        return pkCardSet |= singleton(pkCard);
    }


    /**
     * @brief
     *      Enlève la carte donnée dans l'ensemble de cartes en paramètre
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @param pkCard
     *      La carte sous forme empaquetée
     * @return
     *      L'ensemble de cartes pkCardSet sans la carte pkCard
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert(isValid(pkCardSet));

        return pkCardSet &= ~(singleton(pkCard));
    }


    /**
     * 
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @param pkCard
     *      La carte sous forme empaquetée
     * @return
     *      Un booléen (vrai si pkCard est dans pkCardSet, faux sinon)
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert(isValid(pkCardSet));

        return add(pkCardSet, pkCard) == pkCardSet;
    }


    /**
     * 
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @return
     *      lLe complément de l'ensemble de cartes empaqueté donné,
     */
    public static long complement(long pkCardSet) {
        assert(isValid(pkCardSet));

        return ~pkCardSet & ALL_CARDS;
    }


    /**
     * 
     * @param pkCardSet1
     *      L'ensemble de cartes 1 sous forme empaquetée
     * @param pkCardSet2
     *      L'ensemble de cartes 2 sous forme empaquetée
     * @return
     *      L'union des deux ensembles de cartes empaquetés donnés
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert(isValid(pkCardSet1) && isValid(pkCardSet2));

        return pkCardSet1 |= pkCardSet2;
    }


    /**
     * 
     * @param pkCardSet1
     *      L'ensemble des cartes 1 sous forme empaquetée
     * @param pkCardSet2
     *      L'ensemble des cartes 2 sous forme empaquetée
     * @return
     *      L'intersection des deux ensembles de cartes empaquetés donnés
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert(isValid(pkCardSet1) && isValid(pkCardSet2));

        return pkCardSet1 &= pkCardSet2;
    }


    /**
     * 
     * @param pkCardSet1
     *      L'ensemble de cartes 1 sous forme empaquetée
     * @param pkCardSet2
     *      L'ensemble de cartes 2 sous forme empaquetée
     * @return
     *      La différence entre le premier ensemble de cartes empaqueté donné et le second
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert(isValid(pkCardSet1) && isValid(pkCardSet2));

        return pkCardSet1 & ~pkCardSet2;
    }


    /**
     * 
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @param color
     *      La couleur du sous-ensemble voulu
     * @return
     *      Le sous-ensemble de l'ensemble de cartes empaqueté donné constitué uniquement des cartes de la couleur donnée
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        return pkCardSet &= subsetOfColorValues[color.ordinal()];
    }


    /**
     * 
     * @param pkCardSet
     *      L'ensemble de cartes sous forme empaquetée
     * @return
     *      La représentation textuelle de l'ensemble de cartes empaqueté donné
     */
    public static String toString(long pkCardSet) {
        StringJoiner cards = new StringJoiner(",", "{", "}");
        for(int i = 0; i < size(pkCardSet); ++i) {
            cards.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return cards.toString();
    }
}
