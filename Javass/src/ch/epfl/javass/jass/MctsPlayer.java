package ch.epfl.javass.jass;
import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;
import ch.epfl.javass.Preconditions;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class MctsPlayer implements Player {
    private final PlayerId ownId;
    private final SplittableRandom rng;
    private final int iterations;

    /**
     * Constructeur public de MctsPlayer
     * @param ownId
     *      L'identité du joueur
     * @param rng
     *      Le générateur aléatoire
     * @param iterations
     *      Le nombre d'itérations
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations){
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);

        this.ownId = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    /**
     * Méthode de player redéfinie pour qu'elle retourne la carte choisie par l'algorithme de Monte-Carlo Tree Search au lieu d'une carte aléatoire)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        //retourne la dernière carte à jouer si c'est le dernier tour sans utiliser l'algorithme
        if(state.trick().index() == 8)
            return hand.get(0);
        Node root = new Node(state, new LinkedList<>(), ownId, hand, rng);
        for(int i = 0; i < iterations; ++i) {
            root.runNode();
        }
        return root.allCards[root.chosenChildIndex()];

    }

    private static class Node {
        private Card[] allCards;
        private TurnState turnState;
        private List<Node> path;
        private int totalPoints;
        private int nTurns;
        private Node[] children;
        private CardSet inexistentChildren;
        private PlayerId mctsId;
        private CardSet mctsHand;
        private SplittableRandom rng;

        /**
         * Constructeur de la classe Node
         * @param turnstate
         *      L'état du tour du noeud
         * @param isPlayer
         *      un booléen(vrai si le noeud est le joueur simulé, faux sinon)
         */
        private Node(TurnState turnState, List<Node> parentPath, PlayerId mctsId, CardSet mctsHand, SplittableRandom rng){
            this.turnState = turnState;
            this.path = new LinkedList<>(parentPath);
            this.path.add(this);
            this.totalPoints = 0;
            this.nTurns = 0;
            this.mctsId = mctsId;
            this.mctsHand = mctsHand;
            this.rng = rng;
            this.inexistentChildren = (NodePlayer().equals(mctsId)) ?
                    turnState.trick().playableCards(mctsHand.intersection(turnState.unplayedCards())) :
                        turnState.trick().playableCards(turnState.unplayedCards().difference(mctsHand));
            this.children = new Node[inexistentChildren.size()];
            this.allCards = new Card[inexistentChildren.size()];
        }

        /**
         * Méthode principale et récursive qui développe l'algorithme
         * (ajoute un nouveu noeud fils si tous les chemins n'étaient pas encore épuisés. Sinon, aplique la méthode sur
         *  le « meilleur » fils d'un nœud, c-à-d celui dont la valeur V est la plus élevée)
         */
        private void runNode() {
            if(hasAllChildren()) { 
                bestChild().runNode();
            }
            else {
                addNode(firstCard());
            }
        }

        /**
         * Méthode auxilière qui ajoute un noeud (modifie les attributs du noeud en conséquent)
         * @param card
         *      La carte sur l'arrêt menant à ce noeud
         */
        private void addNode(Card card) {
            if(!(turnState.withNewCardPlayedAndTrickCollected(card).isTerminal())) {


                int index = 0;
                while(children[index] != null && index < children.length) {
                    index++;
                }
                allCards[index] = card;
                children[index] = new Node(turnState.withNewCardPlayedAndTrickCollected(card),path,mctsId,mctsHand,rng);
                inexistentChildren = inexistentChildren.remove(card);
                children[index].updateScores();
            }
        }

        /**
         * Méthode auxilière qui simule un tour aléatoire
         * @return
         *       le score d'un noeud après une simulation
         */
        private Score simulateTurn() {
            //copie défensive du turnState
            TurnState simulatedTurnState = turnState;
            //les cartes que mctsPlayer peut jouer
            CardSet playerPossibleCards;
            //les autres cartes que les autres joueurs pouront jouer
            CardSet otherPossibleCards;
            Card randomCard;
            //Arrête la simulation ssi le tour est fini
            while(!simulatedTurnState.isTerminal()) {
                //Tant que le pli n'est pas plein il ajoute une carte jouable aleatoire en fonction du joueur
                while(!simulatedTurnState.trick().isFull()) {
                    playerPossibleCards = simulatedTurnState.trick().playableCards(mctsHand.intersection(simulatedTurnState.unplayedCards()));
                    otherPossibleCards =  simulatedTurnState.trick().playableCards(simulatedTurnState.unplayedCards().difference(mctsHand));
                    randomCard=(simulatedTurnState.trick().player(simulatedTurnState.trick().size()).equals(mctsId)) ?
                            playerPossibleCards.get(rng.nextInt(playerPossibleCards.size())) :
                                otherPossibleCards.get(rng.nextInt(otherPossibleCards.size()));
                            simulatedTurnState = simulatedTurnState.withNewCardPlayed(randomCard);
                }
                //ramasse si c'est pasle dernier tour mais met à jour le score en tous cas
                if(simulatedTurnState.trick().index() == 8) {
                    try{simulatedTurnState.withTrickCollected().score();}
                    catch (Exception e) {
                        return simulatedTurnState.score();
                    }
                    simulatedTurnState = simulatedTurnState.withTrickCollected();
                    break;
                }else {
                    simulatedTurnState = simulatedTurnState.withTrickCollected();
                    //change au debut du pli les cartes qu'on peut jouer
                }
            }
            return simulatedTurnState.score();
        }

        /**
         * Méthode auxilière qui met à jour tous les scores (du noeud à tous ces encêtres)
         */
        private void updateScores() {
            Score score = simulateTurn();
            for(Node parent : path) {
                parent.totalPoints += score.turnPoints(mctsId.team());
                ++parent.nTurns;
            }
        }

        /**
         * 
         * @return
         *      Le fils avec le plus grand score
         *      (celui dont la valeur V est la plus élevée)
         */
        public Node bestChild() {
            double maxValue = calculateChildCoefficient(children[0]);
            Node maxChild = children[0];

            for(int i = 1; i < children.length; ++i){
                double childCoefficient = calculateChildCoefficient(children[i]);
                if(childCoefficient > maxValue){
                    maxValue = childCoefficient;
                    maxChild = children[i];
                }
            }
            return maxChild;
        }


        public double calculateChildCoefficient(Node child) {
            return (child.nTurns <= 0) ? Double.MAX_VALUE : finalScore() + 40 * Math.sqrt((2 * Math.log(nTurns)) / child.nTurns);
        }

        /**
         * 
         * @return
         *      un booléen (vrai si tous ces fils ont été "épuisés", faux sinon)
         */
        private boolean hasAllChildren() {
            return inexistentChildren.isEmpty();
        }

        /**
         * 
         * @return
         *      un booléen (vrai si le noeud fils est le joueur simulé, faux sinon)
         */
        private PlayerId NodePlayer() {
            return turnState.trick().player(turnState.trick().size());
        }

        /**
         * 
         * @return
         *      Le score moyen (le score total / le nombre de tours simulés)
         */
        private double finalScore() {
            return ((double)totalPoints)/nTurns;
        }

        /**
         * 
         * @return
         *      Le prochain enfant du noeud à ajouter
         */
        private Card firstCard() {
            return inexistentChildren.get(0);
        }
        
        /**
         * Méthode finale qui choisit le fils avec le plus grand score
         * @return
         *      L'index du fils avec le plus grand finalScore
         */
        public int chosenChildIndex() {
            double maxValue = (children[0]).finalScore();
            int indice = 0;
            for(int i = 1; i < children.length; ++i){
                double childCoefficient = (children[i]).finalScore();
                if(childCoefficient > maxValue){
                    maxValue = childCoefficient;
                    indice = i; 
                }
            }
            return indice;
        }
    }
}
