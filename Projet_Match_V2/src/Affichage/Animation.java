package Affichage;

import FenetreGraphique.FenetreGraphique;
import Modele.Plateau;
import Modele.Tuile;

/**
 * Gère l'animation de chute des tuiles après une suppression.
 *
 * Principe :
 *  1. fixerPositionsActuelles() mémorise la position Y actuelle de chaque tuile.
 *  2. Après la suppression+régénération, les nouvelles tuiles ont posYVisuelle=-1,
 *     ce qui signale qu'elles partent du haut de l'écran.
 *  3. animerChute() déplace progressivement chaque tuile vers sa position cible.
 */
public class Animation {

    private final DessinPlateau dessinPlateau = new DessinPlateau();

    /** Mémorise la position Y actuelle de chaque tuile (avant suppression). */
    public void fixerPositionsActuelles(Plateau plateau, int margeY) {
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig(); lig++) {
                Tuile t = plateau.getTuile(col, lig);
                if (t != null) {
                    t.setPosYVisuelle(margeY + hauteurPlateau - lig * Tuile.TAILLE);
                }
            }
        }
    }

    /** Réinitialise toutes les positions visuelles (plus d'animation en cours). */
    public void reinitialiserPositionsVisuelles(Plateau plateau) {
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig(); lig++) {
                Tuile t = plateau.getTuile(col, lig);
                if (t != null) { // correction du bug crash null
                    t.setPosYVisuelle(-1);
                }
            }
        }
    }

    /**
     * Anime la chute de toutes les tuiles vers leur position cible.
     * Les nouvelles tuiles (posYVisuelle == -1) partent du haut de la grille.
     * Les tuiles déjà positionnées continuent leur descente.
     */
    public void animerChute(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY) {
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        boolean enMouvement = true;
        double vitesseBase = 1.0;
        double boostParLigne = 1.0;

        while (enMouvement) {
            enMouvement = false;

            for (int col = 0; col < plateau.getNbCol(); col++) {
                for (int lig = 0; lig < plateau.getNbLig(); lig++) {
                    Tuile t = plateau.getTuile(col, lig);
                    if (t == null) continue;

                    int yCible = margeY + hauteurPlateau - lig * Tuile.TAILLE;
                    double vitesse = vitesseBase + (plateau.getNbLig() - lig) * boostParLigne;

                    // Nouvelle tuile : part du haut
                    if (t.getPosYVisuelle() == -1) {
                        t.setPosYVisuelle(margeY - (lig - plateau.getNbLig() + 3) * Tuile.TAILLE / 2);
                        enMouvement = true;
                    }

                    if (t.getPosYVisuelle() < yCible) {
                        t.setPosYVisuelle(Math.min(yCible, t.getPosYVisuelle() + vitesse));
                        enMouvement = true;
                    }
                }
            }

            dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
            fenetre.attendre(0.002);
        }
        fenetre.actualiser();
    }

    /** Vide la console (utilitaire debug). */
    public static void clearConsole() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
}
