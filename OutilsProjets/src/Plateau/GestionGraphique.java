package Plateau;

import Coordonnees.Coord;
import FenetreGraphique.FenetreGraphique;
import Tuile.Tuile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class GestionGraphique {

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
        dessinerBouton(fenetre, "Coups possibles", boutonX, 60, 160, 30);
        dessinerBouton(fenetre, "Nouvelle partie", boutonX, 100, 160, 30);
        dessinerBouton(fenetre, "Quitter", boutonX, 140, 160, 30);

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
    // -------------------------------------------------------------------------
    // BOUTONS
    // -------------------------------------------------------------------------

    // Dessine un bouton dans la fenêtre
    public void dessinerBouton(FenetreGraphique fenetre, String texte, int x, int y, int largeur, int hauteur) {
        Graphics2D g = fenetre.getGraphics2D();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(texte, x + 10, y + hauteur / 2 + 5);
    }

    // Dessine un bouton et retourne true si le clic est dessus
    public boolean boutonClique(int clicX, int clicY, int x, int y, int largeur, int hauteur) {
        return clicX >= x && clicX <= x + largeur && clicY >= y && clicY <= y + hauteur;
    }

    // -------------------------------------------------------------------------
    // METHODES SUR CLIC
    // -------------------------------------------------------------------------
    public Coord clicVersCoord(Plateau plateau, int clicX, int clicY, int margeX, int margeY) {
        int col = (clicX - margeX) / Tuile.TAILLE;
        int basGrille = margeY + (plateau.getNbLig() + 1) * Tuile.TAILLE; // +1 pour le décalage
        int lig = (basGrille - clicY) / Tuile.TAILLE;

        if (col >= 0 && col < plateau.getNbCol() && lig >= 0 && lig < plateau.getNbLig()) {
            return new Coord(col, lig);
        }
        return null;
    }

    public Coord attendreClicOuBouton(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY) {
        // Valeurs spéciales retournées pour les boutons :
        // (-2, 0) = Coups possibles
        // (-3, 0) = Nouvelle partie  
        // (-4, 0) = Quitter
        // Coord valide = clic sur la grille
        int boutonX = margeX + plateau.getNbCol() * Tuile.TAILLE + 20;

        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int clicX = fenetre.getXDernierClic();
                int clicY = fenetre.getYDernierClic();
                fenetre.effacerDernierClic();

                if (boutonClique(clicX, clicY, boutonX, 60, 160, 30)) {
                    return new Coord(-2, 0);
                }
                if (boutonClique(clicX, clicY, boutonX, 100, 160, 30)) {
                    return new Coord(-3, 0);
                }
                if (boutonClique(clicX, clicY, boutonX, 140, 160, 30)) {
                    return new Coord(-4, 0);
                }

                Coord coord = clicVersCoord(plateau, clicX, clicY, margeX, margeY);
                if (coord != null) {
                    return coord;
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

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
                        t.setPosYVisuelle(margeY - (lig-plateau.getNbCol()+3) * Tuile.TAILLE/2);
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

    public int lireChoix(Plateau plateau, FenetreGraphique fenetre) {
        int boutonX = 20 + plateau.getNbCol() * Tuile.TAILLE + 20;

        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int clicX = fenetre.getXDernierClic();
                int clicY = fenetre.getYDernierClic();
                fenetre.effacerDernierClic();

                if (boutonClique(clicX, clicY, boutonX, 20, 160, 30)) {
                    return 1; // Jouer
                }
                if (boutonClique(clicX, clicY, boutonX, 60, 160, 30)) {
                    return 2; // Coups possibles
                }
                if (boutonClique(clicX, clicY, boutonX, 100, 160, 30)) {
                    return 3; // Nouvelle partie
                }
                if (boutonClique(clicX, clicY, boutonX, 140, 160, 30)) {
                    return 4; // Quitter
                }
            }
        }
    }

    public static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
