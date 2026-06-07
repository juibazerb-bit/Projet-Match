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
import Sons.Son;
import Sons.SonManager;
import java.awt.CardLayout;
import java.awt.Dimension;
import static java.awt.Frame.MAXIMIZED_BOTH;
import static java.awt.Frame.NORMAL;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author fpauvert
 */
public class FenetreGraphiquePropre extends javax.swing.JFrame {

    /**
     * Logger de la classe pour centraliser la gestion des traces et des
     * erreurs.
     */
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
    private int coupsJoues = 0;

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
        spinnerLignes4.setModel(new javax.swing.SpinnerNumberModel(nbLig, 3, 92, 1));
        spinnerColonnes4.setModel(new javax.swing.SpinnerNumberModel(nbCol, 3, 92, 1));
        spinnerTypes4.setModel(new javax.swing.SpinnerNumberModel(nbTypes, 2, 14, 1));
        spinnerIaCoups4.setModel(new javax.swing.SpinnerNumberModel(10, 1, 100, 1));
    }

    /**
     * Crée et intègre le PanneauJeu au centre de scrollGrille.
     */
    private void configurerPanneauJeu() {
        panneauJeu = new PanneauJeu();

        // 1. Créer un conteneur intermédiaire qui va forcer le centrage
        javax.swing.JPanel conteneurCentreur = new javax.swing.JPanel(new java.awt.GridBagLayout());

        // Optionnel : donner au conteneur la même couleur de fond que le JScrollPane/PanneauJeu
        conteneurCentreur.setBackground(scrollGrille.getBackground());

        // 2. Ajouter la grille dans ce conteneur (sans contraintes, elle se place au centre)
        conteneurCentreur.add(panneauJeu);

        // 3. Mettre le conteneur dans le JScrollPane à la place de la grille seule
        scrollGrille.setViewportView(conteneurCentreur);

        // 4. Conserver ton écouteur de clics
        panneauJeu.setCoupJouer(() -> {
            coupsJoues++;
            mettreAJourScore();
        });
    }

    /**
     * Affiche l'écran identifié par sa clé CardLayout.
     */
    private void afficherEcran(String cle) {
        ((CardLayout) panneauConteneur.getLayout()).show(panneauConteneur, cle);
    }

    /**
     * Passe en plein écran natif et affiche l'écran de jeu. CORRECTION :
     * dispose() AVANT setUndecorated() pour éviter
     * IllegalComponentStateException sur les fenêtres déjà affichées.
     */
    private void entrerPleinEcranJeu() {
        if (gd.isFullScreenSupported()) {
            dispose();                      // 1. On libère la fenêtre native
            setUndecorated(true);           // 2. On retire la barre de titre
            gd.setFullScreenWindow(this);   // 3. On passe en plein écran D'ABORD (gère la visibilité)
            setVisible(true);               // 4. On s'assure qu'elle est visible
        } else {
            setExtendedState(MAXIMIZED_BOTH);
            setVisible(true);
        }

        // On change d'écran ET on force Swing à recalculer les dimensions immédiatement
        afficherEcran("EcranJeu");
        panneauConteneur.revalidate();
        panneauConteneur.repaint();
    }

    private void quitterPleinEcran(String ecranCible) {
        if (gd.getFullScreenWindow() == this) {
            gd.setFullScreenWindow(null);   // 1. On désactive le plein écran natif
            dispose();                      // 2. On libère la fenêtre
            setUndecorated(false);          // 3. On remet la barre de titre
            pack();
            setLocationRelativeTo(null);
            setVisible(true);               // 4. On réaffiche en mode fenêtré à la fin
        } else {
            setExtendedState(NORMAL);
        }

        afficherEcran(ecranCible);
        panneauConteneur.revalidate();
        panneauConteneur.repaint();
    }

    /**
     * Mode libre : lance une partie sans contrainte de niveau.
     */
    private void lancerModeLibre() {
        niveauCourant = null;
        initialiserPlateau(10, 10, 5);
        entrerPleinEcranJeu();
    }

    /**
     * Charge un niveau prédéfini puis lance la partie.
     */
    private void lancerNiveau(int numero) {
        niveauCourant = new Niveau(numero);

        int lig = niveauCourant.getNbLignes();
        int col = niveauCourant.getNbColonnes();
        int types = niveauCourant.getNbTypes();

        initialiserPlateau(lig, col, types);

        String nomNiveau = niveauCourant.getNomNiveau();
        int objectif = niveauCourant.getNumeroNiveau();
        int coupsMax = niveauCourant.getNbCoupsMax();

        labelStatus4.setText("▶ " + nomNiveau);
        labelObjectif4.setText("Objectif : " + niveauCourant.getScoreObjectif());
        labelCoupsMax4.setText(coupsMax > 0
                ? "Coups max : " + coupsMax
                : "Coups max : illimité");

        entrerPleinEcranJeu();
    }

    private boolean verifierFinDePartieNiveau() {
        if (niveauCourant == null) {
            return false; 
        }
        int scoreActuel = plateau.getScore();
        int scoreObjectif = niveauCourant.getScoreObjectif();
        int coupsMax = niveauCourant.getNbCoupsMax();

        if (scoreActuel >= scoreObjectif) {
            labelStatus4.setText("🏆 VICTOIRE !");
            SonManager.jouer(Son.GAGNE);

            // Sécurité : on empêche le joueur de re-cliquer pendant l'attente
            if (panneauJeu != null) {
                panneauJeu.setEnabled(false);
            }

            javax.swing.Timer timer = new javax.swing.Timer(3500, e -> {
                // Cette action s'exécutera après les 2.5 secondes
                lancerNiveau(niveauCourant.getNumeroNiveau() + 1);

                // On réactive le panneau pour le prochain niveau
                if (panneauJeu != null) {
                    panneauJeu.setEnabled(true);
                }
            }); 

            timer.setRepeats(false);
            timer.start();
            return true;

        } else if (coupsJoues >= coupsMax) {
            labelStatus4.setText("❌ PLUS DE COUPS !");
            SonManager.jouer(Son.PERDU);
            if (panneauJeu != null) {
                panneauJeu.setEnabled(false);
            }

            javax.swing.Timer timer = new javax.swing.Timer(2500, e -> {
                // Cette action s'exécutera après les 2.5 secondes
                lancerNiveau(niveauCourant.getNumeroNiveau());

                // On réactive le panneau pour le prochain niveau
                if (panneauJeu != null) {
                    panneauJeu.setEnabled(true);
                }
            }); 

            timer.setRepeats(false);
            timer.start();
            return true;
        }

        return false;
    }

    /**
     * Crée un nouveau plateau et redimensionne le panneau.
     */
    private void initialiserPlateau(int lignes, int colonnes, int types) {
        coupsJoues = 0;
        nbLig = lignes;
        nbCol = colonnes;
        nbTypes = types;

        plateau = new Plateau(colonnes, lignes, types, true);
        panneauJeu.setPlateau(plateau);

        int largeur = (colonnes + 2) * Tuile.TAILLE;
        int hauteur = (lignes + 2) * Tuile.TAILLE;
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
    if (plateau == null) {
        return;
    }
    
    int score = plateau.getScore();
    champScore4.setText(String.valueOf(score));
    ChampCoupJoue.setText(String.valueOf(coupsJoues));

    // Calcul dynamique de la barre de score
    if (niveauCourant != null) {
        int scoreObjectif = niveauCourant.getScoreObjectif();
        
        // On s'assure que le maximum de la barre correspond à l'objectif du niveau
        barreScore4.setMaximum(scoreObjectif);
        
        // On donne le score actuel à la barre
        // Math.min évite que la barre dépasse les 100% si le joueur fait un gros coup à la fin
        barreScore4.setValue(Math.min(score, scoreObjectif));
    } else {
        // Mode libre : objectif fictif à 100000 points pour que la barre bouge quand même
        barreScore4.setMaximum(100000);
        barreScore4.setValue(Math.min(score, 100000));
    }

    // Lance la vérification de fin de partie
    verifierFinDePartieNiveau();
}

    // ─────────────────────────────────────────────────────────────────────────
    // CODE GÉNÉRÉ PAR NETBEANS – NE PAS MODIFIER MANUELLEMENT
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        panneauConteneur = new javax.swing.JPanel();
        MenuPrincipal = new javax.swing.JPanel();
        labelTitreJeu = new javax.swing.JLabel();
        boutonJouer = new javax.swing.JButton();
        boutonNiveaux = new javax.swing.JButton();
        boutonQuiterPrincipal = new javax.swing.JButton();
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
        EcranJeu = new javax.swing.JPanel();
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
        CoupJoue = new javax.swing.JLabel();
        ChampCoupJoue = new javax.swing.JTextField();

        jLabel6.setText("jLabel6");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 1000));

        panneauConteneur.setMaximumSize(new java.awt.Dimension(1000, 1000));
        panneauConteneur.setMinimumSize(new java.awt.Dimension(1000, 1000));
        panneauConteneur.setPreferredSize(new java.awt.Dimension(1000, 1000));
        panneauConteneur.setLayout(new java.awt.CardLayout());

        MenuPrincipal.setBackground(new java.awt.Color(51, 0, 102));
        MenuPrincipal.setForeground(new java.awt.Color(51, 0, 102));

        labelTitreJeu.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        labelTitreJeu.setForeground(new java.awt.Color(153, 0, 153));
        labelTitreJeu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTitreJeu.setText("✦ GemCrush");

        boutonJouer.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        boutonJouer.setForeground(new java.awt.Color(102, 0, 102));
        boutonJouer.setText("▶  Jouer");
        boutonJouer.setToolTipText("");
        boutonJouer.setActionCommand("BoutonJouer");
        boutonJouer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonJouerActionPerformed(evt);
            }
        });

        boutonNiveaux.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        boutonNiveaux.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveaux.setText("☆  Niveaux");
        boutonNiveaux.setToolTipText("");
        boutonNiveaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveauxActionPerformed(evt);
            }
        });

        boutonQuiterPrincipal.setBackground(new java.awt.Color(255, 0, 0));
        boutonQuiterPrincipal.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        boutonQuiterPrincipal.setForeground(new java.awt.Color(0, 51, 255));
        boutonQuiterPrincipal.setText("Arreter de jouer :(");
        boutonQuiterPrincipal.setToolTipText("");
        boutonQuiterPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonQuiterPrincipalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuPrincipalLayout = new javax.swing.GroupLayout(MenuPrincipal);
        MenuPrincipal.setLayout(MenuPrincipalLayout);
        MenuPrincipalLayout.setHorizontalGroup(
            MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuPrincipalLayout.createSequentialGroup()
                .addContainerGap(300, Short.MAX_VALUE)
                .addGroup(MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(boutonNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelTitreJeu, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(boutonQuiterPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(boutonJouer, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(300, Short.MAX_VALUE))
        );
        MenuPrincipalLayout.setVerticalGroup(
            MenuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuPrincipalLayout.createSequentialGroup()
                .addContainerGap(372, Short.MAX_VALUE)
                .addComponent(labelTitreJeu, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(boutonJouer, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(boutonNiveaux, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(boutonQuiterPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(248, Short.MAX_VALUE))
        );

        panneauConteneur.add(MenuPrincipal, "MenuPrincipal");
        MenuPrincipal.getAccessibleContext().setAccessibleName("");

        MenuNiveaux.setBackground(new java.awt.Color(51, 0, 102));

        labelTitreNiveaux.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        labelTitreNiveaux.setForeground(new java.awt.Color(153, 0, 153));
        labelTitreNiveaux.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTitreNiveaux.setText("Choisissez un niveau");

        boutonNiveau1.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau1.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau1.setText("Niveau 1");
        boutonNiveau1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau1ActionPerformed(evt);
            }
        });

        boutonNiveau2.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau2.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau2.setText("Niveau 2");
        boutonNiveau2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau2ActionPerformed(evt);
            }
        });

        boutonNiveau3.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau3.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau3.setText("Niveau 3");
        boutonNiveau3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau3ActionPerformed(evt);
            }
        });

        boutonNiveau4.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau4.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau4.setText("Niveau 4");
        boutonNiveau4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau4ActionPerformed(evt);
            }
        });

        boutonNiveau5.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau5.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau5.setText("Niveau 5");
        boutonNiveau5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau5ActionPerformed(evt);
            }
        });

        boutonNiveau6.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau6.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau6.setText("Niveau 6");
        boutonNiveau6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau6ActionPerformed(evt);
            }
        });

        boutonNiveau7.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau7.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau7.setText("Niveau 7");
        boutonNiveau7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau7ActionPerformed(evt);
            }
        });

        boutonNiveau8.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau8.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau8.setText("Niveau 8");
        boutonNiveau8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau8ActionPerformed(evt);
            }
        });

        boutonNiveau9.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        boutonNiveau9.setForeground(new java.awt.Color(102, 0, 102));
        boutonNiveau9.setText("Niveau 9");
        boutonNiveau9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNiveau9ActionPerformed(evt);
            }
        });

        boutonRetourNiveaux.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        boutonRetourNiveaux.setForeground(new java.awt.Color(102, 0, 102));
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
                .addContainerGap(240, Short.MAX_VALUE)
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
                .addContainerGap(240, Short.MAX_VALUE))
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

        EcranJeu.setBackground(new java.awt.Color(51, 0, 102));
        EcranJeu.setLayout(new java.awt.BorderLayout());

        scrollGrille.setBackground(new java.awt.Color(51, 0, 102));
        EcranJeu.add(scrollGrille, java.awt.BorderLayout.CENTER);

        MenuJeu.setBackground(new java.awt.Color(51, 0, 102));
        MenuJeu.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        labelTitre4.setBackground(new java.awt.Color(51, 0, 102));
        labelTitre4.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        labelTitre4.setForeground(new java.awt.Color(255, 255, 255));
        labelTitre4.setText("✦ GemCrush");

        separateur25.setBackground(new java.awt.Color(51, 0, 102));

        labelLignes4.setBackground(new java.awt.Color(51, 0, 102));
        labelLignes4.setForeground(new java.awt.Color(255, 255, 255));
        labelLignes4.setText("Lignes :");

        labelColonnes4.setBackground(new java.awt.Color(51, 0, 102));
        labelColonnes4.setForeground(new java.awt.Color(255, 255, 255));
        labelColonnes4.setText("Colonnes :");

        labelTypes4.setBackground(new java.awt.Color(51, 0, 102));
        labelTypes4.setForeground(new java.awt.Color(255, 255, 255));
        labelTypes4.setText("Types de tuiles :");

        separateur26.setBackground(new java.awt.Color(51, 0, 102));

        boutonGenerer4.setBackground(new java.awt.Color(0, 204, 0));
        boutonGenerer4.setText("Générer");
        boutonGenerer4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonGenerer4ActionPerformed(evt);
            }
        });

        boutonNouvellePartie4.setBackground(new java.awt.Color(0, 204, 0));
        boutonNouvellePartie4.setText("Nouvelle partie");
        boutonNouvellePartie4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNouvellePartie4ActionPerformed(evt);
            }
        });

        boutonAide4.setBackground(new java.awt.Color(0, 204, 0));
        boutonAide4.setText("Aide (meilleur coup)");
        boutonAide4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonAide4ActionPerformed(evt);
            }
        });

        boutonMeilleurCoup4.setBackground(new java.awt.Color(0, 204, 0));
        boutonMeilleurCoup4.setText("Meilleur coup stat.");
        boutonMeilleurCoup4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonMeilleurCoup4ActionPerformed(evt);
            }
        });

        separateur27.setBackground(new java.awt.Color(51, 0, 102));

        labelIaCoups4.setBackground(new java.awt.Color(51, 0, 102));
        labelIaCoups4.setForeground(new java.awt.Color(255, 255, 255));
        labelIaCoups4.setText("Coups IA :");

        boutonIaJoue4.setBackground(new java.awt.Color(0, 204, 0));
        boutonIaJoue4.setText("IA joue N coups");
        boutonIaJoue4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonIaJoue4ActionPerformed(evt);
            }
        });

        separateur28.setBackground(new java.awt.Color(51, 0, 102));

        labelScore4.setBackground(new java.awt.Color(51, 0, 102));
        labelScore4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelScore4.setForeground(new java.awt.Color(255, 255, 255));
        labelScore4.setText("Score :");

        champScore4.setEditable(false);
        champScore4.setBackground(new java.awt.Color(51, 0, 102));
        champScore4.setForeground(new java.awt.Color(255, 255, 255));
        champScore4.setText("0");

        barreScore4.setBackground(new java.awt.Color(51, 0, 102));
        barreScore4.setForeground(new java.awt.Color(255, 0, 255));

        labelObjectif4.setBackground(new java.awt.Color(51, 0, 102));
        labelObjectif4.setForeground(new java.awt.Color(255, 255, 255));
        labelObjectif4.setText("Objectif : —");

        labelCoupsMax4.setBackground(new java.awt.Color(51, 0, 102));
        labelCoupsMax4.setForeground(new java.awt.Color(255, 255, 255));
        labelCoupsMax4.setText("Coups max : —");

        separateur29.setBackground(new java.awt.Color(51, 0, 102));

        labelStatus4.setBackground(new java.awt.Color(51, 0, 102));
        labelStatus4.setForeground(new java.awt.Color(255, 255, 255));
        labelStatus4.setText("Prêt à jouer…");

        separateur30.setBackground(new java.awt.Color(51, 0, 102));

        scrollMessages4.setBackground(new java.awt.Color(51, 0, 102));

        zoneMessages4.setEditable(false);
        zoneMessages4.setBackground(new java.awt.Color(51, 0, 102));
        zoneMessages4.setColumns(20);
        zoneMessages4.setForeground(new java.awt.Color(255, 255, 255));
        zoneMessages4.setRows(6);
        zoneMessages4.setText("Messages de jeu…");
        scrollMessages4.setViewportView(zoneMessages4);

        boutonQuitter5.setBackground(new java.awt.Color(0, 204, 0));
        boutonQuitter5.setText("Retour Menu Principal");
        boutonQuitter5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonQuitter5ActionPerformed(evt);
            }
        });

        CoupJoue.setBackground(new java.awt.Color(51, 0, 102));
        CoupJoue.setForeground(new java.awt.Color(255, 255, 255));
        CoupJoue.setText("Coup Joués : ");

        ChampCoupJoue.setBackground(new java.awt.Color(51, 0, 102));
        ChampCoupJoue.setForeground(new java.awt.Color(255, 255, 255));
        ChampCoupJoue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChampCoupJoueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuJeuLayout = new javax.swing.GroupLayout(MenuJeu);
        MenuJeu.setLayout(MenuJeuLayout);
        MenuJeuLayout.setHorizontalGroup(
            MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(separateur25, 0, 367, Short.MAX_VALUE)
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
            .addComponent(separateur26, 0, 367, Short.MAX_VALUE)
            .addComponent(boutonGenerer4, 0, 367, Short.MAX_VALUE)
            .addComponent(boutonNouvellePartie4, 0, 367, Short.MAX_VALUE)
            .addComponent(boutonAide4, 0, 367, Short.MAX_VALUE)
            .addComponent(boutonMeilleurCoup4, 0, 367, Short.MAX_VALUE)
            .addComponent(separateur27, 0, 367, Short.MAX_VALUE)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelIaCoups4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerIaCoups4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(boutonIaJoue4, 0, 367, Short.MAX_VALUE)
            .addComponent(separateur28, 0, 367, Short.MAX_VALUE)
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addComponent(labelScore4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(champScore4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(barreScore4, 0, 367, Short.MAX_VALUE)
            .addComponent(separateur29, 0, 367, Short.MAX_VALUE)
            .addComponent(separateur30, 0, 367, Short.MAX_VALUE)
            .addComponent(scrollMessages4, 0, 367, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MenuJeuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boutonQuitter5, 0, 355, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelTitre4)
                    .addGroup(MenuJeuLayout.createSequentialGroup()
                        .addComponent(CoupJoue)
                        .addGap(87, 87, 87)
                        .addComponent(ChampCoupJoue, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(labelCoupsMax4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                        .addComponent(labelObjectif4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(MenuJeuLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(labelStatus4, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addGroup(MenuJeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CoupJoue)
                    .addComponent(ChampCoupJoue, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(labelStatus4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separateur30, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollMessages4, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boutonQuitter5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172))
        );

        EcranJeu.add(MenuJeu, java.awt.BorderLayout.EAST);
        MenuJeu.getAccessibleContext().setAccessibleName("card5");

        panneauConteneur.add(EcranJeu, "EcranJeu");

        getContentPane().add(panneauConteneur, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void boutonGenerer4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonGenerer4ActionPerformed
        niveauCourant = null;
        int lig = (int) spinnerLignes4.getValue();
        int col = (int) spinnerColonnes4.getValue();
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
        if (plateau == null) {
            return;
        }
        String conseil = panneauJeu.aideOrdiString(plateau);
        labelStatus4.setText("💡 " + conseil);
        logMessage("Aide : " + conseil);
    }//GEN-LAST:event_boutonAide4ActionPerformed

    private void boutonMeilleurCoup4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonMeilleurCoup4ActionPerformed
        if (plateau == null) {
            return;
        }
        labelStatus4.setText("Calcul en cours…");
        new Thread(() -> {
            ArrayList<Coord> coup = ia.obtenirMeilleurCoupStatistique(plateau, 200);
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (coup.isEmpty()) {
                    labelStatus4.setText("Aucun coup possible !");
                } else {
                    String txt = coup.get(0) + " ↔ " + coup.get(1);
                    labelStatus4.setText(" Stat : " + txt);
                    logMessage("Meilleur coup stat : " + txt);
                    
                }
            });
        }).start();
    }//GEN-LAST:event_boutonMeilleurCoup4ActionPerformed

    private void boutonIaJoue4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonIaJoue4ActionPerformed
        if (plateau == null) {
            return;
        }
        int n = (int) spinnerIaCoups4.getValue();
        labelStatus4.setText(" IA en cours (" + n + " coups)…");
        javax.swing.Timer iaTimer = new javax.swing.Timer(150, null);
        final int[] restants = {n};
        iaTimer.addActionListener(e -> {
            if (restants[0] <= 0 || panneauJeu.isAnimEnCours()) {
                if (restants[0] <= 0) {
                    iaTimer.stop();
                    labelStatus4.setText(" IA terminée. Score : " + plateau.getScore());
                }
                return;
            }
            ArrayList<Coord> coup = ia.aideOrdi(plateau);
            if (coup.isEmpty()) {
                iaTimer.stop();
                labelStatus4.setText(" IA bloquée – aucun coup possible.");
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

    private void ChampCoupJoueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChampCoupJoueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChampCoupJoueActionPerformed

    private void boutonQuiterPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonQuiterPrincipalActionPerformed
        System.exit(0);
    }//GEN-LAST:event_boutonQuiterPrincipalActionPerformed

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────────────────
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
        java.awt.EventQueue.invokeLater(() -> new FenetreGraphiquePropre().setVisible(true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VARIABLES (générées par NetBeans – ne pas modifier)
    // ─────────────────────────────────────────────────────────────────────────
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ChampCoupJoue;
    private javax.swing.JLabel CoupJoue;
    private javax.swing.JPanel EcranJeu;
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
    private javax.swing.JButton boutonQuiterPrincipal;
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
