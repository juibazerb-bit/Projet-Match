/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Plateau;

/**
 *
 * @author flo66
 */
public class TestPlateauConsole {

    public static void main(String[] args) {
        int nbLignes = 5;
        int nbCol = 5;
        int nbTypes = 5;

        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes);

        System.out.println("=== Jeu de Match // CandyCrush ===");
        System.out.println(plateau.afficher());

        boolean continuer = true;
        while (continuer) {
            System.out.println("\nQue voulez-vous faire ?");
            System.out.println("1 - Jouer un coup");
            System.out.println("2 - Liste des echanges possibles");
            System.out.println("3 - Quitter");
            System.out.print("Votre choix : ");
            int choix = Clavier.Clavier.getInt();

            if (choix == 1) {
                plateau.jouerUnCoup();
                System.out.println(plateau.afficher());
            } else if (choix == 2) {
                System.out.println("Analyse des matchs possibles :");
                System.out.print(plateau.listMatchs());
            } else if (choix == 3) {
                continuer = false;
                System.out.println("Merci d'avoir joué :) ");
            }
        }
    }

}
