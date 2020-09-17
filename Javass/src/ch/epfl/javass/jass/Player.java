package ch.epfl.javass.jass;
import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public interface Player {
    /**
     * 
     * @param state
     *      L'état actuel du tour
     * @param hand
     *      La main du joueur
     * @return
     *      La carte que le joueur désire jouer
     */
    abstract Card cardToPlay(TurnState state, CardSet hand);
    
    /**
     * 
     * @param ownId
     *      L'identitée du joueur
     * @param playerNames
     *      Une table associative qui relie à chaque joueur son nom
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {}
    
    /**
     * Méthode appelée à chaque fois que la main du joueur change
     * @param newHand
     *      La nouvelle main du joueur
     */
    
    default void updateHand(CardSet newHand) {}
    
    /**
     * Méthode appelée à chaque fois que l'atout change
     * @param trump
     *      Le nouvel atout
     */
    default void setTrump(Color trump) {}
    
    /**
     * Méthode appelée à chaque fois que le pli change
     * @param newTrick
     *      Le nouveau pli
     */
    default void updateTrick(Trick newTrick) {}
    
    /**
     * Méthode appelée à chaque fois que le pli change
     * @param score
     *      Le nouveau score
     */
    default void updateScore(Score score) {}
    
    /**
     * Métahode appelée une seule fois dès qu'une équipe à gagné en obtenant 1000 points ou plus.
     * @param winningTeam
     *      L'équipe gagnante
     */
    default void setWinningTeam(TeamId winningTeam) {}
}
