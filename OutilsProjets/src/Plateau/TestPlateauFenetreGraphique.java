/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Plateau;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import Tuile.Tuile;

/**
 *
 * @author fpauvert
 */
public class TestPlateauFenetreGraphique {

    public static void main(String[] args) {
        int nbLignes = 10;
        int nbCol = 10;
        int nbTypes = 5;
        int margeX = 100;
        int margeY = 100;

        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes,42L); // 42 = graine fixe

        int largeur = nbCol * Tuile.TAILLE + 300;
        int hauteur = nbLignes * Tuile.TAILLE + 300;
        FenetreGraphique fenetre = new FenetreGraphique("Candy Crush - Mode Graphique", largeur, hauteur);

        System.out.println("=== Jeu de Match // CandyCrush ===");
        plateau.afficherPlateau(fenetre, margeX, margeY);

        Coord premierClic = null;
        boolean continuer = true;

        while (continuer) {
            Coord clic = plateau.attendreClicOuBouton(fenetre, margeX, margeY);

            if (clic.getAbscisse() == -2) {
                System.out.println(plateau.listMatchs());
                premierClic = null;
            } else if (clic.getAbscisse() == -3) {
                plateau = new Plateau(nbLignes, nbCol, nbTypes);
                plateau.afficherPlateau(fenetre, margeX, margeY);
                premierClic = null;
            } else if (clic.getAbscisse() == -4) {
                continuer = false;
                fenetre.dispose();
            } else {
                // Clic sur la grille
                if (premierClic == null) {
                    premierClic = clic;
                    System.out.println("Premier clic : " + premierClic);
                } else {
                    plateau.jouerUnCoup(premierClic, clic);
                    plateau.afficherPlateau(fenetre, margeX, margeY);
                    premierClic = null;
                }
            }
        }

    }
}
