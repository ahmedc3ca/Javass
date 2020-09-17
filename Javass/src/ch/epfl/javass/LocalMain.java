package ch.epfl.javass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class LocalMain extends Application {

    private Map<PlayerId, String> defaultNames;
    private final Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
    private final List<Long> seeds = new ArrayList<>();
    //Définition des constantes
    private final static int S_PLAYER_TYPE_POS = 0;
    private final static int S_NAME_POS = 1;
    private final static int S_IP_POS = 2;
    private final static int S_NITERATIONS_POS = 2;
  
    
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Random generalRandom = new Random();

        //Crée la table non modifiable qui définit les noms par défaut de chaque joueuer
        Map<PlayerId, String> defaultNames = new EnumMap<>(PlayerId.class);
        defaultNames.put(PlayerId.PLAYER_1, "Aline");
        defaultNames.put(PlayerId.PLAYER_2, "Bastien");
        defaultNames.put(PlayerId.PLAYER_3, "Colette");
        defaultNames.put(PlayerId.PLAYER_4, "David");
        this.defaultNames = Collections.unmodifiableMap(defaultNames);
        
        List<String> parameters = getParameters().getRaw();
        int parametersSize = parameters.size();

        // (1) Teste s'il y a un nombre valide d'arguments
        if( parametersSize < 4 || parametersSize > 5)
            throw new HelpfulException("Erreur : nombre d'arguments invalide : " + parametersSize);

        // (2) Teste si la graine de génération est valide
        if(parametersSize == 5) {
            try {
                long generalRandomSeed = Long.parseLong(parameters.get(4));
                generalRandom = new Random(generalRandomSeed);
            }catch (NumberFormatException e) {
                throw new HelpfulException("Erreur : spécification de la graine invalide : "+ parameters.get(4));
            }
        }

        //Défini toutes les graines dans l'ordre
        for(int i = 0; i < 5; ++i) {
            seeds.add(generalRandom.nextLong());
        }

        // (3) Teste si les 4 premiers arguments (les joueurs) sont valides, et les ajoute s'ils le sont
        for(int i = 0; i < PlayerId.COUNT; ++i) {
            String[] components = parameters.get(i).split(":");

            createPlayer(PlayerId.ALL.get(i), components);
        }

        //Lance le fil d'exécution
        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(seeds.get(0), players, playerNames);
            while (! g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    
    /**
     * Enumêration des différents types de joueuers
     * @author Arnaud Poletto (302411)
     * @author Ahmed Ezzo (299897)
     */
    private static enum PlayerType {
        Human("h", 2),
        Simulated("s", 3),
        Ranged("r", 3);

        private final String s;
        private final int maxComponentSize;

        /**
         * 
         * @param s
         *      Le type de joueuer (h, s ou r resp. humain, simulé ou distant)
         * @param maxComponentSize
         *      Le nombre maximum de composants admis
         */
        private PlayerType(String s, int maxComponentSize) {
            this.s = s;
            this.maxComponentSize = maxComponentSize;
        }

        /**
         * 
         * @param name
         *      Nom du type du joueur (h, s ou r)
         * @return
         *      Le type de joueuer correspondant à name (null s'il ne correspond à aucun type)
         */
        private static PlayerType fromName(String name) {
            for(PlayerType type : PlayerType.values()) {
                if(type.s.equals(name))
                    return type;
            }
            return null;
        }
    }

    /**
     * Méthode auxilaire créant un joueur.
     * @param player
     *      L'identité du joueur
     * @param s
     *      Les composants utilisés pour la construction du joueur
     * @throws HelpfulException
     *      Un message d'erreur si les arguments donnés sont invalides
     */
    private void createPlayer(PlayerId player, String[] s) throws HelpfulException {
        PlayerType type = PlayerType.fromName(s[S_PLAYER_TYPE_POS]);
        
        //Lance une exception si le type est invalide
        if(type == null)
            throw new HelpfulException("Erreur : spécification du type du joueur invalide : " + s[S_PLAYER_TYPE_POS]);
        
        //Lance une exception si le nombre de composants est invalide
        if(s.length > type.maxComponentSize)
            throw new HelpfulException("Erreur : spécification d'un joueur "+ type.name() +" invalide, trop de composants : "+s.length+" > "+ type.maxComponentSize);
        
        //Définit le nom du joueur
        playerNames.put(player, (s.length == 1 || s[S_NAME_POS].equals("") ) ? defaultNames.get(player) : s[S_NAME_POS]);
        
        switch (type) {
        case Human:
            createHuman(player, s);
            break;
        case Simulated:
            createSimulated(player, s);
            break;
        case Ranged: 
            createRanged(player, s);
            break;
        }
    }

    /**
     * Méthode auxilaire créant un joueur humain.
     * @param player
     *      L'identité du joueur
     * @param s
     *      Les composants utilisés pour la construction du joueur
     * @throws HelpfulException
     *      Un message d'erreur si les arguments donnés sont invalides
     */
    private void createHuman(PlayerId player, String[] s) throws HelpfulException {
        players.put(player, new GraphicalPlayerAdapter());
    }

    /**
     * Méthode auxilaire créant un joueur simulé.
     * @param player
     *      L'identité du joueur
     * @param s
     *      Les composants utilisés pour la construction du joueur
     * @throws HelpfulException
     *      Un message d'erreur si les arguments donnés sont invalides
     */
    private void createSimulated(PlayerId player, String[] s) throws HelpfulException {
        int sIterations;

        try {
            sIterations = (s.length != PlayerType.Simulated.maxComponentSize) ? 10000 : Integer.parseInt(s[S_NITERATIONS_POS]);
        }catch(NumberFormatException e) {
            throw new HelpfulException("Erreur : spécification du nombre d'itérations pour un joueur simulé invalide : "+s[S_NITERATIONS_POS]);
        }

        if(sIterations < 10)
            throw new HelpfulException("Erreur : spécification du nombre d'itérations pour un joueur simulé invalide, nombre trop petit : "+sIterations+" < 10");

        players.put(player, new PacedPlayer(new MctsPlayer(player, seeds.get(player.ordinal()+1), sIterations), 0));

    }

    /**
     * Méthode auxilaire créant un joueur distant.
     * @param player
     *      L'identité du joueur
     * @param s
     *      Les composants utilisés pour la construction du joueur
     * @throws HelpfulException
     *      Un message d'erreur si les arguments donnés sont invalides
     */
    private void createRanged(PlayerId player, String[] s) throws HelpfulException {

        try {
            players.put(player, new RemotePlayerClient(s[S_IP_POS]));
        }catch(Exception e) {
            throw new HelpfulException("Erreur : le joueur distant n'arrive pas à se connecter avec l'adresse "+s[S_IP_POS]);
        }
    }

    /**
     * Exception renvoyant les messages d'erreurs personalisés, et arrête le programe.
     * @author Arnaud Poletto (302411)
     * @author Ahmed Ezzo (299897)
     */
    @SuppressWarnings("serial")
    private class HelpfulException extends Exception {

        public HelpfulException(String e) {
            System.err.println(e);
            System.err.println("Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>] où :\n" + 
                    "<jn> spécifie le joueur n, ainsi:\n" + 
                    "  h:<nom>  un joueur humain nommé <nom> \n"+
                    "  s:<nom>:<#itérations> un joueur simulé nommé <nom> avec <#itérations> itérations de l'algorithme MCTS \n"+
                    "  r:<nom>:<adresse IP>  un joueur disntant nommé <nom> dont le serveur s'exécute sur l'ordinateur dont l'adresse IP est <adresse IP> \n"+
                    "<graine> est la graine de génération aléatoire de la partie (optionelle)");
            System.exit(1);
        }
    }
}
