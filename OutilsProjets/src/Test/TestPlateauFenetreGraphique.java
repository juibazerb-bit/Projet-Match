/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import Affichage.Animation;
import Affichage.DessinPlateau;
import Controleur.GestionClics;
import Controleur.GestionPartie;
import Modele.Coord;
import FenetreGraphique.FenetreGraphique;
import LogiqueJeu.DetectionMatchs;
import LogiqueJeu.GestionIA;
import LogiqueJeu.SuppressionMatchs;
import Modele.Plateau;
import Modele.Tuile;
import java.util.ArrayList;

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

        Plateau plateau = new Plateau(nbLignes, nbCol, nbTypes, 0); // 42 = graine fixe
        GestionIA ia = new GestionIA();
        GestionPartie gestionPartie = new GestionPartie();
        Animation animation = new Animation();
        GestionClics gestionClics = new GestionClics();
        DessinPlateau dessinPlateau = new DessinPlateau();
        DetectionMatchs detectionMatchs = new DetectionMatchs();
        SuppressionMatchs suppressionMatchs = new SuppressionMatchs();
        int largeur = nbCol * Tuile.TAILLE + 300;
        int hauteur = nbLignes * Tuile.TAILLE + 300;
        FenetreGraphique fenetre = new FenetreGraphique("Candy Crush - Mode Graphique", largeur, hauteur);
        Animation.clearConsole();

        System.out.println("=== Jeu de Match // CandyCrush ===");
        dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);

        Coord premierClic = null;
        boolean continuer = true;

        while (continuer) {
            Coord clic = gestionClics.attendreClicOuBouton(plateau, fenetre, margeX, margeY);

            if (clic.getAbscisse() == -2) {
                System.out.println(ia.listMatchs(plateau));
                premierClic = null;
            } else if (clic.getAbscisse() == -3) {
                plateau = new Plateau(nbLignes, nbCol, nbTypes);
                dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
                premierClic = null;
            } else if (clic.getAbscisse() == -4) {
                continuer = false;
                fenetre.dispose();
            } else if (clic.getAbscisse() == -10) {
                // Changer le nombre de lignes
                int nouvLig = Math.max(3, plateau.getNbLig() + clic.getOrdonnee());
                fenetre.dispose(); // ferme l'ancienne fenetre
                fenetre = creerFenetre(nbCol, nouvLig, "Candy Crush - Mode Graphique");

                plateau = new Plateau(nbCol, nouvLig, nbTypes);
                dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
                premierClic = null;

            } else if (clic.getAbscisse() == -11) {
                // Changer le nombre de colonnes
                int nouvCol = Math.max(3, plateau.getNbCol() + clic.getOrdonnee());
                int nbLig = plateau.getNbLig();
                fenetre.dispose(); // ferme l'ancienne fenetre
                fenetre = creerFenetre(nouvCol, nbLig, "Candy Crush - Mode Graphique");

                plateau = new Plateau(nouvCol, nbLig, nbTypes);
                dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
                premierClic = null;
            } else {
                if (premierClic == null) {
                    premierClic = clic;
                } else {
                    animation.fixerPositionsActuelles(plateau, margeY);

                    //On tente l'échange
                    boolean echangeOk = plateau.echangerTuiles(premierClic, clic);

                    if (echangeOk && detectionMatchs.existeUnMatch(plateau)) {

                        //Boucle: clignotter -> supprimer -> animer -> recommencer
                        boolean encoreDesMatchs = true;
                        while (encoreDesMatchs) {

                            // Collecter les tuiles à supprimer
                            ArrayList<Modele.Coord> aSupprimer
                                    = suppressionMatchs.collecterToutesLesTuilesASupprimer(plateau);

                            if (aSupprimer.isEmpty()) {
                                encoreDesMatchs = false;
                            } else {
                                // clignotement de la tuile
                                for (int i = 0; i < 3; i++) {
                                    dessinPlateau.afficherPlateauClignotant(
                                            plateau, fenetre, margeX, margeY, aSupprimer, true);  // noir
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                    }

                                    dessinPlateau.afficherPlateauClignotant(
                                            plateau, fenetre, margeX, margeY, aSupprimer, false); // normal
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                    }
                                }
                                //  Supprimer 
                                plateau.getLesColonnes(); // accès aux colonnes
                                java.util.Random rand = new java.util.Random();

                                // On supprime colonne par colonne
                                for (int col = 0; col < plateau.getNbCol(); col++) {
                                    java.util.ArrayList<Integer> lignes = new java.util.ArrayList<>();
                                    for (Modele.Coord c : aSupprimer) {
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
                                animation.animerChute(plateau, fenetre, margeX, margeY);

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
                        dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
                    }

                    premierClic = null;
                }
            }
        }
    }

    private static FenetreGraphique creerFenetre(int nbCol, int nbLig, String titre) {
        int largeur = nbCol * Tuile.TAILLE + 300;
        int hauteur = nbLig * Tuile.TAILLE + 300;
        return new FenetreGraphique(titre, largeur, hauteur);
    }

}
