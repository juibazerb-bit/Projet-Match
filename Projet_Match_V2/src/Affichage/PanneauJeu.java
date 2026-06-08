package Affichage;

import Controleur.GestionClics;
import LogiqueJeu.GestionPartie;
import LogiqueJeu.SuppressionMatchs;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import LogiqueJeu.GestionIA;
import Sons.SonManager;

/**
 * Panneau de jeu Swing avec : - Animation de chute des tuiles ciblée (Timer
 * Swing, thread-safe) - Effet de clignotement noir avant disparition -
 * Surbrillance jaune (sélection) et bleue (aide IA) - Grille dark-theme -
 * Callback onCoupJoue déclenché après chaque coup valide
 */
public class PanneauJeu extends JPanel implements MouseListener {

    // ── Palette dark ──────────────────────────────────────────────────
    private static final Color GRID_COLOR = new Color(0x2A2A40);
    private static final Color SEL_BORDER = new Color(0xFFFF00);
    private static final Color IA_BORDER = new Color(0x6699FF);
    private static final Color FLASH_COLOR = Color.BLACK;

    // ── Constantes animation ──────────────────────────────────────────
    private static final int FPS = 60;
    private static final int TIMER_DELAY = 1000 / FPS;
    private static final double GRAVITE = 10;   // px/frame
    private static final double BOOST_LENTE = 3;   // px supplémentaires par ligne de hauteur
    private static final int NB_CLIGNOTS = 4;     // nb d'alternances avant suppression
    private static final int CLIGNOT_MS = 90;    // durée d'une demi-alternance

    // ── État jeu ──────────────────────────────────────────────────────
    private Plateau plateau;
    private Coord premierClic = null;
    private Runnable onCoupJoue;
    private ArrayList<Coord> surbrillanceIA = new ArrayList<>();
    private int tailleTuile = Tuile.TAILLE;

    // ── Animation chute ───────────────────────────────────────────────
    private double[][] posY;
    private boolean animEnCours = false;
    private Timer animTimer;

    // ── Clignotement ─────────────────────────────────────────────────
    private ArrayList<Coord> tuilesAClignote = new ArrayList<>();
    private boolean flashVisible = false;
    private int flashEtape = 0;
    private Timer flashTimer;
    private Runnable apresFlash;

    // ── Services ──────────────────────────────────────────────────────
    private final GestionClics gestionClics = new GestionClics();
    private final SuppressionMatchs suppression = new SuppressionMatchs();

    private int margeX = 20, margeY = 20;

    // ─────────────────────────────────────────────────────────────────
    public PanneauJeu() {
        addMouseListener(this);
        setBackground(Color.WHITE);
    }

    public void setPlateau(Plateau p) {
        this.plateau = p;
        premierClic = null;
        surbrillanceIA.clear();
        tuilesAClignote.clear();
        animEnCours = false;
        if (animTimer != null) {
            animTimer.stop();
            animTimer = null;
        }
        if (flashTimer != null) {
            flashTimer.stop();
            flashTimer = null;
        }
        initialiserPosY();
        revalidate();
        repaint();
    }

    public int getTailleTuile() {
        return tailleTuile;
    }

    public void setTailleTuile(int taille) {
        this.tailleTuile = taille;
        initialiserPosY();
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (plateau == null) {
            return new Dimension(400, 400);
        }
        int w = margeX + tailleTuile + plateau.getNbCol() * tailleTuile + margeX;
        int h = margeY + (plateau.getNbLig() + 1) * tailleTuile + margeY;
        return new Dimension(w, h);
    }

    // ══════════════════════════════════════════════════════════════════
    // DESSIN
    // ══════════════════════════════════════════════════════════════════
    // ══ paintComponent ═══════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (plateau == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        int offsetX = margeX;
        int offsetY = margeY;

        int nbLig = plateau.getNbLig();
        int nbCol = plateau.getNbCol();
        int hPlateau = nbLig * tailleTuile;
        int lPlateau = nbCol * tailleTuile;

