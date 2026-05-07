/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import Plateau.GestionGraphique;
import Plateau.GestionIA;
import Plateau.Plateau;
import Tuile.Tuile;

/**
 *
 * @author fpauvert
 */
public class TestPlateauFenetreGraphique {

    public static void main(String[] args) {
        int nbLignes = 20;
        int nbCol = 20;
        int nbTypes = 7;
        int margeX = 100;
        int margeY = 100;

        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes,42); // 42 = graine fixe
        GestionIA ia = new GestionIA();
        int largeur = nbCol * Tuile.TAILLE + 300;
        int hauteur = nbLignes * Tuile.TAILLE + 300;
        FenetreGraphique fenetre = new FenetreGraphique("Candy Crush - Mode Graphique", largeur, hauteur);
        GestionGraphique.clearConsole();

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
                    plateau.getGestionGraphique().fixerPositionsActuelles(plateau, margeY);

                    //On tente l'échange
                    boolean echangeOk = plateau.echangerTuiles(premierClic, clic);

                    if (echangeOk && plateau.getGestionMatchs().existeUnMatch(plateau)) {

                        //Boucle visible : supprimer → animer → recommencer
                        boolean encoreDesMatchs = true;
                        while (encoreDesMatchs) {

                            // Collecter les tuiles à supprimer
                            java.util.ArrayList<Coordonnees.Coord> aSupprimer
                                    = plateau.getGestionMatchs().collecterToutesLesTuilesASupprimer(plateau);

                            if (aSupprimer.isEmpty()) {
                                encoreDesMatchs = false;
                            } else {
                                //  Supprimer logiquement 
                                plateau.getLesColonnes(); // accès aux colonnes
                                java.util.Random rand = new java.util.Random();

                                // On supprime colonne par colonne
                                for (int col = 0; col < plateau.getNbCol(); col++) {
                                    java.util.ArrayList<Integer> lignes = new java.util.ArrayList<>();
                                    for (Coordonnees.Coord c : aSupprimer) {
                                        if (c.getAbscisse() == col) {
                                            lignes.add(c.getOrdonnee());
                                        }
                                    }
                                    if (!lignes.isEmpty()) {
                                        lignes.sort((a, b) -> a - b);
                                        plateau.getLesColonnes()[col].supprimerTuiles(lignes, rand);
                                    }
                                }

                                // Animer la chute
                                plateau.getGestionGraphique().animerChute(plateau, fenetre, margeX, margeY);

                                // Petite pause entre les vagues pour que ce soit lisible
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                            }
                        }

                    } else if (echangeOk) {
                        // Échange sans match → on annule
                        System.out.println("Pas de match, annulation.");
                        plateau.echangerTuiles(clic, premierClic);
                        plateau.getGestionGraphique().afficherPlateau(plateau, fenetre, margeX, margeY);
                    }

                    premierClic = null;
                }
            }
        }
    }

}
