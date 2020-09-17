package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class Bits64 {
    
    private Bits64() {}
    
    
    /**
     * Retourne un entier représenté par une plage de bits avec uniquement
     * des 1 de start(inclus) à start+size(exclus).
     * 
     * @param start
     *      Index de départ du masquage
     * @param size
     *      Taille du masquage
     * @throws IllegalArgumentException
     *      Si start et size n'est pas une plage valide (de 0 à 63)
     * @return mask
     *      L'entier correspondant à la plage de bits
     */
    public static long mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && start+size <= Long.SIZE);
            return (size == Long.SIZE) ? -1L :  (((1L << size )- 1L ) << start );
    }
    
    
    
    /**
     * Met en évidence une portion d'un vecteur de bits. Découpe
     * bits de start(inclus) à start+size(exclus).
     * 
     * @param bits
     *      Vecteur de bits à découper
     * @param start
     *      Index de départ du découpage (inclus)
     * @param size
     *      Nombre de bits à découper
     * @return nExtracted
     *      Entier dont les bits de poids faible sont les bits du paramètre bits
     */
    public static long extract(long bits, int start, int size) {
        long nExtracted = mask(start, size);
        nExtracted &= bits;
        nExtracted >>>= start;
                return nExtracted;
    }
    
    
    
    /**
     * Retourne les valeurs v1 et v2 empaquetées dans un entier.
     * 
     * @param v1, v2
     *      Nombres à empaqueter
     * @param s1, s2
     *      Taille occupée par v1, v2 respectivement
     * @throws IllegalArgumentException
     *      Si un ou plusieurs des arguments v1,v2,s1 et s2 est(sont) invalide(s) ou si la somme des deux tailles dépassent Long.SIZE
     * @return extracted
     *      Les deux valeurs empaquetées
     *      
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        Preconditions.checkArgument(!invalidArguments(v1, s1) 
                && !invalidArguments(v2, s2) 
                && s1+s2 <= Long.SIZE);
            long extracted = extract(v1, 0, s1);
            long extracted2 = extract(v2, 0, s2) << s1;
            extracted |= extracted2; 
            return extracted;
    }
    

    
    /**
     * Méthode auxiliare pour tester la validité des paramètres de la fonction pack
     * 
     * @param v
     *      nombre représentant un vecteur de bits
     * @param s
     *      taille à occuper par v
     * @return
     *      booléen (vrai si v peut être représenté par un vecteur de s bits en représentation signée, faux sinon)
     */
    private static boolean invalidArguments(long v, int s) {
        boolean isNotInRange = s < 1 && s > Long.SIZE-1;
        boolean hasWrongSize = Long.SIZE-Long.numberOfLeadingZeros(v) > s;
        return(isNotInRange || hasWrongSize);
    }
}
