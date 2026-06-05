package Jouer;

import Controleur.Niveau;
import LogiqueJeu.GestionIA;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import Affichage.PanneauJeu;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * Fenêtre principale – dark theme moderne.
 *
 * Nouveautés v3 : ▸ Animation de chute des tuiles (Timer Swing 60 fps) ▸
 * Clignotement avant suppression ▸ IA animée et respectueuse de la boucle
 * graphique ▸ Sélecteur du nombre de coups IA (spinner) ▸ Sélection de niveau
 * (écran dédié, niveaux 1-9 + libre)
 */
public class FenetreGraphiquePropreV2 extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────
    private static final Color BG_DEEP = new Color(0x0D0D14);
    private static final Color BG_CARD = new Color(0x161624);
    private static final Color BG_PANEL = new Color(0x1C1C2E);
    private static final Color ACCENT = new Color(0x7C5CFC);
    private static final Color ACCENT_GLOW = new Color(0xA07CFF);
    private static final Color ACCENT2 = new Color(0xFF5C8A);
    private static final Color TEXT_MAIN = new Color(0xEEEEFF);
    private static final Color TEXT_DIM = new Color(0x8888AA);
    private static final Color BORDER_COLOR = new Color(0x2E2E4A);
    private static final Color GREEN_OK = new Color(0x3DFFA0);
    private static final Color ORANGE_WARN = new Color(0xFFB347);
    private static final Color GOLD = new Color(0xFFD700);

    // ── État ──────────────────────────────────────────────────────────
    private Plateau plateau;
    private PanneauJeu panneauJeu;
    private GestionIA ia = new GestionIA();
    private Niveau niveauActuel = null;   // null = mode libre
    private int coupsJoues = 0;
    private int nbLig = 10, nbCol = 10, nbTypes = 5;

    // ── Vues ──────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel cardContainer;
    private JPanel menuPanel, levelPanel, gamePanel;

    // ── Widgets latéraux ──────────────────────────────────────────────
    private JLabel scoreValLabel, coupsValLabel, statusLabel;
    private JLabel objectifLabel, coupsMaxLabel;
    private JProgressBar scoreBar, coupsBar;
    private JSpinner spinIaCoups;
    private JSpinner spinLig, spinCol, spinTypes;
    private JPanel niveauInfoPanel;

    // ── Particules menu ───────────────────────────────────────────────
    private Timer particleTimer;
    private final java.util.List<float[]> particles = new java.util.ArrayList<>();

    // ─────────────────────────────────────────────────────────────────
    public FenetreGraphiquePropreV2() {
        super("✦ GemCrush");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1140, 740));
        setBackground(BG_DEEP);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DEEP);
        setContentPane(root);

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(BG_DEEP);

        menuPanel = buildMenuPanel();
        levelPanel = buildLevelPanel();
        gamePanel = buildGamePanel();

        cardContainer.add(menuPanel, "menu");
        cardContainer.add(levelPanel, "levels");
        cardContainer.add(gamePanel, "game");
        root.add(cardContainer, BorderLayout.CENTER);

        cardLayout.show(cardContainer, "menu");
        startParticleAnimation();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════
    // ÉCRAN MENU PRINCIPAL
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                RadialGradientPaint bg = new RadialGradientPaint(w / 2f, h * 0.4f,
                        Math.max(w, h) * 0.75f,
                        new float[]{0f, 0.55f, 1f},
                        new Color[]{new Color(0x1A1040), BG_DEEP, new Color(0x070710)});
                g2.setPaint(bg);
                g2.fillRect(0, 0, w, h);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                g2.setColor(ACCENT);
                for (int x = 0; x < w; x += 55) {
                    g2.drawLine(x, 0, x, h);
                }
                for (int y = 0; y < h; y += 55) {
                    g2.drawLine(0, y, w, y);
                }
                g2.setComposite(AlphaComposite.SrcOver);
                synchronized (particles) {
                    for (float[] p : particles) {
                        float alpha = p[4];
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.8f));
                        g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue()));
                        g2.fillOval((int) p[0], (int) p[1], (int) p[2], (int) p[2]);
                    }
                }
                g2.setComposite(AlphaComposite.SrcOver);
            }
        };
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel logo = makeGlowLabel("✦ GemCrush", new Font("Serif", Font.BOLD, 78), ACCENT_GLOW);
        JLabel sub = new JLabel("Alignez les gemmes — maîtrisez le plateau");
        sub.setFont(new Font("Serif", Font.ITALIC, 18));
        sub.setForeground(TEXT_DIM);

        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(320, 1));
        sep.setForeground(BORDER_COLOR);

        JButton btnNiveaux = makeMenuButton("🎮  Jouer par niveaux", ACCENT);
        JButton btnLibre = makeMenuButton("⚙  Mode libre", BG_CARD);
        JButton btnQuitter = makeMenuButton("✕  Quitter", new Color(0x3A1A2A));

        btnNiveaux.addActionListener(e -> {
            stopParticleAnimation();
            cardLayout.show(cardContainer, "levels");
        });
        btnLibre.addActionListener(e -> {
            stopParticleAnimation();
            lancerPartieLibre();
        });
        btnQuitter.addActionListener(e -> System.exit(0));

        JLabel credits = new JLabel("GemCrush v3.0 — Dark Edition");
        credits.setFont(new Font("Monospaced", Font.PLAIN, 11));
        credits.setForeground(new Color(0x333350));

        panel.add(logo, gbc);
        panel.add(sub, gbc);
        gbc.insets = new Insets(16, 0, 16, 0);
        panel.add(sep, gbc);
        gbc.insets = new Insets(7, 0, 7, 0);
        panel.add(btnNiveaux, gbc);
        panel.add(btnLibre, gbc);
        panel.add(btnQuitter, gbc);
        gbc.insets = new Insets(28, 0, 0, 0);
        panel.add(credits, gbc);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // ÉCRAN SÉLECTION NIVEAUX
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildLevelPanel() {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_DEEP);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.04f));
                g2.setColor(ACCENT);
                for (int x = 0; x < getWidth(); x += 50) {
                    g2.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += 50) {
                    g2.drawLine(0, y, getWidth(), y);
                }
                g2.setComposite(AlphaComposite.SrcOver);
            }
        };
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel titre = makeGlowLabel("Choisissez un niveau", new Font("Serif", Font.BOLD, 42), ACCENT_GLOW);
        titre.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titre, BorderLayout.CENTER);

        JButton btnRetour = makeTopButton("← Retour");
        btnRetour.addActionListener(e -> {
            cardLayout.show(cardContainer, "menu");
            startParticleAnimation();
        });
        topPanel.add(btnRetour, BorderLayout.WEST);

        JPanel grid = new JPanel(new GridLayout(3, 3, 18, 18));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(30, 0, 30, 0));

        String[] descriptions = {
            "5×5 · 3 types · 20 coups",
            "6×6 · 4 types · 18 coups",
            "7×7 · 4 types · 15 coups",
            "8×8 · 5 types · 15 coups",
            "9×9 · 5 types · 12 coups",
            "10×10 · 6 types · 12 coups",
            "12×12 · 6 types · 10 coups",
            "14×14 · 7 types · 10 coups",
            "16×16 · 7 types · 8 coups"
        };
        Color[] accentNiv = {
            new Color(0x3DFFA0), new Color(0x5BFFA0), new Color(0xFFD700),
            new Color(0xFFB347), new Color(0xFF8C42), new Color(0xFF6060),
            new Color(0xFF5C8A), new Color(0xCC44FF), ACCENT_GLOW
        };

        for (int i = 1; i <= 9; i++) {
            final int num = i;
            JPanel card = buildNiveauCard(num, descriptions[i - 1], accentNiv[i - 1]);
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lancerNiveau(num);
                }
            });
            grid.add(card);
        }

        outer.add(topPanel, BorderLayout.NORTH);
        outer.add(grid, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildNiveauCard(int numero, String desc, Color accent) {
        JPanel card = new JPanel(new GridBagLayout()) {
            private boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? new Color(0x22223A) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
                if (hover) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                }
                g2.setColor(hover ? accent : BORDER_COLOR);
                g2.setStroke(new BasicStroke(hover ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel numLbl = new JLabel("Niveau " + numero);
        numLbl.setFont(new Font("Serif", Font.BOLD, 22));
        numLbl.setForeground(accent);

        JLabel descLbl = new JLabel("<html><center>" + desc + "</center></html>");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_DIM);

        card.add(numLbl, gbc);
        card.add(descLbl, gbc);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    // ÉCRAN DE JEU
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_DEEP);
        panel.add(buildTopBar(), BorderLayout.NORTH);

        panneauJeu = new PanneauJeu();
        panneauJeu.setBackground(new Color(0x12121E));

        // Wrapper avec GridBagLayout : centre le plateau dans l'espace disponible.
        // Sa preferredSize est dynamiquement ajustée pour être toujours au moins
        // aussi grande que le viewport → GridBagLayout centre correctement.
        // Si le plateau dépasse le viewport, les scrollbars deviennent actives.
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(0x12121E));
        GridBagConstraints gbcW = new GridBagConstraints();
        gbcW.anchor  = GridBagConstraints.CENTER;
        gbcW.fill    = GridBagConstraints.NONE;
        gbcW.weightx = 1.0;
        gbcW.weighty = 1.0;
        wrapper.add(panneauJeu, gbcW);

        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(wrapper);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(new Color(0x12121E));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(Tuile.TAILLE);
        scroll.getHorizontalScrollBar().setUnitIncrement(Tuile.TAILLE);

        // Ajuste wrapper.preferredSize = max(viewport, plateau) à chaque resize
        scroll.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Dimension vp   = scroll.getViewport().getSize();
                Dimension pref = panneauJeu.getPreferredSize();
                int w = Math.max(vp.width,  pref.width);
                int h = Math.max(vp.height, pref.height);
                wrapper.setPreferredSize(new Dimension(w, h));
                wrapper.revalidate();
            }
        });

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buildSidePanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(0x10101C), getWidth(), 0, new Color(0x191928)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_COLOR);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 52));
        bar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = makeGlowLabel("✦ GemCrush", new Font("Serif", Font.BOLD, 20), ACCENT_GLOW);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        right.setOpaque(false);
        JButton btnMenu = makeTopButton("⬅ Menu");
        btnMenu.addActionListener(e -> {
            cardLayout.show(cardContainer, "menu");
            startParticleAnimation();
        });
        right.add(btnMenu);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Panneau latéral ───────────────────────────────────────────────
    private JPanel buildSidePanel() {
        JPanel side = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG_PANEL);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(BORDER_COLOR);
                g.drawLine(0, 0, 0, getHeight());
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(250, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(14, 14, 14, 14));

        niveauInfoPanel = buildNiveauInfoPanel();
        side.add(niveauInfoPanel);
        side.add(Box.createVerticalStrut(10));

        side.add(sectionTitle("STATISTIQUES"));
        side.add(Box.createVerticalStrut(8));

        JPanel scoreCard = buildStatCard("Score", "0", ACCENT);
        JPanel coupsCard = buildStatCard("Coups joués", "0", ACCENT2);
        scoreValLabel = extractValLabel(scoreCard);
        coupsValLabel = extractValLabel(coupsCard);
        side.add(scoreCard);
        side.add(Box.createVerticalStrut(6));
        side.add(coupsCard);
        side.add(Box.createVerticalStrut(8));

        scoreBar = buildProgressBar(ACCENT);
        coupsBar = buildProgressBar(ACCENT2);
        side.add(progressRow("Score", scoreBar));
        side.add(Box.createVerticalStrut(4));
        side.add(progressRow("Coups", coupsBar));

        side.add(Box.createVerticalStrut(6));
        statusLabel = new JLabel("En jeu");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLabel.setForeground(GREEN_OK);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(statusLabel);

        side.add(Box.createVerticalStrut(16));
        side.add(separator());
        side.add(Box.createVerticalStrut(14));

        side.add(sectionTitle("ACTIONS"));
        side.add(Box.createVerticalStrut(8));

        JButton btnNouvelle = sideButton("🔄  Nouvelle partie", ACCENT, true);
        JButton btnAide = sideButton("💡  Coup suggéré", BG_CARD, false);
        JButton btnPossibles = sideButton("🔍  Voir les coups", BG_CARD, false);

        btnNouvelle.addActionListener(e -> nouvellePartie());
        btnAide.addActionListener(e -> afficherAide());
        btnPossibles.addActionListener(e -> afficherCoupsPossibles());
        side.add(btnNouvelle);
        side.add(Box.createVerticalStrut(5));
        side.add(btnAide);
        side.add(Box.createVerticalStrut(5));
        side.add(btnPossibles);

        side.add(Box.createVerticalStrut(10));
        side.add(sectionTitle("IA – COUPS"));
        side.add(Box.createVerticalStrut(6));
        spinIaCoups = buildSpinner(1, 50, 5);
        side.add(buildSpinnerRow("Nb coups IA", spinIaCoups));
        side.add(Box.createVerticalStrut(6));
        JButton btnIa = sideButton("🤖  IA joue N coups", new Color(0x1A1040), false);
        btnIa.setForeground(ACCENT_GLOW);
        btnIa.addActionListener(e -> iaJoueNCoups((int) spinIaCoups.getValue()));
        side.add(btnIa);

        side.add(Box.createVerticalStrut(16));
        side.add(separator());
        side.add(Box.createVerticalStrut(14));

        side.add(sectionTitle("RÉGLAGES (mode libre)"));
        side.add(Box.createVerticalStrut(8));
        spinLig = buildSpinner(3, 15, nbLig);
        spinCol = buildSpinner(3, 25, nbCol);
        spinTypes = buildSpinner(2, 14, nbTypes);
        side.add(buildSpinnerRow("Lignes", spinLig));
        side.add(Box.createVerticalStrut(5));
        side.add(buildSpinnerRow("Colonnes", spinCol));
        side.add(Box.createVerticalStrut(5));
        side.add(buildSpinnerRow("Types", spinTypes));
        side.add(Box.createVerticalStrut(8));
        JButton btnAppliquer = sideButton("✓  Appliquer", new Color(0x0E2A1A), false);
        btnAppliquer.setForeground(GREEN_OK);
        btnAppliquer.addActionListener(e -> appliquerReglages());
        side.add(btnAppliquer);

        side.add(Box.createVerticalGlue());
        return side;
    }

    private JPanel buildNiveauInfoPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1A1040));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(GOLD);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 14, 10, 14));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        objectifLabel = new JLabel("Mode libre");
        objectifLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        objectifLabel.setForeground(GOLD);
        objectifLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        coupsMaxLabel = new JLabel("");
        coupsMaxLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        coupsMaxLabel.setForeground(TEXT_DIM);
        coupsMaxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(objectifLabel);
        p.add(coupsMaxLabel);
        return p;
    }

    // ── Widgets helpers ───────────────────────────────────────────────
    private JLabel sectionTitle(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Monospaced", Font.BOLD, 10));
        l.setForeground(TEXT_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel separator() {
        JPanel sep = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BORDER_COLOR);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private JPanel buildStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 3, 3);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(6, 2));
        card.setBorder(new EmptyBorder(8, 12, 8, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(TEXT_DIM);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Monospaced", Font.BOLD, 20));
        val.setForeground(TEXT_MAIN);

        JPanel vp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        vp.setOpaque(false);
        vp.add(val);
        card.add(lbl, BorderLayout.NORTH);
        card.add(vp, BorderLayout.CENTER);
        return card;
    }

    private JLabel extractValLabel(JPanel card) {
        JPanel vp = (JPanel) card.getComponent(1);
        return (JLabel) vp.getComponent(0);
    }

    private JProgressBar buildProgressBar(Color color) {
        JProgressBar pb = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22223A));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                int filled = (int) (getWidth() * getValue() / 100.0);
                if (filled > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, color, filled, 0, color.brighter());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, filled, getHeight(), 8, 8);
                }
            }
        };
        pb.setPreferredSize(new Dimension(0, 7));
        pb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
        pb.setBorderPainted(false);
        pb.setOpaque(false);
        return pb;
    }

    private JPanel progressRow(String label, JProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(TEXT_DIM);
        lbl.setPreferredSize(new Dimension(38, 16));
        row.add(lbl, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private JSpinner buildSpinner(int min, int max, int val) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, 1));
        sp.setFont(new Font("Monospaced", Font.BOLD, 13));
        sp.setMaximumSize(new Dimension(72, 28));
        JComponent ed = sp.getEditor();
        if (ed instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) ed).getTextField();
            tf.setBackground(BG_CARD);
            tf.setForeground(TEXT_MAIN);
            tf.setCaretColor(ACCENT);
            tf.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        }
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return sp;
    }

    private JPanel buildSpinnerRow(String label, JSpinner sp) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXT_DIM);
        row.add(lbl, BorderLayout.CENTER);
        row.add(sp, BorderLayout.EAST);
        return row;
    }

    private JButton sideButton(String text, Color bg, boolean primary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(primary && getModel().isRollover() ? ACCENT_GLOW : BORDER_COLOR);
                g2.setStroke(new BasicStroke(primary && getModel().isRollover() ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 10, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }

            @Override
            public void paintBorder(Graphics g) {
            }
        };
        btn.setFont(new Font("SansSerif", primary ? Font.BOLD : Font.PLAIN, 12));
        btn.setForeground(primary ? Color.WHITE : TEXT_DIM);
        btn.setPreferredSize(new Dimension(210, 34));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeMenuButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                if (h) {
                    g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 50));
                    g2.fillRoundRect(-8, -8, getWidth() + 16, getHeight() + 16, 24, 24);
                }
                g2.setColor(h ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(h ? ACCENT_GLOW : BORDER_COLOR);
                g2.setStroke(new BasicStroke(h ? 1.5f : 1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(TEXT_MAIN);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }

            @Override
            public void paintBorder(Graphics g) {
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(280, 50));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeTopButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x282840) : BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(TEXT_DIM);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }

            @Override
            public void paintBorder(Graphics g) {
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(110, 32));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel makeGlowLabel(String text, Font font, Color glow) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.setColor(glow);
                for (int d = 8; d >= 1; d--) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.015f * d));
                    g2.drawString(getText(), tx + d, fm.getAscent() + d);
                    g2.drawString(getText(), tx - d, fm.getAscent() - d);
                }
                g2.setComposite(AlphaComposite.SrcOver);
                super.paintComponent(g);
            }
        };
        lbl.setFont(font);
        lbl.setForeground(TEXT_MAIN);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    // ══════════════════════════════════════════════════════════════════
    // LOGIQUE JEU ET IA ANIMÉE
    // ══════════════════════════════════════════════════════════════════
    private void lancerNiveau(int numero) {
        niveauActuel = new Niveau(numero);
        nbLig = niveauActuel.getNbLignes();
        nbCol = niveauActuel.getNbColonnes();
        nbTypes = niveauActuel.getNbTypes();
        coupsJoues = 0;
        plateau = new Plateau(nbCol, nbLig, nbTypes, System.currentTimeMillis(),true);
        mettreEnPlaceJeu();

        objectifLabel.setText("Niveau " + numero + " — Objectif : " + niveauActuel.getScoreObjectif() + " pts");
        coupsMaxLabel.setText("Coups max : " + niveauActuel.getNbCoupsMax());
        scoreBar.setMaximum(niveauActuel.getScoreObjectif());
        coupsBar.setMaximum(niveauActuel.getNbCoupsMax());
        cardLayout.show(cardContainer, "game");
    }

    private void lancerPartieLibre() {
        niveauActuel = null;
        coupsJoues = 0;
        plateau = new Plateau(nbCol, nbLig, nbTypes, System.currentTimeMillis(),true);
        mettreEnPlaceJeu();
        objectifLabel.setText("Mode libre");
        coupsMaxLabel.setText("");
        scoreBar.setMaximum(10000);
        coupsBar.setMaximum(100);
        cardLayout.show(cardContainer, "game");
    }

    /**
     * Initialise ou réinitialise le panneau de jeu avec le plateau actuel et
     * configure le callback de fin de mouvement.
     */
    private void mettreEnPlaceJeu() {
        panneauJeu.setPlateau(plateau); // Transmet le nouveau plateau au composant graphique

        // Configuration du callback déclenché à CHAQUE coup valide du joueur ou de l'IA
        panneauJeu.setCoupJouer(() -> {
            coupsJoues++;
            mettreAJourStats();
            verifierFinDePartie();
        });

        mettreAJourStats();
        statusLabel.setText("En jeu");
        statusLabel.setForeground(GREEN_OK);
    }

    /**
     * Met à jour les étiquettes de texte et les barres de progression de
     * l'interface.
     */
    private void mettreAJourStats() {
        int scoreActuel = plateau.getScore(); // En supposant que votre classe Plateau possède getScore()
        scoreValLabel.setText(String.valueOf(scoreActuel));
        coupsValLabel.setText(String.valueOf(coupsJoues));

        // Mise à jour de la barre de score
        scoreBar.setValue(Math.min(scoreActuel, scoreBar.getMaximum()));

        // Mise à jour de la barre de coups
        if (niveauActuel != null) {
            int coupsMax = niveauActuel.getNbCoupsMax();
            // Progression inverse : la barre se vide ou se remplit selon votre préférence
            int pourcentCoups = (int) ((coupsJoues / (double) coupsMax) * 100);
            coupsBar.setValue(Math.min(pourcentCoups, 100));
        } else {
            coupsBar.setValue(0); // Mode libre : pas de limite stricte affichée par défaut
        }
    }

    /**
     * Vérifie si les conditions de victoire ou de défaite d'un niveau sont
     * atteintes.
     */
    private boolean verifierFinDePartie() {
        if (niveauActuel == null) {
            return false; // Pas de fin automatique en mode libre
        }
        int scoreActuel = plateau.getScore();
        int scoreObjectif = niveauActuel.getScoreObjectif();
        int coupsMax = niveauActuel.getNbCoupsMax();

        if (scoreActuel >= scoreObjectif) {
            statusLabel.setText("🏆 VICTOIRE !");
            statusLabel.setForeground(GOLD);
            JOptionPane.showMessageDialog(this, "Félicitations ! Niveau réussi !", "✦ Victoire", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else if (coupsJoues >= coupsMax) {
            statusLabel.setText("❌ PLUS DE COUPS");
            statusLabel.setForeground(ACCENT2);
            JOptionPane.showMessageDialog(this, "Dommage ! Vous avez épuisé vos coups.", "✦ Fin de partie", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private void nouvellePartie() {
        if (niveauActuel != null) {
            lancerNiveau(niveauActuel.getNumeroNiveau()); // Relance le niveau en cours
        } else {
            lancerPartieLibre();
        }
    }

    private void afficherAide() {
        if (panneauJeu.isAnimEnCours()) {
            return;
        }
        String coupTexte = panneauJeu.aideOrdiString(plateau);
        statusLabel.setText("💡 Suggestion : " + coupTexte);
        statusLabel.setForeground(GOLD);
    }

    private void afficherCoupsPossibles() {
        if (panneauJeu.isAnimEnCours()) {
            return;
        }
        // Met en surbrillance sans donner la solution directement si voulu, ou affiche le total
        ArrayList<Coord> matchs = ia.aideOrdi(plateau);
        if (matchs.isEmpty()) {
            statusLabel.setText("🔍 Aucun coup possible (Mélange requis)");
            statusLabel.setForeground(ACCENT2);
        } else {
            statusLabel.setText("🔍 Des coups sont disponibles sur la grille.");
            statusLabel.setForeground(GREEN_OK);
        }
    }

    /**
     * Fait jouer l'IA de manière asynchrone pour respecter les animations
     * graphiques.
     */
    private void iaJoueNCoups(int n) {
        if (n <= 0 || verifierFinDePartie()) {
            return;
        }

        // On utilise un Timer récurrent pour "checker" quand l'animation se termine avant de rejouer
        Timer iaTimer = new Timer(120, null);
        iaTimer.addActionListener(new ActionListener() {
            private int coupsRestants = n;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Si le panneau est en train de faire tomber des tuiles ou de flasher, on patiente
                if (panneauJeu.isAnimEnCours()) {
                    return;
                }

                if (coupsRestants <= 0 || verifierFinDePartie()) {
                    iaTimer.stop();
                    return;
                }

                ArrayList<Coord> coup = ia.aideOrdi(plateau);
                if (coup == null || coup.size() < 2) {
                    statusLabel.setText("🤖 IA : Bloquée ! Aucun coup possible.");
                    statusLabel.setForeground(ORANGE_WARN);
                    iaTimer.stop();
                    return;
                }

                // Utilise la méthode dédiée du panneau (déclenche flash + chute + cascade)
                Coord c1 = coup.get(0);
                Coord c2 = coup.get(1);
                panneauJeu.jouerCoup(c1, c2);

                coupsRestants--;
            }
        });
        iaTimer.start();
    }

    private void appliquerReglages() {
        if (niveauActuel != null) {
            JOptionPane.showMessageDialog(this, "Quittez le mode niveau pour modifier la taille librement.", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }
        nbLig = (int) spinLig.getValue();
        nbCol = (int) spinCol.getValue();
        nbTypes = (int) spinTypes.getValue();
        lancerPartieLibre();
    }

    // ══════════════════════════════════════════════════════════════════
    // ANIMATION PARTICULES (MENU)
    // ══════════════════════════════════════════════════════════════════
    private void startParticleAnimation() {
        if (particleTimer != null) {
            return;
        }
        synchronized (particles) {
            if (particles.isEmpty()) {
                java.util.Random rnd = new java.util.Random();
                for (int i = 0; i < 45; i++) {
                    particles.add(new float[]{
                        rnd.nextFloat() * 1200, // X
                        rnd.nextFloat() * 750, // Y
                        rnd.nextFloat() * 5f + 2f, // Taille
                        (rnd.nextFloat() - 0.5f) * 0.8f, // Vitesse X
                        rnd.nextFloat() * 0.3f + 0.05f // Opacité (Alpha)
                    });
                }
            }
        }
        particleTimer = new Timer(35, e -> {
            synchronized (particles) {
                for (float[] p : particles) {
                    p[0] += p[3];      // Applique vitesse X
                    p[1] -= 0.5f;      // Fait monter lentement la particule

                    // Réinitialisation si hors écran
                    if (p[1] < -10) {
                        p[1] = 760;
                        p[0] = (float) (Math.random() * 1200);
                    }
                    if (p[0] < -10) {
                        p[0] = 1210;
                    }
                    if (p[0] > 1210) {
                        p[0] = -10;
                    }
                }
            }
            menuPanel.repaint();
        });
        particleTimer.start();
    }

    private void stopParticleAnimation() {
        if (particleTimer != null) {
            particleTimer.stop();
            particleTimer = null;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // POINT D'ENTRÉE MAIN
    // ══════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Amélioration du rendu du texte (Antialiasing système)
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Personnalisation rapide des boîtes de dialogue au Dark Theme
        UIManager.put("OptionPane.background", BG_PANEL);
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_MAIN);

        SwingUtilities.invokeLater(() -> new FenetreGraphiquePropreV2());
    }
}
