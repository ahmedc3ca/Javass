package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class ScoreBean {

    //Les points du tour des équipes (resp. team1 et team2)
    private final SimpleIntegerProperty team1TurnPoints, team2TurnPoints;
    //Les points de la partie des équipes (resp. team1 et team2)
    private final SimpleIntegerProperty team1GamePoints, team2GamePoints;
    //Le total des points des équipes (resp. team1 et team2)
    private final SimpleIntegerProperty team1TotalPoints, team2TotalPoints;
    //La team gagnante
    private final SimpleObjectProperty<TeamId> winningTeam;

    /**
     * Le constructeur de la classe, définit les attributs
     */
    public ScoreBean() {
        team1TurnPoints = new SimpleIntegerProperty(0);
        team2TurnPoints = new SimpleIntegerProperty(0);
        team1GamePoints = new SimpleIntegerProperty(0);
        team2GamePoints = new SimpleIntegerProperty(0);
        team1TotalPoints = new SimpleIntegerProperty(0);
        team2TotalPoints = new SimpleIntegerProperty(0);
        winningTeam = new SimpleObjectProperty<>(null);
    }
    
    /**
     * 
     * @param team
     *      L'équipe à laquelle on veut voir les points.
     * @return
     *      Les points du tour de l'équipe team.
     */
    ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return (team == TeamId.TEAM_1)? 
                (ReadOnlyIntegerProperty) team1TurnPoints :
                    (ReadOnlyIntegerProperty)team2TurnPoints;            
    }
    
    /**
     * 
     * @param team
     *      L'équipe à laquelle on veut définir les points.
     * @param newTurnPoints
     *      Le nombre de points que l'on veut définir.
     */
    void setTurnPoints(TeamId team, int newTurnPoints) {
        if(team == TeamId.TEAM_1) 
            team1TurnPoints.set(newTurnPoints);
        else 
            team2TurnPoints.set(newTurnPoints);
    }
    
    /**
     * 
     * @param team
     *      L'équipe à laquelle on veut voir les points.
     * @return
     *      Les points du jeu de l'équipe team.
     */
   ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
       return (team == TeamId.TEAM_1)? 
               (ReadOnlyIntegerProperty) team1GamePoints:
                   (ReadOnlyIntegerProperty) team2GamePoints;
    }
   
   /**
    * 
    * @param team
    *       L'équipe à laquelle on veut définir les points.
    * @param newGamePoints
    *       Le nombre de points que l'on veut définir.
    */
    void setGamePoints(TeamId team, int newGamePoints) {
        if(team == TeamId.TEAM_1)
            team1GamePoints.set(newGamePoints);
        else
            team2GamePoints.set(newGamePoints);
    }

    /**
     * 
     * @param team
     *      L'équipe à laquelle on veut voir les points.
     * @return
     *      Les points du totaux de l'équipe team.
     */
    ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return (team == TeamId.TEAM_1)? 
                (ReadOnlyIntegerProperty) team1TotalPoints:
                    (ReadOnlyIntegerProperty) team2TotalPoints;
    }
    
    /**
     * 
     * @param team
     *       L'équipe à laquelle on veut définir les points.
     * @param newTotalPoints
     *       Le nombre de points que l'on veut définir.
     */
    void setTotalPoints(TeamId team, int newTotalPoints) {
        if(team == TeamId.TEAM_1)
            team1TotalPoints.set(newTotalPoints);
        else
            team2TotalPoints.set(newTotalPoints);
    }
    
    /**
     * 
     * @return
     *      L'équipe gagnante (null si le jeu n'est pas terminé)
     */
    ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return (ReadOnlyObjectProperty<TeamId>) winningTeam;
    }
    
    /**
     * Définit l'équipe gagnante.
     * @param winningTeam
     *      L'équipe gagnante.
     */
    void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }
}
