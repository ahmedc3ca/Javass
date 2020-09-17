package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
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
public final class RemotePlayerClient implements Player, AutoCloseable {

    //Attributs de connexion au serveur
    private Socket s;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * 
     * @param ip
     *      Adresse sur laquelle le client se connecte 
     * @throws IOException
     *      Quand le client ne peut pas se connecter au serveur.
     */
    public RemotePlayerClient(String ip) throws IOException {
            this.s = new Socket(ip, 5108);
            writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(),US_ASCII));
            reader = new BufferedReader(new InputStreamReader(s.getInputStream(),US_ASCII));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
            String score = StringSerializer.serializeLong(state.packedScore());
            String UnplayedCards = StringSerializer.serializeLong(state.packedUnplayedCards());
            String trick = StringSerializer.serializeInt(state.packedTrick());
            write(JassCommand.CARD.name()+" "+StringSerializer.stringCombiner(score,UnplayedCards,trick) +" "+ StringSerializer.serializeLong(hand.packed()));
            write("\n");
            flush();
            String message = readLine();
            return Card.ofPacked(StringSerializer.deserializeInt(message));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            String[] names = new String[4];
            
            
            for (Map.Entry<PlayerId, String> e: playerNames.entrySet())
                    names[e.getKey().ordinal()] = StringSerializer.serializeString(e.getValue());
            
            
            String name = StringSerializer.stringCombiner(names[0], names[1], names[2], names[3]);
            write(JassCommand.PLRS.name()+" "+ ownId.ordinal() +" "+ name);
            write("\n");
            flush();


    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
            write(JassCommand.TRMP.name()+" "+StringSerializer.serializeInt(trump.ordinal()));
            write("\n");
            flush();        


    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
            write(JassCommand.HAND.name()+" "+ StringSerializer.serializeLong(newHand.packed()));
            write("\n");
            flush();

    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
            write(JassCommand.TRCK.name()+" "+ StringSerializer.serializeInt(newTrick.packed()));
            write("\n");
            flush();
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
            write(JassCommand.SCOR.name() + " " + StringSerializer.serializeLong(score.packed()));
            write("\n");
            flush();
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
            write(JassCommand.WINR.name()+ " "+StringSerializer.serializeInt(winningTeam.ordinal()));
            write("\n");
            flush();
    }

    /* (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
        s.close();
    }

    /**
     * Méthode appelant flush() sur le writer, et gère ses exceptions
     */
    private void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /**
     * Méthode appelant write() sur le writer, et gère ses exceptions
     * @param s
     *      Message à écrire
     */
    private void write(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
       
    }
    
    /**
     * Méthode appeland readLine() sur le reader, et gère ses exceptions
     * @return
     */
    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
