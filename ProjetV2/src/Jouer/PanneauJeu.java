package Jouer;

import Controleur.GestionClics;
import LogiqueJeu.GestionPartie;   // ← CORRECTION : était Controleur.GestionPartie
import Modele.Plateau;
import Modele.Coord;
import Modele.Tuile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import LogiqueJeu.GestionIA;

public class PanneauJeu extends JPanel implements MouseListener {

    private Plateau plateau;
    private int margeX = 10, margeY = 10;
    private Coord premierClic = null;
    private Runnable coupJouer;
    private GestionClics gestionClics = new GestionClics();
    private GestionPartie gestionPartie = new GestionPartie();
    private ArrayList<Coord> surbrillanceIA = new ArrayList<>();

    public PanneauJeu() {
        addMouseListener(this);
        setBackground(Color.WHITE);
    }

    public void setPlateau(Plateau p) {
        this.plateau = p;
        premierClic = null;         // ← CORRECTION : reset la sélection au changement de plateau
        surbrillanceIA.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (plateau == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        int hauteurPlateau = plateau.getNbLig() * Tuile.TAILLE;
        int largeurPlateau = plateau.getNbCol() * Tuile.TAILLE;
        int offsetX = margeX + Tuile.TAILLE;

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

        surbrillance(g2, offsetX);
        grille(g2, hauteurPlateau, largeurPlateau, offsetX);

        // Surbrillance IA (bleu)
        for (int i = 0; i + 1 < surbrillanceIA.size(); i += 2) {
            dessinerSurbrillanceBleu(g2, surbrillanceIA.get(i), offsetX);
            dessinerSurbrillanceBleu(g2, surbrillanceIA.get(i + 1), offsetX);
        }
    }

    private void dessinerSurbrillanceBleu(Graphics2D g2, Coord c, int offsetX) {
        int posX = offsetX + c.getAbscisse() * Tuile.TAILLE;
        int posY = margeY + (plateau.getNbLig() - c.getOrdonnee()) * Tuile.TAILLE;
        g2.setColor(new Color(0, 100, 255, 120));
        g2.fillRect(posX, posY, Tuile.TAILLE, Tuile.TAILLE);
        g2.setColor(Color.BLUE);
        g2.drawRect(posX, posY, Tuile.TAILLE - 1, Tuile.TAILLE - 1);
    }

    public void surbrillance(Graphics g2, int offsetX) {
        if (premierClic != null) {
            int posX = offsetX + premierClic.getAbscisse() * Tuile.TAILLE;
            int posY = margeY + (plateau.getNbLig() - premierClic.getOrdonnee()) * Tuile.TAILLE;
            g2.setColor(new Color(255, 255, 0, 100));
            g2.fillRect(posX, posY, Tuile.TAILLE, Tuile.TAILLE);
            g2.setColor(Color.YELLOW);
            g2.drawRect(posX, posY, Tuile.TAILLE - 1, Tuile.TAILLE - 1);
        }
    }

    public void grille(Graphics g2, int hauteurPlateau, int largeurPlateau, int offsetX) {
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

        Coord clic = gestionClics.clicVersCoord(plateau, e.getX() - Tuile.TAILLE, e.getY(), margeX, margeY);
        if (clic == null) {
            return;
        }

        if (premierClic == null) {
            premierClic = clic;
            surbrillanceIA.clear();
            repaint();
        } else if (clic.equals(premierClic)) {
            // ← CORRECTION : misclick sur la même tuile → déselectionner
            premierClic = null;
            repaint();
        } else {
            boolean coupValide = gestionPartie.jouerUnCoup(plateau, premierClic, clic);
            premierClic = null;
            repaint();
            if (coupJouer != null) {
                coupJouer.run();
            }
        }
    }

    public void setCoupJouer(Runnable callback) {
        this.coupJouer = callback;
    }

    public String aideOrdiString(Plateau plateau) {
        GestionIA ia = new GestionIA();
        ArrayList<Coord> matchs = ia.aideOrdi(plateau);
        surbrillanceIA.clear();
        surbrillanceIA.addAll(matchs);
        repaint();
        return matchs.isEmpty() ? "Aucun coup possible" : matchs.get(0) + " ↔ " + matchs.get(1);
    }

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
