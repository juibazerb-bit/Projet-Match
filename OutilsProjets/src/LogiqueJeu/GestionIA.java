package LogiqueJeu;

import Modele.Plateau;
import Modele.Coord;
import Modele.StatCoup;
import Sons.SonManager;
import java.util.ArrayList;
import java.util.Random;

/**
 * Classe utilitaire pour l'aide au jeu (IA)
 *
 * @author flo66
 */
public class GestionIA {

    /**
     * Retourne une chaîne de caractères lisible listant les échanges possibles.
     */
    public String listMatchs(Plateau plateau) {
        ArrayList<Coord> matchs = this.listEchange(plateau);
        String res = "Liste des echanges possibles";

        if (matchs.isEmpty()) {
            return res + ": \n Aucun";
        }

        res += " entre:";
        for (int i = 0; i < matchs.size(); i += 2) {
            res += " \n " + matchs.get(i) + " et " + matchs.get(i + 1);
        }

        return res;
    }

    /**
     * Parcourt le plateau pour trouver tous les échanges de tuiles voisines qui
     * créent au moins un match.
     */
    public ArrayList<Coord> listEchange(Plateau plateau) {
        ArrayList<Coord> matchs = new ArrayList<>();
        DetectionMatchs gm = plateau.getDetectionMatchs();
        int nbLig = plateau.getNbLig();
        int nbCol = plateau.getNbCol();

        for (int ordonnee = 0; ordonnee < nbLig - 1; ordonnee++) {
            for (int abscisse = 0; abscisse < nbCol; abscisse++) {
                Coord coord1 = new Coord(abscisse, ordonnee);
                Coord coord2 = new Coord(abscisse, ordonnee + 1);

                plateau.echangerTuiles(coord1, coord2);

                if (gm.existeMatchVertical(plateau, coord1)
                        || gm.existeMatchVertical(plateau, coord2)
                        || gm.existeMatchHorizontal(plateau, coord1)
                        || gm.existeMatchHorizontal(plateau, coord2)) {

                    if (!paireDejaPresente(matchs, coord1, coord2)) {
                        matchs.add(coord1);
                        matchs.add(coord2);
                    }
                }
                plateau.echangerTuiles(coord1, coord2);
            }
        }

        for (int abscisse = 0; abscisse < nbCol - 1; abscisse++) {
            for (int ordonnee = 0; ordonnee < nbLig; ordonnee++) {
                Coord coord1 = new Coord(abscisse, ordonnee);
                Coord coord2 = new Coord(abscisse + 1, ordonnee);

                plateau.echangerTuiles(coord1, coord2);

                if (gm.existeMatchVertical(plateau, coord1)
                        || gm.existeMatchVertical(plateau, coord2)
                        || gm.existeMatchHorizontal(plateau, coord1)
                        || gm.existeMatchHorizontal(plateau, coord2)) {

                    if (!paireDejaPresente(matchs, coord1, coord2)) {
                        matchs.add(coord1);
                        matchs.add(coord2);
                    }
                }
                plateau.echangerTuiles(coord1, coord2);
            }
        }
        return matchs;
    }

    /**
     * Retourne le meilleur coup possible (celui qui rapporte le plus de points)
     * en simulant chaque échange sur une copie du plateau. En cas d'égalité de
     * score, retourne le coup qui supprime le plus de tuiles.
     */
    public ArrayList<Coord> aideOrdi(Plateau plateau) {
        SonManager.desactiver();
        ArrayList<Coord> matchs = this.listEchange(plateau);
        if (matchs.isEmpty()) {
            SonManager.activer();
            return new ArrayList<>();
        }

        ArrayList<Coord> meilleurMatchs = new ArrayList<>();
        int meilleurScore = -1;
        int meilleurNbTuiles = -1;

        for (int i = 0; i < matchs.size(); i += 2) {
            // On travaille sur une copie propre pour ne pas abîmer la vraie partie
            Plateau copy = plateau.copy();
            Coord c1 = matchs.get(i);
            Coord c2 = matchs.get(i + 1);

            // L'IA teste l'échange
            copy.echangerTuiles(c1, c2);

            // --- CHOIX DE LA STRATÉGIE ---
            // OPTION A (Stratégie 1) : On simule les cascades existantes SANS nouvelles tuiles
            int nbTuilesSupprimees = copy.getGestionIA().simulerMatchsDeterministe(copy);
            int scoreGagne = copy.getScore();

            /* // OPTION B (Stratégie 2) : On compte uniquement l'alignement direct (Instantané)
        ArrayList<Coord> aSupprimer = copy.getSuppressionMatchs().collecterToutesLesTuilesASupprimer(copy);
        int nbTuilesSupprimees = aSupprimer.size();
        int scoreGagne = copy.getScore(); 
             */
            // Comparaison pour trouver le meilleur coup réel disponible
            if (scoreGagne > meilleurScore
                    || (scoreGagne == meilleurScore && nbTuilesSupprimees > meilleurNbTuiles)) {
                meilleurScore = scoreGagne;
                meilleurNbTuiles = nbTuilesSupprimees;
                meilleurMatchs.clear();
                meilleurMatchs.add(c1);
                meilleurMatchs.add(c2);
            }
        }

        SonManager.activer();

        if (!meilleurMatchs.isEmpty()) {
            System.out.println("Meilleur coup calcule : " + meilleurMatchs.get(0)
                    + " <-> " + meilleurMatchs.get(1)
                    + " | Score possible : " + meilleurScore
                    + " | Tuiles possibles : " + meilleurNbTuiles);
        }

        return meilleurMatchs;
    }

