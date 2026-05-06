package Plateau;

import Coordonnees.Coord;
import Tuile.Tuile;
import Tuile.TypeTuile;
import java.util.ArrayList;
import java.util.Random;

public class GestionMatchs {

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

    // Collecte toutes les positions à supprimer (vertical + horizontal) sans rien supprimer
    public ArrayList<Coord> collecterToutesLesTuilesASupprimer(Plateau plateau) {
        ArrayList<Coord> aSupprimer = new ArrayList<>();

        // Matchs verticaux
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 2; lig++) {
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 1))
                        && plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 2))) {
                    int fin = lig + 2;
                    while (fin + 1 < plateau.getNbLig() && plateau.getTuile(col, fin + 1).equals(plateau.getTuile(col, lig))) {
                        fin++;
                    }
                    for (int i = lig; i <= fin; i++) {
                        Coord c = new Coord(col, i);
                        if (!contient(aSupprimer, c)) {
                            aSupprimer.add(c);
                        }
                    }

                    lig = fin;
                }
            }
        }

        // Matchs horizontaux
        for (int lig = 0; lig < plateau.getNbLig(); lig++) {
            for (int col = 0; col < plateau.getNbCol() - 2; col++) {
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col + 1, lig))
                        && plateau.getTuile(col, lig).equals(plateau.getTuile(col + 2, lig))) {
                    int fin = col + 2;
                    while (fin + 1 < plateau.getNbCol() && plateau.getTuile(fin + 1, lig).equals(plateau.getTuile(col, lig))) {
                        fin++;
                    }
                    for (int c = col; c <= fin; c++) {
                        Coord coord = new Coord(c, lig);
                        if (!contient(aSupprimer, coord)) {
                            aSupprimer.add(coord);
                        }
                    }
                    col = fin;
                }
            }
        }
        return aSupprimer;
    }
    // Retourne les tuiles à supprimer selon la taille du match
    // vertical=true pour un match vertical, false pour horizontal

    public ArrayList<Coord> appliquerEffetPoint(Plateau plateau, int debut, int lig, int fin, int taille, boolean vertical) {
        ArrayList<Coord> aSupprimer = new ArrayList<>();
        TypeCombinaisons type = TypeCombinaisons.values()[];

        if () {
            // Explosion rayon 2 ( à modifier si on veut)
            plateau.ajouterScore(2000);
            System.out.println("COMBO T OU L ! Macron EXPLOSION ! +2000 pts");
            int centreCol = vertical ? debut : (debut + fin) / 2; //condition ? valeur si vrai : valeur si faux
            int centreLig = vertical ? (debut + fin) / 2 : lig; // lig ici = la ligne du match horizontal
            for (int c = centreCol - 2; c <= centreCol + 2; c++) {
                for (int l = centreLig - 2; l <= centreLig + 2; l++) {
                    if (c >= 0 && c < plateau.getNbCol() && l >= 0 && l < plateau.getNbLig()) { // test si on est bien sur le plateau pour les bords
                        aSupprimer.add(new Coord(c, l));
                    }
                }
            }

        } else if (taille == 6) {
            // Ligne + colonne entière
            plateau.ajouterScore(1000);
            System.out.println("COMBO x6 ! GIGA FUSEE ! +1000 pts");
            int centreCol = vertical ? debut : (debut + fin) / 2;
            int centreLig = vertical ? (debut + fin) / 2 : lig;
            // toute la ligne
            for (int c = 0; c < plateau.getNbCol(); c++) {
                aSupprimer.add(new Coord(c, centreLig));
            }
            // toute la colonne
            for (int l = 0; l < plateau.getNbLig(); l++) {
                aSupprimer.add(new Coord(centreCol, l));
            }

        } else if (taille == 4) {
            // Toute la ligne
            plateau.ajouterScore(500);
            System.out.println("COMBO x4 ! Petite fusee ! +500 pts");
            int centreLig = vertical ? (debut + fin) / 2 : lig;
            for (int c = 0; c < plateau.getNbCol(); c++) {
                aSupprimer.add(new Coord(c, centreLig));
            }

        } else {
            // Match normal 3 tuiles
            plateau.ajouterScore(taille * 100);
            System.out.println("Match x" + taille + " ! +" + (taille * 100) + " pts");

            if (vertical) {
                // On boucle de la ligne du début : lig jusqu'à la fin 
                // On reste dans la même colonne : debut
                for (int l = lig; l <= fin; l++) {
                    aSupprimer.add(new Coord(debut, l));
                }
            } else {
                // On boucle de la colonne du début jusqu'à la fin 
                // On reste sur la même ligne 
                for (int c = debut; c <= fin; c++) {
                    aSupprimer.add(new Coord(c, lig));
                }
            }
        }
        return aSupprimer;
    }

    // Vérifie si une Coord est déjà dans la liste (pour éviter les doublons)
    public boolean contient(ArrayList<Coord> liste, Coord c) {
        boolean flag = false;
        for (Coord coord : liste) {
            if (coord.equals(c)) {
                flag = true;
            }
        }
        return flag;
    }

    public int supprimerTousLesMatchs(Plateau plateau, Random rand) {
        int totalSupprimees = 0;
        boolean matchTrouve = true;
        while (matchTrouve) {
            ArrayList<Coord> aSupprimer = collecterToutesLesTuilesASupprimer(plateau);
            if (aSupprimer.isEmpty()) {
                matchTrouve = false;
            } else {
                totalSupprimees += supprimerCoords(plateau, aSupprimer, rand);
            }
        }
        return totalSupprimees;
    }

    public int supprimerCoords(Plateau plateau, ArrayList<Coord> aSupprimer, Random rand) {
        for (int col = 0; col < plateau.getNbCol(); col++) {
            ArrayList<Integer> lignesASupprimer = new ArrayList<>();
            for (Coord c : aSupprimer) {
                if (c.getAbscisse() == col) {
                    lignesASupprimer.add(c.getOrdonnee());
                }
            }
            if (!lignesASupprimer.isEmpty()) {
                lignesASupprimer.sort((a, b) -> a - b);
                plateau.getLesColonnes()[col].supprimerTuiles(lignesASupprimer, rand);
            }
        }
        return aSupprimer.size();
    }
}
