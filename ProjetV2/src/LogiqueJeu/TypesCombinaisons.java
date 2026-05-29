package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;
import Sons.Son;
import Sons.SonManager;
import java.util.ArrayList;

/**
 * Collecte toutes les tuiles à supprimer sur le plateau en appliquant
 * les règles de combinaisons par ordre de priorité :
 *
 *  1. T / L géant  (>= 7 tuiles)  → zone rayon 2, +1500 pts
 *  2. Ligne x5+                   → supprime toutes les tuiles du même type, +1000 pts
 *  3. T / L normal (5 ou 6 tuiles)→ zone rayon 1, +800 pts
 *  4. Ligne x4                    → supprime toute la ligne/colonne, +500 pts
 *  5. Carré 2×2                   → +400 pts
 *  6. Ligne x3                    → +300 pts
 */
public class TypesCombinaisons {

    // -------------------------------------------------------------------------
    // MÉTHODE PRINCIPALE
    // -------------------------------------------------------------------------

    public ArrayList<Coord> collecterToutesLesTuilesASupprimer(Plateau plateau) {
        ArrayList<Coord> aSupprimer   = new ArrayList<>();
        ArrayList<Coord> dejaTraitees = new ArrayList<>();

        ArrayList<Coord> verticales   = collecterMatchsVerticaux(plateau);
        ArrayList<Coord> horizontales = collecterMatchsHorizontaux(plateau);

        appliquerBonusTetL(plateau, verticales, horizontales, aSupprimer, dejaTraitees, true);
        appliquerMatchsLignes(plateau, verticales,   aSupprimer, dejaTraitees, true,  5);
        appliquerMatchsLignes(plateau, horizontales, aSupprimer, dejaTraitees, false, 5);
        appliquerBonusTetL(plateau, verticales, horizontales, aSupprimer, dejaTraitees, false);
        appliquerMatchsLignes(plateau, verticales,   aSupprimer, dejaTraitees, true,  4);
        appliquerMatchsLignes(plateau, horizontales, aSupprimer, dejaTraitees, false, 4);
        appliquerCarres(plateau, aSupprimer, dejaTraitees);
        appliquerMatchsLignes(plateau, verticales,   aSupprimer, dejaTraitees, true,  3);
        appliquerMatchsLignes(plateau, horizontales, aSupprimer, dejaTraitees, false, 3);

        return aSupprimer;
    }

    // -------------------------------------------------------------------------
    // BONUS T ou L
    // grandUniquement=true  → T/L de 7+ tuiles
    // grandUniquement=false → T/L de 5 ou 6 tuiles
    // -------------------------------------------------------------------------

