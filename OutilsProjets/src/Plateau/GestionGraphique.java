package Plateau;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import Tuile.Tuile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class GestionGraphique {
    private ClicEtBouton clicEtBouton = new ClicEtBouton();
    // -------------------------------------------------------------------------
    // AFFICHAGE GRAPHIQUE PLATEAU
    // -------------------------------------------------------------------------
    public void afficherPlateau(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY) {
        fenetre.effacer();
        int largeurPlateau = plateau.getNbCol() * Tuile.TAILLE;
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        for (int lig = plateau.getNbLig() - 1; lig >= 0; lig--) {
            for (int col = 0; col < plateau.getNbCol(); col++) {
                Tuile t = plateau.getTuile(col, lig);

                if (t != null) {
                    // Calcul de la position : on utilise la TAILLE de la tuile 
                    int posX = margeX + col * Tuile.TAILLE;
                    int posY = margeY + hauteurPlateau - lig * Tuile.TAILLE;

                    // On met à jour les coordonnées internes de la tuile si besoin
                    t.setCoordTuile(new Coord(posX, posY));

                    // On dessine
                    t.dessiner(fenetre, posX, posY);
                }
            }
        }

        // Dessin des boutons à droite de la grille
        int boutonX = 150 + plateau.getNbCol() * Tuile.TAILLE;
        clicEtBouton.dessinerBouton(fenetre, "Coups possibles", boutonX, 60, 160, 30);
        clicEtBouton.dessinerBouton(fenetre, "Nouvelle partie", boutonX, 100, 160, 30);
        clicEtBouton.dessinerBouton(fenetre, "Quitter", boutonX, 140, 160, 30);

        // A la fin de afficherPlateau, apres les boutons existants
        int compteurX = boutonX;
        clicEtBouton.dessinerCompteur(fenetre, compteurX, 200, 160, 90, "Lignes", plateau.getNbLig());
        clicEtBouton.dessinerCompteur(fenetre, compteurX, 300, 160, 90, "Colonnes", plateau.getNbCol());

        //grille de jeu
        // Dessine les lignes HORIZONTALES (de gauche à droite)
        for (int i = 0; i <= plateau.getNbLig(); i++) {
            int y = margeY + (i + 1) * Tuile.TAILLE;
            fenetre.getGraphics2D().drawLine(margeX, y, margeX + largeurPlateau, y);
        }

        // Dessine les lignes VERTICALES (de haut en bas)
        for (int j = 0; j <= plateau.getNbCol(); j++) {
            int x = margeX + j * Tuile.TAILLE;
            fenetre.getGraphics2D().drawLine(x, margeY + Tuile.TAILLE, x, margeY + hauteurPlateau + Tuile.TAILLE);
        }
        fenetre.actualiser();
    }

    public void afficherPlateauClignotant(Plateau plateau, FenetreGraphique fenetre,
            int margeX, int margeY,
            ArrayList<Coord> aNoircir, boolean enNoir) {
        // On affiche le plateau normalement d'abord
        this.afficherPlateau(plateau, fenetre, margeX, margeY);

        // On dessine par-dessus les rectangles noirs pour les tuiles concernées
        if (enNoir) {
            fenetre.getGraphics2D().setColor(java.awt.Color.BLACK);
            for (Coord c : aNoircir) {
                int x = margeX + c.getAbscisse() * Tuile.TAILLE;
                int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
                int y = margeY + hauteurPlateau - c.getOrdonnee() * Tuile.TAILLE;
                fenetre.getGraphics2D().fillRect(x, y, Tuile.TAILLE, Tuile.TAILLE);
            }
        }

        // On force le rafraîchissement de la fenêtre
        fenetre.actualiser();
    }
    // -------------------------------------------------------------------------
    // BOUTONS
    // -------------------------------------------------------------------------

    // Dessine un bouton dans la fenêtre
   

    // -------------------------------------------------------------------------
    // CHUTTE TUILE (REELLE)
    // -------------------------------------------------------------------------
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

            afficherPlateau(plateau, fenetre, margeX, margeY);

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
