package ch.epfl.javass.gui;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class HandBean {

    //La main du joueur
    private final ObservableList<Card> hand;
    //Les cartes jouables du joueur
    private final ObservableSet<Card> playableCards;
    //Un booléen (vrai quand le joueur a déja joué pendant le pli, faux sinon)
    private final SimpleBooleanProperty handModified;

    /**
     * Le constructeur de la classe, définit les attributs
     */
    public HandBean() {
        hand = FXCollections.observableArrayList(Collections.nCopies(HAND_SIZE, null));
        playableCards = FXCollections.observableSet(new HashSet<>());
        handModified = new SimpleBooleanProperty(false);
    }

    /**
     * 
     * @return
     *      La main du joueur.
     */
    ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }

    /**
     * Définit la main du joueur avec les cartes de newHand
     * @param newHand
     *      La nouvelle main du joueur.
     */
    void setHand(CardSet newHand) {
        handModified.set(false);
        if(hand.equals(Collections.nCopies(HAND_SIZE, null))) {
            for(int i = 0; i < HAND_SIZE; ++i) 
                hand.set(i, newHand.get(i));
        }
        else {
            //La nouvelle main est toujours un sous ensemble de la main actuelle, si elle n'est pas vide
            assert(isSubsetOfSet(newHand, hand));
            for(int i = 0; i < HAND_SIZE; ++i) {
                if (hand.get(i) != null && !newHand.contains(hand.get(i)))
                    hand.set(i, null);
            }
        }
    }

    /**
     * 
     * @return
     *      Les cartes jouables de la main du joueur.
     */
    ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }

    /**
     * Définit les cartes jouables pour le joueur.
     * @param newPlayableCards
     *      Un ensemble de cartes jouables.
     */
    void setPlayableCards(CardSet newPlayableCards) {
        handModified.set(true);
        for (int i = 0; i < newPlayableCards.size(); ++i) 
            playableCards.add(newPlayableCards.get(i));

        playableCards.removeIf(card -> !newPlayableCards.contains(card));
    }

    /**
     * 
     * @param subset
     *      Le sous-ensemble à tester.
     * @param set
     *      L'ensemble de test.
     * @return
     *      Un booléen (vrai si subset est bel et bien un sous-ensemble de set, faux sinon).
     */
    private boolean isSubsetOfSet(CardSet subset, Collection<Card> set) {
        List<Card> cardList = new ArrayList<>();

        for(int i = 0; i < subset.size(); ++i) {
            cardList.add(subset.get(i));
        }
        return(set.containsAll(cardList));
    }
    
    ReadOnlyBooleanProperty handModified() {
        return (ReadOnlyBooleanProperty) handModified;
    }
}
