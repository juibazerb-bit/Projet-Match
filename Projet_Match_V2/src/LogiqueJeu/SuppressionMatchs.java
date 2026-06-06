/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LogiqueJeu;

import Modele.Coord;
import Modele.Plateau;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author flo66
 */
public class SuppressionMatchs {

    private TypesCombinaisons typesCombinaisons = new TypesCombinaisons();
    // Collecte toutes les positions à supprimer (vertical + horizontal) sans rien supprimer

    public ArrayList<Coord> collecterToutesLesTuilesASupprimer(Plateau plateau) {
        return typesCombinaisons.collecterToutesLesTuilesASupprimer(plateau, false);
    }

    public ArrayList<Coord> collecterToutesLesTuilesASupprimerSilencieux(Plateau plateau) {
        return typesCombinaisons.collecterToutesLesTuilesASupprimer(plateau, true);
    }

    public int supprimerTousLesMatchsSilencieux(Plateau plateau, Random rand) {
        int totalSupprimees = 0;
        boolean matchTrouve = true;
        while (matchTrouve) {
            ArrayList<Coord> aSupprimer = collecterToutesLesTuilesASupprimerSilencieux(plateau);
            if (aSupprimer.isEmpty()) {
                matchTrouve = false;
            } else {
                totalSupprimees += supprimerCoords(plateau, aSupprimer, rand);
            }
        }
        return totalSupprimees;
    }

    // Vérifie si une Coord est déjà dans la liste (pour éviter les doublons)
    public boolean contient(ArrayList<Coord> liste, Coord c) {
        boolean flag = false;
        for (Coord coord : liste) {
            if (coord.equals(c)) {
                flag = true;
            }
        }
        return flag;
    }

    public int supprimerTousLesMatchs(Plateau plateau, Random rand) {
        int totalSupprimees = 0;
        boolean matchTrouve = true;
        while (matchTrouve) {
            ArrayList<Coord> aSupprimer = collecterToutesLesTuilesASupprimer(plateau);
            if (aSupprimer.isEmpty()) {
                matchTrouve = false;
            } else {
                totalSupprimees += supprimerCoords(plateau, aSupprimer, rand);
            }
        }
        return totalSupprimees;
    }

    public int supprimerCoords(Plateau plateau, ArrayList<Coord> aSupprimer, Random rand) {
        for (int col = 0; col < plateau.getNbCol(); col++) {
            ArrayList<Integer> lignesASupprimer = new ArrayList<>();
            for (Coord c : aSupprimer) {
                if (c.getAbscisse() == col) {
                    lignesASupprimer.add(c.getOrdonnee());
                }
            }
            if (!lignesASupprimer.isEmpty()) {
                lignesASupprimer.sort((a, b) -> a - b);
                plateau.getLesColonnes()[col].supprimerTuiles(lignesASupprimer, rand);
            }
        }
        return aSupprimer.size();
    }
}
