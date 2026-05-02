package Clavier;

import Coordonnees.Coord;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Classe permettant de récupérer des valeurs tapees au clavier dans la console
 *
 * @author christophe.varnier, guillaume.laurent
 * @version 27.09.2015
 */
public class Clavier {

    static public int getInt() {
        int retourInt = 0;
        boolean saisieOk = false;
        while (saisieOk == false) {
            try {
                BufferedReader inr;
                inr = new BufferedReader(new InputStreamReader(System.in));
                String s = inr.readLine();
                retourInt = Integer.parseInt(s);
                saisieOk = true;
            } catch (Exception e) {
                System.out.println(" Erreur de saisie : veuiller entrer un entier ");
            }
        }
        return retourInt;
    }

    static public long getLong() {
        long retourLong = 0;
        boolean saisieOk = false;
        while (saisieOk == false) {
            try {
                BufferedReader inr = new BufferedReader(new InputStreamReader(System.in));
                String s = inr.readLine();
                retourLong = Long.parseLong(s);
                saisieOk = true;
            } catch (Exception e) {
                System.out.println(" Erreur de saisie : veuiller entrer un entier ");
            }
        }
        return retourLong;
    }

    static public boolean getBoolean() {
        boolean retourBoolean = false;
        boolean saisieOk = false;
        while (saisieOk == false) {
            try {
                BufferedReader inr = new BufferedReader(new InputStreamReader(System.in));
                String s = inr.readLine();
                if (s.equals("true") || s.equals("vrai")) {
                    retourBoolean = true;
                    saisieOk = true;
                } else if (s.equals("false") || s.equals("faux")) {
                    retourBoolean = false;
                    saisieOk = true;
                } else {
                    System.out.println(" Erreur de saisie : veuiller entrer un booleen (vrai/true ou faux/false) ");
                }
            } catch (Exception e) {
                System.out.println(" Erreur de saisie : veuiller entrer un booleen (vrai/true ou faux/false) ");
            }

        }
        return retourBoolean;
    }

    static public double getDouble() {
        double retourDouble = 0;
        boolean saisieOk = false;
        while (saisieOk == false) {
            try {
                BufferedReader inr = new BufferedReader(new InputStreamReader(System.in));
                String s = inr.readLine();
                retourDouble = Double.parseDouble(s);
                saisieOk = true;
            } catch (Exception e) {
                System.out.println(" Erreur de saisie : veuiller entrer un reel ");
            }
        }
        return retourDouble;
    }

    @SuppressWarnings("UseSpecificCatch")
    static public float getFloat() {
        float retourFloat = 0;
        boolean saisieOk = false;
        while (saisieOk == false) {
            try {
                BufferedReader inr = new BufferedReader(new InputStreamReader(System.in));
                String s = inr.readLine();
                retourFloat = Float.parseFloat(s);
                saisieOk = true;
            } catch (Exception e) {
                System.out.println(" Erreur de saisie : veuiller entrer un reel ");
            }
        }
        return retourFloat;
    }

    static public String getString() {
        String retourString = "";
        boolean saisieOk = false;
        while (saisieOk == false) {
            try {
                BufferedReader inr = new BufferedReader(new InputStreamReader(System.in));
                retourString = inr.readLine();
                saisieOk = true;
            } catch (Exception e) {
                System.out.println(" Erreur de saisie : veuiller entrer une chaine");
            }
        }
        return retourString;
    }
    
    
    
static public Coord getCoord() {
    Coord retourCoord = new Coord(0, 0);
    boolean saisieOk = false;

    while (saisieOk == false) {
        try {
            BufferedReader inr = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Entrez deux entiers séparés par un espace d'abord colonne puis ligne :");
            String s = inr.readLine();

            // On découpe la ligne en morceaux séparés par un espace
            String[] parties = s.split(" ");

            // On vérifie qu'il y a bien exactement 2 valeurs
            if (parties.length != 2) {
                throw new Exception("Il faut exactement 2 entiers");
            }

            retourCoord.setAbscisse(Integer.parseInt(parties[0]));
            retourCoord.setOrdonnee(Integer.parseInt(parties[1]));
            saisieOk = true;

        } catch (Exception e) {
            System.out.println("Erreur de saisie : veuillez entrer deux entiers séparés par un espace");
        }
    }
    return retourCoord;


}
}
