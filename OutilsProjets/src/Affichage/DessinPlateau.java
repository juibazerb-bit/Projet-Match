/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Affichage;

import Controleur.GestionClics;
import FenetreGraphique.FenetreGraphique;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import java.util.ArrayList;

/**
 *
 * @author flo66
 */
public class DessinPlateau {

    private DessinBoutons dessinBoutons = new DessinBoutons();

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
        dessinBoutons.dessinerBouton(fenetre, "Coups possibles", boutonX, 60, 160, 30);
        dessinBoutons.dessinerBouton(fenetre, "Nouvelle partie", boutonX, 100, 160, 30);
        dessinBoutons.dessinerBouton(fenetre, "Quitter", boutonX, 140, 160, 30);

        // A la fin de afficherPlateau, apres les boutons existants
        int compteurX = boutonX;
        dessinBoutons.dessinerCompteur(fenetre, compteurX, 200, 160, 90, "Lignes", plateau.getNbLig());
        dessinBoutons.dessinerCompteur(fenetre, compteurX, 300, 160, 90, "Colonnes", plateau.getNbCol());

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

    public void afficherCaseCochée(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY, Coord c, boolean selectionnee) {

        // On affiche le plateau normalement d'abord
        this.afficherPlateau(plateau, fenetre, margeX, margeY);

        // On dessine par-dessus les rectangles noirs pour les tuiles concernées
        if (selectionnee) {
            fenetre.getGraphics2D().setColor(java.awt.Color.YELLOW);
            int x = margeX + c.getAbscisse() * Tuile.TAILLE;
            int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
            int y = margeY + hauteurPlateau - c.getOrdonnee() * Tuile.TAILLE;
            fenetre.getGraphics2D().drawRect(x, y, Tuile.TAILLE, Tuile.TAILLE);

        }

        // On force le rafraîchissement de la fenêtre
        fenetre.actualiser();
    }
}
