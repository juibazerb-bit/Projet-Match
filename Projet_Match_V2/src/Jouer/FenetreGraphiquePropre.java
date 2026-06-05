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
import static java.awt.Frame.MAXIMIZED_BOTH;
import static java.awt.Frame.NORMAL;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 *
 * @author fpauvert
 */
public class FenetreGraphiquePropre extends javax.swing.JFrame {

    private static final java.util.logging.Logger LOGGER
            = java.util.logging.Logger.getLogger(FenetreGraphiquePropre.class.getName());

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
    public FenetreGraphiquePropre() {
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

    // ─────────────────────────────────────────────────────────────────────────
    // CODE GÉNÉRÉ PAR NETBEANS – NE PAS MODIFIER MANUELLEMENT
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        panneauConteneur = new javax.swing.JPanel();
        MenuPrincipal = new javax.swing.JPanel();
        labelTitreJeu = new javax.swing.JLabel();
        boutonJouer = new javax.swing.JButton();
        boutonNiveaux = new javax.swing.JButton();
        MenuNiveaux = new javax.swing.JPanel();
        labelTitreNiveaux = new javax.swing.JLabel();
        boutonNiveau1 = new javax.swing.JButton();
        boutonNiveau2 = new javax.swing.JButton();
        boutonNiveau3 = new javax.swing.JButton();
        boutonNiveau4 = new javax.swing.JButton();
        boutonNiveau5 = new javax.swing.JButton();
        boutonNiveau6 = new javax.swing.JButton();
        boutonNiveau7 = new javax.swing.JButton();
        boutonNiveau8 = new javax.swing.JButton();
        boutonNiveau9 = new javax.swing.JButton();
        boutonRetourNiveaux = new javax.swing.JButton();
        scrollGrille = new javax.swing.JScrollPane();
        MenuJeu = new javax.swing.JPanel();
        labelTitre4 = new javax.swing.JLabel();
        separateur25 = new javax.swing.JSeparator();
        labelLignes4 = new javax.swing.JLabel();
        spinnerLignes4 = new javax.swing.JSpinner();
        labelColonnes4 = new javax.swing.JLabel();
        spinnerColonnes4 = new javax.swing.JSpinner();
        labelTypes4 = new javax.swing.JLabel();
        spinnerTypes4 = new javax.swing.JSpinner();
        separateur26 = new javax.swing.JSeparator();
        boutonGenerer4 = new javax.swing.JButton();
        boutonNouvellePartie4 = new javax.swing.JButton();
        boutonAide4 = new javax.swing.JButton();
        boutonMeilleurCoup4 = new javax.swing.JButton();
        separateur27 = new javax.swing.JSeparator();
        labelIaCoups4 = new javax.swing.JLabel();
        spinnerIaCoups4 = new javax.swing.JSpinner();
        boutonIaJoue4 = new javax.swing.JButton();
        separateur28 = new javax.swing.JSeparator();
        labelScore4 = new javax.swing.JLabel();
        champScore4 = new javax.swing.JTextField();
        barreScore4 = new javax.swing.JProgressBar();
        labelObjectif4 = new javax.swing.JLabel();
        labelCoupsMax4 = new javax.swing.JLabel();
        separateur29 = new javax.swing.JSeparator();
        labelStatus4 = new javax.swing.JLabel();
        separateur30 = new javax.swing.JSeparator();
        scrollMessages4 = new javax.swing.JScrollPane();
        zoneMessages4 = new javax.swing.JTextArea();
        boutonQuitter5 = new javax.swing.JButton();

        jLabel6.setText("jLabel6");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 1000));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panneauConteneur.setMaximumSize(new java.awt.Dimension(1000, 1000));
        panneauConteneur.setMinimumSize(new java.awt.Dimension(1000, 1000));
        panneauConteneur.setPreferredSize(new java.awt.Dimension(1000, 1000));
        panneauConteneur.setLayout(new java.awt.CardLayout());

        labelTitreJeu.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        labelTitreJeu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTitreJeu.setText("✦ GemCrush");

        boutonJouer.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        boutonJouer.setText("▶  Jouer");
        boutonJouer.setToolTipText("");
        boutonJouer.setActionCommand("BoutonJouer");
        boutonJouer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonJouerActionPerformed(evt);
            }
        });

        boutonNiveaux.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        boutonNiveaux.setText("☆  Niveaux");
        boutonNiveaux.setToolTipText("");
        boutonNiveaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveauxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuPrincipalLayout = new javax.swing.GroupLayout(MenuPrincipal);
        MenuPrincipal.setLayout(MenuPrincipalLayout);
        MenuPrincipalLayout.setHorizontalGroup(
            MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MenuPrincipalLayout.createSequentialGroup()
                .addContainerGap(2070, Short.MAX_VALUE)
                .addGroup(MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelTitreJeu, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonJouer, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(2070, Short.MAX_VALUE))
        );
        MenuPrincipalLayout.setVerticalGroup(
            MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTitreJeu, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(boutonJouer, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panneauConteneur.add(MenuPrincipal, "MenuPrincipal");
        MenuPrincipal.getAccessibleContext().setAccessibleName("");

        labelTitreNiveaux.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        labelTitreNiveaux.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTitreNiveaux.setText("Choisissez un niveau");

        boutonNiveau1.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau1.setText("Niveau 1");
        boutonNiveau1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau1ActionPerformed(evt);
            }
        });

        boutonNiveau2.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau2.setText("Niveau 2");
        boutonNiveau2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau2ActionPerformed(evt);
            }
        });

        boutonNiveau3.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau3.setText("Niveau 3");
        boutonNiveau3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau3ActionPerformed(evt);
            }
        });

        boutonNiveau4.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau4.setText("Niveau 4");
        boutonNiveau4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau4ActionPerformed(evt);
            }
        });

        boutonNiveau5.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau5.setText("Niveau 5");
        boutonNiveau5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau5ActionPerformed(evt);
            }
        });

        boutonNiveau6.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau6.setText("Niveau 6");
        boutonNiveau6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau6ActionPerformed(evt);
            }
        });

        boutonNiveau7.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau7.setText("Niveau 7");
        boutonNiveau7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau7ActionPerformed(evt);
            }
        });

        boutonNiveau8.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau8.setText("Niveau 8");
        boutonNiveau8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau8ActionPerformed(evt);
            }
        });

        boutonNiveau9.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau9.setText("Niveau 9");
        boutonNiveau9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau9ActionPerformed(evt);
            }
        });

        boutonRetourNiveaux.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        boutonRetourNiveaux.setText("← Retour");
        boutonRetourNiveaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonRetourNiveauxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuNiveauxLayout = new javax.swing.GroupLayout(MenuNiveaux);
        MenuNiveaux.setLayout(MenuNiveauxLayout);
        MenuNiveauxLayout.setHorizontalGroup(
            MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(2010, Short.MAX_VALUE)
                .addGroup(MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelTitreNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(MenuNiveauxLayout.createSequentialGroup()
                        .addComponent(boutonNiveau1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(boutonNiveau2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(boutonNiveau3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MenuNiveauxLayout.createSequentialGroup()
                        .addComponent(boutonNiveau4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(boutonNiveau5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(boutonNiveau6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MenuNiveauxLayout.createSequentialGroup()
                        .addComponent(boutonNiveau7, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(boutonNiveau8, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(boutonNiveau9, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(boutonRetourNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(2010, Short.MAX_VALUE))
        );
        MenuNiveauxLayout.setVerticalGroup(
            MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuNiveauxLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTitreNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boutonNiveau1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boutonNiveau4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(MenuNiveauxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boutonNiveau7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonNiveau9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(boutonRetourNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panneauConteneur.add(MenuNiveaux, "MenuNiveaux");
        panneauConteneur.add(scrollGrille, "MenuJeu");

        MenuJeu.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        labelTitre4.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        labelTitre4.setText("✦ GemCrush");

        labelLignes4.setText("Lignes :");

        labelColonnes4.setText("Colonnes :");

        labelTypes4.setText("Types de tuiles :");

        boutonGenerer4.setText("Générer");
        boutonGenerer4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonGenererActionPerformed(evt);
            }
        });

        boutonNouvellePartie4.setText("Nouvelle partie");
        boutonNouvellePartie4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNouvellePartieActionPerformed(evt);
            }
        });

        boutonAide4.setText("Aide (meilleur coup)");
        boutonAide4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonAideActionPerformed(evt);
            }
        });

        boutonMeilleurCoup4.setText("Meilleur coup stat.");
        boutonMeilleurCoup4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonMeilleurCoupActionPerformed(evt);
            }
        });

        labelIaCoups4.setText("Coups IA :");

        boutonIaJoue4.setText("IA joue N coups");
        boutonIaJoue4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonIaJoueActionPerformed(evt);
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
                boutonQuitterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuJeuLayout = new javax.swing.GroupLayout(MenuJeu);
        MenuJeu.setLayout(MenuJeuLayout);
        MenuJeuLayout.setHorizontalGroup(
            MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelTitre4)
            .addComponent(separateur25, 0, 4538, Short.MAX_VALUE)
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
            .addComponent(separateur26, 0, 4538, Short.MAX_VALUE)
            .addComponent(boutonGenerer4, 0, 4538, Short.MAX_VALUE)
            .addComponent(boutonNouvellePartie4, 0, 4538, Short.MAX_VALUE)
            .addComponent(boutonAide4, 0, 4538, Short.MAX_VALUE)
            .addComponent(boutonMeilleurCoup4, 0, 4538, Short.MAX_VALUE)
            .addComponent(separateur27, 0, 4538, Short.MAX_VALUE)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelIaCoups4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerIaCoups4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(boutonIaJoue4, 0, 4538, Short.MAX_VALUE)
            .addComponent(separateur28, 0, 4538, Short.MAX_VALUE)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelScore4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(champScore4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(barreScore4, 0, 4538, Short.MAX_VALUE)
            .addComponent(labelObjectif4)
            .addComponent(labelCoupsMax4)
            .addComponent(separateur29, 0, 4538, Short.MAX_VALUE)
            .addComponent(labelStatus4)
            .addComponent(separateur30, 0, 4538, Short.MAX_VALUE)
            .addComponent(scrollMessages4, 0, 4538, Short.MAX_VALUE)
            .addComponent(boutonQuitter5, 0, 4538, Short.MAX_VALUE)
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
                .addComponent(scrollMessages4, javax.swing.GroupLayout.PREFERRED_SIZE, 483, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(boutonQuitter5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panneauConteneur.add(MenuJeu, "card5");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panneauConteneur, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ─────────────────────────────────────────────────────────────────────────
    // GESTIONNAIRES D'ÉVÉNEMENTS
    // ─────────────────────────────────────────────────────────────────────────

    private void boutonGenererActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonGenererActionPerformed
        int lig   = (int) spinnerLignes4.getValue();
        int col   = (int) spinnerColonnes4.getValue();
        int types = (int) spinnerTypes4.getValue();
        initialiserPlateau(lig, col, types);
    }//GEN-LAST:event_boutonGenererActionPerformed

    private void boutonNouvellePartieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNouvellePartieActionPerformed
        initialiserPlateau(nbLig, nbCol, nbTypes);
    }//GEN-LAST:event_boutonNouvellePartieActionPerformed

    private void boutonAideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonAideActionPerformed
        if (plateau == null) return;
        String conseil = panneauJeu.aideOrdiString(plateau);
        labelStatus4.setText("💡 " + conseil);
        logMessage("Aide : " + conseil);
    }//GEN-LAST:event_boutonAideActionPerformed

    private void boutonMeilleurCoupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonMeilleurCoupActionPerformed
        if (plateau == null) return;
        labelStatus4.setText("Calcul en cours…");
        // Lance dans un thread pour ne pas bloquer l'EDT
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
    }//GEN-LAST:event_boutonMeilleurCoupActionPerformed

    private void boutonIaJoueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonIaJoueActionPerformed
        if (plateau == null) return;
        int n = (int) spinnerIaCoups4.getValue();
        labelStatus4.setText("🤖 IA en cours (" + n + " coups)…");
        // Timer Swing pour animer un coup à la fois
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
    }//GEN-LAST:event_boutonIaJoueActionPerformed

    private void boutonQuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonQuitterActionPerformed
        quitterPleinEcran("MenuPrincipal");
    }//GEN-LAST:event_boutonQuitterActionPerformed

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

    private void boutonRetourNiveauxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonRetourNiveauxActionPerformed
        afficherEcran("MenuPrincipal");
    }//GEN-LAST:event_boutonRetourNiveauxActionPerformed

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────────────────

    /** Ajoute un message dans la zone de texte défilante. */
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
        java.awt.EventQueue.invokeLater(() -> new FenetreGraphiquePropre().setVisible(true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VARIABLES (générées par NetBeans – ne pas modifier)
    // ─────────────────────────────────────────────────────────────────────────
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
