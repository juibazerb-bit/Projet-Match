/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Colone;

import Tuile.Tuile;
import java.util.ArrayList;

/**
 *
 * @author fpauvert
 */
public class Colonne {

    private ArrayList<Tuile> colonne;

    public Colonne(int nbLignes, int nbTypes) {
        for (int i = 0; i < nbLignes; i++) {
            this.colonne.set(i, new Tuile(nbTypes));
        }
    }

    public ArrayList<Tuile> getColonne() {
        return colonne;
    }
    
    

}
