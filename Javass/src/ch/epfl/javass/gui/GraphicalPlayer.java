package ch.epfl.javass.gui;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class GraphicalPlayer {

    //La différence du point du tour de chaque équipe
    private final SimpleIntegerProperty scoreDiff1, scoreDiff2;
    //Les trois panneaux de la fenêtre principale
    private final Pane scorePane, trickPane, handPane;
    //Les deux panneaux de victoire
    private final StackPane victoryPane;
    //trois maps observables qui relient entre la carte/trump à son image
    private final ObservableMap<Card, Image> cards_240, cards_160;
    private final ObservableMap<Color, Image> trumps;
    //La queue de communication avec le fil secondaire
    private ArrayBlockingQueue<Card> queue;
    //Les consantes utiles dans cette classe
    private static final int IMAGE_CARD_TRICK_WIDTH = 120;
    private static final int IMAGE_CARD_TRICK_HEIGHT = 180;
    private static final int IMAGE_CARD_HAND_WIDTH = 80;
    private static final int IMAGE_CARD_HAND_HEIGHT = 120;
    private static final int IMAGE_TRUMP_SIZE = 101;

    /**
     * Constructeur de la méthode qui se charge de construire la quasi-totalité de l'interface graphique, à l'exception de la fenêtre elle-même
     * @param ownId
     *      L'identité du joueur auquel l'interface correspond
     * @param playerNames
     *      Une table associative associant les noms des joueurs à leur identité
     * @param queue
     *      La queue de communication avec le fil secondaire
     * @param scoreBean
     *      Le bean du score
     * @param trickBean
     *      Le bean du pli
     * @param handBean
     *      Le bean de la main
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames, ArrayBlockingQueue<Card> queue,
            ScoreBean scoreBean, TrickBean trickBean, HandBean handBean) {

        //Initialisation de trois Maps qui relient la carte/trump à son image
        Map<Card, Image> imageOfCard_160 = new HashMap<>();
        imageOfCard_160.put(null, null);
        for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 9; ++j)
                imageOfCard_160.put(Card.of(Color.ALL.get(i), Rank.ALL.get(j)), new Image("/card_"+i+"_"+j+"_160.png"));
        }

        Map<Card, Image> imageOfCard_240 = new HashMap<>();
        imageOfCard_240.put(null, null);
        for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 9; ++j)
                imageOfCard_240.put(Card.of(Color.ALL.get(i), Rank.ALL.get(j)), new Image("/card_"+i+"_"+j+"_240.png"));
        }

        Map<Color, Image> imageOfTrump = new HashMap<>();
        imageOfTrump.put(null, null);
        for(int i = 0; i < 4; ++i)
            imageOfTrump.put(Color.ALL.get(i), new Image("/trump_"+i+".png"));

        //ces trois maps observables
        cards_160 = FXCollections.observableMap(imageOfCard_160);
        cards_240 = FXCollections.observableMap(imageOfCard_240);
        trumps = FXCollections.observableMap(imageOfTrump);

        //Définition de la différence de score (de l'équipe 1 et 2 resp.)
        scoreDiff1 = new SimpleIntegerProperty(0);
        scoreDiff2 = new SimpleIntegerProperty(0);
        
        this.queue = queue;
        this.handPane = createHandPane(handBean);
        this.scorePane = createScorePane(scoreBean, playerNames);
        this.trickPane = createTrickPane(trickBean, ownId, playerNames);
        this.victoryPane = createVictoryPanes(scoreBean, playerNames);
    }

    /**
     * 
     * @return
     *      La fenêtre principale du jeu
     */
    public Stage createStage() {
        Stage stage = new Stage();

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(scorePane);
        mainPane.setCenter(trickPane);
        mainPane.setBottom(handPane);

        StackPane stackPane = victoryPane;
        stackPane.getChildren().add(0, mainPane);

        stage.setScene(new Scene(stackPane));
        return stage;
    }

    /**
     * Méthode auxilière qui s'occupe de la construction du panneau du score
     * 
     * @param scoreBean
     *      Le bean du score
     * @param playerNames
     *      Une table associative associant les noms des joueurs à leur identité
     * @return
     *      le panneau du score
     */
    private Pane createScorePane(ScoreBean scoreBean, Map<PlayerId, String> playerNames) {
        GridPane scorePane = new GridPane();
        //Définition de chaque "Text"
        Text team1 = new Text(playerNames.get(PlayerId.ALL.get(0))+ " et "+ playerNames.get(PlayerId.ALL.get(2))+" : ");
        Text team2 = new Text(playerNames.get(PlayerId.ALL.get(1))+ " et "+ playerNames.get(PlayerId.ALL.get(3))+" : ");
        Text turnPoints1 = new Text();
        Text turnPoints2 = new Text();
        Text turnPointsDiff1 = new Text();
        Text turnPointsDiff2 = new Text();
        Text gamePoints1 = new Text();
        Text gamePoints2 = new Text();

        //Liaison de chaque "Text" à sa propriété
        turnPoints1.textProperty().bind(Bindings.convert(scoreBean.turnPointsProperty(TeamId.TEAM_1)));
        turnPoints2.textProperty().bind(Bindings.convert(scoreBean.turnPointsProperty(TeamId.TEAM_2)));
        turnPointsDiff1.textProperty().bind(Bindings
                .when(scoreDiff1.lessThan(1))
                .then("")
                .otherwise(Bindings.concat(" (+", scoreDiff1, ")")));
        turnPointsDiff2.textProperty().bind(Bindings
                .when(scoreDiff2.lessThan(1))
                .then("")
                .otherwise(Bindings.concat(" (+", scoreDiff2, ")")));
        gamePoints1.textProperty().bind(Bindings.convert(scoreBean.gamePointsProperty(TeamId.TEAM_1)));
        gamePoints2.textProperty().bind(Bindings.convert(scoreBean.gamePointsProperty(TeamId.TEAM_2)));

        //Ajout de chaque "Text" au panneau dans sa place correspondante
        scorePane.add(team1, 0, 0);
        scorePane.add(team2, 0, 1);
        scorePane.add(turnPoints1, 1, 0);
        scorePane.add(turnPoints2, 1, 1);
        scorePane.add(turnPointsDiff1, 2, 0);

        scorePane.add(turnPointsDiff2, 2, 1);
        scorePane.add(new Text("/Total :"), 3, 0);
        scorePane.add(new Text("/Total :"), 3, 1);
        scorePane.add(gamePoints1, 4, 0);
        scorePane.add(gamePoints2, 4, 1);

        //Mise en place du style du panneau
        scorePane.setStyle("-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px; -fx-alignment: center;");

        //EVENTS
        scoreBean.turnPointsProperty(TeamId.TEAM_1).addListener((o, oldValue, newValue) -> {
            scoreDiff1.set(newValue.intValue() - oldValue.intValue());
        });

        scoreBean.turnPointsProperty(TeamId.TEAM_2).addListener((o, oldValue, newValue) -> {
            scoreDiff2.set(newValue.intValue() - oldValue.intValue());
        });

        return scorePane;
    }

    /**
     * Méthode auxilière qui s'occupe de la construction du panneau du pli
     * 
     * @param trickBean
     *      Le bean du pli
     * @param ownId
     *      L'identité du joueur auquel l'interface correspond
     * @param playerNames
     *      Une table associative associant les noms des joueurs à leur identité
     * @return
     *      le panneau du pli
     */
    private Pane createTrickPane(TrickBean trickBean, PlayerId ownId, Map<PlayerId, String> playerNames) {
        GridPane trickPane = new GridPane();
        // Une map qiu relie entre le joueur et son VBox
        Map<PlayerId, VBox> playersNode = new EnumMap<>(PlayerId.class);

        // Boucle qui définit chaque représentation graphique des joueurs
        for(int i = 0; i < PlayerId.COUNT; ++i) {
            PlayerId thisPlayer = PlayerId.ALL.get(i);

            // Nom du joueuer
            Text name = new Text(playerNames.get(thisPlayer));
            name.setStyle("-fx-font: 14 Optima;");

            // représentation de la carte
            ImageView imageView = new ImageView();
            imageView.setFitWidth(IMAGE_CARD_TRICK_WIDTH);
            imageView.setFitHeight(IMAGE_CARD_TRICK_HEIGHT);

            imageView.imageProperty().bind(Bindings.valueAt(cards_240, Bindings.valueAt(trickBean.trick(), thisPlayer)));



            // Halo rouge qui apparît sur la carte la plus forte
            Rectangle rect = new Rectangle();
            rect.setWidth(IMAGE_CARD_TRICK_WIDTH);
            rect.setHeight(IMAGE_CARD_TRICK_HEIGHT);
            rect.setStyle("-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;");

            rect.visibleProperty().bind(trickBean.winningPlayerProperty().isEqualTo(thisPlayer));

            // un stackpane qui met le halo sur l'image de la carte
            StackPane cardPane = new StackPane(imageView,rect);
            // Un VBox qui met le nom sur le stackpane si c'est pas le stackpane du joueur auquel l'interface correspond
            // met le nom sous le stackpane sinon
            VBox cardAndPlayer = (thisPlayer == ownId)? new VBox(cardPane,name) : new VBox(name,cardPane);
            cardAndPlayer.setStyle("-fx-padding: 5px; -fx-alignment: center;");
            playersNode.put(thisPlayer, cardAndPlayer);
        }

        // représentation de l'atout
        ImageView trumpView = new ImageView();
        trumpView.setFitHeight(IMAGE_TRUMP_SIZE);
        trumpView.setFitWidth(IMAGE_TRUMP_SIZE);
        GridPane.setHalignment(trumpView, HPos.CENTER);

        trumpView.imageProperty().bind(Bindings.valueAt(trumps, trickBean.trumpProperty()));

        //Ajout de chaque image sur sa place correspondante sur le panneau
        int position = 3;
        trickPane.add(playersNode.get(PlayerId.ALL.get((ownId.ordinal()+ position--) % 4)), 0, 0, 1, 3);  //left player
        trickPane.add(playersNode.get(PlayerId.ALL.get((ownId.ordinal()+ position--) % 4)), 1, 0);        //top player
        trickPane.add(playersNode.get(PlayerId.ALL.get((ownId.ordinal()+ position--) % 4)), 2, 0 , 1, 3); // right player
        trickPane.add(playersNode.get(ownId), 1, 2);                                                      // bottom player
        trickPane.add(trumpView, 1, 1);  
        trickPane.setStyle("-fx-background-color: whitesmoke;-fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");

        return trickPane;
    }

    /**
     * Méthode auxilière qui s'occupe de la construction du panneau de la main
     * 
     * @param handBean
     *      Le bean de la main
     * @return
     *      Le panneau de la main
     */
    private Pane createHandPane(HandBean handBean) {
        HBox handPane = new HBox();
        ImageView[] allImageViews = new ImageView[Jass.HAND_SIZE];
        handPane.setStyle("-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px;");
        
        // Boucle qui définit la représentation graphique de chaque carte de la main du joueur
        for(int i = 0; i < Jass.HAND_SIZE; ++i) {
            int j =i;
            // représentation de la carte
            ImageView imageView = new ImageView();
            ObjectBinding<Card> card = Bindings.valueAt(handBean.hand(), i);
            imageView.setOnMouseClicked(e -> queue.add(card.getValue()));

            
            // Changement de l'opacité et si la carte est appuyable en fonction de si elle est jouable ou non
            BooleanBinding isPlayable = Bindings.createBooleanBinding(
                    () -> handBean.playableCards().contains(handBean.hand().get(j)),
                    card, handBean.playableCards()).and(handBean.handModified());

            imageView.imageProperty().bind(Bindings.valueAt(cards_160, card));
            imageView.opacityProperty().bind(Bindings.when(isPlayable)
                    .then(1)
                    .otherwise(0.2));
            imageView.disableProperty().bind(isPlayable.not());
            imageView.setFitWidth(IMAGE_CARD_HAND_WIDTH);
            imageView.setFitHeight(IMAGE_CARD_HAND_HEIGHT);

            allImageViews[i] = imageView;
        }

        handPane.getChildren().addAll(allImageViews);
        return handPane;
    }

    /**
     * Méthode auxilière qui s'occupe de la construction des deux panneaux de victoire
     * 
     * @param scoreBean
     *      Le bean du score
     * @param playerNames
     *      Une table associative associant les noms des joueurs à leur identité
     * @return
     *      Un StackPane contenant les deux panneaux de victoire
     */
    private StackPane createVictoryPanes(ScoreBean scoreBean, Map<PlayerId, String> playerNames) {
        //Composé des deux victory panes, seront invisibles devant le main pane jusqu'à la victoire d'une des équipes
        StackPane victoryPanes = new StackPane();
        BorderPane victoryPane1 = new BorderPane();
        BorderPane victoryPane2 = new BorderPane();

        Text team1Win = new Text();
        Text team2Win = new Text();

        // Lie à chaque "Text" sa propriété correspondante
        team1Win.textProperty().bind(Bindings.format("%s et %s ont gagné avec %d points contre %d", 
                playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_3),
                scoreBean.totalPointsProperty(TeamId.TEAM_1), scoreBean.totalPointsProperty(TeamId.TEAM_2)));
        team2Win.textProperty().bind(Bindings.format("%s et %s ont gagné avec %d points contre %d", 
                playerNames.get(PlayerId.PLAYER_2), playerNames.get(PlayerId.PLAYER_4),
                scoreBean.totalPointsProperty(TeamId.TEAM_2), scoreBean.totalPointsProperty(TeamId.TEAM_1)));
        victoryPane1.setStyle("-fx-font: 16 Optima; -fx-background-color: white;");
        victoryPane2.setStyle("-fx-font: 16 Optima; -fx-background-color: white;");

        victoryPane1.setCenter(team1Win);
        victoryPane2.setCenter(team2Win);
        victoryPane1.setVisible(false);
        victoryPane2.setVisible(false);

        // rend le panneau de l'équipe visible ssi elle est léquipe gagnante
        victoryPane1.visibleProperty().bind(Bindings.equal(scoreBean.winningTeamProperty() , TeamId.TEAM_1));
        victoryPane2.visibleProperty().bind(Bindings.equal(scoreBean.winningTeamProperty() , TeamId.TEAM_2));


        victoryPanes.getChildren().addAll(victoryPane1, victoryPane2);
        return victoryPanes;
    }
}