    /**
     * Simule les cascades UNIQUEMENT avec les tuiles déjà présentes. Les cases
     * vides créées en haut du plateau restent vides pendant la simulation.
     */
    public int simulerMatchsDeterministe(Plateau plateau) {
        int nbTuilesTotal = 0;
        boolean encoreDesMatchs = true;

        while (encoreDesMatchs) {
            // 1. On cherche les tuiles alignées (cette méthode ajoute aussi les points au score)
            ArrayList<Coord> aSupprimer = plateau.getSuppressionMatchs().collecterToutesLesTuilesASupprimer(plateau);

            if (aSupprimer.isEmpty()) {
                encoreDesMatchs = false; // Plus aucun alignement naturel sur le plateau actuel
            } else {
                nbTuilesTotal += aSupprimer.size();

                // 2. On retire les tuiles matchées
                plateau.getSuppressionMatchs().supprimerCoords(plateau, aSupprimer, new Random());

                // 3. On fait descendre les tuiles existantes pour combler les trous
                plateau.getSuppressionMatchs().supprimerTousLesMatchs(plateau, new Random());
                // Les cases du haut restent vides (null) pour ne pas fausser les calculs de l'IA.
            }
        }
        return nbTuilesTotal;
    }

    /**
     * Fait jouer l'ordinateur seul pendant N coups. À chaque coup, il choisit
     * le meilleur échange possible, l'applique sur le vrai plateau et affiche
     * le résultat. S'arrête avant N si plus aucun coup n'est possible.
     */
    public void aideNCoups(Plateau plateau, int n) {
        SonManager.desactiver();
        Random rand = new Random();
        int coupJoue = 0;
        boolean peutJouer = true;

        while (coupJoue < n && peutJouer) {
            System.out.println("\n--- Coup " + (coupJoue + 1) + " / " + n + " ---");

            ArrayList<Coord> meilleurCoup = aideOrdi(plateau);

            if (meilleurCoup.isEmpty()) {
                System.out.println("Plus aucun coup possible, l'IA s'arrête.");
                peutJouer = false;
            } else {
                Coord c1 = meilleurCoup.get(0);
                Coord c2 = meilleurCoup.get(1);

                // On joue le coup sur le vrai plateau
                plateau.echangerTuiles(c1, c2);

                // On supprime tous les matchs en cascade
                plateau.getSuppressionMatchs().supprimerTousLesMatchs(plateau, rand);

                System.out.println("Score total après le coup : " + plateau.getScore());

                coupJoue++;
            }
        }
        SonManager.activer();
        System.out.println("\n=== Fin de l'aide IA après " + coupJoue + " coup(s) ===");
        System.out.println("Score final : " + plateau.getScore());
    }

