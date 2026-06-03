package Jouer;

import Clavier.Clavier;
import LogiqueJeu.GestionIA;
import LogiqueJeu.GestionPartie;
import Modele.Coord;
import Modele.Plateau;

/**
 * Mode console : jouer sans interface graphique. Utile pour tester la logique
 * de jeu rapidement.
 */
public class JouerModeConsole {

    public static void main(String[] args) {
        int nbLignes = 10, nbCol = 10, nbTypes = 7;

        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes,false);
        GestionIA ia = new GestionIA();
        GestionPartie gestionPartie = new GestionPartie();

        System.out.println("=== CandyCrush Console ===");
        System.out.println(plateau.afficher());

        boolean continuer = true;
        while (continuer) {
            System.out.println("\n1 - Jouer un coup");
            System.out.println("2 - Liste des echanges possibles");
            System.out.println("3 - Meilleur coup (IA)");
            System.out.println("autre - Quitter");
            System.out.print("Choix : ");

            int choix = Clavier.getInt();

            switch (choix) {
                case 1:
                    System.out.println("Premiere tuile :");
                    Coord c1 = Clavier.getCoord();
                    System.out.println("Deuxieme tuile :");
                    Coord c2 = Clavier.getCoord();
                    gestionPartie.jouerUnCoup(plateau, c1, c2);
                    System.out.println(plateau.afficher());
                    break;

                case 2:
                    System.out.println(ia.listMatchsTexte(plateau));
                    break;

                case 3:
                    System.out.println("Meilleur coup : " + ia.aideOrdi(plateau));
                    break;

                default:
                    continuer = false;
                    System.out.println("Au revoir !");
                    break;
            }
        }
    }
}
