package Modele;

import java.util.ArrayList;

/**
 * Accumule les statistiques d'un coup (paire c1/c2) sur plusieurs simulations
 * Monte-Carlo : nombre d'apparitions, score total, tuiles supprimées, et
 * historique des scores pour calculer l'écart-type.
 */
public class StatCoup {

    public final Coord c1;
    public final Coord c2;

    public int occurrences = 0;
    public long totalScore = 0;
    public long totalTuiles = 0;
    public ArrayList<Integer> historiqueScores = new ArrayList<>();

    public StatCoup(Coord c1, Coord c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    /**
     * Retourne true si ce StatCoup correspond à la même paire (peu importe
     * l'ordre).
     */
    public boolean estIdentique(Coord o1, Coord o2) {
        return (c1.equals(o1) && c2.equals(o2)) || (c1.equals(o2) && c2.equals(o1));
    }

    /**
     * Calcule l'écart-type des scores enregistrés pour ce coup. Retourne 0 si
     * moins de 2 occurrences.
     */
    public double calculerEcartType(double moyenne) {
        if (occurrences <= 1) {
            return 0.0;
        }

        double somme = 0;
        for (int score : historiqueScores) {
            somme += Math.pow(score - moyenne, 2);
        }
        return Math.sqrt(somme / occurrences);
    }
}
