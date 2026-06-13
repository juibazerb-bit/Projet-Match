package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;

/**
 * Détecte la présence de matchs (alignements de 3 tuiles identiques) sur le
 * plateau, sans modifier quoi que ce soit.
 */
public class DetectionMatchs {

    // -------------------------------------------------------------------------
    // VÉRIFICATION D'UN MATCH À PARTIR D'UNE COORDONNÉE
    // -------------------------------------------------------------------------
    /**
     * Retourne true si la tuile en (c) fait partie d'un match vertical.
     */
    public boolean existeMatchVertical(Plateau plateau, Coord c) {
        int col = c.getAbscisse();
        int lig = c.getOrdonnee();
        int type = plateau.getTuile(col, lig).getType();

        int typeH1 = typeOu(plateau, col, lig + 1, -1);
        int typeH2 = typeOu(plateau, col, lig + 2, -1);
        int typeB1 = typeOu(plateau, col, lig - 1, -1);
        int typeB2 = typeOu(plateau, col, lig - 2, -1);

        return (type == typeH1 && type == typeH2)
                || (type == typeB1 && type == typeB2)
                || (type == typeB1 && type == typeH1);
    }

    /**
     * Retourne true si la tuile en (c) fait partie d'un match horizontal.
     */
    public boolean existeMatchHorizontal(Plateau plateau, Coord c) {
        int col = c.getAbscisse();
        int lig = c.getOrdonnee();
        int type = plateau.getTuile(col, lig).getType();

        int typeD1 = typeOu(plateau, col + 1, lig, -1);
        int typeD2 = typeOu(plateau, col + 2, lig, -1);
        int typeG1 = typeOu(plateau, col - 1, lig, -1);
        int typeG2 = typeOu(plateau, col - 2, lig, -1);

        return (type == typeD1 && type == typeD2)
                || (type == typeG1 && type == typeG2)
                || (type == typeG1 && type == typeD1);
    }

    // -------------------------------------------------------------------------
    // RECHERCHE SUR TOUT LE PLATEAU
    // -------------------------------------------------------------------------
    /**
     * Retourne la position du premier match vertical trouvé, ou (-1,-1) si
     * aucun.
     */
    public Coord positionPremierMatchVertical(Plateau plateau) {
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 2; lig++) {
                if (existeMatchVertical(plateau, new Coord(col, lig))) {
                    return new Coord(col, lig);
                }
            }
        }
        return new Coord(-1, -1);
    }

    /**
     * Retourne la position du premier match horizontal trouvé, ou (-1,-1) si
     * aucun.
     */
    public Coord positionPremierMatchHorizontal(Plateau plateau) {
        for (int lig = 0; lig < plateau.getNbLig(); lig++) {
            for (int col = 0; col < plateau.getNbCol() - 2; col++) {
                if (existeMatchHorizontal(plateau, new Coord(col, lig))) {
                    return new Coord(col, lig);
                }
            }
        }
        return new Coord(-1, -1);
    }

    /**
     * Retourne true si au moins un match (vertical ou horizontal) existe sur le
     * plateau.
     */
    public boolean existeUnMatch(Plateau plateau) {
        return positionPremierMatchVertical(plateau).getAbscisse() != -1
                || positionPremierMatchHorizontal(plateau).getAbscisse() != -1;
    }

    // -------------------------------------------------------------------------
    // UTILITAIRE
    // -------------------------------------------------------------------------
    /**
     * Retourne le type de la tuile en (col, lig) si la case est valide, sinon
     * retourne la valeur par défaut fournie.
     */
    public int typeOu(Plateau plateau, int col, int lig, int defaut) {
        if (col < 0 || col >= plateau.getNbCol()) {
            return defaut;
        }
        if (lig < 0 || lig >= plateau.getNbLig()) {
            return defaut;
        }
        return plateau.getTuile(col, lig).getType();
    }
}
