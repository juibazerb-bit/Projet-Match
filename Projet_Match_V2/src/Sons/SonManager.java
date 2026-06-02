package Sons;

/**
 * Interrupteur global des sons. Quand désactivé (ex. pendant les simulations
 * IA), aucun son n'est joué.
 */
public class SonManager {

    private static boolean actif = true;

    public static void activer() {
        actif = true;
    }

    public static void desactiver() {
        actif = false;
    }

    public static boolean estActif() {
        return actif;
    }

    public static void jouer(Son son) {
        if (actif) {
            son.jouer();
        }
    }

    public static void jouerEnBoucle(Son son) {
        if (actif) {
            son.jouerEnBoucle();
        }
    }

    public static void jouerNsecondes(Son son, double sec) {
        if (actif) {
            son.jouerNsecondes(sec);
        }
    }

    public static void stopper(Son son) {
        if (actif) {
            son.stopper();
        }
    }
}
