/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plateau;

import Coordonnees.Coord;
import java.util.ArrayList;

/**
 *
 * @author flo66
 */
public class TypesCombinaisons {

    // -------------------------------------------------------------------------
    // MÉTHODE PRINCIPALE
    // -------------------------------------------------------------------------
    public ArrayList<Coord> collecterToutesLesTuilesASupprimer(Plateau plateau) {
        ArrayList<Coord> aSupprimer = new ArrayList<>();

        ArrayList<Coord> tuilesCarres = collecterCarres(plateau);
        ArrayList<Coord> tuilesVerticaux = collecterMatchsVerticaux(plateau);
        ArrayList<Coord> tuilesHorizontaux = collecterMatchsHorizontaux(plateau);

        appliquerBonusTetL(plateau, tuilesVerticaux, tuilesHorizontaux, aSupprimer);

        for (Coord c : tuilesCarres) {
            ajouterSiAbsent(aSupprimer, c);
        }
        for (Coord c : tuilesVerticaux) {
            ajouterSiAbsent(aSupprimer, c);
        }
        for (Coord c : tuilesHorizontaux) {
            ajouterSiAbsent(aSupprimer, c);
        }

        return aSupprimer;
    }

    // -------------------------------------------------------------------------
    // CARRÉS 2x2
    // -------------------------------------------------------------------------
    private ArrayList<Coord> collecterCarres(Plateau plateau) {
        ArrayList<Coord> tuilesCarres = new ArrayList<>();
        for (int col = 0; col < plateau.getNbCol() - 1; col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 1; lig++) {
                if (estUnCarre(plateau, col, lig)) {
                    plateau.ajouterScore(400);
                    System.out.println("BONUS ! Carré 2x2 en (" + col + "," + lig + ") ! +400 pts");
                    ajouterSiAbsent(tuilesCarres, new Coord(col, lig));
                    ajouterSiAbsent(tuilesCarres, new Coord(col + 1, lig));
                    ajouterSiAbsent(tuilesCarres, new Coord(col, lig + 1));
                    ajouterSiAbsent(tuilesCarres, new Coord(col + 1, lig + 1));
                }
            }
        }
        return tuilesCarres;
    }

    // Retourne true si les 4 tuiles forment un carré de même type
    private boolean estUnCarre(Plateau plateau, int col, int lig) {
        int type = plateau.getTuile(col, lig).getType();
        return plateau.getTuile(col + 1, lig).getType() == type
                && plateau.getTuile(col, lig + 1).getType() == type
                && plateau.getTuile(col + 1, lig + 1).getType() == type;
    }

    // -------------------------------------------------------------------------
    // MATCHS VERTICAUX
    // -------------------------------------------------------------------------
    private ArrayList<Coord> collecterMatchsVerticaux(Plateau plateau) {
        ArrayList<Coord> tuiles = new ArrayList<>();
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 2; lig++) {
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 1))
                        && plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 2))) {
                    int fin = etendreMatch(plateau, col, lig, true);
                    int taille = fin - lig + 1;
                    ArrayList<Coord> effet = appliquerEffetPoint(plateau, col, lig, fin, taille, true);
                    for (Coord c : effet) {
                        ajouterSiAbsent(tuiles, c);
                    }
                    lig = fin;
                }
            }
        }
        return tuiles;
    }

    // -------------------------------------------------------------------------
    // MATCHS HORIZONTAUX
    // -------------------------------------------------------------------------
    private ArrayList<Coord> collecterMatchsHorizontaux(Plateau plateau) {
        ArrayList<Coord> tuiles = new ArrayList<>();
        for (int lig = 0; lig < plateau.getNbLig(); lig++) {
            for (int col = 0; col < plateau.getNbCol() - 2; col++) {
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col + 1, lig))
                        && plateau.getTuile(col, lig).equals(plateau.getTuile(col + 2, lig))) {
                    int fin = etendreMatch(plateau, col, lig, false);
                    int taille = fin - col + 1;
                    ArrayList<Coord> effet = appliquerEffetPoint(plateau, col, lig, fin, taille, false);
                    for (Coord c : effet) {
                        ajouterSiAbsent(tuiles, c);
                    }
                    col = fin;
                }
            }
        }
        return tuiles;
    }

    // -------------------------------------------------------------------------
    // EXTENSION D'UN MATCH AU-DELÀ DE 3
    // -------------------------------------------------------------------------
    // Retourne l'index de fin du match (vertical ou horizontal)
    private int etendreMatch(Plateau plateau, int debut, int lig, boolean vertical) {
        int fin = (vertical ? lig : debut) + 2;
        if (vertical) {
            while (fin + 1 < plateau.getNbLig()
                    && plateau.getTuile(debut, fin + 1).equals(plateau.getTuile(debut, lig))) {
                fin++;
            }
        } else {
            while (fin + 1 < plateau.getNbCol()
                    && plateau.getTuile(fin + 1, lig).equals(plateau.getTuile(debut, lig))) {
                fin++;
            }
        }
        return fin;
    }

    // -------------------------------------------------------------------------
    // EFFETS SELON LA TAILLE DU MATCH
    // -------------------------------------------------------------------------
    // Retourne les tuiles à supprimer selon la taille du match
    public ArrayList<Coord> appliquerEffetPoint(Plateau plateau, int debut, int lig, int fin, int taille, boolean vertical) {
        ArrayList<Coord> aSupprimer = new ArrayList<>();

        if (taille >= 5) {
            // 5 tuiles ou plus = Super : retire toute la couleur du plateau
            int typeCible = plateau.getTuile(debut, lig).getType();
            plateau.ajouterScore(1000);
            System.out.println("BONUS ! Le racisme est a son comble ! +1000 pts");
            for (int c = 0; c < plateau.getNbCol(); c++) {
                for (int l = 0; l < plateau.getNbLig(); l++) {
                    if (plateau.getTuile(c, l).getType() == typeCible) {
                        ajouterSiAbsent(aSupprimer, new Coord(c, l));
                    }
                }
            }

        } else if (taille == 4) {
            // 4 tuiles = Fusée : retire toute la ligne ou toute la colonne
            plateau.ajouterScore(500);
            System.out.println("BONUS ! Fusee ! +500 pts");
            if (vertical) {
                for (int l = 0; l < plateau.getNbLig(); l++) {
                    ajouterSiAbsent(aSupprimer, new Coord(debut, l));
                }
            } else {
                for (int c = 0; c < plateau.getNbCol(); c++) {
                    ajouterSiAbsent(aSupprimer, new Coord(c, lig));
                }
            }

        } else {
            // Match normal 3 tuiles
            plateau.ajouterScore(taille * 100);
            System.out.println("Match x" + taille + " ! +" + (taille * 100) + " pts");
            if (vertical) {
                for (int l = lig; l <= fin; l++) {
                    ajouterSiAbsent(aSupprimer, new Coord(debut, l));
                }
            } else {
                for (int c = debut; c <= fin; c++) {
                    ajouterSiAbsent(aSupprimer, new Coord(c, lig));
                }
            }
        }
        return aSupprimer;
    }

    // -------------------------------------------------------------------------
    // BONUS T ou L
    // -------------------------------------------------------------------------
    // Applique le bonus bombe si une tuile est à l'intersection d'un match vertical ET horizontal
    private void appliquerBonusTetL(Plateau plateau,
            ArrayList<Coord> verticaux,
            ArrayList<Coord> horizontaux,
            ArrayList<Coord> aSupprimer) {
        for (Coord cv : verticaux) {
            for (Coord ch : horizontaux) {
                if (cv.equals(ch) && !contient(aSupprimer, cv)) {
                    plateau.ajouterScore(800);
                    System.out.println("BONUS ! Macron EXPLOSION !!!!! +800 pts");
                    ajouterZone4x4(plateau, cv, aSupprimer);
                }
            }
        }
    }

    // Ajoute toutes les tuiles d'une zone 3x3 autour d'un centre
    private void ajouterZone4x4(Plateau plateau, Coord centre, ArrayList<Coord> aSupprimer) {
        for (int c = centre.getAbscisse() - 2; c <= centre.getAbscisse() + 2; c++) {
            for (int l = centre.getOrdonnee() - 2; l <= centre.getOrdonnee() + 2; l++) {
                if (c >= 0 && c < plateau.getNbCol() && l >= 0 && l < plateau.getNbLig()) { //regarde si on est bien dans les limites du plateau
                    ajouterSiAbsent(aSupprimer, new Coord(c, l));
                }
            }
        }
    }

    public boolean contient(ArrayList<Coord> liste, Coord c) {
        for (Coord coord : liste) {
            if (coord.equals(c)) {
                return true;
            }
        }
        return false;
    }

    private void ajouterSiAbsent(ArrayList<Coord> liste, Coord c) {
        if (!contient(liste, c)) {
            liste.add(c);
        }
    }
}