    /**
     * Méthode privée utilitaire pour éviter les doublons dans la liste
     */
    private boolean paireDejaPresente(ArrayList<Coord> liste, Coord c1, Coord c2) {
        for (int i = 0; i < liste.size(); i += 2) {
            if (liste.get(i).equals(c1) && liste.get(i + 1).equals(c2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simule le coup optimal un grand nombre de fois (Monte-Carlo) et retourne
     * le coup le plus robuste face au hasard avec ses moyennes.
     */
    public ArrayList<Coord> obtenirMeilleurCoupStatistique(Plateau plateau, int nbSimulations) {
    SonManager.desactiver();
    
    ArrayList<StatCoup> stats = new ArrayList<>();
    Random rand = new Random();

    System.out.println("Lancement de " + nbSimulations + " simulations avec calcul d'écart-type...");

    for (int sim = 0; sim < nbSimulations; sim++) {
        ArrayList<Coord> matchsPossibles = this.listEchange(plateau);
        
        if (matchsPossibles.isEmpty()) break;

        Coord meilleurC1 = null, meilleurC2 = null;
        int maxScore = -1;
        int maxTuiles = -1;

        for (int i = 0; i < matchsPossibles.size(); i += 2) {
            Plateau copy = plateau.copy();
            Coord c1 = matchsPossibles.get(i);
            Coord c2 = matchsPossibles.get(i + 1);

            copy.echangerTuiles(c1, c2);
            
            int tuilesSuppr = copy.getSuppressionMatchs().supprimerTousLesMatchs(copy, rand);
            int scoreGagne = copy.getScore();

            if (scoreGagne > maxScore || (scoreGagne == maxScore && tuilesSuppr > maxTuiles)) {
                maxScore = scoreGagne;
                maxTuiles = tuilesSuppr;
                meilleurC1 = c1;
                meilleurC2 = c2;
            }
        }

        if (meilleurC1 != null && meilleurC2 != null) {
            StatCoup statTrouvee = null;
            for (StatCoup s : stats) {
                if (s.estIdentique(meilleurC1, meilleurC2)) {
                    statTrouvee = s;
                    break;
                }
            }

            if (statTrouvee == null) {
                statTrouvee = new StatCoup(meilleurC1, meilleurC2);
                stats.add(statTrouvee);
            }

            statTrouvee.occurrences++;
            statTrouvee.totalScore += maxScore;
            statTrouvee.totalTuiles += maxTuiles;
            
            // CRUCIAL : On stocke le score obtenu pour le calcul de l'écart-type
            statTrouvee.HistoriqueScores.add(maxScore);
        }
    }

    SonManager.activer();

    if (stats.isEmpty()) {
        System.out.println("Aucun coup possible trouvé.");
        return new ArrayList<>();
    }

    // Trouver le coup le plus fréquent
    StatCoup meilleurCoupStat = stats.get(0);
    for (StatCoup s : stats) {
        if (s.occurrences > meilleurCoupStat.occurrences) {
            meilleurCoupStat = s;
        }
    }

    // Calculs des indicateurs de performance
    double moyenneScore = (double) meilleurCoupStat.totalScore / meilleurCoupStat.occurrences;
    double moyenneTuiles = (double) meilleurCoupStat.totalTuiles / meilleurCoupStat.occurrences;
    double pourcentageApparition = ((double) meilleurCoupStat.occurrences / nbSimulations) * 100;
    
    // Appel de notre nouvelle formule mathématique
    double ecartType = meilleurCoupStat.calculerEcartType(moyenneScore);

    // Affichage des statistiques complètes
    System.out.println("\n===== RAPPORT DE L'IA STATISTIQUE AVANCEE =====");
    System.out.println("Coup selectionne : " + meilleurCoupStat.c1 + " <-> " + meilleurCoupStat.c2);
    System.out.println("Frequence d'apparition : " + pourcentageApparition + "% (" + meilleurCoupStat.occurrences + " fois)");
    System.out.printf("Moyenne des points generes  : %.2f pts\n", moyenneScore);
    System.out.printf("Ecart-type du score (Risque) : %.2f pts\n", ecartType);
    System.out.printf("Moyenne des tuiles detruites : %.2f tuiles\n", moyenneTuiles);
    
    // Interprétation simple à modifier car l'écart type varie enormement en fonction de la difficulté du plateau
    if (ecartType < 50) {
        System.out.println("=> Diagnostic : Coup TRES STABLE (Peu dépendant des cascades aleatoires).");
    } else if (ecartType < 200) {
        System.out.println("=> Diagnostic : Coup MODERE (Cascades classiques probables).");
    } else {
        System.out.println("=> Diagnostic : Coup INSTABLE / CHANCEUX (Enorme potentiel de chaos ou flop).");
    }
    System.out.println("===============================================\n");

    ArrayList<Coord> resultat = new ArrayList<>();
    resultat.add(meilleurCoupStat.c1);
    resultat.add(meilleurCoupStat.c2);
    return resultat;
}

}
