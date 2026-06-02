package Controleur;

/**
 * Représente le résultat d'un clic dans la fenêtre de jeu.
 *
 * Au lieu de retourner des Coord avec des abscisses négatives magiques
 * (-2 = coups possibles, -3 = nouvelle partie, etc.), on utilise
 * cet objet explicite.
 */
public class ActionJoueur {

    public enum Type {
        TUILE_SELECTIONNEE,  // le joueur a cliqué sur une tuile
        COUPS_POSSIBLES,     // bouton "Coups possibles"
        NOUVELLE_PARTIE,     // bouton "Nouvelle partie"
        QUITTER,             // bouton "Quitter"
        MEILLEUR_COUP,       // bouton "Meilleur coup statistique"
        ORDI_JOUE,           // bouton "Ordi joue N coups"
        DELTA_LIGNES,        // compteur lignes modifié
        DELTA_COLONNES       // compteur colonnes modifié
    }

    public final Type  type;
    public final Modele.Coord coord; // non-null si TUILE_SELECTIONNEE
    public final int   delta;        // utilisé pour DELTA_LIGNES / DELTA_COLONNES

    /** Constructeur pour une action sans coordonnée ni delta. */
    public ActionJoueur(Type type) {
        this.type  = type;
        this.coord = null;
        this.delta = 0;
    }

    /** Constructeur pour un clic sur une tuile. */
    public ActionJoueur(Modele.Coord coord) {
        this.type  = Type.TUILE_SELECTIONNEE;
        this.coord = coord;
        this.delta = 0;
    }

    /** Constructeur pour un changement de compteur (±1). */
    public ActionJoueur(Type type, int delta) {
        this.type  = type;
        this.coord = null;
        this.delta = delta;
    }

    @Override
    public String toString() {
        return "ActionJoueur{" + type + (coord != null ? ", " + coord : "") + (delta != 0 ? ", delta=" + delta : "") + "}";
    }
}
