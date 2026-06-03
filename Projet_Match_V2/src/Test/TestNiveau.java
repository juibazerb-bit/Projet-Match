package Test;

import Affichage.Animation;
import Affichage.DessinPlateau;
import Controleur.ActionJoueur;
import Controleur.GestionClics;
import FenetreGraphique.FenetreGraphique;
import Plateau.Niveau;
import LogiqueJeu.DetectionMatchs;
import LogiqueJeu.GestionIA;
import LogiqueJeu.SuppressionMatchs;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Point d'entrée du jeu en mode niveaux avec FenetreGraphique. Gère la boucle
 * de jeu, les animations et la progression entre niveaux.
 */
public class TestNiveau {

    // Services partagés (instanciés une seule fois)
    private static final DessinPlateau dessin = new DessinPlateau();
    private static final Animation animation = new Animation();
    private static final DetectionMatchs detection = new DetectionMatchs();
    private static final SuppressionMatchs suppression = new SuppressionMatchs();
    private static final GestionClics clics = new GestionClics();
    private static final GestionIA ia = new GestionIA();

    private static final int MARGE_X = 100;
    private static final int MARGE_Y = 100;

    public static void main(String[] args) {
        int numeroNiveau = 1;

        while (true) {
            Niveau niveau = new Niveau(numeroNiveau);
            Plateau plateau = new Plateau(niveau.getNbColonnes(), niveau.getNbLignes(), niveau.getNbTypes(),false);

            FenetreGraphique fenetre = new FenetreGraphique(
                    "CandyCrush - Niveau " + numeroNiveau,
                    MARGE_X + niveau.getNbColonnes() * Tuile.TAILLE
                    + DessinPlateau.BOUTON_OFFSET + 160 + 40,
                    MARGE_Y + (niveau.getNbLignes() + 2) * Tuile.TAILLE + 250);

            System.out.println("=== " + niveau + " ===");
            dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);

            int coupsJoues = 0;
            Coord premierClic = null;
            boolean niveauEnCours = true;

            while (niveauEnCours) {
                dessin.afficherInfosNiveau(plateau, fenetre, niveau, coupsJoues, MARGE_X);
                fenetre.actualiser();

                ActionJoueur action = clics.attendreAction(plateau, fenetre, MARGE_X, MARGE_Y);

                switch (action.type) {

                    case COUPS_POSSIBLES:
                        System.out.println(ia.listMatchsTexte(plateau));
                        premierClic = null;
                        break;

                    case NOUVELLE_PARTIE:
                        plateau = new Plateau(niveau.getNbColonnes(), niveau.getNbLignes(), niveau.getNbTypes(),false);
                        coupsJoues = 0;
                        premierClic = null;
                        dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
                        break;

                    case QUITTER:
                        fenetre.dispose();
                        return;

                    case MEILLEUR_COUP:
                        ArrayList<Coord> coup = ia.obtenirMeilleurCoupStatistique(plateau, 200);
                        if (coup.isEmpty()) {
                            System.out.println("Aucun coup possible !");
                        } else {
                            dessin.afficherPlateauAvecAide(plateau, fenetre, MARGE_X, MARGE_Y, coup);
                        }
                        premierClic = null;
                        break;

                    case ORDI_JOUE:
                        // ignoré ici (pas de compteur de coups pour l'IA en mode niveau)
                        premierClic = null;
                        break;

                    case DELTA_LIGNES:
                    case DELTA_COLONNES:
                        // ignoré en mode niveau (taille fixée)
                        premierClic = null;
                        break;

                    case TUILE_SELECTIONNEE:
                        if (premierClic == null) {
                            premierClic = action.coord;
                            dessin.afficherPlateauAvecSelection(plateau, fenetre, MARGE_X, MARGE_Y, premierClic);
                        } else if (action.coord.equals(premierClic)) {
                            // Misclick : même tuile → déselection
                            premierClic = null;
                            dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
                        } else {
                            animation.fixerPositionsActuelles(plateau, MARGE_Y);
                            boolean echangeOk = plateau.echangerTuiles(premierClic, action.coord);

                            if (echangeOk && detection.existeUnMatch(plateau)) {
                                coupsJoues++;
                                jouerCascade(plateau, fenetre);
                                System.out.println("Score = " + plateau.getScore());

                                if (niveau.objectifAtteint(plateau.getScore())) {
                                    afficherMessageFin(fenetre, plateau, "NIVEAU RÉUSSI !", new Color(50, 200, 50));
                                    fenetre.attendre(2);
                                    numeroNiveau++;
                                    niveauEnCours = false;
                                    fenetre.dispose();
                                } else if (niveau.plusDeCoup(coupsJoues)) {
                                    afficherMessageFin(fenetre, plateau, "PERDU !", new Color(200, 50, 50));
                                    fenetre.attendre(2);
                                    niveauEnCours = false;
                                    fenetre.dispose();
                                }
                            } else if (echangeOk) {
                                plateau.echangerTuiles(action.coord, premierClic);
                                dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
                            } else {
                                // Non voisines : changer la sélection
                                premierClic = action.coord;
                                dessin.afficherPlateauAvecSelection(plateau, fenetre, MARGE_X, MARGE_Y, premierClic);
                                break;
                            }
                            premierClic = null;
                        }
                        break;
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // BOUCLE CASCADE
    // -------------------------------------------------------------------------
    private static void jouerCascade(Plateau plateau, FenetreGraphique fenetre) {
        Random rand = new Random();
        boolean encoreDesMatchs = true;
        while (encoreDesMatchs) {
            ArrayList<Coord> aSupprimer = suppression.collecterToutesLesTuilesASupprimer(plateau);
            if (aSupprimer.isEmpty()) {
                encoreDesMatchs = false;
            } else {
                // Clignotement
                for (int i = 0; i < 3; i++) {
                    dessin.afficherPlateauClignotant(plateau, fenetre, MARGE_X, MARGE_Y, aSupprimer, true);
                    fenetre.attendre(0.2);
                    dessin.afficherPlateauClignotant(plateau, fenetre, MARGE_X, MARGE_Y, aSupprimer, false);
                    fenetre.attendre(0.2);
                }
                suppression.supprimerCoords(plateau, aSupprimer, rand);
                animation.animerChute(plateau, fenetre, MARGE_X, MARGE_Y);
                fenetre.attendre(0.5);
            }
        }
    }

    // -------------------------------------------------------------------------
    // MESSAGE DE FIN
    // -------------------------------------------------------------------------
    private static void afficherMessageFin(FenetreGraphique fenetre, Plateau plateau,
            String message, Color couleur) {
        Graphics2D g = fenetre.getGraphics2D();
        int cx = MARGE_X + (plateau.getNbCol() * Tuile.TAILLE) / 2;
        int cy = 150 + (plateau.getNbLig() * Tuile.TAILLE) / 2;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(cx - 150, cy - 40, 300, 80, 20, 20);
        g.setColor(couleur);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString(message, cx - 120, cy + 10);
        fenetre.actualiser();
    }
}
