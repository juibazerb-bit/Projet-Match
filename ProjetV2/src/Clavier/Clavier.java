package Clavier;

import Modele.Coord;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utilitaire de saisie console.
 * Toutes les méthodes sont statiques et relancent la saisie en cas d'erreur.
 */
public class Clavier {

    public static int getInt() {
        while (true) {
            try {
                return Integer.parseInt(lireLigne());
            } catch (Exception e) {
                System.out.println("Erreur : entrez un entier.");
            }
        }
    }

    public static long getLong() {
        while (true) {
            try {
                return Long.parseLong(lireLigne());
            } catch (Exception e) {
                System.out.println("Erreur : entrez un entier.");
            }
        }
    }

    public static double getDouble() {
        while (true) {
            try {
                return Double.parseDouble(lireLigne());
            } catch (Exception e) {
                System.out.println("Erreur : entrez un réel.");
            }
        }
    }

    public static float getFloat() {
        while (true) {
            try {
                return Float.parseFloat(lireLigne());
            } catch (Exception e) {
                System.out.println("Erreur : entrez un réel.");
            }
        }
    }

    public static boolean getBoolean() throws Exception {
        while (true) {
            String s = lireLigne().toLowerCase();
            if (s.equals("true") || s.equals("vrai"))   return true;
            if (s.equals("false") || s.equals("faux"))  return false;
            System.out.println("Erreur : entrez vrai/faux ou true/false.");
        }
    }

    public static String getString() {
        while (true) {
            try {
                return lireLigne();
            } catch (Exception e) {
                System.out.println("Erreur de saisie.");
            }
        }
    }

    /**
     * Lit deux entiers séparés par un espace : d'abord la colonne, puis la ligne.
     */
    public static Coord getCoord() {
        while (true) {
            try {
                System.out.println("Entrez : colonne ligne (séparés par un espace)");
                String[] parts = lireLigne().split(" ");
                if (parts.length != 2) throw new Exception("Exactement 2 entiers requis.");
                return new Coord(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            } catch (Exception e) {
                System.out.println("Erreur : entrez deux entiers séparés par un espace.");
            }
        }
    }

    // -------------------------------------------------------------------------

    private static String lireLigne() throws Exception {
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
}
