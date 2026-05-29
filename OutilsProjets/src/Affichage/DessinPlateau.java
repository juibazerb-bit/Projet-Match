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
import Plateau.Niveau;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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
        dessinBoutons.dessinerBouton(fenetre, "Meilleur Coup Statistique", boutonX, 180, 160, 30);
        dessinBoutons.dessinerBouton(fenetre, "Ordi Joue tout seul N coup", boutonX, 220, 160, 30);

        // A la fin de afficherPlateau, apres les boutons existants
        int compteurX = boutonX;
        dessinBoutons.dessinerCompteur(fenetre, compteurX, 290, 160, 90, "Lignes", plateau.getNbLig());
        dessinBoutons.dessinerCompteur(fenetre, compteurX, 390, 160, 90, "Colonnes", plateau.getNbCol());

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

    public void afficherInfosNiveau(Plateau plateau, FenetreGraphique fenetre,
            Niveau niveau, int coupsJoues, int margeX) {
        Graphics2D g = fenetre.getGraphics2D();
        int boutonX = margeX + plateau.getNbCol() * Tuile.TAILLE + 20;

        // Fond des infos
        g.setColor(new Color(230, 230, 250));
        g.fillRoundRect(boutonX - 5, 5, 170, 55, 10, 10);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(boutonX - 5, 5, 170, 55, 10, 10);

        // Niveau
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(50, 50, 150));
        g.drawString("Niveau " + niveau.getNumeroNiveau(), boutonX, 22);

        // Score / Objectif
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.BLACK);
        g.drawString("Score : " + plateau.getScore()
                + " / " + niveau.getScoreObjectif(), boutonX, 38);

        // Coups restants
        int coupsRestants = niveau.getNbCoupsMax() - coupsJoues;
        g.setColor(coupsRestants <= 3 ? Color.RED : Color.BLACK);
        g.drawString("Coups restants : " + coupsRestants, boutonX, 54);
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

    /**
     * Affiche le plateau normalement puis surligne les deux tuiles du meilleur
     * coup avec des couleurs distinctes. - La première tuile est entourée en
     * VERT - La deuxième tuile est entourée en BLEU - Une flèche est dessinée
     * entre les deux
     */
    public void afficherPlateauAvecAide(Plateau plateau, FenetreGraphique fenetre,
            int margeX, int margeY,
            ArrayList<Coord> meilleurCoup) {
        // 1. Affichage normal du plateau
        afficherPlateau(plateau, fenetre, margeX, margeY);

        if (meilleurCoup == null || meilleurCoup.size() < 2) {
            return;
        }

        Coord c1 = meilleurCoup.get(0);
        Coord c2 = meilleurCoup.get(1);

        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        Graphics2D g = fenetre.getGraphics2D();
        g.setStroke(new java.awt.BasicStroke(4));

        // 2. Surlignage tuile 1 en VERT
        int x1 = margeX + c1.getAbscisse() * Tuile.TAILLE;
        int y1 = margeY + hauteurPlateau - c1.getOrdonnee() * Tuile.TAILLE;
        g.setColor(java.awt.Color.GREEN);
        g.drawRoundRect(x1 + 3, y1 + 3, Tuile.TAILLE - 6, Tuile.TAILLE - 6, 8, 8);

        // 3. Surlignage tuile 2 en BLEU
        int x2 = margeX + c2.getAbscisse() * Tuile.TAILLE;
        int y2 = margeY + hauteurPlateau - c2.getOrdonnee()* Tuile.TAILLE;
        g.setColor(java.awt.Color.CYAN);
        g.drawRoundRect(x2 + 3, y2 + 3, Tuile.TAILLE - 6, Tuile.TAILLE - 6, 8, 8);

        // 4. Flèche entre les deux tuiles (centre de chaque tuile)
        int cx1 = x1 + Tuile.TAILLE / 2;
        int cy1 = y1 + Tuile.TAILLE / 2;
        int cx2 = x2 + Tuile.TAILLE / 2;
        int cy2 = y2 + Tuile.TAILLE / 2;
        g.setColor(java.awt.Color.YELLOW);
        g.setStroke(new java.awt.BasicStroke(2));
        g.drawLine(cx1, cy1, cx2, cy2);

        // Pointe de la flèche
        double angle = Math.atan2(cy2 - cy1, cx2 - cx1);
        int taillePointe = 10;
        int px1 = (int) (cx2 - taillePointe * Math.cos(angle - Math.PI / 6));
        int py1 = (int) (cy2 - taillePointe * Math.sin(angle - Math.PI / 6));
        int px2 = (int) (cx2 - taillePointe * Math.cos(angle + Math.PI / 6));
        int py2 = (int) (cy2 - taillePointe * Math.sin(angle + Math.PI / 6));
        g.drawLine(cx2, cy2, px1, py1);
        g.drawLine(cx2, cy2, px2, py2);

        // 5. Texte indicatif au-dessus de la première tuile
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        g.drawString("Meilleur coup !", margeX, margeY - 10);

        fenetre.actualiser();
    }
}
