package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class GraphicalPlayerAdapter implements Player {

    //Le bean du pli
    private final TrickBean trickBean;
    //Le bean des scores
    private final ScoreBean scoreBean;
    //Le bean de la main
    private final HandBean handBean;
    //Le joueur en interface graphique
    private GraphicalPlayer graphicalPlayer;
    //La queue de communication
    private final ArrayBlockingQueue<Card> queue;

    /**
     * Le constructeur de la classe, définit les attributs
     */
    public GraphicalPlayerAdapter() {
        trickBean = new TrickBean();
        scoreBean = new ScoreBean();
        handBean = new HandBean();
        queue = new ArrayBlockingQueue<>(1);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater(() -> {handBean.setPlayableCards(state.trick().playableCards(hand));});
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }       

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, queue, scoreBean, trickBean, handBean);
        Platform.runLater(() -> {graphicalPlayer.createStage().show();});
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> {handBean.setHand(newHand);});
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> {trickBean.setTrump(trump);});
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {trickBean.setTrick(newTrick);});
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            scoreBean.setGamePoints(TeamId.TEAM_1, score.gamePoints(TeamId.TEAM_1));
            scoreBean.setGamePoints(TeamId.TEAM_2, score.gamePoints(TeamId.TEAM_2));
            scoreBean.setTurnPoints(TeamId.TEAM_1, score.turnPoints(TeamId.TEAM_1));            
            scoreBean.setTurnPoints(TeamId.TEAM_2, score.turnPoints(TeamId.TEAM_2));
            scoreBean.setTotalPoints(TeamId.TEAM_1, score.totalPoints(TeamId.TEAM_1));
            scoreBean.setTotalPoints(TeamId.TEAM_2, score.totalPoints(TeamId.TEAM_2));
        });
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {scoreBean.setWinningTeam(winningTeam);});
    }
}
