package ch.epfl.javass.gui;

import java.util.EnumMap;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class TrickBean {
    //L'atout du pli
    private final SimpleObjectProperty<Color> trump;
    //Le pli
    private final ObservableMap<PlayerId, Card> trick;
    //Le joueur ayant posé la meilleure carte
    private final SimpleObjectProperty<PlayerId> winningPlayer;
    
    /**
     * Le constructeur de la classe, définit les attributs
     */
    public TrickBean() {
        trump = new SimpleObjectProperty<>(null);
        winningPlayer = new SimpleObjectProperty<>(null);
        trick = FXCollections.observableMap(new EnumMap<PlayerId, Card>(PlayerId.class));
        trick.put(PlayerId.PLAYER_1, null);
        trick.put(PlayerId.PLAYER_2, null);
        trick.put(PlayerId.PLAYER_3, null);
        trick.put(PlayerId.PLAYER_4, null);
    }
    
    /**
     * 
     * @return
     *      La propriété trump.
     */
    ReadOnlyObjectProperty<Color> trumpProperty() {
        return (ReadOnlyObjectProperty<Color>) trump;
    }
    
    /**
     * Définit l'atout.
     * @param trump
     *      L'atout que l'on veut définir.
     */
    void setTrump(Color trump) {
        this.trump.set(trump);
    }
    
    /**
     * 
     * @return
     *      Le pli non modifiable.
     */
    ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trick);
    }
    
    /**
     * Modifie le pli en fonction du newTrick en paramètre.
     * @param newTrick
     *      Le nouveau pli.
     */
    void setTrick(Trick newTrick) {
        trick.put(PlayerId.PLAYER_1, null);
        trick.put(PlayerId.PLAYER_2, null);
        trick.put(PlayerId.PLAYER_3, null);
        trick.put(PlayerId.PLAYER_4, null);
        
        for(int i = 0; i < newTrick.size(); ++i)
                trick.replace(newTrick.player(i), newTrick.card(i));
        
        if (newTrick.isEmpty())
            winningPlayer.set(null);
        else
            winningPlayer.set(newTrick.winningPlayer());
    }
    
    ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return (ReadOnlyObjectProperty<PlayerId>) winningPlayer;
    }
}
