package ch.epfl.javass;
/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class Preconditions {

    private Preconditions() {}
    
    /**
     * @param b
     *      un booléen
     * @throws IllegalArgumentException
     *      si l'argument est faux
     */
    public static void checkArgument(boolean b) {
        if(!b) {
            throw new IllegalArgumentException();   
        }
    }
    
    /**
     * @param index
     *      l'index à tester (ne doit pas être < 0 et >= size)
     *      
     * @param size
     *      nombre maximal que index peut prendre
     * @throws IndexOutOfBoundsException
     *      si index n'est pas dans l'intervalle
     * @return index
     *      le nombre pris en argument
     */
    public static int checkIndex(int index, int size) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }else {
            return index;
        }
    }
}
