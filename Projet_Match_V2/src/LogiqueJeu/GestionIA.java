package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;
import Modele.StatCoup;
import Modele.Tuile;
import Sons.SonManager;
import java.util.ArrayList;
import java.util.Random;

/**
 * Intelligence artificielle du jeu.
 *
 * Fournit trois services : - listEchange() : tous les échanges légaux qui
 * créent un match - aideOrdi() : le meilleur coup (déterministe, par
 * simulation) - obtenirMeilleurCoupStatistique() : le meilleur coup
 * (Monte-Carlo) - aideNCoups() : fait jouer l'IA seule pendant N coups
 */
public class GestionIA {

    private final DetectionMatchs detection = new DetectionMatchs();
    private final SuppressionMatchs suppression = new SuppressionMatchs();

    // -------------------------------------------------------------------------
    // LISTE DES ÉCHANGES LÉGAUX
    // -------------------------------------------------------------------------
    /**
     * Retourne la liste de tous les échanges voisins qui créent au moins un
     * match, sous la forme [c1, c2, c1', c2', ...].
     */
    public ArrayList<Coord> listEchange(Plateau plateau) {
        ArrayList<Coord> matchs = new ArrayList<>();
        int nbLig = plateau.getNbLig();
        int nbCol = plateau.getNbCol();

        // Échanges verticaux (col, lig) ↔ (col, lig+1)
        for (int lig = 0; lig < nbLig - 1; lig++) {
            for (int col = 0; col < nbCol; col++) {
                testerEchange(plateau, matchs, new Coord(col, lig), new Coord(col, lig + 1));
            }
        }
        // Échanges horizontaux (col, lig) ↔ (col+1, lig)
        for (int col = 0; col < nbCol - 1; col++) {
            for (int lig = 0; lig < nbLig; lig++) {
                testerEchange(plateau, matchs, new Coord(col, lig), new Coord(col + 1, lig));
            }
        }
        return matchs;
    }

