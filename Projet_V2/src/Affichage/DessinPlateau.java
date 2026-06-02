package Affichage;

import FenetreGraphique.FenetreGraphique;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import Jouer.Niveau;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Responsable de tout le rendu graphique du plateau dans une FenetreGraphique.
 *
 * IMPORTANT – cohérence boutons :
 *   boutonX = margeX + nbCol * TAILLE + BOUTON_OFFSET
 *   Cette constante est la même ici (dessin) et dans GestionClics (détection).
 *
 * Méthodes principales :
 *  - afficherPlateau()                → dessin complet (tuiles + grille + boutons)
 *  - afficherPlateauAvecSelection()   → idem + case jaune sur la tuile sélectionnée
 *  - afficherPlateauClignotant()      → surligne certaines tuiles en noir
 *  - afficherPlateauAvecAide()        → surligne le meilleur coup avec une flèche
 *  - afficherInfosNiveau()            → bandeau score/coups en cours de partie
 */
public class DessinPlateau {

    /** Décalage horizontal entre le bord droit de la grille et le bord gauche des boutons.
     */
    public static final int BOUTON_OFFSET = 20;

    private final DessinBoutons dessinBoutons = new DessinBoutons();

    // -------------------------------------------------------------------------
    // AFFICHAGE COMPLET
    // -------------------------------------------------------------------------

    public void afficherPlateau(Plateau plateau, FenetreGraphique fenetre, int margeX, int margeY) {
        fenetre.effacer();
        Graphics2D g = fenetre.getGraphics2D();
        dessinerTuiles(plateau, g, margeX, margeY);
        dessinerGrille(plateau, g, margeX, margeY);
        dessinerBoutons(plateau, fenetre, margeX);
        fenetre.actualiser();
    }

    /**
     * Affiche le plateau et entoure en JAUNE la tuile sélectionnée par le joueur.
     * À appeler à la place de afficherPlateau() dès qu'un premier clic a eu lieu.
     *
     * @param selection coordonnée de la tuile sélectionnée (null = pas de sélection)
     */
    public void afficherPlateauAvecSelection(Plateau plateau, FenetreGraphique fenetre,
            int margeX, int margeY, Coord selection) {
        fenetre.effacer();
        Graphics2D g = fenetre.getGraphics2D();
        dessinerTuiles(plateau, g, margeX, margeY);

        // Case jaune épaisse sur la tuile sélectionnée
        if (selection != null) {
            int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
            int sx = margeX + selection.getAbscisse() * Tuile.TAILLE;
            int sy = margeY + hauteurPlateau - selection.getOrdonnee() * Tuile.TAILLE;

            // Remplissage semi-transparent jaune
            g.setColor(new Color(255, 255, 0, 60));
            g.fillRect(sx, sy, Tuile.TAILLE, Tuile.TAILLE);

            // Bordure jaune épaisse
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(4));
            g.drawRect(sx + 2, sy + 2, Tuile.TAILLE - 4, Tuile.TAILLE - 4);
            g.setStroke(new BasicStroke(1));
        }

