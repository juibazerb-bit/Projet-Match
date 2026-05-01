/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Plateau;

import FenetreGraphique.FenetreGraphique;

/**
 *
 * @author fpauvert
 */
public class TestPlateau {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // 1. Initialisation des paramètres
        int nbLignes = 5;
        int nbCol = 5;
        int nbTypes = 4;

        // 2. Création du plateau
        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes);

        // 3. Création de la fenêtre graphique
        // On calcule la taille en fonction du nombre de tuiles (50 pixels par tuile)
        int largeur = nbCol * 50;
        int hauteur = nbLignes * 50;
        FenetreGraphique fenetre = new FenetreGraphique("Candy Crush - Mode Graphique", largeur, hauteur);

        System.out.println("=== Jeu de Match // CandyCruch ===");
        System.out.println(plateau.afficher()); // Console

        // Premier affichage graphique
        plateau.afficherPlateau(fenetre);

        boolean continuer = true;
        while (continuer) {
            System.out.println("\nQue voulez-vous faire ?");
            System.out.println("1 - Jouer un coup");
            System.out.println("2 - Quitter");
            System.out.print("Votre choix : ");

            int choix = Clavier.Clavier.getInt();

            if (choix == 1) {
                plateau.jouerUnCoup();

                // Mise à jour de l'affichage Console
                System.out.println(plateau.afficher());

                // Mise à jour de l'affichage Graphique
                plateau.afficherPlateau(fenetre);

            } else {
                continuer = false;
                System.out.println("Merci d'avoir joue :) ");
                // Optionnel : fermer la fenêtre à la fin
                fenetre.dispose();
            }
        }
    }

}
