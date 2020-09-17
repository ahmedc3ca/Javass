package ch.epfl.javass.jass;
import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;
/**
 * @author Arnaud Poletto (302411)
 * @author Ahmed Ezzo (299897)
 */
public final class Trick {
    private final int pkTrick;
    public static final Trick INVALID = new Trick(PackedTrick.INVALID);

    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    /**
     * 
     * @param trump
     *      L'atout du pli
     * @param firstPlayer
     *      Le premier joueur à jouer
     * @return
     *      Le pli empaqueté vide
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * 
     * @param packed
     *      Le pli empaqueté
     * @return
     *      L'objet pli correspondant
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * 
     * @return
     *      Le pli empaqueté
     */
    public int packed() {
        return pkTrick;
    }

    /**
     * 
     * @throws IllegalStateException
     *      Si le pli n'est pas plein
     * @return
     *      Le pli empaqueté vide suivant celui donné
     */
    public Trick nextEmpty() {
        if(!isFull()) {
            throw new IllegalStateException();
        }else {
            return new Trick(PackedTrick.nextEmpty(packed()));
        }
    }

    /**
     * 
     * @return
     *      Un booléen(vrai si aucune carte a été jouée, faux sinon)
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packed());
    }

    /**
     * 
     * @return
     *      Un booléen(vrai si tous les joueurs ont posé une carte, faux sinon) 
     */
    public boolean isFull() {
        return PackedTrick.isFull(packed());
    }

    /**
     * 
     * @return
     *      Un booléen(vrai si c'est le dernier plis, faux sinon)
     */
    public boolean isLast() {
        return PackedTrick.isLast(packed());
    }

    /**
     * 
     * @return
     *      Le nombre de cartes jouées
     */
    public int size() {
        return PackedTrick.size(packed());
    }

    /**
     * 
     * @return
     *      L'atout du pli
     */
    public Color trump() {
        return PackedTrick.trump(packed());
    }

    /**
     * 
     * @return
     *      L'index du pli
     */
    public int index() {
        return PackedTrick.index(packed());
    }

    /**
     * 
     * @param index
     *      Le nième joueur ayant posé sa carte
     * @throws IndexOutOfBoundsException
     *      Si l'index n'est pas n'est pas compris entre 0(inclus) et 4(exclus)
     * @return
     *      Le joueur d'index donné dans le pli
     */
    public PlayerId player(int index) {
        if(index < 0 || index > 3) {
            throw new IndexOutOfBoundsException();

        }else {
            return PackedTrick.player(packed(), index); 
        }
    }

    /**
     * 
     * @param index
     *      La nième carte jouée
     * @throws IndexOutOfBoundsException
     *      Si l'index n'est pas compris entre 0 (inclus) et la taille du pli (exclus)
     * @return
     *      La version empaquetée de la carte du pli à l'index donné
     */
    public Card card(int index) {
        if(index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }else {
            return Card.ofPacked(PackedTrick.card(packed(), index));
        }
    }

    /**
     * 
     * @param c
     *      La carte à ajouter
     * @throws IllegalStateException
     *      Si le pli est plein
     * @return
     *      Le pli avec la carte c en plus
     */
    public Trick withAddedCard(Card c) {
        if(isFull()) {
            throw new IllegalStateException();
        }else {
            return new Trick(PackedTrick.withAddedCard(packed(), c.packed()));
        }
    }

    /**
     * 
     * @throws IllegalStateException
     *      Si le pli est vide
     * @return
     *      La couleur de la première carte jouée
     */
    public Color baseColor() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }else {
            return PackedTrick.baseColor(packed());
        }
    }

    /**
     * 
     * @param hand
     *      L'ensemble de cartes d'une main
     * @throws IllegalStateException
     *      Si le pli est plein
     * @return
     *      L'ensemble des cartes jouables dans le pli parmi les cartes de hand
     */
    public CardSet playableCards(CardSet hand) {
        if(isFull()) {
            throw new IllegalStateException();
        }else {
            return CardSet.ofPacked(PackedTrick.playableCards(packed(), hand.packed()));
        }
    }

    /**
     * 
     * @return
     *      Le nombre de points emportés par le pli
     */
    public int points() {
        return PackedTrick.points(packed());
    }

    /**
     * 
     * @throws IllegalStateException
     *      Si le pli est vide        
     * @return
     *      Le joueur ayant gagné le pli 
     */
    public PlayerId winningPlayer() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }else {
            return PackedTrick.winningPlayer(packed());
        }
    }

    /**
     * Une méthode qui s'assure que le récpteur et le paramètre sont égaux
     * @return
     *      Un booleén (vrai si l'objet mis en paramétre est égal à cet objet, faux sinon)
     */
    @Override
    public boolean equals(Object thatO) {
        return (thatO instanceof Trick) && (packed() == ((Trick) thatO).packed());
    }

    /**
     * 
     * @return
     *      Le pli sous forme empaquetée
     */
    @Override
    public int hashCode() {
        return packed();
    }

    /**
     * 
     * @return  
     *      Une représentation textuelle du pli    
     */
    @Override
    public String toString() {
        return PackedTrick.toString(packed());
    }

}