    private void appliquerBonusTetL(Plateau plateau,
            ArrayList<Coord> verticales, ArrayList<Coord> horizontales,
            ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees,
            boolean grandUniquement) {

        for (Coord cv : verticales) {
            for (Coord ch : horizontales) {
                if (!cv.equals(ch) || contient(dejaTraitees, cv)) continue;

                ArrayList<Coord> formantLeT = new ArrayList<>();
                for (Coord t : verticales) {
                    if (t.getAbscisse() == cv.getAbscisse()) ajouterSiAbsent(formantLeT, t);
                }
                for (Coord t : horizontales) {
                    if (t.getOrdonnee() == cv.getOrdonnee()) ajouterSiAbsent(formantLeT, t);
                }

                int n = formantLeT.size();
                boolean estGrand = (n >= 7);
                if (grandUniquement != estGrand) continue;

                if (estGrand) {
                    plateau.ajouterScore(1500);
                    System.out.println("MEGA BONUS T : +1500 pts");
                    SonManager.jouerNsecondes(Son.HIROSHIMA, 2);
                    ajouterZoneRayon(plateau, cv, 2, aSupprimer);
                } else {
                    plateau.ajouterScore(800);
                    System.out.println("BONUS T/L : +800 pts");
                    SonManager.jouer(Son.EXPLOSION);
                    ajouterZoneRayon(plateau, cv, 1, aSupprimer);
                }

                for (Coord t : formantLeT) {
                    ajouterSiAbsent(aSupprimer, t);
                    ajouterSiAbsent(dejaTraitees, t);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // MATCHS EN LIGNE (x3, x4, x5+)
    // -------------------------------------------------------------------------

    private void appliquerMatchsLignes(Plateau plateau, ArrayList<Coord> tuiles,
            ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees,
            boolean estVertical, int tailleVoulue) {

        int nbAxes = estVertical ? plateau.getNbCol() : plateau.getNbLig();

        for (int axe = 0; axe < nbAxes; axe++) {
            ArrayList<Integer> positions = new ArrayList<>();
            for (Coord t : tuiles) {
                int a = estVertical ? t.getAbscisse() : t.getOrdonnee();
                int p = estVertical ? t.getOrdonnee() : t.getAbscisse();
                if (a == axe && !positions.contains(p)) positions.add(p);
            }
            positions.sort(Integer::compareTo);

            int i = 0;
            while (i < positions.size()) {
                int debut = positions.get(i);
                int fin   = debut;

                while (i + 1 < positions.size() && positions.get(i + 1) == fin + 1) {
                    i++;
                    fin++;
                }

                int taille = fin - debut + 1;
                boolean tailleOk = (tailleVoulue >= 5 && taille >= 5)
                        || (tailleVoulue == 4 && taille == 4)
                        || (tailleVoulue == 3 && taille == 3);

                if (tailleOk && !toutesDejaTraitees(axe, debut, fin, estVertical, dejaTraitees)) {
                    appliquerEffet(plateau, axe, debut, fin, taille, estVertical, aSupprimer, dejaTraitees);
                }
                i++;
            }
        }
    }

    // -------------------------------------------------------------------------
    // CARRÉ 2×2
    // -------------------------------------------------------------------------

    private void appliquerCarres(Plateau plateau,
            ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees) {

        for (int col = 0; col < plateau.getNbCol() - 1; col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 1; lig++) {
                if (!estUnCarre(plateau, col, lig)) continue;

                Coord[] coins = {
                    new Coord(col,     lig),
                    new Coord(col + 1, lig),
                    new Coord(col,     lig + 1),
                    new Coord(col + 1, lig + 1)
                };

                boolean toutesTraitees = true;
                for (Coord coin : coins) {
                    if (!contient(dejaTraitees, coin)) { toutesTraitees = false; break; }
                }
                if (toutesTraitees) continue;

                plateau.ajouterScore(400);
                System.out.println("BONUS Carré 2×2 ! +400 pts");
                SonManager.jouer(Son.MATCH_SIMPLE);

                for (Coord coin : coins) {
                    ajouterSiAbsent(aSupprimer, coin);
                    ajouterSiAbsent(dejaTraitees, coin);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // EFFET PAR TAILLE
    // -------------------------------------------------------------------------

    private void appliquerEffet(Plateau plateau, int axe, int debut, int fin,
            int taille, boolean estVertical,
            ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees) {

        if (taille >= 5) {
            // Supprime toutes les tuiles du même type
            int typeCible = estVertical
                    ? plateau.getTuile(axe, debut).getType()
                    : plateau.getTuile(debut, axe).getType();
            plateau.ajouterScore(1000);
            System.out.println("BONUS x5+ : suppression couleur ! +1000 pts");
            SonManager.jouer(Son.RACISME);
            for (int c = 0; c < plateau.getNbCol(); c++) {
                for (int l = 0; l < plateau.getNbLig(); l++) {
                    if (plateau.getTuile(c, l).getType() == typeCible) {
                        ajouterSiAbsent(aSupprimer, new Coord(c, l));
                    }
                }
            }

        } else if (taille == 4) {
            // Supprime toute la ligne ou toute la colonne
            plateau.ajouterScore(500);
            System.out.println("BONUS x4 : ligne entière ! +500 pts");
            SonManager.jouer(Son.BONUS_FUSEE);
            if (estVertical) {
                for (int l = 0; l < plateau.getNbLig(); l++) {
                    ajouterSiAbsent(aSupprimer, new Coord(axe, l));
                }
            } else {
                for (int c = 0; c < plateau.getNbCol(); c++) {
                    ajouterSiAbsent(aSupprimer, new Coord(c, axe));
                }
            }

        } else {
            // Match x3 basique
            plateau.ajouterScore(taille * 100);
            System.out.println("Match x" + taille + " ! +" + (taille * 100) + " pts");
            SonManager.jouer(Son.MATCH_SIMPLE);
            for (int pos = debut; pos <= fin; pos++) {
                Coord t = estVertical ? new Coord(axe, pos) : new Coord(pos, axe);
                ajouterSiAbsent(aSupprimer, t);
            }
        }

        // Marquer les tuiles du segment comme traitées
        for (int pos = debut; pos <= fin; pos++) {
            Coord t = estVertical ? new Coord(axe, pos) : new Coord(pos, axe);
            ajouterSiAbsent(dejaTraitees, t);
        }
    }

    // -------------------------------------------------------------------------
    // COLLECTE DES MATCHS BRUTS
    // -------------------------------------------------------------------------

    public ArrayList<Coord> collecterMatchsVerticaux(Plateau plateau) {
        ArrayList<Coord> res = new ArrayList<>();
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 2; lig++) {
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 1))
                 && plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 2))) {
                    int fin = etendreMatch(plateau, col, lig, true);
                    for (int l = lig; l <= fin; l++) ajouterSiAbsent(res, new Coord(col, l));
                    lig = fin;
                }
            }
        }
        return res;
    }

