/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Plateau;

/**
 *
 * @author fpauvert
 */
public class TestPlateau {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Création d'un plateau de 10 colonnes, 10 lignes, 4 types de tuiles
        Plateau plateau = new Plateau(5, 5, 4);

        System.out.println("=== Jeu de Match // CandyCruch ===");
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
            }
            if (choix == 2) {
                System.out.println(plateau.afficher());
                System.out.print(plateau.listMatchs());
            }
//            else {
//                continuer = false;
//                System.out.println("Merci d'avoir joue :) ");
//            }
        }
    }

}
