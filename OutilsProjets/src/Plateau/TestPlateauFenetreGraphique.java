/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Plateau;

import FenetreGraphique.FenetreGraphique;
import Tuile.Tuile;

/**
 *
 * @author fpauvert
 */
public class TestPlateauFenetreGraphique {

    public static void main(String[] args) {
        int nbLignes = 5;
        int nbCol = 5;
        int nbTypes = 5;
        int margeX = 200;
        int margeY = 200;

        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes);

        int largeur = nbCol * Tuile.TAILLE + 400;
        int hauteur = nbLignes * Tuile.TAILLE + 400;
        FenetreGraphique fenetre = new FenetreGraphique("Candy Crush - Mode Graphique", largeur, hauteur);

        System.out.println("=== Jeu de Match // CandyCrush ===");
        plateau.afficherPlateau(fenetre, margeX, margeY);

        boolean continuer = true;
        while (continuer) {
            int choix = plateau.lireChoix(fenetre);
            
            plateau.echangerTuile(fenetre, margeX, margeY);
            plateau.afficherPlateau(fenetre, margeX, margeY);
            if (choix == 2) {
                System.out.println(plateau.listMatchs());
            } else if (choix == 3) {
                fenetre.effacer();
                plateau = new Plateau(nbLignes, nbCol, nbTypes);
                plateau.afficherPlateau(fenetre, margeX, margeY);
            } else if (choix == 4) {
                continuer = false;
                fenetre.dispose();
            }
        }

    }
}
