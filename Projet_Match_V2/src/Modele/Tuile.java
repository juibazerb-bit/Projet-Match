package Modele;

import java.awt.Graphics2D;
import java.util.Random;

/**
 * Une tuile du plateau. Connaît son type (index dans TypeTuile) et sa position
 * visuelle Y utilisée pendant les animations de chute.
 *
 * La méthode dessiner() prend un Graphics2D : la Tuile ne dépend plus de
 * FenetreGraphique, ce qui supprime un couplage inutile.
 */
public class Tuile {

    public static int TAILLE = 50;

    private int type;
    private double posYVisuelle = -1; // -1 = pas encore d'animation en cours

    // --- Constructeurs ---
    /**
     * Tuile avec type fixe.
     */
    public Tuile(int type) {
        this.type = type;
    }

    /**
     * Tuile avec type aléatoire parmi nbTypes valeurs.
     */
    public Tuile(int nbTypes, Random rand) {
        this.type = rand.nextInt(nbTypes);
    }

    // --- Accesseurs ---
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getPosYVisuelle() {
        return posYVisuelle;
    }

    public void setPosYVisuelle(double y) {
        this.posYVisuelle = y;
    }

    // --- Affichage ---
    /**
     * Dessine la tuile à la position (x, y) de la grille. Si une animation est
     * en cours (posYVisuelle != -1), utilise posYVisuelle comme ordonnée.
     */
    public void dessiner(int x, int y, Graphics2D g2) {
        TypeTuile monType = TypeTuile.values()[type % TypeTuile.values().length];
        int yFinal = (posYVisuelle == -1) ? y : (int) posYVisuelle;
        g2.drawImage(monType.getImage(), x, yFinal, TAILLE, TAILLE, null);
    }

    /**
     * Représentation console.
     */
    @Override
    public String toString() {
        TypeTuile[] valeurs = TypeTuile.values();
        return valeurs[type % valeurs.length].afficher();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tuile)) {
            return false;
        }
        return this.type == ((Tuile) obj).type;
    }

}
