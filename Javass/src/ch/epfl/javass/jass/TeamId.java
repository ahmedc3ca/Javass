package ch.epfl.javass.jass;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public enum TeamId {
    TEAM_1(),
    TEAM_2();

    public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 2;

    
    /**
     * 
     * @return
     *      L'équipe adverse
     */         
    public TeamId other() {
        return this.equals(TeamId.TEAM_1) ? TEAM_2 : TEAM_1;
    }
}
