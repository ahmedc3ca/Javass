package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public enum PlayerId {
    PLAYER_1(),
    PLAYER_2(),
    PLAYER_3(),
    PLAYER_4();
    
    public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 4;
    
    
    /**
     * 
     * @return
     *       L'équipe à laquelle appartient le joueur auquel on l'applique
     */     
    public TeamId team() {
        switch(this) {
        case PLAYER_1:
        case PLAYER_3: return TeamId.TEAM_1;
        case PLAYER_2:
        case PLAYER_4: return TeamId.TEAM_2;
        default: return null;
        }
    }
}
