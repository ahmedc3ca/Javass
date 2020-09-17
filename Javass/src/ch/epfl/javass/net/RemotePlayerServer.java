package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class RemotePlayerServer{
    
    //Le joueur local
    private Player player;
    
    
    /**
     * Constructeur de RemotePlayerServer
     * @param player
     *      le Joueur local du serveur
     */
    public RemotePlayerServer(Player player){
        this.player = player;
    }

    /**
     * Cette méthode dans une boucle infinie : attend un message du client,
     * appelle la méthode correspondante du joueur local, 
     * et dans le cas de cardToPlay, renvoie la valeur de retour au client.
     * 
     * @throws UncheckedIOException
     *      quand la méthode détecte un IOException
     */
    public void run(){

        try (ServerSocket s0 = new ServerSocket(5108);
                Socket s = s0.accept();
                BufferedReader r =
                        new BufferedReader(
                                new InputStreamReader(s.getInputStream(),
                                        US_ASCII));
                BufferedWriter w =
                        new BufferedWriter(
                                new OutputStreamWriter(s.getOutputStream(),
                                        US_ASCII))) {
            while(true) {
                String message = r.readLine();
                String[] splittedMessage = StringSerializer.spaceSeparator(message);
                JassCommand command = JassCommand.valueOf(splittedMessage[0]);
                switch(command) {

                case PLRS: 
                    Map<PlayerId, String> playerNames = new EnumMap<PlayerId, String>(PlayerId.class);
                    String[] separatedNames = StringSerializer.stringSeparator(splittedMessage[2]);
                    for(int i = 0; i < separatedNames.length ; ++i) 
                        playerNames.put(PlayerId.ALL.get(i),StringSerializer.deserializeString(separatedNames[i]));
                    player.setPlayers(PlayerId.ALL.get(StringSerializer.deserializeInt(splittedMessage[1])), playerNames) ;
                    break;

                case TRMP :
                    player.setTrump(Color.ALL.get(StringSerializer.deserializeInt(splittedMessage[1]))) ;
                    break;

                case HAND :
                    CardSet newHand = CardSet.ofPacked(StringSerializer.deserializeLong(splittedMessage[1]));
                    player.updateHand(newHand);
                    break;

                case TRCK :
                    Trick newTrick = Trick.ofPacked(StringSerializer.deserializeInt(splittedMessage[1]));
                    player.updateTrick(newTrick);
                    break;

                case CARD :
                    String[] stateStats = StringSerializer.stringSeparator(splittedMessage[1]);

                    TurnState state = TurnState.ofPackedComponents(StringSerializer.deserializeLong(stateStats[0]),
                            StringSerializer.deserializeLong(stateStats[1]),
                            StringSerializer.deserializeInt(stateStats[2]));
                    CardSet hand = CardSet.ofPacked(StringSerializer.deserializeLong(splittedMessage[2]));
                    Card c = player.cardToPlay(state, hand);
                    w.write(StringSerializer.serializeInt(c.packed()));
                    w.write('\n');
                    w.flush();
                    break;

                case SCOR :
                    Score score = Score.ofPacked(StringSerializer.deserializeLong(splittedMessage[1]));
                    player.updateScore(score);
                    break;

                case WINR :
                    player.setWinningTeam(TeamId.ALL.get(StringSerializer.deserializeInt(splittedMessage[1])));
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }


    }
}
