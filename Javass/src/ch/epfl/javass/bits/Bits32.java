package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class Bits32 {

    private Bits32() {}

    /**
     * Retourne un entier représenté par une plage de bits avec uniquement
     * des 1 de start(inclus) à start+size(exclus).
     * 
     * @param start
     *      Index de départ du masquage
     * @param size
     *      Taille du masquage
     * @throws IllegalArgumentException
     *      Si start et size n'est pas une plage valide (de 0 à 31)
     * @return mask
     *      L'entier correspondant à la plage de bits
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && start+size <= Integer.SIZE);
            return (size == Integer.SIZE) ? -1 :  (((1 << size )- 1 ) << start );
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
     * @return
     *      Un entier dont les bits de poids faible sont les bits du paramètre bits
     */
    public static int extract(int bits, int start, int size) {
        int nExtracted = mask(start, size);
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
     *      Si un ou plusieurs des arguments v1,v2,s1 et s2 est(sont) invalide(s) ou si la somme des deux tailles dépassent Integer.SIZE
     * @return extracted
     *      Les deux valeurs empaquetées
     *      
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        Preconditions.checkArgument(!invalidArguments(v1, s1) 
                && !invalidArguments(v2, s2) 
                && s1+s2 <= Integer.SIZE);
        
            int extracted = extract(v1, 0, s1);
            int extracted2 = extract(v2, 0, s2) << s1;
            extracted |= extracted2; 
            return extracted;
    }

    
    
    /**
     * Retourne les valeurs v1 et v2, v3 empaquetées dans un entier.
     * 
     * @param v1, v2, v3
     *      Nombres à empaqueter
     * @param s1, s2, s3
     *      Taille occupée par v1, v2, v3 respectivement
     * @throws IllegalArgumentException
     *       Si un ou plusieurs des arguments v1,v2,v3,s1,s2 et s3 est(sont) invalide(s) ou si la somme des trois tailles dépassent Integer.SIZE
     * @return extracted
     *      Les trois valeurs empaquetées
     *      
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        Preconditions.checkArgument(!invalidArguments(v1, s1) 
                && !invalidArguments(v2, s2) 
                && !invalidArguments(v3, s3)
                && s1+s2+s3 <= Integer.SIZE);
            int temp1 = extract(v1, 0, s1);
            int temp2 = extract(v2, 0, s2) << s1;
            int temp3 = extract(v3, 0, s3) << s1+s2;
            temp1 |= temp2;
            temp1 |= temp3; 
            return temp1;
    }
    
    
    
    /**
     * Retourne les valeurs v1 et v2, ..., v7 empaquetées dans un entier.
     * 
     * @param v1, v2, ..., v7
     *      Nombres à empaqueter
     * @param s1, s2, ..., s7
     *      Taille occupée par v1, v2, ...,v7 respectivement
     * @throws IllegalArgumentException
     *      Si un ou plusieurs des arguments v1,v2,..,v7,s1,s2,...,s7 est(sont) invalide(s) ou si la somme des sept tailles dépassent Integer.SIZE  
     * @return extracted
     *      Toutes les valeurs empaquetées
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        Preconditions.checkArgument(!invalidArguments(v1, s1) 
                && !invalidArguments(v2, s2)
                && !invalidArguments(v3, s3)
                && !invalidArguments(v4, s4)
                && !invalidArguments(v5, s5)
                && !invalidArguments(v6, s6)
                && !invalidArguments(v7, s7)
                && s1+s2+s3+s4+s5+s6+s7 <= Integer.SIZE);
            
            int temp1 = extract(v1, 0, s1);
            int temp2 = extract(v2, 0, s2) << s1;
            int temp3 = extract(v3, 0, s3) << s1+s2;
            int temp4 = extract(v4, 0, s4) << s1+s2+s3;
            int temp5 = extract(v5, 0, s5) << s1+s2+s3+s4;
            int temp6 = extract(v6, 0, s6) << s1+s2+s3+s4+s5;
            int temp7 = extract(v7, 0, s7) << s1+s2+s3+s4+s5+s6;
            temp1 |= temp2 |= temp3 |= temp4 |= temp5 |= temp6 |= temp7; 
            return temp1;
    }

    
    
    /**
     * Méthode auxiliare pour tester la validité des paramètres de la fonction pack
     * 
     * @param v
     *      Nombre représentant un vecteur de bits
     * @param s
     *      Taille à occuper par v
     * @return
     *      Booléen (vrai si v peut être représenté par un vecteur de s bits en représentation signée, faux sinon)
     */
    private static boolean invalidArguments(int v, int s) {
        boolean isNotInRange = s < 1 && s > Integer.SIZE-1;
        boolean hasWrongSize = Integer.SIZE-Integer.numberOfLeadingZeros(v) > s;
        return(isNotInRange || hasWrongSize);
    }
}
