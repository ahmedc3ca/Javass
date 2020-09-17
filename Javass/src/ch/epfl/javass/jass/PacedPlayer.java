package ch.epfl.javass.jass;
import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class PacedPlayer implements Player {
    private final Player underlyingPlayer;
    private final double minTime;
    
    
    
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = minTime;
    }
    
   /**
    * @return
    *       la carte que le joueur désire jouer avec un délai de 'minTime' seconde(s)
    */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long startTime = System.currentTimeMillis();
        
        Card card = underlyingPlayer.cardToPlay(state, hand);
        
        long currentTime = System.currentTimeMillis();
        long minTimeMs = (long) minTime*1000;
        long dt = minTimeMs + startTime - currentTime;
        if(dt > 0) {
            try {
                Thread.sleep(dt);
            }catch (InterruptedException e) {}
        }
        return card;
    }
    
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }
    
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }
    
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }
    
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }
    
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }

}
