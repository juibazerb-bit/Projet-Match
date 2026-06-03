package Affichage;

import FenetreGraphique.FenetreGraphique;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Dessine les boutons et compteurs de l'interface FenetreGraphique.
 */
public class DessinBoutons {

    private static final Font FONTE_BOUTON   = new Font("Arial", Font.BOLD, 12);
    private static final Font FONTE_COMPTEUR = new Font("Arial", Font.BOLD, 13);
    private static final Font FONTE_SIGNE    = new Font("Arial", Font.BOLD, 14);

    /** Dessine un bouton rectangulaire arrondi avec du texte. */
    public void dessinerBouton(FenetreGraphique fenetre, String texte,
            int x, int y, int largeur, int hauteur) {
        Graphics2D g = fenetre.getGraphics2D();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setFont(FONTE_BOUTON);
        g.drawString(texte, x + 10, y + hauteur / 2 + 5);
    }

    /**
     * Dessine un compteur avec trois zones empilées :
     *  - zone verte  (+) en haut
     *  - valeur      au centre
     *  - zone rouge  (-) en bas
     */
    public void dessinerCompteur(FenetreGraphique fenetre,
            int x, int y, int largeur, int hauteur,
            String label, int valeur) {
        Graphics2D g = fenetre.getGraphics2D();
        int tiers = hauteur / 3;

        // Fond général
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);

        // Bouton +
        g.setColor(new Color(100, 200, 100));
        g.fillRoundRect(x + 2, y + 2, largeur - 4, tiers - 2, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(FONTE_SIGNE);
        g.drawString("+", x + largeur / 2 - 5, y + tiers - 5);

        // Valeur centrale
        g.setColor(Color.WHITE);
        g.fillRect(x + 2, y + tiers, largeur - 4, tiers);
        g.setColor(Color.BLACK);
        g.setFont(FONTE_COMPTEUR);
        g.drawString(label + ": " + valeur, x + 5, y + tiers + tiers / 2 + 5);

        // Bouton -
        g.setColor(new Color(200, 100, 100));
        g.fillRoundRect(x + 2, y + 2 * tiers + 2, largeur - 4, tiers - 4, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(FONTE_SIGNE);
        g.drawString("-", x + largeur / 2 - 4, y + hauteur - 8);
    }
}
