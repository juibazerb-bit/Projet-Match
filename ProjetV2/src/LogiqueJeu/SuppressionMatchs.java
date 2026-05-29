package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;
import java.util.ArrayList;
import java.util.Random;

/**
 * Supprime les tuiles alignées du plateau en cascade :
 * tant qu'il existe des matchs, on collecte, on supprime, on recommence.
 *
 * La collecte est déléguée à TypesCombinaisons qui gère les priorités
 * et les bonus (T, L, x4, x5…).
 */
public class SuppressionMatchs {

    private final TypesCombinaisons combinaisons = new TypesCombinaisons();

    /**
     * Collecte toutes les tuiles à supprimer (sans rien modifier).
     * Délègue à TypesCombinaisons.
     */
    public ArrayList<Coord> collecterToutesLesTuilesASupprimer(Plateau plateau) {
        return combinaisons.collecterToutesLesTuilesASupprimer(plateau);
    }

    /**
     * Supprime en cascade tous les matchs du plateau.
     * Retourne le nombre total de tuiles supprimées.
     */
    public int supprimerTousLesMatchs(Plateau plateau, Random rand) {
        int total = 0;
        ArrayList<Coord> aSupprimer;
        while (!(aSupprimer = collecterToutesLesTuilesASupprimer(plateau)).isEmpty()) {
            total += supprimerCoords(plateau, aSupprimer, rand);
        }
        return total;
    }

    /**
     * Supprime les tuiles aux coordonnées indiquées et remplace chacune
     * par une nouvelle tuile aléatoire en haut de sa colonne.
     * Retourne le nombre de tuiles supprimées.
     */
    public int supprimerCoords(Plateau plateau, ArrayList<Coord> aSupprimer, Random rand) {
        for (int col = 0; col < plateau.getNbCol(); col++) {
            ArrayList<Integer> lignes = new ArrayList<>();
            for (Coord c : aSupprimer) {
                if (c.getAbscisse() == col) lignes.add(c.getOrdonnee());
            }
            if (!lignes.isEmpty()) {
                lignes.sort(Integer::compareTo);
                plateau.getLesColonnes()[col].supprimerTuiles(lignes, rand);
            }
        }
        return aSupprimer.size();
    }
}
