/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controleur;

import FenetreGraphique.FenetreGraphique;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import java.awt.Graphics2D;

/**
 *
 * @author flo66
 */
public class GestionClics {
    // Retourne +1, -1 ou 0 selon ou le joueur a clique

    public int clicSurCompteur(int clicX, int clicY, int x, int y,
            int largeur, int hauteur) {
        int tiers = hauteur / 3;

        // Zone +
        if (boutonClique(clicX, clicY, x, y, largeur, tiers)) {
            return 1;
        }
        // Zone -
        if (boutonClique(clicX, clicY, x, y + 2 * tiers, largeur, tiers)) {
            return -1;
        }
        return 0; // clic sur la valeur, on ignore
    }

    // Dessine un bouton et retourne true si le clic est dessus
    public boolean boutonClique(int clicX, int clicY, int x, int y, int largeur, int hauteur) {
        return clicX >= x && clicX <= x + largeur && clicY >= y && clicY <= y + hauteur;
    }

    // -------------------------------------------------------------------------
    // METHODES SUR CLIC
    // -------------------------------------------------------------------------
    public Coord clicVersCoord(Plateau plateau, int clicX, int clicY, int margeX, int margeY) {
        int col = (clicX - margeX) / Tuile.TAILLE;
        int basGrille = margeY + (plateau.getNbLig() + 1) * Tuile.TAILLE; // +1 pour le décalage
        int lig = (basGrille - clicY) / Tuile.TAILLE;

        if (col >= 0 && col < plateau.getNbCol() && lig >= 0 && lig < plateau.getNbLig()) {
            return new Coord(col, lig);
        }
        return null;
    }

    public Coord attendreClicOuBouton(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY) {
    int boutonX = margeX + plateau.getNbCol() * Tuile.TAILLE + 20;

    while (true) {
        if (fenetre.unClicAEuLieu()) {
            int clicX = fenetre.getXDernierClic();
            int clicY = fenetre.getYDernierClic();
            fenetre.effacerDernierClic();

            // Boutons en priorité
            if (boutonClique(clicX, clicY, boutonX, 60, 160, 30)) return new Coord(-2, 0);
            if (boutonClique(clicX, clicY, boutonX, 100, 160, 30)) return new Coord(-3, 0);
            if (boutonClique(clicX, clicY, boutonX, 140, 160, 30)) return new Coord(-4, 0);

            int deltaLig = clicSurCompteur(clicX, clicY, boutonX, 200, 160, 90);
            if (deltaLig != 0) return new Coord(-10, deltaLig);

            int deltaCol = clicSurCompteur(clicX, clicY, boutonX, 300, 160, 90);
            if (deltaCol != 0) return new Coord(-11, deltaCol);

            // Clic sur la grille
            Coord coord = clicVersCoord(plateau, clicX, clicY, margeX, margeY);
            if (coord != null) {
                // on convertit la Coord logique en pixels pour dessiner au bon endroit
                int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
                int x = margeX + coord.getAbscisse() * Tuile.TAILLE;
                int y = margeY + hauteurPlateau - coord.getOrdonnee() * Tuile.TAILLE;

                // Contour jaune épais
                Graphics2D g = fenetre.getGraphics2D();
                g.setColor(java.awt.Color.YELLOW);
                g.setStroke(new java.awt.BasicStroke(4)); // épaisseur 4 pixels
                g.drawRect(x + 2, y + 2, Tuile.TAILLE - 4, Tuile.TAILLE - 4);
                g.setStroke(new java.awt.BasicStroke(1)); // on remet l'épaisseur par défaut
                fenetre.actualiser();

                return coord;
            }
        }
        fenetre.attendre(0.02);
    }
}

    public int lireChoix(Plateau plateau, FenetreGraphique fenetre) {
        int boutonX = 20 + plateau.getNbCol() * Tuile.TAILLE + 20;

        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int clicX = fenetre.getXDernierClic();
                int clicY = fenetre.getYDernierClic();
                fenetre.effacerDernierClic();

                if (boutonClique(clicX, clicY, boutonX, 20, 160, 30)) {
                    return 1; // Jouer
                }
                if (boutonClique(clicX, clicY, boutonX, 60, 160, 30)) {
                    return 2; // Coups possibles
                }
                if (boutonClique(clicX, clicY, boutonX, 100, 160, 30)) {
                    return 3; // Nouvelle partie
                }
                if (boutonClique(clicX, clicY, boutonX, 140, 160, 30)) {
                    return 4; // Quitter
                }
            }
        }
    }
}
