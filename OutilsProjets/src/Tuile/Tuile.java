package Tuile;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import TypeTuile.TypeTuile;
import java.util.Random;

public class Tuile {

    public static final int TAILLE = 30;

    private int type;
    private Coord coordTuile;

    // Constructeur avec type fixe
    public Tuile(int typeTuile) {
        this.type = typeTuile;
    }

    // Constructeur aléatoire 
    public Tuile(int nbTuiles, Random rand) {
        this.type = rand.nextInt(nbTuiles);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        TypeTuile[] valeurs = TypeTuile.values();
        return valeurs[this.type % valeurs.length].afficher();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Tuile other = (Tuile) obj;
        return this.type == other.type;
    }

    public Coord getCoordTuile() {
        return coordTuile;
    }

    public void setCoordTuile(Coord coordTuile) {
        this.coordTuile = coordTuile;
    }

    public void dessiner(FenetreGraphique fenetre, int x, int y) {
        TypeTuile monType = TypeTuile.values()[this.type % TypeTuile.values().length];
        fenetre.getGraphics2D().drawImage(monType.getImage(), x, y, TAILLE, TAILLE, null);
    }
}