        dessinerGrille(plateau, g, margeX, margeY);
        dessinerBoutons(plateau, fenetre, margeX);
        fenetre.actualiser();
    }

    public static int getBOUTON_OFFSET() {
        return BOUTON_OFFSET;
    }
    

    // -------------------------------------------------------------------------
    // VARIANTES D'AFFICHAGE
    // -------------------------------------------------------------------------

    /** Affiche le plateau puis noircit les tuiles de la liste si enNoir=true. */
    public void afficherPlateauClignotant(Plateau plateau, FenetreGraphique fenetre,
            int margeX, int margeY, ArrayList<Coord> aNoircir, boolean enNoir) {
        afficherPlateau(plateau, fenetre, margeX, margeY);
        if (enNoir) {
            Graphics2D g = fenetre.getGraphics2D();
            g.setColor(Color.BLACK);
            int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
            for (Coord c : aNoircir) {
                int x = margeX + c.getAbscisse() * Tuile.TAILLE;
                int y = margeY + hauteurPlateau - c.getOrdonnee() * Tuile.TAILLE;
                g.fillRect(x, y, Tuile.TAILLE, Tuile.TAILLE);
            }
        }
        fenetre.actualiser();
    }

    /** Affiche le plateau et surligne le meilleur coup avec une flèche. */
    public void afficherPlateauAvecAide(Plateau plateau, FenetreGraphique fenetre,
            int margeX, int margeY, ArrayList<Coord> meilleurCoup) {
        afficherPlateau(plateau, fenetre, margeX, margeY);
        if (meilleurCoup == null || meilleurCoup.size() < 2) return;

        Graphics2D g = fenetre.getGraphics2D();
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;

        Coord c1 = meilleurCoup.get(0);
        Coord c2 = meilleurCoup.get(1);
        int x1 = margeX + c1.getAbscisse() * Tuile.TAILLE;
        int y1 = margeY + hauteurPlateau - c1.getOrdonnee() * Tuile.TAILLE;
        int x2 = margeX + c2.getAbscisse() * Tuile.TAILLE;
        int y2 = margeY + hauteurPlateau - c2.getOrdonnee() * Tuile.TAILLE;

        g.setStroke(new BasicStroke(4));
        g.setColor(Color.GREEN);
        g.drawRoundRect(x1 + 3, y1 + 3, Tuile.TAILLE - 6, Tuile.TAILLE - 6, 8, 8);
        g.setColor(Color.CYAN);
        g.drawRoundRect(x2 + 3, y2 + 3, Tuile.TAILLE - 6, Tuile.TAILLE - 6, 8, 8);

        int cx1 = x1 + Tuile.TAILLE / 2;
        int cy1 = y1 + Tuile.TAILLE / 2;
        int cx2 = x2 + Tuile.TAILLE / 2;
        int cy2 = y2 + Tuile.TAILLE / 2;
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(2));
        g.drawLine(cx1, cy1, cx2, cy2);
        dessinerPointeDefleche(g, cx1, cy1, cx2, cy2);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("Meilleur coup !", margeX, margeY - 10);

        fenetre.actualiser();
    }

    /** Affiche le bandeau d'informations du niveau (score, coups restants). */
    public void afficherInfosNiveau(Plateau plateau, FenetreGraphique fenetre,
            Niveau niveau, int coupsJoues, int margeX) {
        Graphics2D g = fenetre.getGraphics2D();
        int x = margeX + plateau.getNbCol() * Tuile.TAILLE + BOUTON_OFFSET;

        g.setColor(new Color(230, 230, 250));
        g.fillRoundRect(x - 5, 5, 170, 55, 10, 10);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(x - 5, 5, 170, 55, 10, 10);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(50, 50, 150));
        g.drawString("Niveau " + niveau.getNumeroNiveau(), x, 22);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.BLACK);
        g.drawString("Score : " + plateau.getScore() + " / " + niveau.getScoreObjectif(), x, 38);

        int coupsRestants = niveau.getNbCoupsMax() - coupsJoues;
        g.setColor(coupsRestants <= 3 ? Color.RED : Color.BLACK);
        g.drawString("Coups restants : " + coupsRestants, x, 54);
    }

    // -------------------------------------------------------------------------
    // SOUS-MÉTHODES INTERNES
    // -------------------------------------------------------------------------

    private void dessinerTuiles(Plateau plateau, Graphics2D g, int margeX, int margeY) {
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        for (int lig = plateau.getNbLig() - 1; lig >= 0; lig--) {
            for (int col = 0; col < plateau.getNbCol(); col++) {
                Tuile t = plateau.getTuile(col, lig);
                if (t != null) {
                    int posX = margeX + col * Tuile.TAILLE;
                    int posY = margeY + hauteurPlateau - lig * Tuile.TAILLE;
                    t.dessiner(posX, posY, g);
                }
            }
        }
    }

    private void dessinerGrille(Plateau plateau, Graphics2D g, int margeX, int margeY) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        int largeur = plateau.getNbCol() * Tuile.TAILLE;
        int hauteur = plateau.getNbLig() * Tuile.TAILLE;

        for (int i = 0; i <= plateau.getNbLig(); i++) {
            int y = margeY + (i + 1) * Tuile.TAILLE;
            g.drawLine(margeX, y, margeX + largeur, y);
        }
        for (int j = 0; j <= plateau.getNbCol(); j++) {
            int x = margeX + j * Tuile.TAILLE;
            g.drawLine(x, margeY + Tuile.TAILLE, x, margeY + hauteur + Tuile.TAILLE);
        }
    }

    private void dessinerBoutons(Plateau plateau, FenetreGraphique fenetre, int margeX) {
        // boutonX = même calcul que dans GestionClics.attendreAction
        int bx = margeX + plateau.getNbCol() * Tuile.TAILLE + BOUTON_OFFSET;
        dessinBoutons.dessinerBouton(fenetre, "Coups possibles",           bx, 60,  160, 30);
        dessinBoutons.dessinerBouton(fenetre, "Nouvelle partie",           bx, 100, 160, 30);
        dessinBoutons.dessinerBouton(fenetre, "Quitter",                   bx, 140, 160, 30);
        dessinBoutons.dessinerBouton(fenetre, "Meilleur Coup Statistique", bx, 180, 160, 30);
        dessinBoutons.dessinerBouton(fenetre, "Ordi joue 10 coups",        bx, 220, 160, 30);
        // Compteur Lignes   → Y=290, hauteur=90  (même que GestionClics)
        dessinBoutons.dessinerCompteur(fenetre, bx, 290, 160, 90, "Lignes",   plateau.getNbLig());
        // Compteur Colonnes → Y=390, hauteur=90  (même que GestionClics)
        dessinBoutons.dessinerCompteur(fenetre, bx, 390, 160, 90, "Colonnes", plateau.getNbCol());
    }

    private void dessinerPointeDefleche(Graphics2D g, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int taille = 10;
        int px1 = (int) (x2 - taille * Math.cos(angle - Math.PI / 6));
        int py1 = (int) (y2 - taille * Math.sin(angle - Math.PI / 6));
        int px2 = (int) (x2 - taille * Math.cos(angle + Math.PI / 6));
        int py2 = (int) (y2 - taille * Math.sin(angle + Math.PI / 6));
        g.drawLine(x2, y2, px1, py1);
        g.drawLine(x2, y2, px2, py2);
    }
}
