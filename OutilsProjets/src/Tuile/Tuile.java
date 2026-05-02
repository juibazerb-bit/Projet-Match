/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tuile;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import TypeTuile.TypeTuile;

/**
 *
 * @author fpauvert
 */
public class Tuile {
    //taille des images  = 50px de hauteur et de largeur
    public static final int TAILLE = 20;
    private int type;
    private Coord coordTuile;

    // -------------------------------------------------------------------------
    // TYPES TUILES
    // -------------------------------------------------------------------------
    public Tuile(int typeTuile) {
        this.type = typeTuile;
    }

    // Constructeur pour créer une tuile aléatoire
    public Tuile(int nbTuiles, boolean aleatoire) {
        this.type = (int) (Math.random() * nbTuiles);
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
        return valeurs[this.type].afficher();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuile other = (Tuile) obj;
        return this.type == other.type;
    }

    // -------------------------------------------------------------------------
    // COORDONNEES TUILE
    // -------------------------------------------------------------------------
    public Coord getCoordTuile() {
        return coordTuile;
    }

    public void setCoordTuile(Coord coordTuile) {
        this.coordTuile = coordTuile;
    }
    // -------------------------------------------------------------------------
    // DESSINER TUILE
    // -------------------------------------------------------------------------

    public void dessiner(FenetreGraphique fenetre, int x, int y) {
        // 1. On récupère l'énumération correspondante au type de cette tuile
        TypeTuile[] valeurs = TypeTuile.values();
        TypeTuile monType = valeurs[this.type % valeurs.length];

        // 2. On récupère l'image associée à ce type (via le getter que tu as ajouté)
        java.awt.Image img = monType.getImage();

        // 3. On dessine l'image aux coordonnées demandées
        // x et y sont généralement : colonne * TAILLE et ligne * TAILLE
        fenetre.getGraphics2D().drawImage(img, x, y, TAILLE, TAILLE, null);
    }

}
