package ch.epfl.javass.net;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class StringSerializer {

    /**
     * Constructeur privé car la classe est non instanciable.
     */
    private StringSerializer() {
    }
    
    /**
     * 
     * @param i
     *      L'entier à sérializer
     * @return
     *      Une représentation en String de "i" comme un entier non signé en base 16
     */
    public static String serializeInt(int i) {
        return Integer.toHexString(i);
    }
    
    /**
     * 
     * @param l
     *      Le long à sérializer
     * @return
     *      Une représentation en String de "l" comme un long en base 16
     */
    public static String serializeLong(Long l) {
        return Long.toHexString(l);
    }
    
    /**
     * 
     * @param s
     *      Le String s sous forme d'un entier non signée en base 16 à désérializer
     * @return
     *      L'entier que le string représente
     */
    public static  int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }
    
    /**
     * 
     * @param s
     *      Le String s sous forme d'un long non signée en base 16 à désérializer
     * @return
     *      Le long que le string représente
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, 16);
    }

    /**
     * 
     * @param s
     *      Le string à encoder en UTF-8
     * @throws IllegalArgumentException
     *      Quand l'encoding d'un charactère du String n'est pas supporté
     *      Le String s encodé en UTF-8
     * @return
     */
    public static String serializeString(String s){
        try {
            return Base64.getEncoder().encodeToString(s.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 
     * @param s
     *      Le string à décoder en UTF-8
     * @throws IllegalArgumentException
     *      Quand l'encoding d'un charactère du String n'est pas supporté
     * @return
     *      Le String s décodé (sachant qu'il été encodé avant)
     */
    public static String deserializeString(String s){
        byte[] asBytes = Base64.getDecoder().decode(s);
        try {
            return new String(asBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 
     * @param elements
     *      Une séquence d'éléments
     * @return
     *      Les éléments combinés avec "," en un seul String
     */
    public static String stringCombiner(CharSequence... elements) {
       return String.join(",",elements);
    }
    
    /**
     * 
     * @param elements
     *      Un String à séparer
     * @return
     *      Le String s séparé (à chaque fois un "," apparaît) en un tableau de Strings
     */
    public static String[] stringSeparator(String elements) {
        return elements.split(",");
    }

    /**
     * 
     * @param elements
     *      Un String à séparer
     * @return
     *      Le String s séparé (à chaque fois un " " apparaît) en un tableau de Strings
     */
    public static String[] spaceSeparator(String elements) {
        return elements.split(" ");
    }
}
