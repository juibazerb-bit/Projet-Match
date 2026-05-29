package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;
import java.util.Random;

/**
 * Gère la logique d'un coup joué :
 *  1. Tente l'échange de deux tuiles voisines.
 *  2. Vérifie qu'un match est créé.
 *  3. Si oui  : supprime les matchs en cascade, retourne true.
 *  4. Si non  : annule l'échange, retourne false.
 *
 * Cette classe ne fait PAS d'affichage ni d'animation : c'est le rôle
 * de l'appelant (PanneauJeu, TestNiveau…) d'appeler le rendu après.
 */
public class GestionPartie {

    private final DetectionMatchs  detection   = new DetectionMatchs();
    private final SuppressionMatchs suppression = new SuppressionMatchs();

    /**
     * Tente de jouer le coup c1 ↔ c2.
     *
     * @return true si l'échange a créé au moins un match (et a été validé),
     *         false si les tuiles ne sont pas voisines ou si aucun match n'a été créé.
     */
    public boolean jouerUnCoup(Plateau plateau, Coord c1, Coord c2) {
        if (!plateau.echangerTuiles(c1, c2)) {
            System.out.println("Échange impossible : tuiles non voisines.");
            return false;
        }
        if (!detection.existeUnMatch(plateau)) {
            plateau.echangerTuiles(c2, c1); // annulation
            System.out.println("Pas de match créé, échange annulé.");
            return false;
        }
        suppression.supprimerTousLesMatchs(plateau, new Random());
        System.out.println("Score : " + plateau.getScore());
        return true;
    }
}
