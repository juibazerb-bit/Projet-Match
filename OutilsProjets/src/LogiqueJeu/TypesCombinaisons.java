package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;
import Sons.Son;
import Sons.SonManager;
import java.util.ArrayList;

public class TypesCombinaisons {

    // -------------------------------------------------------------------------
    // MÉTHODE PRINCIPALE
    // -------------------------------------------------------------------------
    public ArrayList<Coord> collecterToutesLesTuilesASupprimer(Plateau plateau) {
        ArrayList<Coord> aSupprimer = new ArrayList<>();
        ArrayList<Coord> dejaTraitees = new ArrayList<>();

        ArrayList<Coord> tuileVerticales = collecterMatchsVerticaux(plateau);
        ArrayList<Coord> tuileHorizontales = collecterMatchsHorizontaux(plateau);

        // Priorité 1 : T ou L (>= 7 tuiles)
        appliquerBonusTetL(plateau, tuileVerticales, tuileHorizontales, aSupprimer, dejaTraitees, true);

        // Priorité 2 : Match x5+ (ligne droite uniquement)
        appliquerMatchsLignes(plateau, tuileVerticales, aSupprimer, dejaTraitees, true, 5);
        appliquerMatchsLignes(plateau, tuileHorizontales, aSupprimer, dejaTraitees, false, 5);

        // Priorité 3 : T ou L (5 ou 6 tuiles)
        appliquerBonusTetL(plateau, tuileVerticales, tuileHorizontales, aSupprimer, dejaTraitees, false);

        // Priorité 4 : Match x4 (ligne droite uniquement)
        appliquerMatchsLignes(plateau, tuileVerticales, aSupprimer, dejaTraitees, true, 4);
        appliquerMatchsLignes(plateau, tuileHorizontales, aSupprimer, dejaTraitees, false, 4);

        // Priorité 5 : Carré 2x2
        appliquerCarres(plateau, aSupprimer, dejaTraitees);

        // Priorité 6 : Match x3
        appliquerMatchsLignes(plateau, tuileVerticales, aSupprimer, dejaTraitees, true, 3);
        appliquerMatchsLignes(plateau, tuileHorizontales, aSupprimer, dejaTraitees, false, 3);

        return aSupprimer;
    }

