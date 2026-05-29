package IHM;

import LogiqueJeu.GestionIA;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Fenêtre Swing principale du jeu (mode libre, sans système de niveaux).
 *
 * Contient :
 *  - un JScrollPane avec le PanneauJeu (grille cliquable)
 *  - des spinners pour configurer lignes / colonnes / types de tuiles
 *  - un bouton Générer, un bouton Quitter
 *  - un bouton Aide qui affiche le meilleur coup dans un JTextField
 *  - un JTextArea pour les messages de jeu (anciennement dans la console)
 */
public class FenetreGraphiquePropre extends JFrame {

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(FenetreGraphiquePropre.class.getName());

    // --- Modèle ---
    private Plateau    plateau;
    private PanneauJeu panneauJeu;

    // --- Composants UI ---
    private JSpinner   spinnerLignes;
    private JSpinner   spinnerColonnes;
    private JSpinner   spinnerTypes;
    private JTextField champScore;
    private JTextField champAide;
    private JTextArea  zoneMessages;
    private JScrollPane scrollGrille;

    // -------------------------------------------------------------------------
    // CONSTRUCTION
    // -------------------------------------------------------------------------

    public FenetreGraphiquePropre() {
        super("CandyCrush");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        construireInterface();
        initialiserPlateau(15, 15, 7);
        pack();
    }

    private void construireInterface() {
        // --- Panneau de contrôle (droite) ---
        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        spinnerLignes   = new JSpinner(new SpinnerNumberModel(15, 3, 30, 1));
        spinnerColonnes = new JSpinner(new SpinnerNumberModel(15, 3, 30, 1));
        spinnerTypes    = new JSpinner(new SpinnerNumberModel(7,  2, 14, 1));
        champScore      = new JTextField("0", 8);
        champScore.setEditable(false);
        champAide       = new JTextField("Appuyez sur Aide…", 20);
        champAide.setEditable(false);

        JButton boutonGenerer = new JButton("Générer");
        JButton boutonQuitter = new JButton("Quitter");
        JButton boutonAide    = new JButton("Aide");

        boutonGenerer.addActionListener(e -> genererPlateau());
        boutonQuitter.addActionListener(e -> System.exit(0));
        boutonAide.addActionListener(e -> afficherAide());

        controles.add(creerLigne("Lignes",          spinnerLignes));
        controles.add(creerLigne("Colonnes",        spinnerColonnes));
        controles.add(creerLigne("Types de tuiles", spinnerTypes));
        controles.add(creerLigne("Score",           champScore));
        controles.add(Box.createVerticalStrut(10));
        controles.add(boutonGenerer);
        controles.add(Box.createVerticalStrut(5));
        controles.add(boutonQuitter);
        controles.add(Box.createVerticalStrut(10));
        controles.add(boutonAide);
        controles.add(Box.createVerticalStrut(5));
        controles.add(champAide);
        controles.add(Box.createVerticalStrut(10));

        zoneMessages = new JTextArea(10, 30);
        zoneMessages.setEditable(false);
        zoneMessages.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollMessages = new JScrollPane(zoneMessages);
        scrollMessages.setBorder(BorderFactory.createTitledBorder("Messages"));
        controles.add(scrollMessages);

        // --- Zone de jeu (gauche) ---
        panneauJeu = new PanneauJeu();
        scrollGrille = new JScrollPane(panneauJeu);
        scrollGrille.setPreferredSize(new Dimension(900, 900));

        // --- Mise en page ---
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollGrille, BorderLayout.CENTER);
        getContentPane().add(controles,    BorderLayout.EAST);
    }

    /** Crée une ligne label + composant pour le panneau de contrôle. */
    private JPanel creerLigne(String label, JComponent composant) {
        JPanel ligne = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligne.add(new JLabel(label + " : "));
        ligne.add(composant);
        return ligne;
    }

    // -------------------------------------------------------------------------
    // ACTIONS
    // -------------------------------------------------------------------------

    private void genererPlateau() {
        int lignes   = (int) spinnerLignes.getValue();
        int colonnes = (int) spinnerColonnes.getValue();
        int types    = (int) spinnerTypes.getValue();
        initialiserPlateau(lignes, colonnes, types);
    }

    private void initialiserPlateau(int lignes, int colonnes, int types) {
        plateau = new Plateau(colonnes, lignes, types);
        panneauJeu.setPlateau(plateau);
        panneauJeu.setCoupJouer(this::mettreAJourScore);

        int largeur = (colonnes + 2) * Tuile.TAILLE;
        int hauteur = (lignes   + 2) * Tuile.TAILLE;
        panneauJeu.setPreferredSize(new Dimension(largeur, hauteur));
        panneauJeu.revalidate();
        panneauJeu.repaint();
        mettreAJourScore();
    }

    private void mettreAJourScore() {
        if (plateau != null) {
            champScore.setText(String.valueOf(plateau.getScore()));
        }
    }

    private void afficherAide() {
        if (plateau == null) return;
        GestionIA ia = new GestionIA();
        ArrayList<Coord> meilleur = ia.aideOrdi(plateau);
        if (meilleur.isEmpty()) {
            champAide.setText("Aucun coup possible.");
        } else {
            champAide.setText(meilleur.get(0) + " ↔ " + meilleur.get(1));
        }
    }

    // -------------------------------------------------------------------------
    // MAIN
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
            LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
        }

        SwingUtilities.invokeLater(() -> new FenetreGraphiquePropre().setVisible(true));
    }
}
