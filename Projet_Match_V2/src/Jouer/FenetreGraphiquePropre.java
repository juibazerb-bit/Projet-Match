package Jouer;

import Affichage.PanneauJeu;
import LogiqueJeu.GestionIA;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import java.awt.Dimension;
import java.util.ArrayList;

/**
 * Fenêtre principale du jeu – Mode JFrame Form (NetBeans GUI Builder).
 *
 * Ce fichier est couplé à FenetreGraphiquePropre.form.
 * Le bloc initComponents() est généré automatiquement par NetBeans ; ne pas le modifier à la main.
 *
 * Packages attendus (à adapter selon votre organisation) :
 *   - Affichage.PanneauJeu
 *   - LogiqueJeu.GestionIA
 *   - Modele.{Plateau, Tuile, Coord}
 *   - Vue.Niveau (optionnel – utilisé pour les infos de niveau)
 */
public class FenetreGraphiquePropre extends javax.swing.JFrame {

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(FenetreGraphiquePropre.class.getName());

    // ── Modèle ────────────────────────────────────────────────────────────────
    private Plateau    plateau;
    private PanneauJeu panneauJeu;
    private final GestionIA ia = new GestionIA();

    // ── État ──────────────────────────────────────────────────────────────────
    private int nbLig   = 10;
    private int nbCol   = 10;
    private int nbTypes = 5;

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

    /** Configure les modèles de valeurs des spinners. */
    private void configurerSpinners() {
        spinnerLignes.setModel(new javax.swing.SpinnerNumberModel(nbLig,   3, 30, 1));
        spinnerColonnes.setModel(new javax.swing.SpinnerNumberModel(nbCol,  3, 30, 1));
        spinnerTypes.setModel(new javax.swing.SpinnerNumberModel(nbTypes, 2, 14, 1));
        spinnerIaCoups.setModel(new javax.swing.SpinnerNumberModel(10,     1, 100, 1));
    }

    /** Crée et intègre le PanneauJeu dans le JScrollPane. */
    private void configurerPanneauJeu() {
        panneauJeu = new PanneauJeu();
        scrollGrille.setViewportView(panneauJeu);

        // Callback : met à jour l'affichage du score après chaque coup
        panneauJeu.setCoupJouer(this::mettreAJourScore);
    }

    /** Crée un nouveau plateau et redimensionne le panneau. */
    private void initialiserPlateau(int lignes, int colonnes, int types) {
        nbLig   = lignes;
        nbCol   = colonnes;
        nbTypes = types;

        plateau = new Plateau(colonnes, lignes, types,true);
        panneauJeu.setPlateau(plateau);

        int largeur = (colonnes + 2) * Tuile.TAILLE;
        int hauteur = (lignes   + 2) * Tuile.TAILLE;
        panneauJeu.setPreferredSize(new Dimension(largeur, hauteur));
        panneauJeu.revalidate();
        panneauJeu.repaint();

        mettreAJourScore();
        labelStatus.setText("Nouveau plateau généré (" + colonnes + "×" + lignes + ").");
        labelObjectif.setText("Objectif : —");
        labelCoupsMax.setText("Coups max : —");
        barreScore.setValue(0);
        zoneMessages.setText("");
    }

