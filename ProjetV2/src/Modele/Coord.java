package Modele;

/**
 * Représente une position (colonne, ligne) sur le plateau.
 * abscisse = colonne, ordonnee = ligne.
 */
public class Coord {

    private int abscisse;
    private int ordonnee;

    public Coord(int abscisse, int ordonnee) {
        this.abscisse = abscisse;
        this.ordonnee = ordonnee;
    }

    public int getAbscisse() { return abscisse; }
    public int getOrdonnee() { return ordonnee; }
    public void setAbscisse(int abscisse) { this.abscisse = abscisse; }
    public void setOrdonnee(int ordonnee) { this.ordonnee = ordonnee; }

    /** Deux cases sont voisines si elles ne diffèrent que d'une case en X ou en Y. */
    public boolean estVoisine(Coord c) {
        int diffAbs = Math.abs(this.abscisse - c.abscisse);
        int diffOrd = Math.abs(this.ordonnee - c.ordonnee);
        return (diffAbs == 1 && diffOrd == 0) || (diffAbs == 0 && diffOrd == 1);
    }

    @Override
    public String toString() {
        return "(" + abscisse + ", " + ordonnee + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Coord)) return false;
        Coord other = (Coord) obj;
        return this.abscisse == other.abscisse && this.ordonnee == other.ordonnee;
    }

    @Override
    public int hashCode() {
        return 31 * abscisse + ordonnee;
    }
}
