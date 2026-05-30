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

/**
 * Panneau de jeu Swing avec : - Animation de chute des tuiles ciblée (Timer
 * Swing, thread-safe) - Effet de clignotement noir avant disparition -
 * Surbrillance jaune (sélection) et bleue (aide IA) - Grille dark-theme -
 * Callback onCoupJoue déclenché après chaque coup valide
 */
public class PanneauJeu extends JPanel implements MouseListener {

    // ── Palette dark ──────────────────────────────────────────────────
    private static final Color BG_GRILLE = new Color(0x12121E);
    private static final Color GRID_COLOR = new Color(0x2A2A40);
    private static final Color SEL_FILL = new Color(0xFFFF00, true);  // jaune semi-transparent
    private static final Color SEL_BORDER = new Color(0xFFFF00);
    private static final Color IA_FILL = new Color(0x4466FF, true);
    private static final Color IA_BORDER = new Color(0x6699FF);
    private static final Color FLASH_COLOR = Color.BLACK; // Modification : Clignotement en noir

    // ── Constantes animation ──────────────────────────────────────────
    private static final int FPS = 60;
    private static final int TIMER_DELAY = 1000 / FPS;
    private static final double GRAVITE = 3.5;   // px/frame
    private static final double BOOST_LENTE = 0.8;   // px supplémentaires par ligne de hauteur
    private static final int NB_CLIGNOTS = 4;     // nb d'alternances avant suppression
    private static final int CLIGNOT_MS = 90;    // durée d'une demi-alternance

    // ── État jeu ──────────────────────────────────────────────────────
    private Plateau plateau;
    private Coord premierClic = null;
    private Runnable onCoupJoue;
    private ArrayList<Coord> surbrillanceIA = new ArrayList<>();

    // ── Animation chute ───────────────────────────────────────────────
    // posY[col][lig] = position Y visuelle courante (-1 = pas d'anim)
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
    private final GestionPartie gestionPartie = new GestionPartie();
    private final SuppressionMatchs suppression = new SuppressionMatchs();

    private int margeX = 20, margeY = 20;

    // ─────────────────────────────────────────────────────────────────
    public PanneauJeu() {
        addMouseListener(this);
        setBackground(BG_GRILLE);
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
        revalidate(); // recalcule preferredSize après changement de plateau
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (plateau == null) {
            return new Dimension(400, 400);
        }
        // paintComponent trace la grille de margeY+TAILLE à margeY+(nbLig+1)*TAILLE en Y
        // et de offsetX à offsetX+nbCol*TAILLE en X, avec offsetX = margeX+TAILLE
        int w = margeX + Tuile.TAILLE + plateau.getNbCol() * Tuile.TAILLE + margeX;
        int h = margeY + (plateau.getNbLig() + 1) * Tuile.TAILLE + margeY;
        return new Dimension(w, h);
    }

    // ══════════════════════════════════════════════════════════════════
    // DESSIN
    // ══════════════════════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (plateau == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int offsetX = margeX + Tuile.TAILLE;
        int nbLig = plateau.getNbLig();
        int nbCol = plateau.getNbCol();
        int hPlateau = nbLig * Tuile.TAILLE;
        int lPlateau = nbCol * Tuile.TAILLE;

        // Fond du plateau
        g2.setColor(new Color(0x0D0D18));
        g2.fillRoundRect(offsetX - 4, margeY + Tuile.TAILLE - 4,
                lPlateau + 8, hPlateau + 8, 8, 8);

