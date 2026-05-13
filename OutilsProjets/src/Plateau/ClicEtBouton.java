/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plateau;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import Tuile.Tuile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author flo66
 */
public class ClicEtBouton {

    public void dessinerBouton(FenetreGraphique fenetre, String texte, int x, int y, int largeur, int hauteur) {
        Graphics2D g = fenetre.getGraphics2D();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(texte, x + 10, y + hauteur / 2 + 5);
    }

    // Dessine le compteur avec les zones + et -
    public void dessinerCompteur(FenetreGraphique fenetre, int x, int y,
            int largeur, int hauteur, String label, int valeur) {
        Graphics2D g = fenetre.getGraphics2D();
        int tiers = hauteur / 3;

        // Fond general
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);

        // Bouton +
        g.setColor(new Color(100, 200, 100));
        g.fillRoundRect(x + 2, y + 2, largeur - 4, tiers - 2, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("+", x + largeur / 2 - 5, y + tiers - 5);

        // Valeur au centre
        g.setColor(Color.WHITE);
        g.fillRect(x + 2, y + tiers, largeur - 4, tiers);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString(label + ": " + valeur, x + 5, y + tiers + tiers / 2 + 5);

        // Bouton -
        g.setColor(new Color(200, 100, 100));
        g.fillRoundRect(x + 2, y + 2 * tiers + 2, largeur - 4, tiers - 4, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("-", x + largeur / 2 - 4, y + hauteur - 8);
    }
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
        // Valeurs spéciales retournées pour les boutons :
        // (-2, 0) = Coups possibles
        // (-3, 0) = Nouvelle partie  
        // (-4, 0) = Quitter
        // Coord valide = clic sur la grille
        int boutonX = margeX + plateau.getNbCol() * Tuile.TAILLE + 20;

        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int clicX = fenetre.getXDernierClic();
                int clicY = fenetre.getYDernierClic();
                fenetre.getGraphics2D().setColor(java.awt.Color.YELLOW);
                int x = margeX + clicX * Tuile.TAILLE;
                int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
                int y = margeY + hauteurPlateau - clicY * Tuile.TAILLE;
                fenetre.getGraphics2D().fillRect(x, y, Tuile.TAILLE, Tuile.TAILLE);
                fenetre.actualiser();
                fenetre.effacerDernierClic();

                if (boutonClique(clicX, clicY, boutonX, 60, 160, 30)) {
                    return new Coord(-2, 0);
                }
                if (boutonClique(clicX, clicY, boutonX, 100, 160, 30)) {
                    return new Coord(-3, 0);
                }
                if (boutonClique(clicX, clicY, boutonX, 140, 160, 30)) {
                    return new Coord(-4, 0);
                }
                // Compteur lignes
                int deltaLig = clicSurCompteur(clicX, clicY, boutonX, 200, 160, 90);
                if (deltaLig != 0) {
                    return new Coord(-10, deltaLig); // -10 = signal "changer lignes"
                }

                // Compteur colonnes
                int deltaCol = clicSurCompteur(clicX, clicY, boutonX, 300, 160, 90);
                if (deltaCol != 0) {
                    return new Coord(-11, deltaCol); // -11 = signal "changer colonnes"
                }

                Coord coord = clicVersCoord(plateau, clicX, clicY, margeX, margeY);
                if (coord != null) {
                    return coord;
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

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
