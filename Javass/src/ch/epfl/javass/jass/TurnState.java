package ch.epfl.javass.jass;
import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class TurnState {
    private final long pkScore;
    private final long pkUnplayedCards;
    private final int pkTrick;


    /**
     * Constructerur privé de TurnState
     * @param pkScore
     *      Le score actuel sous forme empaquetée
     * @param pkUnplayedCards
     *      L'ensemble des cartes empaquetée qui n'ont pas encore été jouées durant le tour
     * @param pkTrick
     *      Le pli actuel sous forme empaquetée
     */
    private TurnState(long pkScore, long pkUnplayedCards, int pkTrick) {
        this.pkScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards;
        this.pkTrick = pkTrick;
    }

    /**
     * 
     * @param trump
     *      L'atout du tour
     * @param score
     *      Le score intitial du tour
     * @param firstPlayer
     *      Le joueur initial du tour
     * @return
     *      L'état initial correspondant à un tour de jeu dont l'atout,
     *      le score initial et le joueur initial sont ceux donnés
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        long pkScore = score.packed();
        long pkUnplayedCards = PackedCardSet.ALL_CARDS;
        int pkTrick = PackedTrick.firstEmpty(trump, firstPlayer);

        return ofPackedComponents(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * 
     * @param pkScore
     *      Le score actuel sous forme empaquetée
     * @param pkUnplayedCards
     *      L'ensemble des cartes empaquetée qui n'ont pas encore été jouées durant le tour
     * @param pkTrick
     *      Le pli actuel sous forme empaqueté
     * @throws IllegalArgumentException
     *          si un des arguments n'est pas valide
     * @return
     *      un nouveau état de tour ayant comme attributs les arguments donnés
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick));
        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * Le getter de pkScore
     */
    public long packedScore() {
        return pkScore;
    }

    /**
     * Le getter de pkUnplayedCards
     */
    public long packedUnplayedCards() {
        return pkUnplayedCards;
    }

    /**
     * Le getter de pkTrick
     */
    public int packedTrick() {
        return pkTrick;
    }

    /**
     * 
     * @return
     *      Le score actuel
     */
    public Score score() {
        return Score.ofPacked(packedScore());
    }

    /**
     * 
     * @return
     *   L'ensemble des cartes qui n'ont pas encore été jouées durant le tour   
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(packedUnplayedCards());
    }

    /**
     * 
     * @return
     *      Le pli actuel
     */
    public Trick trick() {
        return Trick.ofPacked(packedTrick());
    }

    /**
     * 
     * @return
     *      Un booléem(vrai si le dernier pli a été joué, faux sinon)
     */
    public boolean isTerminal() {
        return packedTrick() == PackedTrick.INVALID;
    }

    /**
     * @throws IllegalStateException
     *      Si le pli est plein
     * @return
     *       L'identité du joueur devant jouer la prochaine carte
     */
    public PlayerId nextPlayer() {
        if(trick().isFull()) {
            throw new IllegalStateException(); 
        }else {
            return trick().player(trick().size());
        }
    }

    /**
     * 
     * @param card
     *      La carte que le prochain joueur va jouer
     * @throws IllegalStateException
     *      Si le pli courant est plein
     * @return
     *      L'état du tour après que la carte a été jouée
     */
    public TurnState withNewCardPlayed(Card card) {
        if(trick().isFull()) {
            throw new IllegalStateException();
        }else {
            long updatedPkUnplayedCards = unplayedCards().remove(card).packed();
            int updatedPkTrick = trick().withAddedCard(card).packed();

            return new TurnState(packedScore(), updatedPkUnplayedCards, updatedPkTrick);
        }
    }

    /**
     * @throws IllegalStateException
     *      Si le pli courant n'est pas terminé 
     * @return
     *      L'état du tour après avoir ramassé le plis
     */

    public TurnState withTrickCollected() {
        if(trick().isFull()) {
            long updatedpkScore = score().withAdditionalTrick(trick().winningPlayer().team(), trick().points()).packed();
            int updatedPkTrick = trick().nextEmpty().packed();

            return new TurnState(updatedpkScore, packedUnplayedCards(), updatedPkTrick);
        }else {
            throw new IllegalStateException();
        }
    }

    /**
     * Une méthode qui combine entre withNewCardPlayed et withTrickCollected
     * @param card
     *      la carte que le prochain joueur va jouer
     * @return
     *      l'état correspondant à celui auquel on l'applique après que le prochain joueur ait joué la carte donnée,
     *      et que le pli courant ait été ramassé s'il est plein
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        TurnState updatedTurnState = withNewCardPlayed(card);

        if(PackedTrick.isFull(updatedTurnState.packedTrick())) {
            updatedTurnState = updatedTurnState.withTrickCollected();
        }

        return updatedTurnState;
    }
}
