/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Tuile;

import FenetreGraphique.FenetreGraphique;
import java.awt.Color;

/**
 *
 * @author flo66
 */
public class TestTuile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // 1. Création de la fenêtre (300x300 pour avoir de la place)
        FenetreGraphique fen = new FenetreGraphique("Test de la Tuile", 300, 300);

        // 2. Création de deux tuiles de test
        // Une tuile de type 0 (normalement ROUGE / ruby.png)
        Tuile t1 = new Tuile(0);

        // Une tuile aléatoire parmi les 7 types disponibles
        Tuile t2 = new Tuile(7, true);

        System.out.println("Test Console :");
        System.out.println("Tuile 1 (type 0) : " + t1.toString());
        System.out.println("Tuile 2 (aleatoire) : " + t2.toString());

        // 3. Boucle d'affichage
        while (true) {
            fen.effacer(Color.WHITE); // Fond blanc pour bien voir les gemmes

            // On dessine la tuile 1 à la position (50, 50)
            t1.dessiner(fen, 50, 50);

            // On dessine la tuile 2 à la position (150, 50)
            t2.dessiner(fen, 150, 50);

            // Petit texte indicatif
            fen.getGraphics2D().setColor(Color.BLACK);
            fen.getGraphics2D().drawString("Tuile Type 0", 45, 120);
            fen.getGraphics2D().drawString("Tuile Aleatoire", 145, 120);

            fen.actualiser();

          
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }

        }
    }
}
