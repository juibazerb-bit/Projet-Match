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

        ArrayList<Coord> verticales = collecterMatchsVerticaux(plateau);
        ArrayList<Coord> horizontales = collecterMatchsHorizontaux(plateau);

        // Priorité 1 & 3 : T/L géant puis T/L normal
        appliquerBonusTetL(plateau, verticales, horizontales, aSupprimer, dejaTraitees, true);
        appliquerBonusTetL(plateau, verticales, horizontales, aSupprimer, dejaTraitees, false);

        // Priorité 2, 4, 6 : lignes (x5+ > x4 > x3 gérés dans appliquerEffet)
        appliquerMatchsLignes(plateau, verticales, aSupprimer, dejaTraitees, true);
        appliquerMatchsLignes(plateau, horizontales, aSupprimer, dejaTraitees, false);

        // Priorité 5 : carré 2×2
        appliquerCarres(plateau, aSupprimer, dejaTraitees);

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
                    if (SonManager.estActif()) System.out.println("MEGA BONUS T : Hiroshima ! +1500 pts");
                    SonManager.jouerNsecondes(Son.HIROSHIMA, 2);
                    ajouterZoneRayon(plateau, tuileVerticale, 2, aSupprimer);
                } else {
                    plateau.ajouterScore(800);
                    if (SonManager.estActif()) System.out.println("BONUS T/L : Macron EXPLOSION ! +800 pts");
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
// MATCHS EN LIGNE (x3, x4, x5+) 
// -------------------------------------------------------------------------
    private void appliquerMatchsLignes(Plateau plateau, ArrayList<Coord> tuiles, ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees, boolean estVertical) {

        // 1. Déclaration du dictionnaire pour regrouper les tuiles par ligne ou colonne Cle et valeur associé
        java.util.HashMap<Integer, ArrayList<Integer>> parAxe = new java.util.HashMap<>();

        // Remplissage de la HashMap : on classe la position de la tuile selon son axe
        for (Coord t : tuiles) {
            int axe = estVertical ? t.getAbscisse() : t.getOrdonnee();
            int pos = estVertical ? t.getOrdonnee() : t.getAbscisse();

            // Crée l'ArrayList pour l'axe s'il n'existe pas, puis y ajoute la position
            parAxe.computeIfAbsent(axe, k -> new ArrayList<>()).add(pos);
        }

        // 2. Parcours de la HashMap axe par axe
        for (java.util.Map.Entry<Integer, ArrayList<Integer>> entree : parAxe.entrySet()) {
            int axe = entree.getKey();                  // Numéro de la ligne/colonne
            ArrayList<Integer> positions = entree.getValue(); // Liste des positions sur cet axe

            // Tri indispensable pour trouver les tuiles côte à côte
            positions.sort(Integer::compareTo);

            // Détection des segments continus (tuiles adjacentes)
            int i = 0;
            while (i < positions.size()) {
                int debut = positions.get(i);
                int fin = debut;

                while (i + 1 < positions.size() && positions.get(i + 1) == fin + 1) {
                    i++;
                    fin++;
                }

                int taille = fin - debut + 1;

                // Validation et application des scores/effets
                if (taille >= 3 && !toutesDejaTraitees(axe, debut, fin, estVertical, dejaTraitees)) {
                    appliquerEffet(plateau, axe, debut, fin, taille, estVertical, aSupprimer, dejaTraitees);
                }

                i++;
            }
        }
    }

    // -------------------------------------------------------------------------
    // CARRÉ 2×2
    // -------------------------------------------------------------------------
    private void appliquerCarres(Plateau plateau,ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees) {

        for (int col = 0; col < plateau.getNbCol() - 1; col++) {
            for (int lig = 0; lig < plateau.getNbLig() - 1; lig++) {
                if (!estUnCarre(plateau, col, lig)) {
                    continue;
                }

                Coord[] coins = {
                    new Coord(col, lig),
                    new Coord(col + 1, lig),
                    new Coord(col, lig + 1),
                    new Coord(col + 1, lig + 1)
                };

                boolean toutesTraitees = true;
                for (Coord coin : coins) {
                    if (!contient(dejaTraitees, coin)) {
                        toutesTraitees = false;
                        break;
                    }
                }
                if (toutesTraitees) {
                    continue;
                }

                plateau.ajouterScore(400);
                if (SonManager.estActif()) System.out.println("BONUS ! Carre 2x2 ! +400 pts");
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
    private void appliquerEffet(Plateau plateau, int axe, int debut, int fin,int taille, boolean estVertical,ArrayList<Coord> aSupprimer, ArrayList<Coord> dejaTraitees) {

        if (taille >= 5) {
            // Supprime toutes les tuiles du même type
            Modele.Tuile tuileRef = estVertical? plateau.getTuile(axe, debut): plateau.getTuile(debut, axe);
            if (tuileRef == null) {
                return; // garde null : ne peut pas déterminer le type cible
            }
            int typeCible = tuileRef.getType();
            plateau.ajouterScore(1000);
            if (SonManager.estActif()) System.out.println("BONUS: OUI je suis raciste ! +1000 pts");
            SonManager.jouer(Son.RACISME);
            for (int c = 0; c < plateau.getNbCol(); c++) {
                for (int l = 0; l < plateau.getNbLig(); l++) {
                    Modele.Tuile t = plateau.getTuile(c, l);
                    if (t != null && t.getType() == typeCible) {
                        ajouterSiAbsent(aSupprimer, new Coord(c, l));
                    }
                }
            }

        } else if (taille == 4) {
            // Supprime toute la ligne ou toute la colonne
            plateau.ajouterScore(500);
            if (SonManager.estActif()) System.out.println("BONUS: PIOU PIOU ! +500 pts");
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
            if (SonManager.estActif()) System.out.println("Match x3! + 300 pts");
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
                Modele.Tuile t0 = plateau.getTuile(col, lig);
                Modele.Tuile t1 = plateau.getTuile(col, lig + 1);
                Modele.Tuile t2 = plateau.getTuile(col, lig + 2);
                // sert a prendre que les tuiles présentes pour eviter de crash a la ligne suivante 
                if (t0 == null || t1 == null || t2 == null) {
                    continue;
                }
                if (t0.equals(t1) && t0.equals(t2)) {
                    int fin = etendreMatch(plateau, col, lig, true);
                    for (int l = lig; l <= fin; l++) {
                        ajouterSiAbsent(res, new Coord(col, l));
                    }
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
                Modele.Tuile t0 = plateau.getTuile(col, lig);
                Modele.Tuile t1 = plateau.getTuile(col + 1, lig);
                Modele.Tuile t2 = plateau.getTuile(col + 2, lig);
                if (t0 == null || t1 == null || t2 == null) {
                    continue;
                }
                if (t0.equals(t1) && t0.equals(t2)) {
                    int fin = etendreMatch(plateau, col, lig, false);
                    for (int c = col; c <= fin; c++) {
                        ajouterSiAbsent(res, new Coord(c, lig));
                    }
                    col = fin;
                }
            }
        }
        return res;
    }

    /**
     * Étend un match jusqu'à ce qu'il n'y ait plus de tuile identique dans la
     * direction.
     */
    private int etendreMatch(Plateau plateau, int debutAxe, int ligneDepart, boolean estVertical) {
        int fin = (estVertical ? ligneDepart : debutAxe) + 2;
        boolean continuer = true;

        while (continuer) {
            if (estVertical) {
                // 1. Vérification de la bordure du plateau
                if (fin + 1 >= plateau.getNbLig()) {
                    continuer = false;
                } else {
                    Modele.Tuile suivante = plateau.getTuile(debutAxe, fin + 1);
                    Modele.Tuile ref = plateau.getTuile(debutAxe, ligneDepart);

                    // 2. Vérification de la validité de la tuile suivante
                    if (suivante == null || ref == null || !suivante.equals(ref)) {
                        continuer = false;
                    }
                }
            } else {
                // 1. Vérification de la bordure du plateau
                if (fin + 1 >= plateau.getNbCol()) {
                    continuer = false;
                } else {
                    Modele.Tuile suivante = plateau.getTuile(fin + 1, ligneDepart);
                    Modele.Tuile ref = plateau.getTuile(debutAxe, ligneDepart);

                    // 2. Vérification de la validité de la tuile suivante
                    if (suivante == null || ref == null || !suivante.equals(ref)) {
                        continuer = false;
                    }
                }
            }

            // On incrémente la position uniquement si toutes les conditions sont validées
            if (continuer) {
                fin++;
            }
        }

        return fin;
    }

    // -------------------------------------------------------------------------
    // UTILITAIRES
    // -------------------------------------------------------------------------
    private boolean estUnCarre(Plateau plateau, int col, int lig) {
        Modele.Tuile t00 = plateau.getTuile(col, lig);
        Modele.Tuile t10 = plateau.getTuile(col + 1, lig);
        Modele.Tuile t01 = plateau.getTuile(col, lig + 1);
        Modele.Tuile t11 = plateau.getTuile(col + 1, lig + 1);
        if (t00 == null || t10 == null || t01 == null || t11 == null) {
            return false;
        }
        int type = t00.getType();
        return t10.getType() == type && t01.getType() == type && t11.getType() == type;
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
            if (!contient(dejaTraitees, t)) {
                return false;
            }
        }
        return true;
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