    // -------------------------------------------------------------------------
    // PRIORITÉ 1 & 3 : BONUS T ou L
    // grandUniquement = true  → ne traite que les T/L de 7 tuiles ou plus
    // grandUniquement = false → ne traite que les T/L de 5 ou 6 tuiles
    // -------------------------------------------------------------------------
    public void appliquerBonusTetL(Plateau plateau, ArrayList<Coord> tuileVerticales, ArrayList<Coord> tuileHorizontales, ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees, boolean grandUniquement) {

        for (Coord tuileVerticale : tuileVerticales) {
            for (Coord tuileHorizontale : tuileHorizontales) {

                boolean memePosition = tuileVerticale.equals(tuileHorizontale);
                boolean dejaVue = contient(dejaTraitees, tuileVerticale);

                if (!memePosition || dejaVue) {
                    continue;
                }

                // Collecte des tuiles formant le T ou L autour de l'intersection
                ArrayList<Coord> tuilesFormantLeT = new ArrayList<>();
                for (Coord tuileCourante : tuileVerticales) {
                    if (tuileCourante.getAbscisse() == tuileVerticale.getAbscisse()) {
                        ajouterSiAbsent(tuilesFormantLeT, tuileCourante);
                    }
                }
                for (Coord tuileCourante : tuileHorizontales) {
                    if (tuileCourante.getOrdonnee() == tuileVerticale.getOrdonnee()) {
                        ajouterSiAbsent(tuilesFormantLeT, tuileCourante);
                    }
                }

                int nombreTuiles = tuilesFormantLeT.size();
                boolean estGrand = nombreTuiles >= 7;

                if (grandUniquement != estGrand) {
                    continue;
                }

                if (estGrand) {
                    plateau.ajouterScore(1500);
                    System.out.println("MEGA BONUS T : Hiroshima ! +1500 pts");
                    SonManager.jouerNsecondes(Son.HIROSHIMA, 2);
                    ajouterZoneRayon(plateau, tuileVerticale, 2, aSupprimer);
                } else {
                    plateau.ajouterScore(800);
                    System.out.println("BONUS T/L : Macron EXPLOSION ! +800 pts");
                    SonManager.jouer(Son.EXPLOSION);
                    ajouterZoneRayon(plateau, tuileVerticale, 1, aSupprimer);
                }

                for (Coord tuileDuT : tuilesFormantLeT) {
                    ajouterSiAbsent(aSupprimer, tuileDuT);
                    ajouterSiAbsent(dejaTraitees, tuileDuT);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // PRIORITÉS 2, 4, 6 : MATCHS EN LIGNE (x5+, x4, x3)
    // -------------------------------------------------------------------------
    public void appliquerMatchsLignes(Plateau plateau, ArrayList<Coord> tuiles, ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees, boolean estVertical, int tailleVoulue) {
        int nombreAxes = estVertical ? plateau.getNbCol() : plateau.getNbLig();
        for (int numeroAxe = 0; numeroAxe < nombreAxes; numeroAxe++) {

            // Collecte les positions alignées sur cet axe
            ArrayList<Integer> positionsSurAxe = new ArrayList<>();
            for (Coord tuileCourante : tuiles) {
                int axe = estVertical ? tuileCourante.getAbscisse() : tuileCourante.getOrdonnee();
                int position = estVertical ? tuileCourante.getOrdonnee() : tuileCourante.getAbscisse();
                if (axe == numeroAxe && !contientIndice(positionsSurAxe, position)) {
                    positionsSurAxe.add(position);
                }
            }
            positionsSurAxe.sort(Integer::compareTo);

            // Regroupe les positions consécutives en segments
            int indiceCourant = 0;
            while (indiceCourant < positionsSurAxe.size()) {
                int positionDebut = positionsSurAxe.get(indiceCourant);
                int positionFin = positionDebut;

                boolean segmentContinue = true;
                while (segmentContinue) {
                    boolean positionSuivanteExiste = indiceCourant + 1 < positionsSurAxe.size();
                    boolean positionSuivanteConsecutive = positionSuivanteExiste&& positionsSurAxe.get(indiceCourant + 1) == positionFin + 1;
                    if (positionSuivanteConsecutive) {
                        indiceCourant++;
                        positionFin++;
                    } else {
                        segmentContinue = false;
                    }
                }

                int nombreTuiles = positionFin - positionDebut + 1;
                boolean taillCorrespond = (tailleVoulue >= 5 && nombreTuiles >= 5)
                        || (tailleVoulue == 4 && nombreTuiles == 4)
                        || (tailleVoulue == 3 && nombreTuiles == 3);

                if (taillCorrespond) {
                    // Vérifie qu'au moins une tuile n'est pas déjà traitée
                    boolean toutesDejaTraitees = true;
                    int positionVerif = positionDebut;
                    while (positionVerif <= positionFin && toutesDejaTraitees) {
                        Coord tuileAVerifier = estVertical
                                ? new Coord(numeroAxe, positionVerif)
                                : new Coord(positionVerif, numeroAxe);
                        if (!contient(dejaTraitees, tuileAVerifier)) {
                            toutesDejaTraitees = false;
                        }
                        positionVerif++;
                    }

                    if (!toutesDejaTraitees) {
                        appliquerEffet(plateau, numeroAxe, positionDebut, positionFin,
                                nombreTuiles, estVertical, aSupprimer, dejaTraitees);
                    }
                }
                indiceCourant++;
            }
        }
    }

    // -------------------------------------------------------------------------
    // PRIORITÉ 5 : CARRÉ 2x2
    // -------------------------------------------------------------------------
    public void appliquerCarres(Plateau plateau, ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees) {

        for (int colonne = 0; colonne < plateau.getNbCol() - 1; colonne++) {
            for (int ligne = 0; ligne < plateau.getNbLig() - 1; ligne++) {
                if (!estUnCarre(plateau, colonne, ligne)) {
                    continue;
                }

                Coord coinBasGauche = new Coord(colonne, ligne);
                Coord coinBasDroit = new Coord(colonne + 1, ligne);
                Coord coinHautGauche = new Coord(colonne, ligne + 1);
                Coord coinHautDroit = new Coord(colonne + 1, ligne + 1);

                boolean toutesDejaTraitees = contient(dejaTraitees, coinBasGauche)
                        && contient(dejaTraitees, coinBasDroit)
                        && contient(dejaTraitees, coinHautGauche)
                        && contient(dejaTraitees, coinHautDroit);

                if (toutesDejaTraitees) {
                    continue;
                }

                plateau.ajouterScore(400);
                System.out.println("BONUS ! Carre 2x2 ! +400 pts");
                SonManager.jouer(Son.MATCH_SIMPLE);

                for (Coord coinDuCarre : new Coord[]{coinBasGauche, coinBasDroit, coinHautGauche, coinHautDroit}) {
                    ajouterSiAbsent(aSupprimer, coinDuCarre);
                    ajouterSiAbsent(dejaTraitees, coinDuCarre);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // APPLICATION DE L'EFFET (x3, x4, x5+)
    // -------------------------------------------------------------------------
    public void appliquerEffet(Plateau plateau, int numeroAxe, int positionDebut, int positionFin, int nombreTuiles, boolean estVertical, ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees) {

        if (nombreTuiles >= 5) {
            int typeCible = estVertical
                    ? plateau.getTuile(numeroAxe, positionDebut).getType()
                    : plateau.getTuile(positionDebut, numeroAxe).getType();
            plateau.ajouterScore(1000);
            System.out.println("BONUS: OUI je suis raciste ! +1000 pts");
            SonManager.jouer(Son.RACISME);
            for (int colonne = 0; colonne < plateau.getNbCol(); colonne++) {
                for (int ligne = 0; ligne < plateau.getNbLig(); ligne++) {
                    if (plateau.getTuile(colonne, ligne).getType() == typeCible) {
                        ajouterSiAbsent(aSupprimer, new Coord(colonne, ligne));
                    }
                }
            }

        } else if (nombreTuiles == 4) {
            plateau.ajouterScore(500);
            System.out.println("BONUS: PIOU PIOU ! +500 pts");
            SonManager.jouer(Son.BONUS_FUSEE);
            if (estVertical) {
                for (int ligne = 0; ligne < plateau.getNbLig(); ligne++) {
                    ajouterSiAbsent(aSupprimer, new Coord(numeroAxe, ligne));
                }
            } else {
                for (int colonne = 0; colonne < plateau.getNbCol(); colonne++) {
                    ajouterSiAbsent(aSupprimer, new Coord(colonne, numeroAxe));
                }
            }

        } else {
            plateau.ajouterScore(nombreTuiles * 100);
            System.out.println("Match x" + nombreTuiles + " ! +" + (nombreTuiles * 100) + " pts");
            SonManager.jouer(Son.MATCH_SIMPLE);
            for (int position = positionDebut; position <= positionFin; position++) {
                Coord tuileCourante = estVertical ? new Coord(numeroAxe, position) : new Coord(position, numeroAxe);
                ajouterSiAbsent(aSupprimer, tuileCourante);
            }
        }

        // Marquer toutes les tuiles du segment comme traitées
        for (int position = positionDebut; position <= positionFin; position++) {
            Coord tuileCourante = estVertical ? new Coord(numeroAxe, position) : new Coord(position, numeroAxe);
            ajouterSiAbsent(dejaTraitees, tuileCourante);
        }
    }

    // -------------------------------------------------------------------------
    // COLLECTE HORIZONTALE / VERTICALE
    // -------------------------------------------------------------------------
    public ArrayList<Coord> collecterMatchsVerticaux(Plateau plateau) {
        ArrayList<Coord> tuilesTrouvees = new ArrayList<>();
        for (int colonne = 0; colonne < plateau.getNbCol(); colonne++) {
            for (int ligne = 0; ligne < plateau.getNbLig() - 2; ligne++) {
                if (plateau.getTuile(colonne, ligne).equals(plateau.getTuile(colonne, ligne + 1))
                        && plateau.getTuile(colonne, ligne).equals(plateau.getTuile(colonne, ligne + 2))) {
                    int ligneFin = etendreMatch(plateau, colonne, ligne, true);
                    for (int ligneMatch = ligne; ligneMatch <= ligneFin; ligneMatch++) {
                        ajouterSiAbsent(tuilesTrouvees, new Coord(colonne, ligneMatch));
                    }
                    ligne = ligneFin;
                }
            }
        }
        return tuilesTrouvees;
    }

    public ArrayList<Coord> collecterMatchsHorizontaux(Plateau plateau) {
        ArrayList<Coord> tuilesTrouvees = new ArrayList<>();
        for (int ligne = 0; ligne < plateau.getNbLig(); ligne++) {
            for (int colonne = 0; colonne < plateau.getNbCol() - 2; colonne++) {
                if (plateau.getTuile(colonne, ligne).equals(plateau.getTuile(colonne + 1, ligne))
                        && plateau.getTuile(colonne, ligne).equals(plateau.getTuile(colonne + 2, ligne))) {
                    int colonneFin = etendreMatch(plateau, colonne, ligne, false);
                    for (int colonneMatch = colonne; colonneMatch <= colonneFin; colonneMatch++) {
                        ajouterSiAbsent(tuilesTrouvees, new Coord(colonneMatch, ligne));
                    }
                    colonne = colonneFin;
                }
            }
        }
        return tuilesTrouvees;
    }

    public int etendreMatch(Plateau plateau, int debutAxe, int ligneDepart, boolean estVertical) {
        int positionFin = (estVertical ? ligneDepart : debutAxe) + 2;
        boolean peutEtendre = true;
        while (peutEtendre) {
            if (estVertical) {
                boolean suivantExiste = positionFin + 1 < plateau.getNbLig();
                boolean suivantIdentique = suivantExiste&& plateau.getTuile(debutAxe, positionFin + 1).equals(plateau.getTuile(debutAxe, ligneDepart));
                if (suivantIdentique) {
                    positionFin++;
                } else {
                    peutEtendre = false;
                }
            } else {
                boolean suivantExiste = positionFin + 1 < plateau.getNbCol();
                boolean suivantIdentique = suivantExiste&& plateau.getTuile(positionFin + 1, ligneDepart).equals(plateau.getTuile(debutAxe, ligneDepart));
                if (suivantIdentique) {
                    positionFin++;
                } else {
                    peutEtendre = false;
                }
            }
        }
        return positionFin;
    }

    // -------------------------------------------------------------------------
    // UTILITAIRES
    // -------------------------------------------------------------------------
    public boolean estUnCarre(Plateau plateau, int colonne, int ligne) {
        int typeTuile = plateau.getTuile(colonne, ligne).getType();
        return plateau.getTuile(colonne + 1, ligne).getType() == typeTuile
                && plateau.getTuile(colonne, ligne + 1).getType() == typeTuile
                && plateau.getTuile(colonne + 1, ligne + 1).getType() == typeTuile;
    }

    public void ajouterZoneRayon(Plateau plateau, Coord centre, int rayon, ArrayList<Coord> aSupprimer) {
        for (int colonne = centre.getAbscisse() - rayon; colonne <= centre.getAbscisse() + rayon; colonne++) {
            for (int ligne = centre.getOrdonnee() - rayon; ligne <= centre.getOrdonnee() + rayon; ligne++) {
                if (colonne >= 0 && colonne < plateau.getNbCol() && ligne >= 0 && ligne < plateau.getNbLig()) {
                    ajouterSiAbsent(aSupprimer, new Coord(colonne, ligne));
                }
            }
        }
    }

    public boolean contient(ArrayList<Coord> liste, Coord coordonnee) {
        boolean trouve = false;
        int indice = 0;
        while (indice < liste.size() && !trouve) {
            if (liste.get(indice).equals(coordonnee)) {
                trouve = true;
            }
            indice++;
        }
        return trouve;
    }

    public boolean contientIndice(ArrayList<Integer> liste, int valeur) {
        boolean trouve = false;
        int indice = 0;
        while (indice < liste.size() && !trouve) {
            if (liste.get(indice) == valeur) {
                trouve = true;
            }
            indice++;
        }
        return trouve;
    }

    public void ajouterSiAbsent(ArrayList<Coord> liste, Coord coordonnee) {
        if (!contient(liste, coordonnee)) {
            liste.add(coordonnee);
        }
    }
}
