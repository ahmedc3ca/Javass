package ch.epfl.javass.jass;

import java.util.StringJoiner;
import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class PackedTrick {
    public static final int INVALID = 0b11_11_1111_111111_111111_111111_111111;

    private PackedTrick() {}

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      Un booléen (vrai si, l'index est compris entre 0 et 8 (inclus) et la représentation 
     *      des cartes est valide, faux sinon)
     */
    public static boolean isValid(int pkTrick) {
        int firstCard = Bits32.extract(pkTrick, 0, 6);
        int secondCard = Bits32.extract(pkTrick, 6, 6);
        int thirdCard = Bits32.extract(pkTrick, 12, 6);
        int fourthCard = Bits32.extract(pkTrick, 18, 6);
        int index = Bits32.extract(pkTrick, 24, 4);
        boolean isValidIndex = index >= 0 && index <= 8;
        boolean fourValidCards = PackedCard.isValid(firstCard) && PackedCard.isValid(secondCard) 
                && PackedCard.isValid(thirdCard) && PackedCard.isValid(fourthCard);
        boolean threeValidCards = PackedCard.isValid(firstCard) && PackedCard.isValid(secondCard) 
                && PackedCard.isValid(thirdCard) && fourthCard == PackedCard.INVALID;
        boolean twoValidCards = PackedCard.isValid(firstCard) && PackedCard.isValid(secondCard) 
                && thirdCard == PackedCard.INVALID && fourthCard == PackedCard.INVALID;
        boolean oneValidCard = PackedCard.isValid(firstCard) && secondCard == PackedCard.INVALID 
                && thirdCard == PackedCard.INVALID && fourthCard == PackedCard.INVALID;
        boolean noValidCard = firstCard == PackedCard.INVALID && secondCard == PackedCard.INVALID 
                && thirdCard == PackedCard.INVALID && fourthCard == PackedCard.INVALID;

        return isValidIndex && (fourValidCards || threeValidCards || twoValidCards || oneValidCard || noValidCard);
    }

    /**
     * 
     * @param trump
     *      La couleur de l'atout
     * @param firstPlayer
     *      Le joueur qui joue la première carte du pli
     * @return
     *      Le pli empaqueté vide
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return Bits32.pack(PackedCard.INVALID, 6, PackedCard.INVALID, 6, PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, firstPlayer.ordinal(), 2, trump.ordinal(), 2);
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      Le pli empaqueté vide suivant celui donné
     */
    public static int nextEmpty(int pkTrick) {
        //assert(isFull(pkTrick));

        if(isLast(pkTrick)) {
            return INVALID;
        }else {
            return Bits32.pack(PackedCard.INVALID, 6, PackedCard.INVALID, 6, PackedCard.INVALID, 6, PackedCard.INVALID, 6, index(pkTrick)+1, 4, winningPlayer(pkTrick).ordinal(), 2, trump(pkTrick).ordinal(), 2);
        }
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      Un booléen (vrai si c'est le dernier pli, faux sinon)
     */
    public static boolean isLast(int pkTrick) {
        assert(isValid(pkTrick));

        return index(pkTrick) == Jass.TRICKS_PER_TURN-1;
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      Un booléen (vrai si aucune carte n'a été joué, faux sinon)
     */
    public static boolean isEmpty(int pkTrick) {
        return Bits32.extract(pkTrick, 0, 24) == 0b111111111111111111111111;
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      Un booléen (vrai si tous les joueurs ont posé une carte, faux sinon) 
     */
    public static boolean isFull(int pkTrick) {  
        return size(pkTrick) == 4;
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      Le nombre de cartes jouées
     */
    public static int size(int pkTrick) {     
        int size = 0;
        for(int i = 0; i < 4; ++i) {
            if(card(pkTrick, i) != PackedCard.INVALID) {
                size++;
            }
        }
        return size;
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      L'atout du pli
     */
    public static Color trump(int pkTrick) {
        assert(isValid(pkTrick));

        return Color.ALL.get(Bits32.extract(pkTrick, 30, 2));
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @param index
     *      Le nième joueur ayant posé sa carte
     * @return 
     *      Le joueur d'index donné dans le pli
     */
    public static PlayerId player(int pkTrick, int index) {
        assert(isValid(pkTrick) && index >= 0 && index <= 3);

        return PlayerId.ALL.get((Bits32.extract(pkTrick, 28, 2)+index) % 4);
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      L'index du pli
     */
    public static int index(int pkTrick) {
        assert(isValid(pkTrick));

        return Bits32.extract(pkTrick, 24, 4);
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @param index
     *      La nième carte jouée
     * @return
     *      La version empaquetée de la carte du pli à l'index donné
     */
    public static int card(int pkTrick, int index) {
        assert(isValid(pkTrick) && index >= 0 && index <= 3);

        return Bits32.extract(pkTrick, 6*index, 6);
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaqueté
     * @param pkCard
     *      La carte sous sa forme empaquetée
     * @return
     *      Le pli pkTrick avec la carte pkCard en plus
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert(!isFull(pkTrick));

        int[] cards =  {card(pkTrick, 0), card(pkTrick, 1), card(pkTrick, 2), card(pkTrick, 3)};
        cards[size(pkTrick)] = pkCard;

        return Bits32.pack(cards[0], 6, cards[1], 6, cards[2], 6, cards[3], 6, index(pkTrick), 4, player(pkTrick, 0).ordinal(), 2, trump(pkTrick).ordinal(), 2); 
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaquetée
     * @return
     *      La couleur de la première carte jouée
     */
    public static Color baseColor(int pkTrick) {
        assert(!isEmpty(pkTrick));

        return PackedCard.color(card(pkTrick, 0));
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaqueté
     * @param pkHand
     *      L'ensemble de cartes d'une main
     * @return
     *      L'ensemble des cartes jouables dans le pli pkTrick parmi les cartes de pkHand
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert(PackedCardSet.isValid(pkHand) && !isFull(pkTrick));
        //Si le pli n'a pas de cartes jouées, le joueur peut jouer n'importe quelle carte
        if(size(pkTrick) == 0) {
            return pkHand;
        }else {
            Color baseColor = baseColor(pkTrick);
            Color trump = trump(pkTrick);
            long maskPlayableTrump = baseColor.equals(trump) ? 0L : PackedCardSet.subsetOfColor(PackedCardSet.ALL_CARDS, trump);
            long maskPlayableBaseColor = PackedCardSet.subsetOfColor(PackedCardSet.ALL_CARDS, baseColor);

            for(int i = 0; i < size(pkTrick); ++i) {
                int currentCard = card(pkTrick, i);

                if(!baseColor.equals(trump) && PackedCard.color(currentCard).equals(trump)) {
                    maskPlayableTrump &= PackedCardSet.trumpAbove(currentCard);
                }
            }

            long playableTrump = PackedCardSet.intersection(maskPlayableTrump, pkHand);
            long playableBaseColor = PackedCardSet.intersection(maskPlayableBaseColor, pkHand);
            if(PackedCardSet.size(playableBaseColor) == 0) {
                //Exception 1 : Un joueur qui ne possède aucune carte de la couleur de base
                if(!baseColor.equals(trump) 
                        && PackedCardSet.intersection(pkHand, PackedCardSet.subsetOfColor(PackedCardSet.ALL_CARDS, trump)) == pkHand
                        && PackedCardSet.size(playableTrump) == 0) {
                    return pkHand;
                }
                //Exception 3 : Un joueur qui n'a plus que des cartes d'atout plus faibles que la pus forte carte d'atout
                long newPlayableBaseColor = PackedCardSet.intersection(PackedCardSet.complement(PackedCardSet.subsetOfColor(PackedCardSet.ALL_CARDS, trump)), pkHand);
                return PackedCardSet.union(newPlayableBaseColor, playableTrump);
            }
            //Exception 2 : Lorsque la couleur de base est atout et le joueur ne possède plus d'autres cartes que le valet
            if(baseColor.equals(trump) && PackedCardSet.size(playableBaseColor) == 1 
                    && PackedCard.rank(PackedCardSet.get(playableBaseColor, 0)).equals(Rank.JACK)) {
                return pkHand;
            }
            return PackedCardSet.union(playableBaseColor, playableTrump);
        }
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaqueté
     * @return
     *      Le nombre de points emportés par le pli
     */
    public static int points(int pkTrick) {
        int points = 0;
        for(int i = 0; i < size(pkTrick); ++i) {
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));
        }
        if(isLast(pkTrick)) {
            points += 5;
        }
        return points;
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaqueté
     * @return
     *      Le joueur ayant gagné le pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        int cardIndex = 0;

        for(int i = 1; i < size(pkTrick); ++i) {
            if(PackedCard.isBetter(trump(pkTrick), card(pkTrick, i), card(pkTrick, cardIndex))) {
                cardIndex = i;
            }
        }
        return player(pkTrick, cardIndex);
    }

    /**
     * 
     * @param pkTrick
     *      Le pli sous forme empaqueté
     * @return
     *      Une représentation textuelle du pli
     */
    public static String toString(int pkTrick) {
        StringJoiner playedCards = new StringJoiner(",", "{", "}");
        for(int i = 0; i < size(pkTrick); ++i) {
            playedCards.add(PackedCard.toString(card(pkTrick, i)));
        }
        return "Trump: "+trump(pkTrick)+" Player: "+player(pkTrick, 0)+" Index: "+index(pkTrick)+" Cards: "+playedCards.toString();
    }

}
