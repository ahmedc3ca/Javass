package ch.epfl.javass.jass;
import java.util.List;
import ch.epfl.javass.Preconditions;

/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class CardSet {
    private final long pkCardSet;

    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);

    private CardSet(long pkCardSet) {
        this.pkCardSet = pkCardSet;
    }

    /**
     * 
     * @param cards
     *      Une liste de cartes à empaqueter
     * @return
     *      L'ensemble des cartes dans la liste
     */
    public static CardSet of(List<Card> cards) {
        long pkCardSet = PackedCardSet.EMPTY;
        for(Card c: cards) {
            pkCardSet = PackedCardSet.add(pkCardSet, c.packed());
        }
        return new CardSet(pkCardSet);
    }


    /**
     * 
     * @param packed
     *      L'ensemble des cartes sous forme empaqueté
     * @throws IllegalArgumentException
     *      Si l'ensemble de cartes n'est pas valide
     * @return
     *      L'ensemble de cartes
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }


    /**
     * 
     * @return
     *      La forme empaquetée de l'ensemble de cartes
     */
    public long packed() {
        return pkCardSet;
    }


    /**
     * 
     * @return
     *      Un booléen (vrai si l'ensemble est vide, faux sinon)
     */
    public boolean isEmpty() {
        return equals(EMPTY);
    }


    /**
     * 
     * @return
     *      la taille de l'ensemble de cartes
     */
    public int size() {
        return PackedCardSet.size(pkCardSet);
    }


    /**
     * 
     * @param index
     *      L'index de la carte voulue dans l'ensemble de cartes
     * @return
     *      La "index"ième carte de l'ensemble
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(pkCardSet, index));
    }


    /**
     * 
     * @param card
     *      La carte à ajouter dans l'ensemble de cartes
     * @return
     *      L'ensemble de cartes danslaquelle on a ajouté la carte "card"
     *      
     */
    public CardSet add(Card card) {
        return ofPacked(PackedCardSet.add(pkCardSet, card.packed()));
    }


    /**
     * 
     * @param card
     *      la carte à enlever dans l'ensemble de cartes
     * @return
     *      l'ensemble de cartes sans la carte "card"
     */
    public CardSet remove(Card card) {
        return ofPacked(PackedCardSet.remove(pkCardSet, card.packed()));
    }


    /**
     * 
     * @param card
     *      La carte à chercher dans l'ensemble de cartes
     * @return
     *      Un booléen (vrai si la carte "card" est dans l'ensemble de cartes
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(pkCardSet, card.packed());
    }


    /**
     * 
     * @return
     *      Le complément de l'ensemble des cartes
     */
    public CardSet complement() {
        return ofPacked(PackedCardSet.complement(pkCardSet));
    }


    /**
     * 
     * @param that
     *      L'ensemble de cartes qu'on veut unir avec l'ensemble de cartes
     * @return
     *      L'union des deux ensembles de cartes
     */
    public CardSet union(CardSet that) {
        return ofPacked(PackedCardSet.union(pkCardSet, that.packed()));
    }


    /**
     * 
     * @param that
     *      L'ensemble de cartes dont on veut trouver l'intersection avec l'autre ensemble de carte appelant la méthode
     * @return
     *      L'intersection des deux ensembles de cartes
     */
    public CardSet intersection(CardSet that) {
        return ofPacked(PackedCardSet.intersection(pkCardSet, that.packed()));
    }


    /**
     * 
     * @param that
     *      L'ensemble de cartes qu'on veut trouver la difference avec l'autre ensemble de carte appelant la méthode
     * @return
     *      La différence entre l'ensemble de cartes et that
     */
    public CardSet difference(CardSet that) {
        return ofPacked(PackedCardSet.difference(pkCardSet, that.packed()));
    }


    /**
     * 
     * @param color
     *      La couleur unique du sous-ensemble
     * @return
     *      Le sous-ensemble de cartes qui contient seulement la couleur "color"
     */
    public CardSet subsetOfColor(Card.Color color) {
        return ofPacked(PackedCardSet.subsetOfColor(pkCardSet, color));
    }

    /**
     * Méthode qui s'assure que le récepteur et le paramètre sont égaux
     * @param thatO
     *      L'autre ensemble de cartes comparée
     * @return
     *      Un booleén (vrai si l'objet mis en paramètre est égal à cet objet, faux sinon)
     */
    @Override
    public boolean equals(Object thatO) {
        return (thatO instanceof CardSet) && (pkCardSet == ((CardSet) thatO).packed());
    }

    /**
     * @return  
     *      La version empaquetée de l'ensemble des cartes
     */
    @Override
    public int hashCode() {
        return Long.hashCode(pkCardSet);
    }

    /**
     * @return
     *      Une représentation textuelle du score
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(pkCardSet);
    }


}
