package Controleur;

import FenetreGraphique.FenetreGraphique;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;

/**
 * Gère la détection et l'interprétation des clics souris dans la fenêtre
 * FenetreGraphique (mode non-Swing).
 *
 * Retourne un ActionJoueur explicite au lieu de Coord avec valeurs négatives.
 */
public class GestionClics {

    // -------------------------------------------------------------------------
    // CONVERSION PIXEL → COORDONNÉE GRILLE
    // -------------------------------------------------------------------------

    /**
     * Convertit des coordonnées pixels en coordonnées logiques du plateau.
     * Retourne null si le clic est en dehors de la grille.
     */
    public Coord clicVersCoord(Plateau plateau, int clicX, int clicY, int margeX, int margeY) {
        int col     = (clicX - margeX) / Tuile.TAILLE;
        int basGrille = margeY + (plateau.getNbLig() + 1) * Tuile.TAILLE;
        int lig     = (basGrille - clicY) / Tuile.TAILLE;

        if (col >= 0 && col < plateau.getNbCol() && lig >= 0 && lig < plateau.getNbLig()) {
            return new Coord(col, lig);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // ATTENTE D'UN CLIC
    // -------------------------------------------------------------------------

    /**
     * Bloque jusqu'à ce que le joueur clique quelque part,
     * puis retourne un ActionJoueur décrivant ce qui a été cliqué.
     */
    public ActionJoueur attendreAction(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY) {
        int boutonX = margeX + plateau.getNbCol() * Tuile.TAILLE + 20;

        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int x = fenetre.getXDernierClic();
                int y = fenetre.getYDernierClic();
                fenetre.effacerDernierClic();

                ActionJoueur action = interpreterClic(plateau, boutonX, x, y, margeX, margeY);
                if (action != null) return action;
            }
            fenetre.attendre(0.02);
        }
    }

    // -------------------------------------------------------------------------
    // INTERPRÉTATION D'UN CLIC
    // -------------------------------------------------------------------------

    private ActionJoueur interpreterClic(Plateau plateau, int boutonX,
            int clicX, int clicY, int margeX, int margeY) {

        // Boutons fixes
        if (surBouton(clicX, clicY, boutonX, 60,  160, 30)) return new ActionJoueur(ActionJoueur.Type.COUPS_POSSIBLES);
        if (surBouton(clicX, clicY, boutonX, 100, 160, 30)) return new ActionJoueur(ActionJoueur.Type.NOUVELLE_PARTIE);
        if (surBouton(clicX, clicY, boutonX, 140, 160, 30)) return new ActionJoueur(ActionJoueur.Type.QUITTER);
        if (surBouton(clicX, clicY, boutonX, 180, 160, 30)) return new ActionJoueur(ActionJoueur.Type.MEILLEUR_COUP);
        if (surBouton(clicX, clicY, boutonX, 220, 160, 30)) return new ActionJoueur(ActionJoueur.Type.ORDI_JOUE);

        // Compteur Lignes
        int deltaLig = clicSurCompteur(clicX, clicY, boutonX, 290, 160, 90);
        if (deltaLig != 0) return new ActionJoueur(ActionJoueur.Type.DELTA_LIGNES, deltaLig);

        // Compteur Colonnes
        int deltaCol = clicSurCompteur(clicX, clicY, boutonX, 390, 160, 90);
        if (deltaCol != 0) return new ActionJoueur(ActionJoueur.Type.DELTA_COLONNES, deltaCol);

        // Clic sur la grille
        Coord coord = clicVersCoord(plateau, clicX, clicY, margeX, margeY);
        if (coord != null) return new ActionJoueur(coord);

        return null; // clic ignoré (zone vide)
    }

    // -------------------------------------------------------------------------
    // UTILITAIRES
    // -------------------------------------------------------------------------

    /** Retourne true si (clicX, clicY) est dans le rectangle donné. */
    public boolean surBouton(int clicX, int clicY, int x, int y, int largeur, int hauteur) {
        return clicX >= x && clicX <= x + largeur && clicY >= y && clicY <= y + hauteur;
    }

    /**
     * Retourne +1 si le clic est sur la zone "+", -1 sur la zone "-", 0 sinon.
     * Le compteur est divisé en 3 tiers verticaux : +, valeur, -.
     */
    public int clicSurCompteur(int clicX, int clicY, int x, int y, int largeur, int hauteur) {
        int tiers = hauteur / 3;
        if (surBouton(clicX, clicY, x, y,               largeur, tiers)) return  1;
        if (surBouton(clicX, clicY, x, y + 2 * tiers,   largeur, tiers)) return -1;
        return 0;
    }
}
