package ch.epfl.javass.jass;
import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class PackedScore {
    public static final long INITIAL = 0L;

    private PackedScore() {}
    
    /**
     * Méthode utilisée pour s'assurer que la valeur donnée est un score empaqueté valid
     * 
     * @param pkScore
     *      Le score sous forme empaquetée
     * @return
     *      Un booléen (vrai si  les six composantes contiennent des valeurs comprises dans leurs bornes précisées
     *      et les bits inutilisés valent tous 0, faux sinon)
     */     
    public static boolean isValid(long pkScore) {
        long validTotalTricks = Bits64.extract(pkScore, 0, 4) + Bits64.extract(pkScore, 32, 4);

        return validTotalTricks <= Jass.TRICKS_PER_TURN && teamIsValid(pkScore, TeamId.TEAM_1) && teamIsValid(pkScore, TeamId.TEAM_2);
    }

    
    /**
     * Méthode permettant de manipuler les groupes de 32 bits contenant les scores d'une équipe,
     * 
     * @param pkScore
     *      le score sous forme empaquetée
     * @param t
     *      L'équipe testée   
     * @return
     */
    private static boolean teamIsValid(long pkScore, TeamId t) {
        int teamStart = t.equals(TeamId.TEAM_1) ? 0 : 32;
        
        long pkTurnTricks = Bits64.extract(pkScore, teamStart, 4);
        long pkTurnPoints = Bits64.extract(pkScore, teamStart+4, 9);
        long pkGamePoints = Bits64.extract(pkScore, teamStart+13, 11);
        long unusedBits = Bits64.extract(pkScore, teamStart+24, 8);
        
        boolean validTurnTricks = pkTurnTricks >= 0 && pkTurnTricks <= 9;
        boolean validTurnPoints = pkTurnPoints >= 0 && pkTurnPoints <= 257;
        boolean validGamePoints = pkGamePoints >= 0 && pkGamePoints <= 2000;
        boolean validUnusedBits = unusedBits == 0;
        return (validTurnTricks && validTurnPoints && validGamePoints && validUnusedBits);
    }

    
    /**
     * La méthode qui empaquète les six composantes des scores dans un entier de type long.
     * 
     * @param turnTricks1
     *      Le nombre de pli remportés par l'équipe 1 dans le tour courant
     * @param turnPoints1
     *      Le nombre de points remportés par l'équipe 1 dans le tour courant
     * @param gamePoints1
     *      Le nombre de points reportés par l'équipe 1 dans les tours précédents (sans inclure le tour courant)
     * @param turnTricks2
     *      Le nombre de pli remportés par l'équipe 2 dans le tour courant
     * @param turnPoints2
     *      Le nombre de points remportés par l'équipe 2 dans le tour courant
     * @param gamePoints2
     *      Le nombre de points reportés par l'équipe 2 dans les tours précédents (sans inclure le tour courant)
     * @return
     *      Le score sous sa forme epaquetée
     */     
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
        int pkScoreTeam1 = Bits32.pack(turnTricks1, 4, turnPoints1, 9, gamePoints1, 11);
        int pkScoreTeam2 = Bits32.pack(turnTricks2, 4, turnPoints2, 9, gamePoints2, 11);
        return Bits64.pack(pkScoreTeam1, 32, pkScoreTeam2, 32);
    }

    
    /**
     * 
     * @param pkScore
     *      Le score sous sa forme empaquetée
     * @param t
     *      L'équipe pour laquelle on veut savoir le nombre de pli remportés   
     * @return
     *      Le nombre de pli remportés par l'équipe t sachant que pkScore est le score sous sa forme empaquetée
     */      
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore);

        return Bits32.extract(pkScoreTeam(pkScore, t), 0, 4);
    }
    
    
    /**
     * 
     * @param pkScore
     *      Le score sous sa forme empaquetée
     * @param t
     *      L'équipe pour laquelle on veut savoir le nombre de points remportés
     * @return
     *      Le nombre de points remportés par l'équipe t sachant que pkScore est le score sous sa forme empaqueté
     */     
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);

        return Bits32.extract(pkScoreTeam(pkScore, t), 4, 9);
    }

    
    /**
     * 
     * @param pkScore
     *      Le score sous sa forme empaquetée
     * @param t
     *      L'équipe pour laquelle on veut savoir le nombre de points reportés 
     *      par l'équipe donnée dans les tours précédents (sans inclure le tour courant)
     * @return
     *      Le nombre de points reportés par l'équipe donnée dans les tours précédents (sans inclure le tour courant) de
     *      l'équipe t sachant que pkScore est le score sous sa forme empaquetée
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore);

        return Bits32.extract(pkScoreTeam(pkScore, t), 13, 11);
    }

    
    /**
     * Méthode auxilière qui extracte le score de l'équipe t de pkScore
     * @param pkScore
     *      Le score sous sa forme empaquetée
     * @param t
     *      L'équipe pour laquelle on veut extraire le score
     * @return
     *      Le score de l'équipe t uniquement sous forme de int (integer)
     */
    private static int pkScoreTeam(long pkScore, TeamId t) {
        int teamBitsStart = t.equals(TeamId.TEAM_1) ? 0 : 32;

        return (int) Bits64.extract(pkScore, teamBitsStart, 32);
    }

    
    /**
     * 
     * @param pkScore
     *      Le score sous sa forme empaquetée
     * @param t
     *      L'équipe pour laquelle on veut savoir le nombre total de points remportés par l'équipe donnée dans la partie courante 
     * @return
     *      La somme des points remportés dans les tours précédents et ceux remportés dans le tour courant par l'équipe t
     */
    public static int totalPoints(long pkScore, TeamId t) {
        return gamePoints(pkScore, t)+turnPoints(pkScore, t);
    }

    
    /**
     * 
     * @param pkScore
     *      Le score sous forme empaquetée
     * @param winningTeam
     *      L'équipe gagnant le pli
     * @param trickPoints
     *      La valeur totale du pli
     * @return
     *      Les scores empaquetés donnés mis à jour pour tenir compte du fait que l'équipe winningTeam a remporté un pli valant trickPoints.
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        int winnerTurnTricks = turnTricks(pkScore, winningTeam)+1;
        int winnerTurnPoints = turnPoints(pkScore, winningTeam)+trickPoints;
        if(winnerTurnTricks == Jass.TRICKS_PER_TURN) {
            winnerTurnPoints += Jass.MATCH_ADDITIONAL_POINTS;
        }
        int winnerGamePoints = gamePoints(pkScore, winningTeam);
        
        int pkWinnerUpdated = Bits32.pack(winnerTurnTricks, 4, winnerTurnPoints, 9, winnerGamePoints, 11);
        int pkLoser = pkScoreTeam(pkScore, winningTeam.other());
        
        return winningTeam.equals(TeamId.TEAM_1) ? Bits64.pack(pkWinnerUpdated, 32, pkLoser, 32) : Bits64.pack(pkLoser, 32, pkWinnerUpdated, 32);
    }

    
    /**
     * Méthode qui calcule les points obtenus par chaque équipe dans le tour courant ajoutés à leur nombre de
     *        points remportés lors de la partie, et retourne les deux autres composantes remises à 0,
     * 
     * @param pkScore
     *      Le score sous forme empaquetée
     * @return
     *      Les scores empaquetés donnés mis à jour pour le tour prochain
     */
    public static long nextTurn(long pkScore) {
        int turnPoints1 = turnPoints(pkScore, TeamId.TEAM_1);
        int turnPoints2 = turnPoints(pkScore, TeamId.TEAM_2);
        int newGamePoints1 = gamePoints(pkScore, TeamId.TEAM_1)+turnPoints1;
        int newGamePoints2 = gamePoints(pkScore, TeamId.TEAM_2)+turnPoints2;

        return pack(0, 0, newGamePoints1, 0, 0, newGamePoints2);
    }
    
    
    /**
     * 
     * @param pkScore
     *      Le score sous forme epaquetée
     * @return
     *      Une représentation textuelle du score
     */
    public static String toString(long pkScore) {
        return "("+turnTricks(pkScore, TeamId.TEAM_1)+","+turnPoints(pkScore, TeamId.TEAM_1)+","+gamePoints(pkScore, TeamId.TEAM_1)+")/("
                +turnTricks(pkScore, TeamId.TEAM_2)+","+turnPoints(pkScore, TeamId.TEAM_2)+","+gamePoints(pkScore, TeamId.TEAM_2)+")";
    }
}
