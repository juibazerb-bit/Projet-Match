package Plateau;

import Coordonnees.Coord;
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
     * @param plateau
     * @return 
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
     * @param plateau
     * @return 
     */
    public ArrayList<Coord> listEchange(Plateau plateau) {
        ArrayList<Coord> matchs = new ArrayList<>();
        GestionMatchs gm = plateau.getGestionMatchs();
        int nbLig = plateau.getNbLig();
        int nbCol = plateau.getNbCol();

        // 1. Vérification des échanges VERTICAUX
        for (int ordonnee = 0; ordonnee < nbLig - 1; ordonnee++) {
            for (int abscisse = 0; abscisse < nbCol; abscisse++) {
                Coord coord1 = new Coord(abscisse, ordonnee);
                Coord coord2 = new Coord(abscisse, ordonnee + 1);

                plateau.echangerTuiles(coord1, coord2); // On teste l'échange

                if (gm.existeMatchVertical(plateau, coord1)
                        || gm.existeMatchVertical(plateau, coord2)
                        || gm.existeMatchHorizontal(plateau, coord1)
                        || gm.existeMatchHorizontal(plateau, coord2)) {

                    if (!paireDejaPresente(matchs, coord1, coord2)) {
                        matchs.add(coord1);
                        matchs.add(coord2);
                    }
                }
                plateau.echangerTuiles(coord1, coord2); // On remet en place
            }
        }

        // 2. Vérification des échanges HORIZONTAUX
        for (int abscisse = 0; abscisse < nbCol - 1; abscisse++) {
            for (int ordonnee = 0; ordonnee < nbLig; ordonnee++) {
                Coord coord1 = new Coord(abscisse, ordonnee);
                Coord coord2 = new Coord(abscisse + 1, ordonnee);

                plateau.echangerTuiles(coord1, coord2); // On teste

                if (gm.existeMatchVertical(plateau, coord1)
                        || gm.existeMatchVertical(plateau, coord2)
                        || gm.existeMatchHorizontal(plateau, coord1)
                        || gm.existeMatchHorizontal(plateau, coord2)) {

                    if (!paireDejaPresente(matchs, coord1, coord2)) {
                        matchs.add(coord1);
                        matchs.add(coord2);
                    }
                }
                plateau.echangerTuiles(coord1, coord2); // On remet en place
            }
        }
        return matchs;
    }

    /**
     * Analyse tous les coups possibles sur une copie du plateau et retourne le
     * meilleur coup (celui qui rapporte le plus de points).
     * @param plateau
     * @return 
     */
    public ArrayList<Coord> aideOrdi(Plateau plateau, int nbCoups) {
        int compteur=0;
        ArrayList<Coord> matchs = this.listEchange(plateau);
        if (matchs.isEmpty()) return new ArrayList<>();
        ArrayList<Coord> meilleurMatchs = new ArrayList<>();
        int meilleurScore = -1;
        Random rand = new Random();

        for (int i = 0; i < matchs.size(); i += 2) {
            // On travaille sur une COPIE pour ne pas modifier le vrai jeu
            Plateau copy = plateau.copy();
            Coord c1 = matchs.get(i);
            Coord c2 = matchs.get(i + 1);

            copy.echangerTuiles(c1, c2);
            // On simule la suppression pour voir combien de points cela rapporte
            int scoreGagne = copy.getGestionMatchs().supprimerTousLesMatchs(copy, rand) * 100;

            if (scoreGagne > meilleurScore) {
                meilleurScore = scoreGagne;
                meilleurMatchs.clear();
                meilleurMatchs.add(c1);
                meilleurMatchs.add(c2);
            }
        }
        return meilleurMatchs;
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

public void aideNCoups(int N){
    
    
    
    
}





}
