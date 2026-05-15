/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Affichage;

import FenetreGraphique.FenetreGraphique;
import Modele.Plateau;
import Modele.Tuile;

/**
 *
 * @author flo66
 */
public class Animation {
    private DessinPlateau dessinPlateau = new DessinPlateau();
    
    public void fixerPositionsActuelles(Plateau plateau, int margeY) {
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig(); lig++) {
                Tuile t = plateau.getTuile(col, lig);
                if (t != null) {
                    // On enregistre sa position Y actuelle en pixels
                    int yActuel = margeY + hauteurPlateau - (lig * Tuile.TAILLE);
                    t.setPosYVisuelle(yActuel);
                }
            }
        }
    }

    public void animerChute(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY) {
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        boolean enMouvement = true;
        double vitesse = 1.0; // pixels par frame
        double boostParLigne = 1.0;

        while (enMouvement) {
            enMouvement = false;

            for (int col = 0; col < plateau.getNbCol(); col++) {
                for (int lig = 0; lig < plateau.getNbLig(); lig++) {
                    Tuile t = plateau.getTuile(col, lig);
                    if (t == null) {
                        continue;
                    }

                    int yCible = margeY + hauteurPlateau - (lig * Tuile.TAILLE);
                    double vitesseTuile = vitesse + ((plateau.getNbLig() - lig) * boostParLigne);

                    // Nouvelle tuile : elle part du haut de la grille
                    if (t.getPosYVisuelle() == -1) {
                        // Plus la tuile est haute dans la grille (lig grand), plus elle part de loin
                        t.setPosYVisuelle(margeY - (lig - plateau.getNbCol() + 3) * Tuile.TAILLE / 2);
                        enMouvement = true;
                    }

                    // Déplacement vers la cible
                    if (t.getPosYVisuelle() < yCible) {
                        // On avance selon la vitesse propre à cette ligne
                        t.setPosYVisuelle(Math.min(yCible, t.getPosYVisuelle() + vitesseTuile));
                        enMouvement = true;
                    }
                }
            }

            dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);

            // Petite pause pour que l'animation soit visible
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
            }
        }
    }

    public void reinitialiserPositionsVisuelles(Plateau plateau) {
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig(); lig++) {
                plateau.getTuile(col, lig).setPosYVisuelle(-1);
            }
        }
    }
    public static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
