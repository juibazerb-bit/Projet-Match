/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import Plateau.GestionIA;
import Plateau.Plateau;
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
        int margeX = 100;
        int margeY = 100;

        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes, 42L); // 42 = graine fixe
        GestionIA ia = new GestionIA();
        int largeur = nbCol * Tuile.TAILLE + 300;
        int hauteur = nbLignes * Tuile.TAILLE + 300;
        FenetreGraphique fenetre = new FenetreGraphique("Candy Crush - Mode Graphique", largeur, hauteur);

        System.out.println("=== Jeu de Match // CandyCrush ===");
        plateau.getGestionGraphique().afficherPlateau(plateau, fenetre, margeX, margeY);

        Coord premierClic = null;
        boolean continuer = true;

        while (continuer) {
            Coord clic = plateau.getGestionGraphique().attendreClicOuBouton(plateau, fenetre, margeX, margeY);

            if (clic.getAbscisse() == -2) {
                System.out.println(ia.listMatchs(plateau));
                premierClic = null;
            } else if (clic.getAbscisse() == -3) {
                plateau = new Plateau(nbLignes, nbCol, nbTypes);
                plateau.getGestionGraphique().afficherPlateau(plateau, fenetre, margeX, margeY);
                premierClic = null;
            } else if (clic.getAbscisse() == -4) {
                continuer = false;
                fenetre.dispose();
            } else {
                if (premierClic == null) {
                    premierClic = clic;
                } else {
                    // 1. On "mémorise" où sont les tuiles AVANT le mouvement
                    plateau.getGestionGraphique().fixerPositionsActuelles(plateau, margeY);

                    // 2. On fait le calcul logique (suppression/descente dans les listes)
                    plateau.jouerUnCoup(premierClic, clic);

                    // 3. On anime le passage de l'ancienne position à la nouvelle
                    plateau.getGestionGraphique().animerChute(plateau, fenetre, margeX, margeY);

                    premierClic = null;
                }
            }
        }
    }

}
