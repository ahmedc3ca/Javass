package ch.epfl.javass.jass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */

public final class JassGame {
    //Différents générateurs aléatoires 
    private final Random shuffleRng;
    private final Random trumpRng;
    //Une table associative qui associe à chaque joueur son identité
    private final Map<PlayerId, Player> players;
    //Une table associative qui associe à chaque joueur son nom
    private final Map<PlayerId, String> playerNames;
    //L'état actuel du jeu
    TurnState turnState;
    //Une table associative qui associe à chaque joueur sa main
    private Map<PlayerId, CardSet> playerHands;
    //L'identitée du joueur qui commence le jeu
    private PlayerId startPlayer;


    /**
     * Constructeur public de JassGame
     * @param rngSeed
     *      La graine utilisée pour l'aléatoire
     * @param players
     *      Table associative qui associe à chaque joueur son identité
     * @param playerNames
     *      Table associative qui associe à chaque joueur son nom
     *      
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections.unmodifiableMap(new EnumMap<>(playerNames));

        this.playerHands = new EnumMap<>(PlayerId.class);

        initialisePlayers();
    }

    /**
     * 
     * @return
     *      Un booléen(vrai si le tour est fini c.à.d. une équipe a remporté 1000 points à la fin du pli, faux sinon)
     */
    public boolean isGameOver() {
        return turnState == null ? false : turnState.score().totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS || turnState.score().totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS;
    }

    // LE NOUVEAU
    /**
     *  Méthode auxilière qui fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait rien si la partie est terminée.
     */
    public void advanceToEndOfNextTrick() {
        //On execute le jeu
        if (!isGameOver()) {
            if(turnState == null) 
                nextTurn();
            else 
                turnState = turnState.withTrickCollected();
            if(turnState.isTerminal()) {
                nextTurn();
            }
            nextTrick();
        }
        if(isGameOver()) {
            TeamId winningTeam = turnState.score().totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS ? TeamId.TEAM_1 : TeamId.TEAM_2;
            for(PlayerId p : PlayerId.ALL) {
                players.get(p).updateScore(turnState.score());
                players.get(p).setWinningTeam(winningTeam);
            }
        }
        else {
            while(!turnState.trick().isFull()) {
                nextPlayerPlays();
            }
        }
    }

        //    LE VIEUX, POINTS EN MOINS POUR LA MISE EN PLACE
        //
        //    /**
        //     *  Méthode auxilière qui fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait rien si la partie est terminée.
        //     */
        //    public void advanceToEndOfNextTrick() {
        //        //Ramasse le pli précédent (s'il existe)
        //        if(turnState != null && !PackedTrick.isEmpty(turnState.packedTrick())) {
        //            turnState = turnState.withTrickCollected();
        //        }
        //
        //        if(!isGameOver()) {
        //            //Début de partie OU fin de tour: Distribue les cartes, défini l'atout
        //            if(turnState == null || turnState.isTerminal()) {
        //                nextTurn();
        //
        //            }
        //
        //            //Début du pli: Affiche score et update le pli
        //            nextTrick();
        //
        //            //Fait jouer les 4 joueurs
        //            while(!turnState.trick().isFull()) {
        //                nextPlayerPlays();
        //            }
        //        }else {
        //            TeamId winningTeam = turnState.score().totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS ? TeamId.TEAM_1 : TeamId.TEAM_2;
        //
        //            for(PlayerId p : PlayerId.ALL) {
        //                players.get(p).updateScore(turnState.score());
        //                players.get(p).setWinningTeam(winningTeam);
        //            }
        //        }
        //    }

        /**
         * Méthode auxilière qui sert à attribuer à chaque joueur son nom
         */
        private void initialisePlayers() {
            for(PlayerId p : PlayerId.ALL) {
                players.get(p).setPlayers(p, playerNames);
            }
        }

        /**
         * Méthode qui distribue les cartes(donne à chaque joueur une main aléatoire)
         * @return
         *      Le joueur qui commence le tour (celui qui a le ♦7 pour le premier tour ou le prochain joueur autrement)
         */
        private PlayerId MixDistributeCards() {
            long allCards = PackedCardSet.ALL_CARDS;
            List<Card> deck = new ArrayList<>();
            for(int i = 0; i < PackedCardSet.size(allCards); ++i) {
                deck.add(Card.ofPacked(PackedCardSet.get(allCards, i)));
            }
            Collections.shuffle(deck, shuffleRng);


            for(int i = 0; i < 4; ++i) {
                CardSet playerHand = CardSet.of(deck.subList(9*i, 9*(i+1)));
                PlayerId player = PlayerId.ALL.get(i);

                playerHands.put(player, playerHand);
                players.get(player).updateHand(playerHand);
                if(turnState == null) {
                    if(playerHand.contains(Card.of(Color.DIAMOND, Rank.SEVEN))) {
                        startPlayer = player;

                    }
                }


            }
            if(turnState != null) {
                startPlayer = 
                        PlayerId.ALL.get((startPlayer.ordinal() + 1) % 4);
            }
            return startPlayer;
        }

        /**
         * Méthode auxilière qui commence le tour prochain(en distribuant de nouvau les cartes et Indiquant le premier joueur,l'atout et le score)
         */
        private void nextTurn() {
            PlayerId firstPlayer = MixDistributeCards();
            Color newTrump = Color.ALL.get(trumpRng.nextInt(4));
            Score score = turnState == null ? Score.INITIAL : turnState.score().nextTurn();

            turnState = TurnState.initial(newTrump, score, firstPlayer);
            for(PlayerId p : PlayerId.ALL) {
                players.get(p).setTrump(newTrump);
            }
        }

        /**
         * Méthode auxilière qui fait passer au prochain pli(en mettant en jour le score et le pli de chaque joueur)
         */
        private void nextTrick() {
            for(PlayerId p : PlayerId.ALL) {
                players.get(p).updateScore(turnState.score());
                players.get(p).updateTrick(turnState.trick());
            }
        }

        /**
         * Méthode auxilière qui fait jouer le prochain joueur et met à jour le tour 
         */
        private void nextPlayerPlays() {
            PlayerId nextPlayer = turnState.nextPlayer();

            Card card = players.get(nextPlayer).cardToPlay(turnState, playerHands.get(nextPlayer));
            turnState = turnState.withNewCardPlayed(card);
            CardSet newCardSet = playerHands.get(nextPlayer).remove(card);
            playerHands.put(nextPlayer, newCardSet);
            players.get(nextPlayer).updateHand(playerHands.get(nextPlayer));
            for(PlayerId p : PlayerId.ALL) {
                players.get(p).updateTrick(turnState.trick());
            }
        }

    }
