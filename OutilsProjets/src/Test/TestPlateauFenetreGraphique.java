package Test;

import Affichage.Animation;
import Affichage.DessinPlateau;
import Controleur.GestionClics;
import Controleur.GestionPartie;
import Modele.Coord;
import FenetreGraphique.FenetreGraphique;
import LogiqueJeu.DetectionMatchs;
import LogiqueJeu.GestionIA;
import LogiqueJeu.SuppressionMatchs;
import Modele.Plateau;
import Modele.Tuile;
import Sons.Son;
import Sons.SonManager;
import java.util.ArrayList;
import java.util.Random;

public class TestPlateauFenetreGraphique {

    static int nbLignes = 6, nbCol = 10, nbTypes = 7;
    static int margeX = 100, margeY = 100;
    static Animation animation = new Animation();
    static DessinPlateau dessinPlateau = new DessinPlateau();
    static SuppressionMatchs suppressionMatchs = new SuppressionMatchs();

    public static void main(String[] args) {
        Plateau plateau = new Plateau(nbCol, nbLignes, nbTypes, 0);
        GestionIA ia = new GestionIA();
        GestionClics gestionClics = new GestionClics();
        DetectionMatchs detectionMatchs = new DetectionMatchs();

        FenetreGraphique fenetre = creerFenetre(nbCol, nbLignes, "Candy Crush - Mode Graphique");
        Animation.clearConsole();

        dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
        verifierFinDePartie(plateau, ia);

        Coord premierClic = null;
        boolean continuer = true;
        if (verifierFinDePartie(plateau, ia)) {
            System.out.println("GAME OVER : Plus aucun coup possible !");
            fenetre.dispose();
        }
        while (continuer) {
            Coord clic = gestionClics.attendreClicOuBouton(plateau, fenetre, margeX, margeY);
            int abs = clic.getAbscisse();

            if (abs == -2) {
                System.out.println(ia.listMatchs(plateau));
                premierClic = null;

            } else if (abs == -3) {
                plateau = new Plateau(nbLignes, nbCol, nbTypes);
                dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
                verifierFinDePartie(plateau, ia);
                premierClic = null;

            } else if (abs == -4) {
                continuer = false;
                fenetre.dispose();

            } else if (abs == -5) {
                ArrayList<Coord> coup = ia.obtenirMeilleurCoupStatistique(plateau, 200);
                if (coup.isEmpty()) {
                    System.out.println("Aucun coup possible !");
                } else {
                    dessinPlateau.afficherPlateauAvecAide(plateau, fenetre, margeX, margeY, coup);
                }
                premierClic = null;

            } else if (abs == -6) {
                int nbCoupsAJouer = 10;
                Random rand = new Random();
                for (int i = 0; i < nbCoupsAJouer; i++) {
                    ArrayList<Coord> coup = ia.aideOrdi(plateau);
                    if (coup.isEmpty()) {
                        System.out.println("IA bloquée après " + i + " coups.");
                        break;
                    }

                    System.out.println("IA coup " + (i + 1) + " : " + coup.get(0) + " <-> " + coup.get(1));
                    animation.fixerPositionsActuelles(plateau, margeY);
                    plateau.echangerTuiles(coup.get(0), coup.get(1));
                    jouerCascade(plateau, fenetre, rand, 0.15, 0.5);
                    fenetre.attendre(0.8);
                    System.out.println("Score : " + plateau.getScore());
                    if (verifierFinDePartie(plateau, ia)) {
                        break;
                    }
                }
                premierClic = null;

            } else if (abs == -10 || abs == -11) {
                int nouvLig = (abs == -10) ? Math.max(3, plateau.getNbLig() + clic.getOrdonnee()) : plateau.getNbLig();
                int nouvCol = (abs == -11) ? Math.max(3, plateau.getNbCol() + clic.getOrdonnee()) : plateau.getNbCol();
                fenetre.dispose();
                fenetre = creerFenetre(nouvCol, nouvLig, "Candy Crush - Mode Graphique");
                plateau = new Plateau(nouvCol, nouvLig, nbTypes);
                dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
                verifierFinDePartie(plateau, ia);
                premierClic = null;

            } else {
                if (premierClic == null) {
                    premierClic = clic;
                    
                } else {
                    animation.fixerPositionsActuelles(plateau, margeY);
                    boolean echangeOk = plateau.echangerTuiles(premierClic, clic);

                    if (echangeOk && detectionMatchs.existeUnMatch(plateau)) {
                        jouerCascade(plateau, fenetre, new Random(), 0.2, 1);
                        System.out.println("Score total = " + plateau.getScore());
                        if (verifierFinDePartie(plateau, ia)) {
                            System.out.println("GAME OVER : Plus aucun coup possible !");
                            fenetre.dispose();
                        }
                    } else if (echangeOk) {
                        System.out.println("Pas de match, annulation.");
                        plateau.echangerTuiles(clic, premierClic);
                        dessinPlateau.afficherPlateau(plateau, fenetre, margeX, margeY);
                    }
                    premierClic = null;
                }
            }
        }
    }

    // Boucle complète : clignotement → suppression → animation de chute
    private static void jouerCascade(Plateau plateau, FenetreGraphique fenetre,
            Random rand, double pauseCligno, double pauseChute) {
        boolean encoreDesMatchs = true;
        while (encoreDesMatchs) {
            ArrayList<Coord> aSupprimer = suppressionMatchs.collecterToutesLesTuilesASupprimer(plateau);
            if (aSupprimer.isEmpty()) {
                encoreDesMatchs = false;
            } else {
                for (int i = 0; i < 3; i++) {
                    dessinPlateau.afficherPlateauClignotant(plateau, fenetre, margeX, margeY, aSupprimer, true);
                    fenetre.attendre(pauseCligno);
                    dessinPlateau.afficherPlateauClignotant(plateau, fenetre, margeX, margeY, aSupprimer, false);
                    fenetre.attendre(pauseCligno);
                }
                for (int col = 0; col < plateau.getNbCol(); col++) {
                    ArrayList<Integer> lignes = new ArrayList<>();
                    for (Coord c : aSupprimer) {
                        if (c.getAbscisse() == col) {
                            lignes.add(c.getOrdonnee());
                        }
                    }
                    if (!lignes.isEmpty()) {
                        lignes.sort((a, b) -> a - b);
                        plateau.getLesColonnes()[col].supprimerTuiles(lignes, rand);
                    }
                }
                animation.animerChute(plateau, fenetre, margeX, margeY);
                fenetre.attendre(pauseChute);
            }
        }
    }

    private static FenetreGraphique creerFenetre(int nbCol, int nbLig, String titre) {
        return new FenetreGraphique(titre, nbCol * Tuile.TAILLE + 300, nbLig * Tuile.TAILLE + 300);
    }

    private static boolean verifierFinDePartie(Plateau plateau, GestionIA ia) {
        if (ia.listEchange(plateau).isEmpty()) {
            System.out.println("\n[ATTENTION] Aucun coup légal restant !");
            SonManager.jouer(Son.RACISME);
            return true;
        }
        return false;
    }
}
