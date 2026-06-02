package Modele;

import LogiqueJeu.SuppressionMatchs;
import java.util.Random;

/**
 * Le plateau de jeu : données uniquement.
 *
 * Cette classe contient UNIQUEMENT : - la grille (colonnes de tuiles) - les
 * dimensions et le score - echangerTuiles() car c'est une mutation directe des
 * données - afficher() pour la console - copy() pour l'IA
 *
 * Tout ce qui concerne l'affichage graphique, les clics, l'animation ou l'IA
 * est géré par les classes de leur package respectif.
 */
public class Plateau {

    private static final int NB_TYPES_MIN = 2;
    private static final int NB_TYPES_MAX = 14;

    private Colonne[] lesColonnes;
    private int nbCol;
    private int nbLig;
    private int nbTypesTuile;
    private int score;

    // -------------------------------------------------------------------------
    // CONSTRUCTEURS
    // -------------------------------------------------------------------------
    /**
     * Crée un plateau aléatoire sans graine fixe. Les matchs initiaux sont
     * supprimés automatiquement.
     */
    public Plateau(int nbColonnes, int nbLignes, int nbTypes) {
        validerNbTypes(nbTypes);
        this.nbCol = nbColonnes;
        this.nbLig = nbLignes;
        this.nbTypesTuile = nbTypes;
        this.score = 0;
        this.lesColonnes = new Colonne[nbColonnes];

        for (int i = 0; i < nbColonnes; i++) {
            lesColonnes[i] = new Colonne(nbLignes, nbTypes);
        }
        new SuppressionMatchs().supprimerTousLesMatchs(this, new Random());
        this.score = 0; // remet à 0 après la suppression initiale
    }

    /**
     * Crée un plateau avec une graine fixe (reproductible). Utile pour les
     * tests et les niveaux déterministes.
     */
    public Plateau(int nbColonnes, int nbLignes, int nbTypes, long seed) {
        validerNbTypes(nbTypes);
        this.nbCol = nbColonnes;
        this.nbLig = nbLignes;
        this.nbTypesTuile = nbTypes;
        this.score = 0;
        this.lesColonnes = new Colonne[nbColonnes];

        Random rand = new Random(seed);
        for (int i = 0; i < nbColonnes; i++) {
            lesColonnes[i] = new Colonne(nbLignes, nbTypes, rand);
        }
        new SuppressionMatchs().supprimerTousLesMatchs(this, rand);
        this.score = 0;
    }

    /**
     * Constructeur privé vide — utilisé uniquement par copy().
     */
    private Plateau(int nbColonnes, int nbLignes, int nbTypes, boolean vide) {
        this.nbCol = nbColonnes;
        this.nbLig = nbLignes;
        this.nbTypesTuile = nbTypes;
        this.score = 0;
        this.lesColonnes = new Colonne[nbColonnes];
    }

    // -------------------------------------------------------------------------
    // COPIE (pour l'IA)
    // -------------------------------------------------------------------------
    /**
     * Retourne une copie profonde du plateau. Utilisé par l'IA pour simuler des
     * coups.
     */
    public Plateau copy() {
        Plateau copie = new Plateau(nbCol, nbLig, nbTypesTuile, true);
        copie.score = this.score;
        for (int i = 0; i < nbCol; i++) {
            Colonne col = new Colonne();
            col.setNbTypes(nbTypesTuile);
            for (int j = 0; j < nbLig; j++) {
                col.ajouterTuile(new Tuile(lesColonnes[i].getTuile(j).getType()));
            }
            copie.lesColonnes[i] = col;
        }
        return copie;
    }

    // -------------------------------------------------------------------------
    // ÉCHANGE DE TUILES
    // -------------------------------------------------------------------------
    /**
     * Échange deux tuiles voisines. Retourne true si l'échange a eu lieu, false
     * si les tuiles ne sont pas voisines.
     */
    public boolean echangerTuiles(Coord c1, Coord c2) {
        if (!c1.estVoisine(c2)) {
            return false;
        }
        int typeTemp = getTuile(c1).getType();
        getTuile(c1).setType(getTuile(c2).getType());
        getTuile(c2).setType(typeTemp);
        return true;
    }

    // -------------------------------------------------------------------------
    // AFFICHAGE CONSOLE
    // -------------------------------------------------------------------------
    public String afficher() {
        StringBuilder sb = new StringBuilder();
        for (int lig = nbLig - 1; lig >= 0; lig--) {
            sb.append(lig).append("\t| ");
            for (int col = 0; col < nbCol; col++) {
                sb.append(getTuile(col, lig).toString());
            }
            sb.append("\n");
        }
        sb.append("\t  ");
        for (int col = 0; col < nbCol; col++) {
            sb.append("- ");
        }
        sb.append("\n\t  ");
        for (int col = 0; col < nbCol; col++) {
            sb.append(col).append(" ");
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // GETTERS / SETTERS
    // -------------------------------------------------------------------------
    public Tuile getTuile(int col, int lig) {
        return lesColonnes[col].getTuile(lig);
    }

    public Tuile getTuile(Coord c) {
        return lesColonnes[c.getAbscisse()].getTuile(c.getOrdonnee());
    }

    /**
     * Retourne la tuile en (c), ou null si la case est hors bornes ou marquée
     * null (utilisé par la simulation déterministe de l'IA).
     */
    public Tuile getTuileOuNull(Coord c) {
        int col = c.getAbscisse();
        int lig = c.getOrdonnee();
        if (col < 0 || col >= nbCol || lig < 0 || lig >= nbLig) {
            return null;
        }
        return lesColonnes[col].getTuile(lig); // peut retourner null après setTuileNull()
    }

    public Colonne[] getLesColonnes() {
        return lesColonnes;
    }

    public void setLesColonnes(Colonne[] c) {
        this.lesColonnes = c;
    }

    public int getNbCol() {
        return nbCol;
    }

    public int getNbLig() {
        return nbLig;
    }

    public int getNbTypesTuile() {
        return nbTypesTuile;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void ajouterScore(int points) {
        this.score += points;
    }

    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------
    private static void validerNbTypes(int nbTypes) {
        if (nbTypes < NB_TYPES_MIN || nbTypes > NB_TYPES_MAX) {
            throw new IllegalArgumentException(
                    "Le nombre de types de tuiles doit etre entre "
                    + NB_TYPES_MIN + " et " + NB_TYPES_MAX + ".");
        }
    }

    public static class PanneauJeu {

        public PanneauJeu() {
        }
    }
}