    public ArrayList<Coord> collecterMatchsHorizontaux(Plateau plateau) {
        ArrayList<Coord> res = new ArrayList<>();
        for (int lig = 0; lig < plateau.getNbLig(); lig++) {
            for (int col = 0; col < plateau.getNbCol() - 2; col++) {
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col + 1, lig))
                 && plateau.getTuile(col, lig).equals(plateau.getTuile(col + 2, lig))) {
                    int fin = etendreMatch(plateau, col, lig, false);
                    for (int c = col; c <= fin; c++) ajouterSiAbsent(res, new Coord(c, lig));
                    col = fin;
                }
            }
        }
        return res;
    }

    /** Étend un match jusqu'à ce qu'il n'y ait plus de tuile identique dans la direction. */
    private int etendreMatch(Plateau plateau, int debutAxe, int ligneDepart, boolean estVertical) {
        int fin = (estVertical ? ligneDepart : debutAxe) + 2;
        while (true) {
            if (estVertical) {
                if (fin + 1 >= plateau.getNbLig()) break;
                if (!plateau.getTuile(debutAxe, fin + 1).equals(plateau.getTuile(debutAxe, ligneDepart))) break;
            } else {
                if (fin + 1 >= plateau.getNbCol()) break;
                if (!plateau.getTuile(fin + 1, ligneDepart).equals(plateau.getTuile(debutAxe, ligneDepart))) break;
            }
            fin++;
        }
        return fin;
    }

    // -------------------------------------------------------------------------
    // UTILITAIRES
    // -------------------------------------------------------------------------

    private boolean estUnCarre(Plateau plateau, int col, int lig) {
        int type = plateau.getTuile(col, lig).getType();
        return plateau.getTuile(col + 1, lig).getType()     == type
            && plateau.getTuile(col,     lig + 1).getType() == type
            && plateau.getTuile(col + 1, lig + 1).getType() == type;
    }

    private void ajouterZoneRayon(Plateau plateau, Coord centre, int rayon, ArrayList<Coord> liste) {
        for (int c = centre.getAbscisse() - rayon; c <= centre.getAbscisse() + rayon; c++) {
            for (int l = centre.getOrdonnee() - rayon; l <= centre.getOrdonnee() + rayon; l++) {
                if (c >= 0 && c < plateau.getNbCol() && l >= 0 && l < plateau.getNbLig()) {
                    ajouterSiAbsent(liste, new Coord(c, l));
                }
            }
        }
    }

    private boolean toutesDejaTraitees(int axe, int debut, int fin,
            boolean estVertical, ArrayList<Coord> dejaTraitees) {
        for (int pos = debut; pos <= fin; pos++) {
            Coord t = estVertical ? new Coord(axe, pos) : new Coord(pos, axe);
            if (!contient(dejaTraitees, t)) return false;
        }
        return true;
    }

    public boolean contient(ArrayList<Coord> liste, Coord c) {
        for (Coord coord : liste) {
            if (coord.equals(c)) return true;
        }
        return false;
    }

    private void ajouterSiAbsent(ArrayList<Coord> liste, Coord c) {
        if (!contient(liste, c)) liste.add(c);
    }
}