        for (int lig = nbLig - 1; lig >= 0; lig--) {
            for (int col = 0; col < nbCol; col++) {
                Tuile t = plateau.getTuile(col, lig);
                if (t == null) {
                    continue;
                }

                int posX = offsetX + col * tailleTuile;
                int cibleY = offsetY + (nbLig - 1 - lig) * tailleTuile;

                int drawY;
                if (animEnCours && posY != null
                        && col < posY.length && lig < posY[col].length
                        && posY[col][lig] != -9999) {
                    drawY = (int) posY[col][lig];
                } else {
                    drawY = cibleY;
                }

                g2.setClip(offsetX, offsetY, lPlateau, hPlateau);
                t.dessiner(posX, drawY, g2);
                g2.setClip(null);

                if (flashVisible && contientCoord(tuilesAClignote, col, lig)) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                    g2.setColor(FLASH_COLOR);
                    g2.fillRect(posX, cibleY, tailleTuile, tailleTuile);
                    g2.setComposite(AlphaComposite.SrcOver);
                }
            }
        }

        // Surbrillance sélection
        if (premierClic != null) {
            int px = offsetX + premierClic.getAbscisse() * tailleTuile;
            // ← CORRIGÉ : même formule que cibleY
            int py = offsetY + (nbLig - 1 - premierClic.getOrdonnee()) * tailleTuile;
            g2.setColor(new Color(255, 255, 0, 70));
            g2.fillRect(px, py, tailleTuile, tailleTuile);
            g2.setColor(SEL_BORDER);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRect(px + 2, py + 2, tailleTuile - 4, tailleTuile - 4);
            g2.setStroke(new BasicStroke(1f));
        }

        // Surbrillance IA
        for (int i = 0; i + 1 < surbrillanceIA.size(); i += 2) {
            dessinerSurbrillanceIA(g2, surbrillanceIA.get(i), offsetX, offsetY, nbLig, true);
            dessinerSurbrillanceIA(g2, surbrillanceIA.get(i + 1), offsetX, offsetY, nbLig, false);
        }

        // Grille
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(0.8f));
        for (int i = 0; i <= nbLig; i++) {
            int y = offsetY + i * tailleTuile;
            g2.drawLine(offsetX, y, offsetX + lPlateau, y);
        }
        for (int j = 0; j <= nbCol; j++) {
            int x = offsetX + j * tailleTuile;
            g2.drawLine(x, offsetY, x, offsetY + hPlateau);
        }

        g2.dispose();
    }

// ── dessinerSurbrillanceIA ────────────────────────────────────────
    private void dessinerSurbrillanceIA(Graphics2D g2, Coord c,
            int offsetX, int offsetY, int nbLig, boolean isPrimary) {
        int px = offsetX + c.getAbscisse() * tailleTuile;
        int py = offsetY + (nbLig - 1 - c.getOrdonnee()) * tailleTuile;
        g2.setColor(new Color(68, 102, 255, isPrimary ? 100 : 60));
        g2.fillRect(px, py, tailleTuile, tailleTuile);
        g2.setColor(isPrimary ? new Color(0x88AAFF) : IA_BORDER);
        g2.setStroke(new BasicStroke(isPrimary ? 2.5f : 1.5f));
        g2.drawRect(px + 1, py + 1, tailleTuile - 2, tailleTuile - 2);
        g2.setStroke(new BasicStroke(1f));
    }

