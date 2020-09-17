package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * Lance le serveur
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class RemoteMain extends Application {

    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread gameThread = new Thread(() -> {
            try{
                RemotePlayerServer server = new RemotePlayerServer(new GraphicalPlayerAdapter());
                
                server.run();
            }catch (Exception e) {System.out.println(e);}
        }
                );
        gameThread.setDaemon(true);
        System.out.println("La partie commencera à la connexion du client...");
        gameThread.start();
    }
}
