package Test;

import Affichage.Animation;
import Affichage.DessinPlateau;
import Controleur.ActionJoueur;
import Controleur.GestionClics;
import FenetreGraphique.FenetreGraphique;
import LogiqueJeu.DetectionMatchs;
import LogiqueJeu.GestionIA;
import LogiqueJeu.SuppressionMatchs;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import Sons.Son;
import Sons.SonManager;
import java.util.ArrayList;
import java.util.Random;

/**
 * Mode libre avec FenetreGraphique (sans système de niveaux).
 * Permet de jouer, de changer la taille du plateau et de faire jouer l'IA.
 */
public class TestPlateauFenetreGraphique {

    private static final int MARGE_X   = 100;
    private static final int MARGE_Y   = 100;
    private static final int NB_TYPES  = 5;

    private static final DessinPlateau     dessin      = new DessinPlateau();
    private static final Animation         animation   = new Animation();
    private static final SuppressionMatchs suppression = new SuppressionMatchs();
    private static final DetectionMatchs   detection   = new DetectionMatchs();
    private static final GestionClics      clics       = new GestionClics();
    private static final GestionIA         ia          = new GestionIA();

    public static void main(String[] args) {
        int nbLig = 15, nbCol = 10;
        Plateau plateau          = new Plateau(nbLig, nbCol, NB_TYPES, 0);
        FenetreGraphique fenetre = creerFenetre(nbCol,nbLig );
        Animation.clearConsole();

        dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
        verifierFinDePartie(plateau);

        Coord    premierClic = null;
        boolean  continuer   = true;

        while (continuer) {
            ActionJoueur action = clics.attendreAction(plateau, fenetre, MARGE_X, MARGE_Y);

            switch (action.type) {

                case COUPS_POSSIBLES:
                    System.out.println(ia.listMatchsTexte(plateau));
                    premierClic = null;
                    break;

                case NOUVELLE_PARTIE:
                    plateau = new Plateau(nbLig, nbCol, NB_TYPES);
                    dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
                    verifierFinDePartie(plateau);
                    premierClic = null;
                    break;

                case QUITTER:
                    continuer = false;
                    fenetre.dispose();
                    break;

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
                    jouerIANCoups(plateau, fenetre, 10);
                    premierClic = null;
                    break;

                case DELTA_LIGNES:
                    nbLig = Math.max(3, nbLig + action.delta);
                    fenetre.dispose();
                    fenetre  = creerFenetre(nbLig, nbCol);
                    plateau  = new Plateau(nbLig, nbCol, NB_TYPES);
                    dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
                    verifierFinDePartie(plateau);
                    premierClic = null;
                    break;

                case DELTA_COLONNES:
                    nbCol = Math.max(3, nbCol + action.delta);
                    fenetre.dispose();
                    fenetre  = creerFenetre(nbLig, nbCol);
                    plateau  = new Plateau(nbLig, nbCol, NB_TYPES);
                    dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
                    verifierFinDePartie(plateau);
                    premierClic = null;
                    break;

                case TUILE_SELECTIONNEE:
                    if (premierClic == null) {
                        premierClic = action.coord;
                    } else {
                        animation.fixerPositionsActuelles(plateau, MARGE_Y);
                        boolean echangeOk = plateau.echangerTuiles(premierClic, action.coord);

                        if (echangeOk && detection.existeUnMatch(plateau)) {
                            jouerCascade(plateau, fenetre);
                            System.out.println("Score : " + plateau.getScore());
                            verifierFinDePartie(plateau);
                        } else if (echangeOk) {
                            System.out.println("Pas de match, annulation.");
                            plateau.echangerTuiles(action.coord, premierClic);
                            dessin.afficherPlateau(plateau, fenetre, MARGE_X, MARGE_Y);
                        }
                        premierClic = null;
                    }
                    break;
            }
        }
    }

    // -------------------------------------------------------------------------

    private static void jouerCascade(Plateau plateau, FenetreGraphique fenetre) {
        Random rand = new Random();
        boolean encoreDesMatchs = true;
        while (encoreDesMatchs) {
            ArrayList<Coord> aSupprimer = suppression.collecterToutesLesTuilesASupprimer(plateau);
            if (aSupprimer.isEmpty()) {
                encoreDesMatchs = false;
            } else {
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

    private static void jouerIANCoups(Plateau plateau, FenetreGraphique fenetre, int n) {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            ArrayList<Coord> coup = ia.aideOrdi(plateau);
            if (coup.isEmpty()) { System.out.println("IA bloquée après " + i + " coups."); break; }

            System.out.println("IA coup " + (i + 1) + " : " + coup.get(0) + " ↔ " + coup.get(1));
            animation.fixerPositionsActuelles(plateau, MARGE_Y);
            plateau.echangerTuiles(coup.get(0), coup.get(1));
            jouerCascade(plateau, fenetre);
            fenetre.attendre(0.8);
            System.out.println("Score : " + plateau.getScore());
        }
    }

    private static FenetreGraphique creerFenetre(int nbLig, int nbCol) {
        return new FenetreGraphique("CandyCrush",
            nbCol * Tuile.TAILLE + 300,
            nbLig * Tuile.TAILLE + 300);
    }

    private static boolean verifierFinDePartie(Plateau plateau) {
        if (ia.listEchange(plateau).isEmpty()) {
            System.out.println("[FIN] Aucun coup légal restant !");
            SonManager.jouer(Son.PERDU);
            return true;
        }
        return false;
    }
}
