/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plateau;

import Coordonnees.Coord;
import Tuile.Tuile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PanneauJeu extends JPanel implements MouseListener {

    private Plateau plateau;
    private GestionGraphique gestionGraphique = new GestionGraphique();
    private int margeX = 10, margeY = 10;  //position de la grille
    private Coord premierClic = null; // Pour gérer les deux clics successifs
    private Runnable coupJouer;

    public PanneauJeu() {
        addMouseListener(this);
        setBackground(Color.WHITE);
    }

    public void setPlateau(Plateau p) {
        this.plateau = p;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //efface le contenu precedent
        // le .super permet d'appeller la focntion d'origine 
        if (plateau == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        int largeurPlateau = plateau.getNbCol() * Tuile.TAILLE;

        int offsetX = margeX + Tuile.TAILLE; // décalage d'une case à droite

        // Dessin des tuiles
        for (int lig = plateau.getNbLig() - 1; lig >= 0; lig--) {
            for (int col = 0; col < plateau.getNbCol(); col++) {
                Tuile t = plateau.getTuile(col, lig);
                int posX = offsetX + col * Tuile.TAILLE;
                int posY = margeY + (plateau.getNbLig() - lig) * Tuile.TAILLE;
                t.setPosYVisuelle(-1);
                t.dessiner(posX, posY, g2);
            }
        }

        // Grille
        g2.setColor(Color.BLACK);
        for (int i = 0; i <= plateau.getNbLig(); i++) {
            int y = margeY + (i + 1) * Tuile.TAILLE;
            g2.drawLine(offsetX, y, offsetX + largeurPlateau, y);
        }
        for (int j = 0; j <= plateau.getNbCol(); j++) {
            int x = offsetX + j * Tuile.TAILLE;
            g2.drawLine(x, margeY + Tuile.TAILLE, x, margeY + hauteurPlateau + Tuile.TAILLE);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (plateau == null) {
            return;
        }
        Coord clic = gestionGraphique.clicVersCoord(plateau, e.getX() - Tuile.TAILLE, e.getY(), margeX, margeY);
        if (clic == null) {
            return;
        }
        
    // <editor-fold desc="à enlever a la TOUTE FIN: aide pour verifier que les trucs marche">
        System.out.println("Clic pixel : x=" + e.getX() + " y=" + e.getY());
        System.out.println("Coord tuile : " + (clic == null ? "NULL (hors plateau)" : "col=" + clic.getAbscisse() + " lig=" + clic.getOrdonnee()));
        // </editor-fold>
        

 // a la fin retirer les "System.out.println" si voulu
        if (premierClic == null) {
            premierClic = clic;
            System.out.println("Premier clic enregistré : col=" + premierClic.getAbscisse() + " lig=" + premierClic.getOrdonnee());
        } else {
            System.out.println("Tentative échange : (" + premierClic.getAbscisse() + "," + premierClic.getOrdonnee() + ") <-> (" + clic.getAbscisse() + "," + clic.getOrdonnee() + ")");
            plateau.jouerUnCoup(premierClic, clic);
            premierClic = null;
            repaint();
            // actualisation du score
            if (coupJouer != null) {
                coupJouer.run();
            }
        }
    }

    public void setCoupJouer(Runnable callback) {
        this.coupJouer = callback;
    }
    

    // Méthodes vides obligatoires de MouseListener
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
