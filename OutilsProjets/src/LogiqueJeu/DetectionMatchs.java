/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;
import java.util.ArrayList;

/**
 *
 * @author flo66
 */
public class DetectionMatchs {

    // Vérifie s'il existe un match vertical à partir de la coordonnée donnée
    public boolean existeMatchVertical(Plateau plateau, Coord coordonnee) {
        int col = coordonnee.getAbscisse();
        int lig = coordonnee.getOrdonnee();
        int typeSource = plateau.getTuile(col, lig).getType();
        int typeHaut1 = -1;
        int typeHaut2 = -1;
        int typeBas1 = -1;
        int typeBas2 = -1;
//      On attribut des types au 2 case au dessus et en dessous s'il existe
//      Sinon on leurs donne la valeur -1 
        if (lig + 2 < plateau.getNbLig()) {
            typeHaut1 = plateau.getTuile(col, lig + 1).getType();
            typeHaut2 = plateau.getTuile(col, lig + 2).getType();
        } else if (lig + 1 < plateau.getNbLig()) {
            typeHaut1 = plateau.getTuile(col, lig + 1).getType();
        }
        if (lig - 2 >= 0) {
            typeBas1 = plateau.getTuile(col, lig - 1).getType();
            typeBas2 = plateau.getTuile(col, lig - 2).getType();
        } else if (lig - 1 >= 0) {
            typeBas1 = plateau.getTuile(col, lig - 1).getType();
        }
        return (typeSource == typeHaut1 && typeSource == typeHaut2
                || typeSource == typeBas1 && typeSource == typeBas2
                || typeSource == typeBas1 && typeSource == typeHaut1);
    }

    // Vérifie s'il existe un match horizontal à partir de la coordonnée donnée
    public boolean existeMatchHorizontal(Plateau plateau, Coord coordonnee) {
        int col = coordonnee.getAbscisse();
        int lig = coordonnee.getOrdonnee();
        int typeSource = plateau.getTuile(col, lig).getType();
        int typeDroite1 = -1;
        int typeDroite2 = -1;
        int typeGauche1 = -1;
        int typeGauche2 = -1;

//      On attribut des types aux 2 case a droite et a gauche s'il existe
//      Sinon on leurs donne la valeur -1
        if (col + 2 < plateau.getNbCol()) {
            typeDroite1 = plateau.getTuile(col + 1, lig).getType();
            typeDroite2 = plateau.getTuile(col + 2, lig).getType();
        } else if (col + 1 < plateau.getNbCol()) {
            typeDroite1 = plateau.getTuile(col + 1, lig).getType();
        }
        if (col - 2 >= 0) {
            typeGauche1 = plateau.getTuile(col - 1, lig).getType();
            typeGauche2 = plateau.getTuile(col - 2, lig).getType();
        } else if (col - 1 >= 0) {
            typeGauche1 = plateau.getTuile(col - 1, lig).getType();
        }
        return (typeSource == typeDroite1 && typeSource == typeDroite2
                || typeSource == typeGauche1 && typeSource == typeGauche2
                || typeSource == typeGauche1 && typeSource == typeDroite1);
    }

    // Retourne la position du premier match vertical trouvé, ou (-1,-1) si aucun
    public Coord posMatchVertical(Plateau plateau) {
        Coord pos = new Coord(-1, -1);
        int col = 0;
        boolean trouve = false;
        while (col < plateau.getNbCol() && !trouve) {
            int lig = 0;
            while (lig < plateau.getNbLig() - 2 && !trouve) {
                if (existeMatchVertical(plateau, new Coord(col, lig))) {
                    trouve = true;
                    pos = new Coord(col, lig);
                } else {
                    lig++;
                }
            }
            col++;
        }
        return pos;
    }

    // Retourne la position du premier match horizontal trouvé, ou (-1,-1) si aucun
    public Coord posMatchHorizontal(Plateau plateau) {
        Coord pos = new Coord(-1, -1);
        int lig = 0;
        boolean trouve = false;
        while (lig < plateau.getNbLig() && !trouve) {
            int col = 0;
            while (col < plateau.getNbCol() - 2 && !trouve) {
                if (existeMatchHorizontal(plateau, new Coord(col, lig))) {
                    trouve = true;
                    pos = new Coord(col, lig);
                } else {
                    col++;
                }
            }
            lig++;
        }
        return pos;
    }

    // Retourne true s'il existe au moins un match (vertical ou horizontal) sur le plateau
    public boolean existeUnMatch(Plateau plateau) {
        return posMatchVertical(plateau).getAbscisse() != -1
                || posMatchHorizontal(plateau).getAbscisse() != -1;
    }

}