// ══ lancerAnimationChute ═════════════════════════════════════════
    public void lancerAnimationChute(ArrayList<Coord> aSupprimer, Runnable apresAnimation) {
        if (plateau == null) {
            if (apresAnimation != null) {
                apresAnimation.run();
            }
            return;
        }
        int nbCol = plateau.getNbCol();
        int nbLig = plateau.getNbLig();
        posY = new double[nbCol][nbLig];

        int localOffsetY = margeY;

        for (int col = 0; col < nbCol; col++) {
            boolean[] supprDansCol = new boolean[nbLig];
            for (Coord c : aSupprimer) {
                if (c.getAbscisse() == col) {
                    int l = c.getOrdonnee();
                    if (l >= 0 && l < nbLig) {
                        supprDansCol[l] = true;
                    }
                }
            }
            ArrayList<Integer> anciennesLignesRestantes = new ArrayList<>();
            for (int lig = 0; lig < nbLig; lig++) {
                if (!supprDansCol[lig]) {
                    anciennesLignesRestantes.add(lig);
                }
            }
            int nbRestantes = anciennesLignesRestantes.size();

            for (int lig = 0; lig < nbLig; lig++) {
                if (lig < nbRestantes) {
                    int ancienneLig = anciennesLignesRestantes.get(lig);
                    if (ancienneLig == lig) {
                        posY[col][lig] = -9999;
                    } else {
                        posY[col][lig] = localOffsetY + (nbLig - 1 - ancienneLig) * tailleTuile;
                    }
                } else {
                    int indexNouvelle = lig - nbRestantes;
                    // Nouvelles tuiles arrivent du haut (y négatif)
                    posY[col][lig] = localOffsetY - (indexNouvelle + 1) * tailleTuile;
                }
            }
        }

        animEnCours = true;
        if (animTimer != null) {
            animTimer.stop();
        }
        animTimer = new Timer(TIMER_DELAY, null);
        animTimer.addActionListener(e -> {
            boolean encoreMouvement = false;
            int nbL = plateau.getNbLig();
            int nbC = plateau.getNbCol();
            int currentOffsetY = margeY;

            for (int col = 0; col < nbC; col++) {
                for (int lig = 0; lig < nbL; lig++) {
                    if (posY[col][lig] != -9999) {
                        int cible = currentOffsetY + (nbL - 1 - lig) * tailleTuile;
                        if (posY[col][lig] < cible) {
                            double vitesse = GRAVITE + (nbL - lig) * BOOST_LENTE;
                            posY[col][lig] = Math.min(cible, posY[col][lig] + vitesse);
                            if (posY[col][lig] < cible) {
                                encoreMouvement = true;
                            }
                        }
                    }
                }
            }
            repaint();
            if (!encoreMouvement) {
                animTimer.stop();
                animEnCours = false;
                initialiserPosY();
                repaint();
                if (apresAnimation != null) {
                    SwingUtilities.invokeLater(apresAnimation);
                }
            }
        });
        animTimer.start();
    }

    // ══════════════════════════════════════════════════════════════════
    // ANIMATION CHUTE
    // ══════════════════════════════════════════════════════════════════
    private void initialiserPosY() {
        if (plateau == null) {
            return;
        }
        int nbCol = plateau.getNbCol();
        int nbLig = plateau.getNbLig();
        posY = new double[nbCol][nbLig];
        for (int col = 0; col < nbCol; col++) {
            for (int lig = 0; lig < nbLig; lig++) {
                posY[col][lig] = -9999;
            }
        }
    }

    public void lancerFlash(ArrayList<Coord> coordsAFlasher, Runnable suite) {
        if (coordsAFlasher == null || coordsAFlasher.isEmpty()) {
            if (suite != null) {
                suite.run();
            }
            return;
        }
        tuilesAClignote = new ArrayList<>(coordsAFlasher);
        flashEtape = 0;
        flashVisible = true;
        apresFlash = suite;

        if (flashTimer != null) {
            flashTimer.stop();
        }
        flashTimer = new Timer(CLIGNOT_MS, null);
        flashTimer.addActionListener(e -> {
            flashEtape++;
            flashVisible = (flashEtape % 2 == 1);
            repaint();
            if (flashEtape >= NB_CLIGNOTS * 2) {
                flashTimer.stop();
                flashVisible = false;
                tuilesAClignote.clear();
                repaint();
                if (apresFlash != null) {
                    SwingUtilities.invokeLater(apresFlash);
                }
            }
        });
        flashTimer.start();
    }

    // ══════════════════════════════════════════════════════════════════
    // CLIC SOURIS
    // ══════════════════════════════════════════════════════════════════
    @Override
    public void mouseClicked(MouseEvent e) {
        if (plateau == null || animEnCours || (flashTimer != null && flashTimer.isRunning())) {
            return;
        }

        // clicVersCoord soustrait lui-même margeX/margeY → on passe les coords brutes
        Coord clic = gestionClics.clicVersCoord(plateau, e.getX(), e.getY() + tailleTuile, margeX, margeY);
        if (clic == null) {
            return;
        }

        if (premierClic == null) {
            premierClic = clic;
            surbrillanceIA.clear();
            repaint();
        } else if (clic.equals(premierClic)) {
            premierClic = null;
            repaint();
        } else {
            Coord c1 = premierClic;
            Coord c2 = clic;
            premierClic = null;
            surbrillanceIA.clear();
            jouerCoupAvecAnimation(c1, c2);
        }
    }

    private void jouerCoupAvecAnimation(Coord c1, Coord c2) {
        plateau.echangerTuiles(c1, c2);
        ArrayList<Coord> premiersMatchs = suppression.collecterToutesLesTuilesASupprimer(plateau);

        if (premiersMatchs.isEmpty()) {
            plateau.echangerTuiles(c2, c1);
            repaint();
            return;
        }

        lancerFlashPuisSupprimerPuisChute(premiersMatchs, () -> {
            if (onCoupJoue != null) {
                SwingUtilities.invokeLater(onCoupJoue);
            }
        });
    }

    private void lancerFlashPuisSupprimerPuisChute(ArrayList<Coord> aSupprimer, Runnable apres) {
        lancerFlash(aSupprimer, () -> {
            suppression.supprimerCoords(plateau, aSupprimer, new Random());
            lancerAnimationChute(aSupprimer, () -> lancerCascade(apres));
        });
    }

    public void lancerCascade(Runnable apres) {
        ArrayList<Coord> aSupprimer = suppression.collecterToutesLesTuilesASupprimer(plateau);
        if (aSupprimer.isEmpty()) {
            repaint();
            if (apres != null) {
                apres.run();
            }
            return;
        }
        lancerFlashPuisSupprimerPuisChute(aSupprimer, apres);
    }

    public void jouerCoup(Coord c1, Coord c2) {
        if (plateau == null || isAnimEnCours()) {
            return;
        }
        premierClic = null;
        surbrillanceIA.clear();
        jouerCoupAvecAnimation(c1, c2);
    }

    // ── Accesseurs ────────────────────────────────────────────────────
    public void setCoupJouer(Runnable callback) {
        this.onCoupJoue = callback;
    }

    public String aideOrdiString(Plateau plateau) {
        GestionIA ia = new GestionIA();
        ArrayList<Coord> matchs = ia.aideOrdi(plateau);
        surbrillanceIA.clear();
        surbrillanceIA.addAll(matchs);
        repaint();
        return matchs.isEmpty() ? "Aucun coup possible" : matchs.get(0) + " ↔ " + matchs.get(1);
    }

    public boolean isAnimEnCours() {
        return animEnCours || (flashTimer != null && flashTimer.isRunning());
    }

    // ── Utils ─────────────────────────────────────────────────────────
    private boolean contientCoord(ArrayList<Coord> liste, int col, int lig) {
        for (Coord c : liste) {
            if (c.getAbscisse() == col && c.getOrdonnee() == lig) {
                return true;
            }
        }
        return false;
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
