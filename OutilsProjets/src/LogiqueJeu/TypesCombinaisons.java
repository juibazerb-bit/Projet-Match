package LogiqueJeu;

import Modele.Plateau;
import Modele.Coord;
import java.util.ArrayList;

public class TypesCombinaisons {

    // -------------------------------------------------------------------------
    // MÉTHODE PRINCIPALE
    // -------------------------------------------------------------------------
    public ArrayList<Coord> collecterToutesLesTuilesASupprimer(Plateau plateau) {
        ArrayList<Coord> aSupprimer = new ArrayList<>();

        ArrayList<Coord> tuilesCarres = collecterCarres(plateau);
        ArrayList<Coord> tuilesVerticaux = collecterMatchsVerticaux(plateau);
        ArrayList<Coord> tuilesHorizontaux = collecterMatchsHorizontaux(plateau);

        ArrayList<Coord> tuilesDejaTraitees = new ArrayList<>();

        // 1. Bonus T/L — prioritaire
        appliquerBonusTetL(plateau, tuilesVerticaux, tuilesHorizontaux,
                aSupprimer, tuilesDejaTraitees);

        // 2. Carrés
        for (Coord c : tuilesCarres) {
            ajouterSiAbsent(aSupprimer, c);
            ajouterSiAbsent(tuilesDejaTraitees, c);
        }

        // 3. Matchs verticaux
        for (int col = 0; col < plateau.getNbCol(); col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 2; lig++) {

                // Vérification de base : est-ce que les 3 tuiles consécutives (lig, lig+1, lig+2) sont identiques 
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 1))
                        && plateau.getTuile(col, lig).equals(plateau.getTuile(col, lig + 2))) {

                    // On a un match d'au moins 3 tuiles. 
                    // On appelle etendreMatch pour voir s'il y en a 4 ou 5 à la suite
                    int fin = etendreMatch(plateau, col, lig, true);
                    int taille = fin - lig + 1; // Calcul du nombre total de tuiles alignées

                    // On veut savoir si ce groupe de tuiles appartient déjà à un bonus T, L ou Carré
                    boolean groupeDejaTraite = true;

                    // On vérifie chaque tuile du match vertical qu'on vient de trouver
                    for (int l = lig; l <= fin; l++) {
                        // Si au moins une tuile du groupe n'est PAS dans la liste des déjà traitées,
                        // alors le groupe doit être comptabilisé.
                        if (!contient(tuilesDejaTraitees, new Coord(col, l))) {
                            groupeDejaTraite = false;
                        }
                    }

                    // Si le groupe est unique (pas mélangé a d'autres types)
                    if (!groupeDejaTraite) {
                        // On applique l'effet 
                        ArrayList<Coord> effet = appliquerEffetPoint(plateau, col, lig, fin, taille, true);

                        // On ajoute toutes les tuiles résultant de l'effet à la liste globale de suppression
                        for (Coord c : effet) {
                            ajouterSiAbsent(aSupprimer, c);
                        }
                    }

                    // On déplace l'indice de la boucle 'lig' directement à la fin du match
                    // pour éviter de retester les tuiles qu'on vient de traiter
                    lig = fin;
                }
            }
        }

        // 4. Matchs horizontaux
        for (int lig = 0; lig < plateau.getNbLig(); lig++) {
            for (int col = 0; col < plateau.getNbCol() - 2; col++) {
                if (plateau.getTuile(col, lig).equals(plateau.getTuile(col + 1, lig))
                        && plateau.getTuile(col, lig).equals(plateau.getTuile(col + 2, lig))) {

                    int fin = etendreMatch(plateau, col, lig, false);
                    int taille = fin - col + 1;

                    boolean groupeDejaTraite = true;
                    for (int c = col; c <= fin; c++) {
                        if (!contient(tuilesDejaTraitees, new Coord(c, lig))) {
                            groupeDejaTraite = false;
                        }
                    }

                    if (!groupeDejaTraite) {
                        ArrayList<Coord> effet = appliquerEffetPoint(plateau, col, lig, fin, taille, false);
                        for (Coord c : effet) {
                            ajouterSiAbsent(aSupprimer, c);
                        }
                    }

                    col = fin;
                }
            }
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
                    System.out.println("BONUS ! Carre 2x2 ! +400 pts");
                    ajouterSiAbsent(tuilesCarres, new Coord(col, lig));
                    ajouterSiAbsent(tuilesCarres, new Coord(col + 1, lig));
                    ajouterSiAbsent(tuilesCarres, new Coord(col, lig + 1));
                    ajouterSiAbsent(tuilesCarres, new Coord(col + 1, lig + 1));
                }
            }
        }
        return tuilesCarres;
    }

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
                    // On collecte juste les positions, sans appliquer les effets ici
                    for (int l = lig; l <= fin; l++) {
                        ajouterSiAbsent(tuiles, new Coord(col, l));
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
                    // On collecte juste les positions, sans appliquer les effets ici
                    for (int c = col; c <= fin; c++) {
                        ajouterSiAbsent(tuiles, new Coord(c, lig));
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
    // EFFETS SELON LA TAILLE DU MATCH — appliqué uniquement pour les matchs simples
    // -------------------------------------------------------------------------
    public ArrayList<Coord> appliquerEffetPoint(Plateau plateau, int debut, int lig,
            int fin, int taille, boolean vertical) {
        ArrayList<Coord> aSupprimer = new ArrayList<>();

        if (taille >= 5) {
            int typeCible = plateau.getTuile(debut, lig).getType();
            plateau.ajouterScore(1000);
            System.out.println("BONUS Super x5 ! Le racisme est a son comble! +1000 pts");
            for (int c = 0; c < plateau.getNbCol(); c++) {
                for (int l = 0; l < plateau.getNbLig(); l++) {
                    if (plateau.getTuile(c, l).getType() == typeCible) {
                        ajouterSiAbsent(aSupprimer, new Coord(c, l));
                    }
                }
            }
        } else if (taille == 4) {
            plateau.ajouterScore(500);
            System.out.println("BONUS Fusee x4 ! +500 pts");
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
    // BONUS T ou L — choisit le meilleur effet selon la taille totale
    // -------------------------------------------------------------------------
    private void appliquerBonusTetL(Plateau plateau,
            ArrayList<Coord> verticaux,
            ArrayList<Coord> horizontaux,
            ArrayList<Coord> aSupprimer,
            ArrayList<Coord> tuilesDejaTraitees) {

        for (Coord cv : verticaux) {
            for (Coord ch : horizontaux) {
                if (cv.equals(ch) && !contient(tuilesDejaTraitees, cv)) {

                    // On compte la taille totale du T ou L
                    // (tuiles verticales + horizontales autour de l'intersection)
                    ArrayList<Coord> tuilesT = new ArrayList<>();
                    for (Coord c : verticaux) {
                        if (c.getAbscisse() == cv.getAbscisse()) {
                            ajouterSiAbsent(tuilesT, c);
                        }
                    }
                    for (Coord c : horizontaux) {
                        if (c.getOrdonnee() == cv.getOrdonnee()) {
                            ajouterSiAbsent(tuilesT, c);
                        }
                    }
                    int tailleT = tuilesT.size();

                    if (tailleT >= 7) {
                        // T de 7 cases ou plus = explosion rayon 2
                        plateau.ajouterScore(1500);
                        System.out.println("MEGA BONUS T geant ! Hiroshima is comming! +1500 pts");
                        ajouterZoneRayon(plateau, cv, 2, aSupprimer);
                    } else {
                        // T ou L normal = explosion rayon 3x3
                        plateau.ajouterScore(800);
                        System.out.println("BONUS! Macron EXPLOSION ! +800 pts");
                        ajouterZone3x3(plateau, cv, aSupprimer);
                    }

                    // On marque toutes les tuiles du T comme deja traitees
                    for (Coord c : tuilesT) {
                        ajouterSiAbsent(tuilesDejaTraitees, c);
                        ajouterSiAbsent(aSupprimer, c);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // ZONES D'EXPLOSION
    // -------------------------------------------------------------------------
    // Ajoute toutes les tuiles d'une zone 3x3 autour d'un centre
    private void ajouterZone3x3(Plateau plateau, Coord centre, ArrayList<Coord> aSupprimer) {
        ajouterZoneRayon(plateau, centre, 1, aSupprimer);
    }

    // Ajoute toutes les tuiles dans un rayon N autour d'un centre
    private void ajouterZoneRayon(Plateau plateau, Coord centre, int rayon,
            ArrayList<Coord> aSupprimer) {
        for (int c = centre.getAbscisse() - rayon; c <= centre.getAbscisse() + rayon; c++) {
            for (int l = centre.getOrdonnee() - rayon; l <= centre.getOrdonnee() + rayon; l++) {
                if (c >= 0 && c < plateau.getNbCol() && l >= 0 && l < plateau.getNbLig()) {
                    ajouterSiAbsent(aSupprimer, new Coord(c, l));
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // UTILITAIRES
    // -------------------------------------------------------------------------
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
