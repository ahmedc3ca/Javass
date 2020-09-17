package ch.epfl.javass.jass;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class Score {
    public static final Score INITIAL = new Score(0L);
    private final long packed;
    
    /**
     * 
     * @param packed
     *      La version empaquetée du score
     */
    private Score(long packed) {
        this.packed = packed;
    }
    
    
    /**
     * 
     * @param packed
     *      Le score sous forme empaqutée
     * @return
     *      Le score dont packed est la version empaquetée
     */
    public static Score ofPacked(long packed) {
        if(PackedScore.isValid(packed)) {
            return new Score(packed);
        }else {
            throw new IllegalArgumentException();
        }
    }
    
    
    /**
     * 
     * @return
     *      Le score sous forme empaquetée
     */
    public long packed() {
        return packed;
    }
    
    
    /**
     * 
     * @param t
     *      L'équipe pour laquelle on veut savoir le nombre de plis remportés dans le tour courant
     * @return
     *      Le nombre de plis remportés par l'équipe t
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packed(), t);
    }
    
    
    /**
     * 
     * @param t
     *      L'équipe pour laquelle on veut savoir le nombre de points remportés dans le tour courant
     * @return
     *      Le nombre de points remportés par l'équipe t
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packed(), t);
    }
    
    
    /**
     * 
     * @param t
     *    L'équipe pour laquelle on veut savoir lee nombre de points remportés dans les tours précédents
     *     (sans inclure le tour courant) du récepteur
     * @return
     *    Le nombre de points remportés par l'équipe t dans les tours précédents (sans inclure le tour courant) du réceptuer
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packed(), t);
    }
    
    
    /**
     * 
     * @param t
     *      L'équipe pour laquelle on veut savoir le nombre total de points remportés dans la partie courante du récepteur
     * @return
     *      Le nombre total de points remportés par l'équipe t dans la partie courante du récepteur,
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packed(), t);
    }
    
    
    /**
     * 
     * @param winningTeam
     *      L'équipe gagnante
     * @param trickPoints
     *      La valeur totale du plis
     * @return
     *      Les scores empaquetés donnés mis à jour pour tenir compte du fait que l'équipe winningTeam 
     *      a remporté un pli valant trickPoints.
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        if(trickPoints >= 0) {
            return new Score(PackedScore.withAdditionalTrick(packed(), winningTeam, trickPoints));
        }else {
            throw new IllegalArgumentException();
        }
    }
    
    
    /**
     * 
     * @return
     *      Les scores empaquetés donnés mis à jour pour le tour prochain
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(packed()));
    }
    
    /**
     * Méthode qui s'assure que le récepteur et le paramètre sont égaux
     * @return
     *      Un booleén (vrai si l'objet mis en paramètre est égal à cet objet, faux sinon)
     */
    @Override
    public boolean equals(Object thatO) {
        return (thatO instanceof Score) && (this.hashCode() == ((Score) thatO).hashCode());
    }
    
    /**
     * @return  
     *      La version empaquetée du score
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packed());
    }
    
    /**
     * @return
     *      Une représentation textuelle du score
     */
    @Override
    public String toString() {
        return PackedScore.toString(packed());
    }
    
}
