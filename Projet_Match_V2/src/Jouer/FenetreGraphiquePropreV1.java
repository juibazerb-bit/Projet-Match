/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Jouer;

import LogiqueJeu.GestionIA;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import Affichage.PanneauJeu;
import Controleur.Niveau;
import Sons.SonManager;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

/**
 *
 * @author fpauvert
 */
public class FenetreGraphiquePropreV1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger LOGGER
            = java.util.logging.Logger.getLogger(FenetreGraphiquePropreV1.class.getName());

    // ── Modèle ────────────────────────────────────────────────────────────────
    private Plateau plateau;
    private PanneauJeu panneauJeu;
    private final GestionIA ia = new GestionIA();

    // ── Plein écran ──────────────────────────────────────────────────────────
    private final GraphicsDevice gd
            = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    // ── État ──────────────────────────────────────────────────────────────────
    private int nbLig = 10;
    private int nbCol = 10;
    private int nbTypes = 5;
    private Niveau niveauCourant = null;

    // ─────────────────────────────────────────────────────────────────────────
    // CONSTRUCTEUR
    // ─────────────────────────────────────────────────────────────────────────
    public FenetreGraphiquePropreV1() {
        initComponents();
        configurerSpinners();
        configurerPanneauJeu();
        initialiserPlateau(nbLig, nbCol, nbTypes);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Configure les modèles de valeurs des spinners (variables suffixe 4).
     */
    private void configurerSpinners() {
        spinnerLignes4.setModel(new javax.swing.SpinnerNumberModel(nbLig, 3, 30, 1));
        spinnerColonnes4.setModel(new javax.swing.SpinnerNumberModel(nbCol, 3, 30, 1));
        spinnerTypes4.setModel(new javax.swing.SpinnerNumberModel(nbTypes, 2, 14, 1));
        spinnerIaCoups4.setModel(new javax.swing.SpinnerNumberModel(10, 1, 100, 1));
    }

    /**
     * Crée et intègre le PanneauJeu dans scrollGrille.
     * scrollGrille est le panneau GAUCHE de panneauEcranJeu (BorderLayout CENTER).
     * MenuJeu (contrôles) est le panneau DROIT (BorderLayout EAST).
     */
    private void configurerPanneauJeu() {
        panneauJeu = new PanneauJeu();
        scrollGrille.setViewportView(panneauJeu);
        panneauJeu.setCoupJouer(this::mettreAJourScore);
    }

    /**
     * Affiche l'écran identifié par sa clé CardLayout.
     */
    private void afficherEcran(String cle) {
        ((CardLayout) panneauConteneur.getLayout()).show(panneauConteneur, cle);
    }

    /**
     * Passe en plein écran natif et affiche l'écran de jeu.
     * CORRECTION : dispose() AVANT setUndecorated() pour éviter
     * IllegalComponentStateException sur les fenêtres déjà affichées.
     */
    private void entrerPleinEcranJeu() {
        if (gd.isFullScreenSupported()) {
            dispose();                      // libère la fenêtre native
            setUndecorated(true);           // retire la barre de titre
            setVisible(true);               // réaffiche avant de passer en plein écran
            gd.setFullScreenWindow(this);
        } else {
            setExtendedState(MAXIMIZED_BOTH);
        }
        afficherEcran("MenuJeu");
    }

    /**
     * Quitte le plein écran et revient à l'écran demandé.
     * CORRECTION : dispose() + setUndecorated(false) + setVisible() dans le bon ordre.
     */
    private void quitterPleinEcran(String ecranCible) {
        if (gd.getFullScreenWindow() == this) {
            gd.setFullScreenWindow(null);
            dispose();                      // libère la fenêtre native
            setUndecorated(false);          // remet la barre de titre
            setVisible(true);               // réaffiche en mode fenêtré
            pack();
            setLocationRelativeTo(null);
        } else {
            setExtendedState(NORMAL);
        }
        afficherEcran(ecranCible);
    }

    /** Mode libre : lance une partie sans contrainte de niveau. */
    private void lancerModeLibre() {
        niveauCourant = null;
        initialiserPlateau(nbLig, nbCol, nbTypes);
        entrerPleinEcranJeu();
    }

    /** Charge un niveau prédéfini puis lance la partie. */
    private void lancerNiveau(int numero) {
        niveauCourant = new Niveau(numero);

        int lig   = niveauCourant.getNbLignes();
        int col   = niveauCourant.getNbColonnes();
        int types = niveauCourant.getNbTypes();

        initialiserPlateau(lig, col, types);

        String nomNiveau = niveauCourant.getNomNiveau();
        int    objectif  = niveauCourant.getNumeroNiveau();
        int    coupsMax  = niveauCourant.getNbCoupsMax();

        labelStatus4.setText("▶ " + nomNiveau);
        labelObjectif4.setText("Objectif : " + objectif);
        labelCoupsMax4.setText(coupsMax > 0
                ? "Coups max : " + coupsMax
                : "Coups max : illimité");

        entrerPleinEcranJeu();
    }

    /**
     * Crée un nouveau plateau et redimensionne le panneau.
     */
    private void initialiserPlateau(int lignes, int colonnes, int types) {
        nbLig   = lignes;
        nbCol   = colonnes;
        nbTypes = types;

        plateau = new Plateau(colonnes, lignes, types, true);
        panneauJeu.setPlateau(plateau);

        int largeur = (colonnes + 2) * Tuile.TAILLE;
        int hauteur = (lignes   + 2) * Tuile.TAILLE;
        panneauJeu.setPreferredSize(new Dimension(largeur, hauteur));
        panneauJeu.revalidate();
        panneauJeu.repaint();

        mettreAJourScore();

        if (niveauCourant == null) {
            labelStatus4.setText("Nouveau plateau généré (" + colonnes + "×" + lignes + ").");
            labelObjectif4.setText("Objectif : —");
            labelCoupsMax4.setText("Coups max : —");
        }
        barreScore4.setValue(0);
        zoneMessages4.setText("");
    }

    /**
     * Rafraîchit le champ score et la barre de progression.
     */
    private void mettreAJourScore() {
        if (plateau == null) return;
        int score = plateau.getScore();
        champScore4.setText(String.valueOf(score));
        barreScore4.setValue(Math.min(100, score / 100));

        if (niveauCourant != null && score >= niveauCourant.getScoreObjectif()) {
            labelStatus4.setText("🎉 Objectif atteint ! Score : " + score);
            logMessage("Niveau terminé avec " + score + " points !");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        panneauConteneur = new javax.swing.JPanel();

        // ── MenuPrincipal ────────────────────────────────────────────────────
        MenuPrincipal    = new javax.swing.JPanel();
        labelTitreJeu    = new javax.swing.JLabel();
        boutonJouer      = new javax.swing.JButton();
        boutonNiveaux    = new javax.swing.JButton();

        // ── MenuNiveaux ──────────────────────────────────────────────────────
        MenuNiveaux          = new javax.swing.JPanel();
        labelTitreNiveaux    = new javax.swing.JLabel();
        boutonNiveau1        = new javax.swing.JButton();
        boutonNiveau2        = new javax.swing.JButton();
        boutonNiveau3        = new javax.swing.JButton();
        boutonNiveau4        = new javax.swing.JButton();
        boutonNiveau5        = new javax.swing.JButton();
        boutonNiveau6        = new javax.swing.JButton();
        boutonNiveau7        = new javax.swing.JButton();
        boutonNiveau8        = new javax.swing.JButton();
        boutonNiveau9        = new javax.swing.JButton();
        boutonRetourNiveaux  = new javax.swing.JButton();

        // ── Écran de jeu : panneauEcranJeu (carte "MenuJeu") ─────────────────
        // Architecture :
        //   panneauEcranJeu (JPanel, BorderLayout)
        //     ├─ CENTER : scrollGrille (JScrollPane) ← reçoit panneauJeu dans configurerPanneauJeu()
        //     └─ EAST   : MenuJeu (JPanel, contrôles latéraux)
        panneauEcranJeu  = new javax.swing.JPanel();
        scrollGrille     = new javax.swing.JScrollPane();
        MenuJeu          = new javax.swing.JPanel();

        // ── Composants de MenuJeu ────────────────────────────────────────────
        labelTitre4         = new javax.swing.JLabel();
        separateur25        = new javax.swing.JSeparator();
        labelLignes4        = new javax.swing.JLabel();
        spinnerLignes4      = new javax.swing.JSpinner();
        labelColonnes4      = new javax.swing.JLabel();
        spinnerColonnes4    = new javax.swing.JSpinner();
        labelTypes4         = new javax.swing.JLabel();
        spinnerTypes4       = new javax.swing.JSpinner();
        separateur26        = new javax.swing.JSeparator();
        boutonGenerer4      = new javax.swing.JButton();
        boutonNouvellePartie4 = new javax.swing.JButton();
        boutonAide4         = new javax.swing.JButton();
        boutonMeilleurCoup4 = new javax.swing.JButton();
        separateur27        = new javax.swing.JSeparator();
        labelIaCoups4       = new javax.swing.JLabel();
        spinnerIaCoups4     = new javax.swing.JSpinner();
        boutonIaJoue4       = new javax.swing.JButton();
        separateur28        = new javax.swing.JSeparator();
        labelScore4         = new javax.swing.JLabel();
        champScore4         = new javax.swing.JTextField();
        barreScore4         = new javax.swing.JProgressBar();
        labelObjectif4      = new javax.swing.JLabel();
        labelCoupsMax4      = new javax.swing.JLabel();
        separateur29        = new javax.swing.JSeparator();
        labelStatus4        = new javax.swing.JLabel();
        separateur30        = new javax.swing.JSeparator();
        scrollMessages4     = new javax.swing.JScrollPane();
        zoneMessages4       = new javax.swing.JTextArea();
        boutonQuitter5      = new javax.swing.JButton();

        // ── Composants hors écran (non visuels) ──────────────────────────────
        jLabel6.setText("jLabel6");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panneauConteneur.setLayout(new java.awt.CardLayout());

        // ─────────────────────────────────────────────────────────────────────
        // CARTE 1 : MenuPrincipal
        // ─────────────────────────────────────────────────────────────────────
        labelTitreJeu.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 36));
        labelTitreJeu.setText("✦ GemCrush");
        labelTitreJeu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        boutonJouer.setText("▶  Jouer");
        boutonJouer.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
        boutonJouer.setToolTipText("");
        boutonJouer.setActionCommand("BoutonJouer");
        boutonJouer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonJouerActionPerformed(evt);
            }
        });

        boutonNiveaux.setText("☆  Niveaux");
        boutonNiveaux.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 16));
        boutonNiveaux.setToolTipText("");
        boutonNiveaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveauxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuPrincipalLayout = new javax.swing.GroupLayout(MenuPrincipal);
        MenuPrincipal.setLayout(MenuPrincipalLayout);
        MenuPrincipalLayout.setHorizontalGroup(
            MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(MenuPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelTitreJeu,  javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonJouer,    javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveaux,  javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MenuPrincipalLayout.setVerticalGroup(
            MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTitreJeu,  javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(boutonJouer,    javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveaux,  javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panneauConteneur.add(MenuPrincipal, "MenuPrincipal");
        MenuPrincipal.getAccessibleContext().setAccessibleName("");

        // ─────────────────────────────────────────────────────────────────────
        // CARTE 2 : MenuNiveaux
        // ─────────────────────────────────────────────────────────────────────
        labelTitreNiveaux.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 26));
        labelTitreNiveaux.setText("Choisissez un niveau");
        labelTitreNiveaux.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        String[] textesBoutons = {
            "Niveau 1","Niveau 2","Niveau 3",
            "Niveau 4","Niveau 5","Niveau 6",
            "Niveau 7","Niveau 8","Niveau 9"
        };
        javax.swing.JButton[] boutonsNiveau = {
            boutonNiveau1, boutonNiveau2, boutonNiveau3,
            boutonNiveau4, boutonNiveau5, boutonNiveau6,
            boutonNiveau7, boutonNiveau8, boutonNiveau9
        };
        for (int i = 0; i < boutonsNiveau.length; i++) {
            boutonsNiveau[i].setText(textesBoutons[i]);
            boutonsNiveau[i].setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 15));
        }
        boutonNiveau1.addActionListener(e -> lancerNiveau(1));
        boutonNiveau2.addActionListener(e -> lancerNiveau(2));
        boutonNiveau3.addActionListener(e -> lancerNiveau(3));
        boutonNiveau4.addActionListener(e -> lancerNiveau(4));
        boutonNiveau5.addActionListener(e -> lancerNiveau(5));
        boutonNiveau6.addActionListener(e -> lancerNiveau(6));
        boutonNiveau7.addActionListener(e -> lancerNiveau(7));
        boutonNiveau8.addActionListener(e -> lancerNiveau(8));
        boutonNiveau9.addActionListener(e -> lancerNiveau(9));

        boutonRetourNiveaux.setText("← Retour");
        boutonRetourNiveaux.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14));
        boutonRetourNiveaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonRetourNiveauxActionPerformed(evt);
            }
        });

        int BW = 160, BH = 70;
        javax.swing.GroupLayout MenuNiveauxLayout = new javax.swing.GroupLayout(MenuNiveaux);
        MenuNiveaux.setLayout(MenuNiveauxLayout);
        MenuNiveauxLayout.setHorizontalGroup(
            MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTitreNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(boutonNiveau1, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveau2, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveau3, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(boutonNiveau4, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveau5, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveau6, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(boutonNiveau7, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveau8, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveau9, javax.swing.GroupLayout.PREFERRED_SIZE, BW, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(boutonRetourNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MenuNiveauxLayout.setVerticalGroup(
            MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTitreNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boutonNiveau1, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau2, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau3, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boutonNiveau4, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau5, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau6, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boutonNiveau7, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau8, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau9, javax.swing.GroupLayout.PREFERRED_SIZE, BH, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(boutonRetourNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panneauConteneur.add(MenuNiveaux, "MenuNiveaux");

        // ─────────────────────────────────────────────────────────────────────
        // CARTE 3 : panneauEcranJeu (clé "MenuJeu")
        //   BorderLayout : scrollGrille (CENTER) + MenuJeu contrôles (EAST)
        // ─────────────────────────────────────────────────────────────────────
        panneauEcranJeu.setLayout(new java.awt.BorderLayout());
        panneauEcranJeu.add(scrollGrille, java.awt.BorderLayout.CENTER);

        // ── MenuJeu : panneau de contrôle latéral (EAST) ─────────────────────
        MenuJeu.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));

        labelTitre4.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        labelTitre4.setText("✦ GemCrush");

        labelLignes4.setText("Lignes :");
        labelColonnes4.setText("Colonnes :");
        labelTypes4.setText("Types de tuiles :");

        boutonGenerer4.setText("Générer");
        boutonGenerer4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonGenerer4ActionPerformed(evt);
            }
        });

        boutonNouvellePartie4.setText("Nouvelle partie");
        boutonNouvellePartie4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNouvellePartie4ActionPerformed(evt);
            }
        });

        boutonAide4.setText("Aide (meilleur coup)");
        boutonAide4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonAide4ActionPerformed(evt);
            }
        });

        boutonMeilleurCoup4.setText("Meilleur coup stat.");
        boutonMeilleurCoup4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonMeilleurCoup4ActionPerformed(evt);
            }
        });

        labelIaCoups4.setText("Coups IA :");

        boutonIaJoue4.setText("IA joue N coups");
        boutonIaJoue4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonIaJoue4ActionPerformed(evt);
            }
        });

        labelScore4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelScore4.setText("Score :");

        champScore4.setEditable(false);
        champScore4.setText("0");

        labelObjectif4.setText("Objectif : —");
        labelCoupsMax4.setText("Coups max : —");
        labelStatus4.setText("Prêt à jouer…");

        zoneMessages4.setEditable(false);
        zoneMessages4.setColumns(20);
        zoneMessages4.setRows(6);
        zoneMessages4.setText("Messages de jeu…");
        scrollMessages4.setViewportView(zoneMessages4);

        boutonQuitter5.setText("Quitter");
        boutonQuitter5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonQuitter5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuJeuLayout = new javax.swing.GroupLayout(MenuJeu);
        MenuJeu.setLayout(MenuJeuLayout);
        MenuJeuLayout.setHorizontalGroup(
            MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelTitre4)
            .addComponent(separateur25, 0, 4484, Short.MAX_VALUE)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelLignes4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerLignes4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelColonnes4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerColonnes4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelTypes4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerTypes4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(separateur26, 0, 4484, Short.MAX_VALUE)
            .addComponent(boutonGenerer4, 0, 4484, Short.MAX_VALUE)
            .addComponent(boutonNouvellePartie4, 0, 4484, Short.MAX_VALUE)
            .addComponent(boutonAide4, 0, 4484, Short.MAX_VALUE)
            .addComponent(boutonMeilleurCoup4, 0, 4484, Short.MAX_VALUE)
            .addComponent(separateur27, 0, 4484, Short.MAX_VALUE)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelIaCoups4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerIaCoups4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(boutonIaJoue4, 0, 4484, Short.MAX_VALUE)
            .addComponent(separateur28, 0, 4484, Short.MAX_VALUE)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelScore4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(champScore4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(barreScore4, 0, 4484, Short.MAX_VALUE)
            .addComponent(labelObjectif4)
            .addComponent(labelCoupsMax4)
            .addComponent(separateur29, 0, 4484, Short.MAX_VALUE)
            .addComponent(labelStatus4)
            .addComponent(separateur30, 0, 4484, Short.MAX_VALUE)
            .addComponent(scrollMessages4, 0, 4484, Short.MAX_VALUE)
            .addComponent(boutonQuitter5, 0, 4484, Short.MAX_VALUE)
        );
        MenuJeuLayout.setVerticalGroup(
            MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelTitre4)
                .addGap(4, 4, 4)
                .addComponent(separateur25, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLignes4)
                    .addComponent(spinnerLignes4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelColonnes4)
                    .addComponent(spinnerColonnes4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTypes4)
                    .addComponent(spinnerTypes4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(separateur26, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(boutonGenerer4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(boutonNouvellePartie4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(boutonAide4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(boutonMeilleurCoup4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(separateur27, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIaCoups4)
                    .addComponent(spinnerIaCoups4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(boutonIaJoue4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(separateur28, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelScore4)
                    .addComponent(champScore4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(barreScore4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(labelObjectif4)
                .addGap(2, 2, 2)
                .addComponent(labelCoupsMax4)
                .addGap(8, 8, 8)
                .addComponent(separateur29, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(labelStatus4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(separateur30, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(scrollMessages4, javax.swing.GroupLayout.PREFERRED_SIZE, 395, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(boutonQuitter5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        // MenuJeu (contrôles) est placé à l'EST de panneauEcranJeu
        panneauEcranJeu.add(MenuJeu, java.awt.BorderLayout.EAST);

        // panneauEcranJeu est la carte "MenuJeu" du CardLayout
        panneauConteneur.add(panneauEcranJeu, "MenuJeu");

        // ── Ajout de panneauConteneur à la fenêtre ───────────────────────────
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panneauConteneur, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void boutonJouerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonJouerActionPerformed
        lancerModeLibre();
    }//GEN-LAST:event_boutonJouerActionPerformed

    private void boutonNiveauxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveauxActionPerformed
        afficherEcran("MenuNiveaux");
    }//GEN-LAST:event_boutonNiveauxActionPerformed

    private void boutonNiveau1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau1ActionPerformed
        lancerNiveau(1);
    }//GEN-LAST:event_boutonNiveau1ActionPerformed

    private void boutonNiveau2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau2ActionPerformed
        lancerNiveau(2);
    }//GEN-LAST:event_boutonNiveau2ActionPerformed

    private void boutonNiveau3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau3ActionPerformed
        lancerNiveau(3);
    }//GEN-LAST:event_boutonNiveau3ActionPerformed

    private void boutonNiveau4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau4ActionPerformed
        lancerNiveau(4);
    }//GEN-LAST:event_boutonNiveau4ActionPerformed

    private void boutonNiveau5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau5ActionPerformed
        lancerNiveau(5);
    }//GEN-LAST:event_boutonNiveau5ActionPerformed

    private void boutonNiveau6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau6ActionPerformed
        lancerNiveau(6);
    }//GEN-LAST:event_boutonNiveau6ActionPerformed

    private void boutonNiveau7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau7ActionPerformed
        lancerNiveau(7);
    }//GEN-LAST:event_boutonNiveau7ActionPerformed

    private void boutonNiveau8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau8ActionPerformed
        lancerNiveau(8);
    }//GEN-LAST:event_boutonNiveau8ActionPerformed

    private void boutonNiveau9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNiveau9ActionPerformed
        lancerNiveau(9);
    }//GEN-LAST:event_boutonNiveau9ActionPerformed

    private void boutonGenerer4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonGenerer4ActionPerformed
        niveauCourant = null;
        int lig   = (int) spinnerLignes4.getValue();
        int col   = (int) spinnerColonnes4.getValue();
        int types = (int) spinnerTypes4.getValue();
        initialiserPlateau(lig, col, types);
    }//GEN-LAST:event_boutonGenerer4ActionPerformed

    private void boutonNouvellePartie4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNouvellePartie4ActionPerformed
        if (niveauCourant != null) {
            lancerNiveau(niveauCourant.getNumeroNiveau());
        } else {
            initialiserPlateau(nbLig, nbCol, nbTypes);
        }
    }//GEN-LAST:event_boutonNouvellePartie4ActionPerformed

    private void boutonAide4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonAide4ActionPerformed
        if (plateau == null) return;
        String conseil = panneauJeu.aideOrdiString(plateau);
        labelStatus4.setText("💡 " + conseil);
        logMessage("Aide : " + conseil);
    }//GEN-LAST:event_boutonAide4ActionPerformed

    private void boutonMeilleurCoup4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonMeilleurCoup4ActionPerformed
        if (plateau == null) return;
        labelStatus4.setText("Calcul en cours…");
        new Thread(() -> {
            ArrayList<Coord> coup = ia.obtenirMeilleurCoupStatistique(plateau, 200);
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (coup.isEmpty()) {
                    labelStatus4.setText("Aucun coup possible !");
                } else {
                    String txt = coup.get(0) + " ↔ " + coup.get(1);
                    labelStatus4.setText("📊 Stat : " + txt);
                    logMessage("Meilleur coup stat : " + txt);
                }
            });
        }).start();
    }//GEN-LAST:event_boutonMeilleurCoup4ActionPerformed

    private void boutonIaJoue4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonIaJoue4ActionPerformed
        if (plateau == null) return;
        int n = (int) spinnerIaCoups4.getValue();
        labelStatus4.setText("🤖 IA en cours (" + n + " coups)…");
        javax.swing.Timer iaTimer = new javax.swing.Timer(150, null);
        final int[] restants = {n};
        iaTimer.addActionListener(e -> {
            if (restants[0] <= 0 || panneauJeu.isAnimEnCours()) {
                if (restants[0] <= 0) {
                    iaTimer.stop();
                    labelStatus4.setText("🤖 IA terminée. Score : " + plateau.getScore());
                }
                return;
            }
            ArrayList<Coord> coup = ia.aideOrdi(plateau);
            if (coup.isEmpty()) {
                iaTimer.stop();
                labelStatus4.setText("🤖 IA bloquée – aucun coup possible.");
                return;
            }
            panneauJeu.jouerCoup(coup.get(0), coup.get(1));
            restants[0]--;
            mettreAJourScore();
        });
        iaTimer.start();
    }//GEN-LAST:event_boutonIaJoue4ActionPerformed

    private void boutonQuitter5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonQuitter5ActionPerformed
        quitterPleinEcran("MenuPrincipal");
    }//GEN-LAST:event_boutonQuitter5ActionPerformed

    private void boutonRetourNiveauxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonRetourNiveauxActionPerformed
        afficherEcran("MenuPrincipal");
    }//GEN-LAST:event_boutonRetourNiveauxActionPerformed

    /**
     * Ajoute un message dans la zone de texte défilante.
     */
    private void logMessage(String msg) {
        zoneMessages4.append(msg + "\n");
        zoneMessages4.setCaretPosition(zoneMessages4.getDocument().getLength());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new FenetreGraphiquePropreV1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MenuJeu;
    private javax.swing.JPanel MenuNiveaux;
    private javax.swing.JPanel MenuPrincipal;
    private javax.swing.JProgressBar barreScore4;
    private javax.swing.JButton boutonAide4;
    private javax.swing.JButton boutonGenerer4;
    private javax.swing.JButton boutonIaJoue4;
    private javax.swing.JButton boutonJouer;
    private javax.swing.JButton boutonMeilleurCoup4;
    private javax.swing.JButton boutonNiveau1;
    private javax.swing.JButton boutonNiveau2;
    private javax.swing.JButton boutonNiveau3;
    private javax.swing.JButton boutonNiveau4;
    private javax.swing.JButton boutonNiveau5;
    private javax.swing.JButton boutonNiveau6;
    private javax.swing.JButton boutonNiveau7;
    private javax.swing.JButton boutonNiveau8;
    private javax.swing.JButton boutonNiveau9;
    private javax.swing.JButton boutonNiveaux;
    private javax.swing.JButton boutonNouvellePartie4;
    private javax.swing.JButton boutonQuitter5;
    private javax.swing.JButton boutonRetourNiveaux;
    private javax.swing.JTextField champScore4;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel labelColonnes4;
    private javax.swing.JLabel labelCoupsMax4;
    private javax.swing.JLabel labelIaCoups4;
    private javax.swing.JLabel labelLignes4;
    private javax.swing.JLabel labelObjectif4;
    private javax.swing.JLabel labelScore4;
    private javax.swing.JLabel labelStatus4;
    private javax.swing.JLabel labelTitre4;
    private javax.swing.JLabel labelTitreJeu;
    private javax.swing.JLabel labelTitreNiveaux;
    private javax.swing.JLabel labelTypes4;
    private javax.swing.JPanel panneauConteneur;
    private javax.swing.JPanel panneauEcranJeu;
    private javax.swing.JScrollPane scrollGrille;
    private javax.swing.JScrollPane scrollMessages4;
    private javax.swing.JSeparator separateur25;
    private javax.swing.JSeparator separateur26;
    private javax.swing.JSeparator separateur27;
    private javax.swing.JSeparator separateur28;
    private javax.swing.JSeparator separateur29;
    private javax.swing.JSeparator separateur30;
    private javax.swing.JSpinner spinnerColonnes4;
    private javax.swing.JSpinner spinnerIaCoups4;
    private javax.swing.JSpinner spinnerLignes4;
    private javax.swing.JSpinner spinnerTypes4;
    private javax.swing.JTextArea zoneMessages4;
    // End of variables declaration//GEN-END:variables
}