        // Tuiles
        for (int lig = nbLig - 1; lig >= 0; lig--) {
            for (int col = 0; col < nbCol; col++) {
                Tuile t = plateau.getTuile(col, lig);
                if (t == null) {
                    continue;
                }

                int posX = offsetX + col * Tuile.TAILLE;
                int cibleY = margeY + (nbLig - lig) * Tuile.TAILLE;

                // Position Y : animée ou statique
                int drawY;
                if (animEnCours && posY != null && col < posY.length && lig < posY[col].length
                        && posY[col][lig] != -9999) {
                    drawY = (int) posY[col][lig];
                } else {
                    drawY = cibleY;
                }

                // Clip pour ne pas déborder au-dessus de la grille
                g2.setClip(offsetX, margeY + Tuile.TAILLE, lPlateau, hPlateau);
                t.dessiner(posX, drawY, g2);
                g2.setClip(null);

                // Flash clignotant noir (effet disparition)
                if (flashVisible && contientCoord(tuilesAClignote, col, lig)) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                    g2.setColor(FLASH_COLOR);
                    g2.fillRect(posX, cibleY, Tuile.TAILLE, Tuile.TAILLE);
                    g2.setComposite(AlphaComposite.SrcOver);
                }
            }
        }

        // Surbrillance sélection (jaune)
        if (premierClic != null) {
            int px = offsetX + premierClic.getAbscisse() * Tuile.TAILLE;
            int py = margeY + (nbLig - premierClic.getOrdonnee()) * Tuile.TAILLE;
            g2.setColor(new Color(255, 255, 0, 70));
            g2.fillRect(px, py, Tuile.TAILLE, Tuile.TAILLE);
            g2.setColor(SEL_BORDER);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRect(px + 2, py + 2, Tuile.TAILLE - 4, Tuile.TAILLE - 4);
            g2.setStroke(new BasicStroke(1f));
        }

        // Surbrillance IA (bleu pulsé)
        for (int i = 0; i + 1 < surbrillanceIA.size(); i += 2) {
            dessinerSurbrillanceIA(g2, surbrillanceIA.get(i), offsetX, nbLig, i == 0);
            dessinerSurbrillanceIA(g2, surbrillanceIA.get(i + 1), offsetX, nbLig, false);
        }

        // Grille
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(0.8f));
        for (int i = 0; i <= nbLig; i++) {
            int y = margeY + (i + 1) * Tuile.TAILLE;
            g2.drawLine(offsetX, y, offsetX + lPlateau, y);
        }
        for (int j = 0; j <= nbCol; j++) {
            int x = offsetX + j * Tuile.TAILLE;
            g2.drawLine(x, margeY + Tuile.TAILLE, x, margeY + hPlateau + Tuile.TAILLE);
        }

        g2.dispose();
    }

    private void dessinerSurbrillanceIA(Graphics2D g2, Coord c, int offsetX, int nbLig, boolean isPrimary) {
        int px = offsetX + c.getAbscisse() * Tuile.TAILLE;
        int py = margeY + (nbLig - c.getOrdonnee()) * Tuile.TAILLE;
        g2.setColor(new Color(68, 102, 255, isPrimary ? 100 : 60));
        g2.fillRect(px, py, Tuile.TAILLE, Tuile.TAILLE);
        g2.setColor(isPrimary ? new Color(0x88AAFF) : IA_BORDER);
        g2.setStroke(new BasicStroke(isPrimary ? 2.5f : 1.5f));
        g2.drawRect(px + 1, py + 1, Tuile.TAILLE - 2, Tuile.TAILLE - 2);
        g2.setStroke(new BasicStroke(1f));
    }

    // ══════════════════════════════════════════════════════════════════
    // ANIMATION CHUTE
    // ══════════════════════════════════════════════════════════════════
    /**
     * Initialise les positions visuelles Y à leur valeur "au repos".
     */
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

    /**
     * Lance l'animation de chute de manière ciblée après une suppression.
     * Seules les tuiles affectées ou nouvelles s'animent.
     */
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

        // Calcul intelligent des décalages colonne par colonne
        for (int col = 0; col < nbCol; col++) {
            // 1. On répertorie les lignes qui ont été supprimées dans cette colonne
            boolean[] supprDansCol = new boolean[nbLig];
            for (Coord c : aSupprimer) {
                if (c.getAbscisse() == col) {
                    int l = c.getOrdonnee();
                    if (l >= 0 && l < nbLig) {
                        supprDansCol[l] = true;
                    }
                }
            }

            // 2. On liste les anciennes lignes qui restent (qui n'ont pas été supprimées)
            ArrayList<Integer> anciennesLignesRestantes = new ArrayList<>();
            for (int lig = 0; lig < nbLig; lig++) {
                if (!supprDansCol[lig]) {
                    anciennesLignesRestantes.add(lig);
                }
            }

            int nbRestantes = anciennesLignesRestantes.size();

            // 3. On applique la position de départ visuelle sur le nouveau plateau nettoyé
            for (int lig = 0; lig < nbLig; lig++) {
                if (lig < nbRestantes) {
                    // C'est une ancienne tuile restée ou ayant glissé vers le bas
                    int ancienneLig = anciennesLignesRestantes.get(lig);
                    if (ancienneLig == lig) {
                        // Elle n'a pas bougé du tout -> Pas d'animation
                        posY[col][lig] = -9999;
                    } else {
                        // Elle a glissé. Sa position de départ visuelle correspond à son ancienne hauteur
                        posY[col][lig] = margeY + (nbLig - ancienneLig) * Tuile.TAILLE;
                    }
                } else {
                    // C'veut dire que c'est une NOUVELLE tuile apparue en haut du plateau
                    int indexNouvelle = lig - nbRestantes;
                    // On l'empile de manière fluide juste au-dessus de la grille
                    posY[col][lig] = margeY - indexNouvelle * Tuile.TAILLE;
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
            for (int col = 0; col < nbC; col++) {
                for (int lig = 0; lig < nbL; lig++) {
                    if (posY[col][lig] != -9999) { // On n'anime que celles concernées
                        int cible = margeY + (nbL - lig) * Tuile.TAILLE;
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

    /**
     * Anime le clignotement de {@code coordsAFlasher}, puis appelle
     * {@code suite}.
     */
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
        if (plateau == null || animEnCours || flashTimer != null && flashTimer.isRunning()) {
            return;
        }

        int offsetX = Tuile.TAILLE; // décalage du clicVersCoord
        Coord clic = gestionClics.clicVersCoord(plateau, e.getX() - offsetX, e.getY(), margeX, margeY);
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

    /**
     * Tente l'échange c1↔c2.
     * Séquence : échange visuel → collecte matchs → flash → suppression → chute → cascade.
     */
    private void jouerCoupAvecAnimation(Coord c1, Coord c2) {
        // 1. Effectue uniquement l'échange (sans supprimer) pour vérifier la validité
        boolean ok = gestionPartie.jouerUnCoup1(plateau, c1, c2);
        if (!ok) {
            repaint();
            return;
        }

        // 2. Collecte les matchs AVANT toute suppression
        ArrayList<Coord> premiersMatchs = suppression.collecterToutesLesTuilesASupprimer(plateau);
        if (premiersMatchs.isEmpty()) {
            // Échange invalide : on annule et on revient en arrière
            gestionPartie.jouerUnCoup1(plateau, c2, c1);
            repaint();
            return;
        }

        // 3. Flash → suppression → chute → cascade → callback fin de coup
        lancerFlashPuisSupprimerPuisChute(premiersMatchs, () -> {
            if (onCoupJoue != null) {
                SwingUtilities.invokeLater(onCoupJoue);
            }
        });
    }

    /**
     * Flash les tuiles marquées, les supprime, anime la chute, puis relance
     * une cascade si de nouveaux matchs apparaissent.
     */
    private void lancerFlashPuisSupprimerPuisChute(ArrayList<Coord> aSupprimer, Runnable apres) {
        lancerFlash(aSupprimer, () -> {
            suppression.supprimerCoords(plateau, aSupprimer, new Random());
            lancerAnimationChute(aSupprimer, () -> lancerCascade(apres));
        });
    }

    /**
     * Boucle de cascade : collecte les matchs, flash, supprime, anime la chute,
     * recommence jusqu'à plus rien.
     */
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

    /**
     * Méthode publique pour que l'IA puisse déclencher un coup directement
     * sans simuler des événements souris.
     */
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
