/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Affichage;

import FenetreGraphique.FenetreGraphique;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author flo66
 */
public class DessinBoutons {
    public void dessinerBouton(FenetreGraphique fenetre, String texte, int x, int y, int largeur, int hauteur) {
        Graphics2D g = fenetre.getGraphics2D();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(texte, x + 10, y + hauteur / 2 + 5);
    }

    // Dessine le compteur avec les zones + et -
    public void dessinerCompteur(FenetreGraphique fenetre, int x, int y,
            int largeur, int hauteur, String label, int valeur) {
        Graphics2D g = fenetre.getGraphics2D();
        int tiers = hauteur / 3;

        // Fond general
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);

        // Bouton +
        g.setColor(new Color(100, 200, 100));
        g.fillRoundRect(x + 2, y + 2, largeur - 4, tiers - 2, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("+", x + largeur / 2 - 5, y + tiers - 5);

        // Valeur au centre
        g.setColor(Color.WHITE);
        g.fillRect(x + 2, y + tiers, largeur - 4, tiers);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString(label + ": " + valeur, x + 5, y + tiers + tiers / 2 + 5);

        // Bouton -
        g.setColor(new Color(200, 100, 100));
        g.fillRoundRect(x + 2, y + 2 * tiers + 2, largeur - 4, tiers - 4, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("-", x + largeur / 2 - 4, y + hauteur - 8);
    }
}