    /**
     * Retourne une description texte de tous les échanges possibles.
     */
    public String listMatchsTexte(Plateau plateau) {
        ArrayList<Coord> matchs = listEchange(plateau);
        if (matchs.isEmpty()) {
            return "Aucun échange possible.";
        }

        StringBuilder sb = new StringBuilder("Échanges possibles :");
        for (int i = 0; i < matchs.size(); i += 2) {
            sb.append("\n  ").append(matchs.get(i)).append(" ↔ ").append(matchs.get(i + 1));
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // MEILLEUR COUP (DÉTERMINISTE)
    // -------------------------------------------------------------------------
    /**
     * Retourne le meilleur coup en simulant chaque échange sur une copie du
     * plateau. Critère : score maximum, puis nombre de tuiles supprimées en cas
     * d'égalité.
     */
    public ArrayList<Coord> aideOrdi(Plateau plateau) {
        SonManager.desactiver();
        ArrayList<Coord> matchs = listEchange(plateau);
        if (matchs.isEmpty()) {
            SonManager.activer();
            return new ArrayList<>();
        }

        ArrayList<Coord> meilleur = new ArrayList<>();
        int meilleurScore = -1;
        int meilleurTuiles = -1;

        for (int i = 0; i < matchs.size(); i += 2) {
            Coord c1 = matchs.get(i);
            Coord c2 = matchs.get(i + 1);

            Plateau copie = plateau.copy();
            copie.echangerTuiles(c1, c2);
            int nbTuiles = simulerMatchsDeterministe(copie);
            int score = copie.getScore();

            if (score > meilleurScore || (score == meilleurScore && nbTuiles > meilleurTuiles)) {
                meilleurScore = score;
                meilleurTuiles = nbTuiles;
                meilleur.clear();
                meilleur.add(c1);
                meilleur.add(c2);
            }
        }
        SonManager.activer();

        if (!meilleur.isEmpty()) {
            System.out.println("Meilleur coup : " + meilleur.get(0) + " ↔ " + meilleur.get(1)
                    + " | Score : " + meilleurScore + " | Tuiles : " + meilleurTuiles);
        }
        return meilleur;
    }

    /**
     * Simule les matchs en cascade UNIQUEMENT avec les tuiles déjà présentes.
     * Les cases vides (haut de colonne) sont représentées par null dans la
     * liste, et getTuile() retourne null pour les indices hors bornes — ce que
     * TypesCombinaisons gère via des vérifications de null.
     *
     *
     * Retourne le nombre total de tuiles supprimées.
     */
    public int simulerMatchsDeterministe(Plateau plateau) {
        int total = 0;
        ArrayList<Coord> aSupprimer;

        while (!(aSupprimer = collecterAvecNullGuard(plateau)).isEmpty()) {
            total += aSupprimer.size();

            // Remplace les tuiles supprimées par null
            for (Coord c : aSupprimer) {
                remplacerParNull(plateau, c);
            }

            // Compacte chaque colonne : les tuiles non-null descendent, null reste en haut
            for (int col = 0; col < plateau.getNbCol(); col++) {
                compacterColonne(plateau, col);
            }
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // MEILLEUR COUP (MONTE-CARLO)
    // -------------------------------------------------------------------------
    /**
     * Lance nbSimulations simulations aléatoires et retourne le coup le plus
     * souvent choisi comme optimal.
     */
    public ArrayList<Coord> obtenirMeilleurCoupStatistique(Plateau plateau, int nbSimulations) {
        SonManager.desactiver();
        ArrayList<StatCoup> stats = new ArrayList<>();
        Random rand = new Random();

        System.out.println("Lancement de " + nbSimulations + " simulations Monte-Carlo…");

        for (int sim = 0; sim < nbSimulations; sim++) {
            ArrayList<Coord> possibles = listEchange(plateau);
            if (possibles.isEmpty()) {
                break;
            }

            Coord meilleurC1 = null, meilleurC2 = null;
            int maxScore = -1, maxTuiles = -1;

            for (int i = 0; i < possibles.size(); i += 2) {
                Coord c1 = possibles.get(i);
                Coord c2 = possibles.get(i + 1);
                Plateau copie = plateau.copy();
                copie.echangerTuiles(c1, c2);
                int tuiles = suppression.supprimerTousLesMatchs(copie, rand);
                int score = copie.getScore();

                if (score > maxScore || (score == maxScore && tuiles > maxTuiles)) {
                    maxScore = score;
                    maxTuiles = tuiles;
                    meilleurC1 = c1;
                    meilleurC2 = c2;
                }
            }

            if (meilleurC1 == null) {
                continue;
            }

            StatCoup stat = trouverOuCreerStat(stats, meilleurC1, meilleurC2);
            stat.occurrences++;
            stat.totalScore += maxScore;
            stat.totalTuiles += maxTuiles;
            stat.historiqueScores.add(maxScore);
        }

        SonManager.activer();

        if (stats.isEmpty()) {
            System.out.println("Aucun coup possible.");
            return new ArrayList<>();
        }

        StatCoup meilleur = stats.get(0);
        for (StatCoup s : stats) {
            if (s.occurrences > meilleur.occurrences) {
                meilleur = s;
            }
        }

        double moyScore = (double) meilleur.totalScore / meilleur.occurrences;
        double moyTuiles = (double) meilleur.totalTuiles / meilleur.occurrences;
        double pct = 100.0 * meilleur.occurrences / nbSimulations;
        double ecartType = meilleur.calculerEcartType(moyScore);

        System.out.println("\n===== RAPPORT IA STATISTIQUE =====");
        System.out.println("Coup : " + meilleur.c1 + " ↔ " + meilleur.c2);
        System.out.printf("Fréquence : %.1f%% (%d/%d)%n", pct, meilleur.occurrences, nbSimulations);
        System.out.printf("Score moyen    : %.2f pts%n", moyScore);
        System.out.printf("Tuiles moyennes: %.2f%n", moyTuiles);
        System.out.printf("Écart-type     : %.2f pts%n", ecartType);
        if (ecartType < 50) {
            System.out.println("=> Coup TRÈS STABLE.");
        } else if (ecartType < 200) {
            System.out.println("=> Coup MODÉRÉ.");
        } else {
            System.out.println("=> Coup INSTABLE / CHANCEUX.");
        }
        System.out.println("==================================\n");

        ArrayList<Coord> resultat = new ArrayList<>();
        resultat.add(meilleur.c1);
        resultat.add(meilleur.c2);
        return resultat;
    }

    // -------------------------------------------------------------------------
    // IA JOUE SEULE N COUPS
    // -------------------------------------------------------------------------
    public void aideNCoups(Plateau plateau, int n) {
        SonManager.desactiver();
        Random rand = new Random();

        for (int coup = 1; coup <= n; coup++) {
            System.out.println("\n--- Coup IA " + coup + " / " + n + " ---");
            ArrayList<Coord> meilleur = aideOrdi(plateau);
            if (meilleur.isEmpty()) {
                System.out.println("Plus aucun coup possible.");
                break;
            }
            plateau.echangerTuiles(meilleur.get(0), meilleur.get(1));
            suppression.supprimerTousLesMatchs(plateau, rand);
            System.out.println("Score : " + plateau.getScore());
        }

        SonManager.activer();
        System.out.println("Score final : " + plateau.getScore());
    }

    // -------------------------------------------------------------------------
    // UTILITAIRES PRIVÉS
    // -------------------------------------------------------------------------
    /**
     * Teste un échange et l'ajoute à matchs s'il crée un match (puis annule).
     */
    private void testerEchange(Plateau plateau, ArrayList<Coord> matchs, Coord c1, Coord c2) {
        plateau.echangerTuiles(c1, c2);
        if (detection.existeMatchVertical(plateau, c1)
                || detection.existeMatchVertical(plateau, c2)
                || detection.existeMatchHorizontal(plateau, c1)
                || detection.existeMatchHorizontal(plateau, c2)) {
            if (!paireDejaPresente(matchs, c1, c2)) {
                matchs.add(c1);
                matchs.add(c2);
            }
        }
        plateau.echangerTuiles(c1, c2);
    }

    private boolean paireDejaPresente(ArrayList<Coord> liste, Coord c1, Coord c2) {
        for (int i = 0; i < liste.size(); i += 2) {
            if (liste.get(i).equals(c1) && liste.get(i + 1).equals(c2)) {
                return true;
            }
        }
        return false;
    }

    private StatCoup trouverOuCreerStat(ArrayList<StatCoup> stats, Coord c1, Coord c2) {
        for (StatCoup s : stats) {
            if (s.estIdentique(c1, c2)) {
                return s;
            }
        }
        StatCoup nouveau = new StatCoup(c1, c2);
        stats.add(nouveau);
        return nouveau;
    }

    /**
     * Collecte les tuiles à supprimer en ignorant les cases null (produites par
     * simulerMatchsDeterministe).
     */
    private ArrayList<Coord> collecterAvecNullGuard(Plateau plateau) {
        ArrayList<Coord> res = new ArrayList<>();
        // On réutilise TypesCombinaisons mais via SuppressionMatchs qui
        // appelle getTuile() — Plateau.getTuile() retourne null si hors bornes
        // grâce à la compaction. On filtre ici au cas où.
        ArrayList<Coord> candidats = suppression.collecterToutesLesTuilesASupprimer(plateau);
        for (Coord c : candidats) {
            Tuile t = plateau.getTuileOuNull(c);
            if (t != null) {
                res.add(c);
            }
        }
        return res;
    }

    /**
     * Place null à la position (c) dans le plateau de simulation.
     */
    private void remplacerParNull(Plateau plateau, Coord c) {
        plateau.getLesColonnes()[c.getAbscisse()].setTuileNull(c.getOrdonnee());
    }

    /**
     * Compacte une colonne : déplace toutes les tuiles non-null vers le bas et
     * laisse null en haut. Conserve la taille de la colonne.
     */
    private void compacterColonne(Plateau plateau, int col) {
        int nbLig = plateau.getNbLig();
        ArrayList<Modele.Tuile> tuiles = plateau.getLesColonnes()[col].getTuiles();

        // Collecte les tuiles non-null dans l'ordre bas → haut
        ArrayList<Modele.Tuile> nonNull = new ArrayList<>();
        for (int l = 0; l < nbLig; l++) {
            if (tuiles.get(l) != null) {
                nonNull.add(tuiles.get(l));
            }
        }

        // Remet : non-null en bas, null en haut
        for (int l = 0; l < nbLig; l++) {
            tuiles.set(l, l < nonNull.size() ? nonNull.get(l) : null);
        }
    }
}
