package Controleur;

/**
 * Définit la configuration d'un niveau de jeu : dimensions du plateau, nombre
 * de types de tuiles, score à atteindre et nombre de coups maximum.
 *
 * Les niveaux 1 à 9 sont prédéfinis ; au-delà, la difficulté augmente
 * automatiquement.
 */
public class Niveau {

    private final int numeroNiveau;
    private int nbLignes;
    private int nbColonnes;
    private int nbTypes;
    private int scoreObjectif;
    private int nbCoupsMax;
    private String nomNiveau;

    public Niveau(int numeroNiveau) {
        this.numeroNiveau = numeroNiveau;
        configurerNiveau();
    }

    // -------------------------------------------------------------------------
    // CONFIGURATION
    // -------------------------------------------------------------------------
    public void configurerNiveau() {
        switch (numeroNiveau) {
            case 1:
                nbLignes = 5;
                nbColonnes = 5;
                nbTypes = 3;
                scoreObjectif = 1000;
                nbCoupsMax = 20;
                nomNiveau ="Niveau 1";
                break;
            case 2:
                nbLignes = 6;
                nbColonnes = 6;
                nbTypes = 4;
                scoreObjectif = 4000;
                nbCoupsMax = 18;
                nomNiveau ="Niveau 2";
                break;
            case 3:
                nbLignes = 7;
                nbColonnes = 7;
                nbTypes = 4;
                scoreObjectif = 8000;
                nbCoupsMax = 15;
                nomNiveau ="Niveau 3";
                break;
            case 4:
                nbLignes = 8;
                nbColonnes = 8;
                nbTypes = 5;
                scoreObjectif = 12000;
                nbCoupsMax = 15;
                nomNiveau ="Niveau 4";
                break;
            case 5:
                nbLignes = 9;
                nbColonnes = 9;
                nbTypes = 5;
                scoreObjectif = 15000;
                nbCoupsMax = 12;
                nomNiveau ="Niveau 5";
                break;
            case 6:
                nbLignes = 10;
                nbColonnes = 10;
                nbTypes = 6;
                scoreObjectif = 17000;
                nbCoupsMax = 12;
                nomNiveau ="Niveau 6";
                break;
            case 7:
                nbLignes = 11;
                nbColonnes = 11;
                nbTypes = 6;
                scoreObjectif = 20000;
                nbCoupsMax = 10;
                nomNiveau ="Niveau 7";
                break;
            case 8:
                nbLignes = 12;
                nbColonnes = 12;
                nbTypes = 7;
                scoreObjectif = 25000;
                nbCoupsMax = 10;
                nomNiveau ="Niveau 8";
                break;
            case 9:
                nbLignes = 13;
                nbColonnes = 13;
                nbTypes = 7;
                scoreObjectif = 30000;
                nbCoupsMax = 8;
                nomNiveau ="Niveau 9";
                break;
            default:
                nbLignes = Math.min(20, 13 + (numeroNiveau - 9));
                nbColonnes = Math.min(20, 13 + (numeroNiveau - 9));
                nbTypes = 7;
                scoreObjectif = 20000 + (numeroNiveau - 9) * 5000;
                nbCoupsMax = Math.max(5, 8 - (numeroNiveau - 9));
                nomNiveau ="Niveau Basique";
                break;
        }
    }

    // -------------------------------------------------------------------------
    // ÉTAT EN COURS
    // -------------------------------------------------------------------------
    public boolean objectifAtteint(int score) {
        return score >= scoreObjectif;
    }

    public boolean plusDeCoup(int coupsJoues) {
        return coupsJoues >= nbCoupsMax;
    }

    public boolean estTermine(int score, int coupsJoues) {
        return objectifAtteint(score) || plusDeCoup(coupsJoues);
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------
    public int getNumeroNiveau() {
        return numeroNiveau;
    }

    public int getNbLignes() {
        return nbLignes;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }

    public int getNbTypes() {
        return nbTypes;
    }

    public int getScoreObjectif() {
        return scoreObjectif;
    }

    public int getNbCoupsMax() {
        return nbCoupsMax;
    }

    public String getNomNiveau() {
        return nomNiveau;
    }

    @Override
    public String toString() {
        return "Niveau " + numeroNiveau
                + " | " + nbColonnes + "×" + nbLignes
                + " | " + nbTypes + " types"
                + " | Objectif : " + scoreObjectif + " pts"
                + " | Coups max : " + nbCoupsMax;
    }
}
