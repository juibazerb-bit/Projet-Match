/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plateau;

import Colone.Colonne;
import Coordonnées.Coord;
import Tuile.Tuile;

/**
 *
 * @author fpauvert
 */
public class Plateau {

    private Colonne[] lesColonnes;
    private int nbCol;
    private int nbLig;
    private int nbTypesTuile;

    public Plateau(int nbColonnes, int nbLignes, int nbTypes) {
        this.lesColonnes = new Colonne[nbColonnes];
        this.nbCol = nbColonnes;
        this.nbLig = nbLignes;
        this.nbTypesTuile = nbTypes;
        for (int i = 0; i < nbColonnes; i++) {
            this.lesColonnes[i] = new Colonne(nbLignes, nbTypes);
        }
    }

    public Tuile getTuile(int colonne, int ligne) {
        return this.lesColonnes[colonne].getColonne().get(ligne);
    }

    public String afficher() {
        String res = "";
        for (int lig = this.nbLig - 1; lig >= 0; lig--) {
            for (int col = 0; col < this.nbCol; col++) {
                res += this.getTuile(col, lig);
            }
            res += "\n";
        }
        return res;
    }

    public Coord posMatchVertical() {
        Coord pos = new Coord(-1, -1);
        int col = 0;
        boolean trouve = false;
        while (col < this.nbCol && !trouve) {
            int lig = 0;
            while (lig < this.nbLig - 2 && !trouve) {
                if (this.existeMatch(new Coord(col, lig))) {
                    trouve = true;
                    pos = new Coord(col, lig);

                } else {
                    lig++;
                }
            }
            col++;
        }
        return pos;
    }

    public boolean existeMatch(Coord cordonnee) {
        int col = cordonnee.getAbscisse();
        int lig = cordonnee.getOrdonnee();

        // On récupère le type de la tuile source
        int typeSource = getTuile(col, lig).getType();

        if (lig + 2 < nbLig) {
            int typeSuivant1 = getTuile(col, lig + 1).getType();
            int typeSuivant2 = getTuile(col, lig + 2).getType();

            return (typeSource == typeSuivant1 && typeSource == typeSuivant2);
        }

        return false;
    }
}