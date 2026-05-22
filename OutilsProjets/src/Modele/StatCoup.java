/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.util.ArrayList;

/**
 *
 * @author flo66
 */
public class StatCoup {

    public Coord c1, c2;
    public int occurrences = 0;
    public long totalScore = 0;
    public long totalTuiles = 0;
    public ArrayList<Integer> HistoriqueScores = new ArrayList<>();

    public StatCoup(Coord c1, Coord c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    // Vérifie si c'est le même échange (peu importe l'ordre c1/c2)
    public boolean estIdentique(Coord o1, Coord o2) {
        return (c1.equals(o1) && c2.equals(o2)) || (c1.equals(o2) && c2.equals(o1));
    }

    /**
     * Calcule l'écart-type des scores enregistrés pour ce coup.
     */
    public double calculerEcartType(double moyenne) {
        if (occurrences <= 1) {
            return 0.0; // Pas de dispersion possible avec 0 ou 1 valeur
        }
        double sommeCarresDesEcarts = 0;
        for (int score : HistoriqueScores) {
            // (Valeur - Moyenne)²
            sommeCarresDesEcarts += Math.pow(score - moyenne, 2);
        }

        // Variance = somme / N
        double variance = sommeCarresDesEcarts / occurrences;

        // Écart-type = racine carrée de la variance
        return Math.sqrt(variance);
    }
}