    /** Rafraîchit le champ score et la barre de progression. */
    private void mettreAJourScore() {
        if (plateau == null) return;
        int score = plateau.getScore();
        champScore.setText(String.valueOf(score));
        // Barre de progression (indicative – peut être liée à un objectif de niveau)
        barreScore.setValue(Math.min(100, score / 100));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CODE GÉNÉRÉ PAR NETBEANS – NE PAS MODIFIER MANUELLEMENT
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollGrille = new javax.swing.JScrollPane();
        panneauControle = new javax.swing.JPanel();
        labelTitre = new javax.swing.JLabel();
        separateur1 = new javax.swing.JSeparator();
        labelLignes = new javax.swing.JLabel();
        spinnerLignes = new javax.swing.JSpinner();
        labelColonnes = new javax.swing.JLabel();
        spinnerColonnes = new javax.swing.JSpinner();
        labelTypes = new javax.swing.JLabel();
        spinnerTypes = new javax.swing.JSpinner();
        separateur2 = new javax.swing.JSeparator();
        boutonGenerer = new javax.swing.JButton();
        boutonNouvellePartie = new javax.swing.JButton();
        boutonAide = new javax.swing.JButton();
        boutonMeilleurCoup = new javax.swing.JButton();
        separateur3 = new javax.swing.JSeparator();
        labelIaCoups = new javax.swing.JLabel();
        spinnerIaCoups = new javax.swing.JSpinner();
        boutonIaJoue = new javax.swing.JButton();
        separateur4 = new javax.swing.JSeparator();
        labelScore = new javax.swing.JLabel();
        champScore = new javax.swing.JTextField();
        barreScore = new javax.swing.JProgressBar();
        labelObjectif = new javax.swing.JLabel();
        labelCoupsMax = new javax.swing.JLabel();
        separateur5 = new javax.swing.JSeparator();
        labelStatus = new javax.swing.JLabel();
        separateur6 = new javax.swing.JSeparator();
        scrollMessages = new javax.swing.JScrollPane();
        zoneMessages = new javax.swing.JTextArea();
        boutonQuitter = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GemCrush");

        // ── Panneau de contrôle ─────────────────────────────────────────────
        panneauControle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelTitre.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
        labelTitre.setText("\u2726 GemCrush");

        labelLignes.setText("Lignes :");
        labelColonnes.setText("Colonnes :");
        labelTypes.setText("Types de tuiles :");

        boutonGenerer.setText("G\u00e9n\u00e9rer");
        boutonGenerer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonGenererActionPerformed(evt);
            }
        });

        boutonNouvellePartie.setText("Nouvelle partie");
        boutonNouvellePartie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonNouvellePartieActionPerformed(evt);
            }
        });

        boutonAide.setText("Aide (meilleur coup)");
        boutonAide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonAideActionPerformed(evt);
            }
        });

        boutonMeilleurCoup.setText("Meilleur coup stat.");
        boutonMeilleurCoup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonMeilleurCoupActionPerformed(evt);
            }
        });

        labelIaCoups.setText("Coups IA :");

        boutonIaJoue.setText("IA joue N coups");
        boutonIaJoue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonIaJoueActionPerformed(evt);
            }
        });

        labelScore.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
        labelScore.setText("Score :");

        champScore.setEditable(false);
        champScore.setText("0");

        labelObjectif.setText("Objectif : \u2014");
        labelCoupsMax.setText("Coups max : \u2014");

        labelStatus.setText("Pr\u00eat \u00e0 jouer\u2026");

        zoneMessages.setColumns(20);
        zoneMessages.setRows(6);
        zoneMessages.setEditable(false);
        zoneMessages.setText("Messages de jeu\u2026");
        scrollMessages.setViewportView(zoneMessages);

        boutonQuitter.setText("Quitter");
        boutonQuitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonQuitterActionPerformed(evt);
            }
        });

        // ── Layout panneau de contrôle (GroupLayout) ────────────────────────
        javax.swing.GroupLayout panneauControleLayout = new javax.swing.GroupLayout(panneauControle);
        panneauControle.setLayout(panneauControleLayout);

        panneauControleLayout.setHorizontalGroup(
            panneauControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelTitre)
            .addComponent(separateur1)
            .addGroup(panneauControleLayout.createSequentialGroup()
                .addComponent(labelLignes, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerLignes, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panneauControleLayout.createSequentialGroup()
                .addComponent(labelColonnes, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerColonnes, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panneauControleLayout.createSequentialGroup()
                .addComponent(labelTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(separateur2)
            .addComponent(boutonGenerer, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addComponent(boutonNouvellePartie, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addComponent(boutonAide, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addComponent(boutonMeilleurCoup, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addComponent(separateur3)
            .addGroup(panneauControleLayout.createSequentialGroup()
                .addComponent(labelIaCoups, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spinnerIaCoups, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(boutonIaJoue, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addComponent(separateur4)
            .addGroup(panneauControleLayout.createSequentialGroup()
                .addComponent(labelScore, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(champScore, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(barreScore, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addComponent(labelObjectif)
            .addComponent(labelCoupsMax)
            .addComponent(separateur5)
            .addComponent(labelStatus)
            .addComponent(separateur6)
            .addComponent(scrollMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addComponent(boutonQuitter, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );

        panneauControleLayout.setVerticalGroup(
            panneauControleLayout.createSequentialGroup()
            .addComponent(labelTitre)
            .addGap(4, 4, 4)
            .addComponent(separateur1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(8, 8, 8)
            .addGroup(panneauControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelLignes)
                .addComponent(spinnerLignes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(4, 4, 4)
            .addGroup(panneauControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelColonnes)
                .addComponent(spinnerColonnes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(4, 4, 4)
            .addGroup(panneauControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelTypes)
                .addComponent(spinnerTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(8, 8, 8)
            .addComponent(separateur2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(6, 6, 6)
            .addComponent(boutonGenerer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(4, 4, 4)
            .addComponent(boutonNouvellePartie, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(4, 4, 4)
            .addComponent(boutonAide, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(4, 4, 4)
            .addComponent(boutonMeilleurCoup, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(8, 8, 8)
            .addComponent(separateur3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(6, 6, 6)
            .addGroup(panneauControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelIaCoups)
                .addComponent(spinnerIaCoups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(4, 4, 4)
            .addComponent(boutonIaJoue, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(8, 8, 8)
            .addComponent(separateur4, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(6, 6, 6)
            .addGroup(panneauControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelScore)
                .addComponent(champScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(4, 4, 4)
            .addComponent(barreScore, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(4, 4, 4)
            .addComponent(labelObjectif)
            .addGap(2, 2, 2)
            .addComponent(labelCoupsMax)
            .addGap(8, 8, 8)
            .addComponent(separateur5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(6, 6, 6)
            .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(6, 6, 6)
            .addComponent(separateur6, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(4, 4, 4)
            .addComponent(scrollMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
            .addGap(8, 8, 8)
            .addComponent(boutonQuitter, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        // ── Layout fenêtre principale (BorderLayout) ─────────────────────────
        getContentPane().setLayout(new java.awt.BorderLayout(6, 0));
        getContentPane().add(scrollGrille,    java.awt.BorderLayout.CENTER);
        getContentPane().add(panneauControle, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ─────────────────────────────────────────────────────────────────────────
    // GESTIONNAIRES D'ÉVÉNEMENTS
    // ─────────────────────────────────────────────────────────────────────────

    private void boutonGenererActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonGenererActionPerformed
        int lig   = (int) spinnerLignes.getValue();
        int col   = (int) spinnerColonnes.getValue();
        int types = (int) spinnerTypes.getValue();
        initialiserPlateau(lig, col, types);
    }//GEN-LAST:event_boutonGenererActionPerformed

    private void boutonNouvellePartieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonNouvellePartieActionPerformed
        initialiserPlateau(nbLig, nbCol, nbTypes);
    }//GEN-LAST:event_boutonNouvellePartieActionPerformed

    private void boutonAideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonAideActionPerformed
        if (plateau == null) return;
        String conseil = panneauJeu.aideOrdiString(plateau);
        labelStatus.setText("💡 " + conseil);
        logMessage("Aide : " + conseil);
    }//GEN-LAST:event_boutonAideActionPerformed

    private void boutonMeilleurCoupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonMeilleurCoupActionPerformed
        if (plateau == null) return;
        labelStatus.setText("Calcul en cours…");
        // Lance dans un thread pour ne pas bloquer l'EDT
        new Thread(() -> {
            ArrayList<Coord> coup = ia.obtenirMeilleurCoupStatistique(plateau, 200);
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (coup.isEmpty()) {
                    labelStatus.setText("Aucun coup possible !");
                } else {
                    String txt = coup.get(0) + " ↔ " + coup.get(1);
                    labelStatus.setText("📊 Stat : " + txt);
                    logMessage("Meilleur coup stat : " + txt);
                }
            });
        }).start();
    }//GEN-LAST:event_boutonMeilleurCoupActionPerformed

    private void boutonIaJoueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonIaJoueActionPerformed
        if (plateau == null) return;
        int n = (int) spinnerIaCoups.getValue();
        labelStatus.setText("🤖 IA en cours (" + n + " coups)…");
        // Timer Swing pour animer un coup à la fois
        javax.swing.Timer iaTimer = new javax.swing.Timer(150, null);
        final int[] restants = {n};
        iaTimer.addActionListener(e -> {
            if (restants[0] <= 0 || panneauJeu.isAnimEnCours()) {
                if (restants[0] <= 0) {
                    iaTimer.stop();
                    labelStatus.setText("🤖 IA terminée. Score : " + plateau.getScore());
                }
                return;
            }
            ArrayList<Coord> coup = ia.aideOrdi(plateau);
            if (coup.isEmpty()) {
                iaTimer.stop();
                labelStatus.setText("🤖 IA bloquée – aucun coup possible.");
                return;
            }
            panneauJeu.jouerCoup(coup.get(0), coup.get(1));
            restants[0]--;
            mettreAJourScore();
        });
        iaTimer.start();
    }//GEN-LAST:event_boutonIaJoueActionPerformed

    private void boutonQuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boutonQuitterActionPerformed
        System.exit(0);
    }//GEN-LAST:event_boutonQuitterActionPerformed

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────────────────

    /** Ajoute un message dans la zone de texte défilante. */
    private void logMessage(String msg) {
        zoneMessages.append(msg + "\n");
        zoneMessages.setCaretPosition(zoneMessages.getDocument().getLength());
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
    private javax.swing.JProgressBar barreScore;
    private javax.swing.JButton      boutonAide;
    private javax.swing.JButton      boutonGenerer;
    private javax.swing.JButton      boutonIaJoue;
    private javax.swing.JButton      boutonMeilleurCoup;
    private javax.swing.JButton      boutonNouvellePartie;
    private javax.swing.JButton      boutonQuitter;
    private javax.swing.JTextField   champScore;
    private javax.swing.JLabel       labelCoupsMax;
    private javax.swing.JLabel       labelIaCoups;
    private javax.swing.JLabel       labelColonnes;
    private javax.swing.JLabel       labelLignes;
    private javax.swing.JLabel       labelObjectif;
    private javax.swing.JLabel       labelScore;
    private javax.swing.JLabel       labelStatus;
    private javax.swing.JLabel       labelTitre;
    private javax.swing.JLabel       labelTypes;
    private javax.swing.JPanel       panneauControle;
    private javax.swing.JScrollPane  scrollGrille;
    private javax.swing.JScrollPane  scrollMessages;
    private javax.swing.JSeparator   separateur1;
    private javax.swing.JSeparator   separateur2;
    private javax.swing.JSeparator   separateur3;
    private javax.swing.JSeparator   separateur4;
    private javax.swing.JSeparator   separateur5;
    private javax.swing.JSeparator   separateur6;
    private javax.swing.JSpinner     spinnerColonnes;
    private javax.swing.JSpinner     spinnerIaCoups;
    private javax.swing.JSpinner     spinnerLignes;
    private javax.swing.JSpinner     spinnerTypes;
    private javax.swing.JTextArea    zoneMessages;
    // End of variables declaration//GEN-END:variables
}
