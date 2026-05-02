/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Colone;

import Tuile.Tuile;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author fpauvert
 */
public class Colonne {

    private ArrayList<Tuile> colonne;
    private int nbTypes;

    public Colonne() {
        this.colonne = new ArrayList<Tuile>();
    }
//pour avoir la meme seed de colonne et après avoir le meme tableau aléatoire

    public Colonne(int nbLignes, int nbTypes, Random rand) {
        this.nbTypes = nbTypes;
        this.colonne = new ArrayList<>();
        for (int i = 0; i < nbLignes; i++) {
            this.colonne.add(new Tuile(rand.nextInt(nbTypes)));
        }
    }

    public Colonne(int nbLignes, int nbTypes) {
        this.nbTypes = nbTypes;
        this.colonne = new ArrayList<>();

        for (int i = 0; i < nbLignes; i++) {
            this.colonne.add(new Tuile(nbTypes, true));
        }
    }

    public Colonne(int nbLignes, int nbTypes, boolean random) {
        this.nbTypes = nbTypes;
        this.colonne = new ArrayList<>();

        if (random == true) {
            for (int i = 0; i < nbLignes; i++) {
                this.colonne.add(new Tuile(nbTypes, true));
            }
        } else {

            for (int i = 0; i < nbLignes; i++) {
                this.colonne.add(new Tuile(nbTypes, true));
            }

        }
    }

    public void setTuile(Tuile tuile) {
        colonne.add(tuile);

    }

    public ArrayList<Tuile> getColonne() {
        return colonne;
    }

    public int getNbTypes() {
        return nbTypes;
    }

    public void setNbTypes(int nbTypes) {
        this.nbTypes = nbTypes;
    }

    public Tuile getTuile(int ligne) {
        return this.colonne.get(ligne);
    }

    // Supprime la tuile à la ligne donnée et ajoute une nouvelle tuile aléatoire en haut
    public void supprimerTuile(int ligne) {
        this.colonne.remove(ligne);
        this.colonne.add(new Tuile(this.nbTypes, true));
    }

    // Supprime toutes les tuiles aux lignes indiquées (liste triée par ordre croissant)
    public void supprimerTuiles(ArrayList<Integer> lignes) {
        for (int i = lignes.size() - 1; i >= 0; i--) {
            this.colonne.remove((int) lignes.get(i));
        }
        for (int i = 0; i < lignes.size(); i++) {
            this.colonne.add(new Tuile(this.nbTypes, true));
        }
    }

}
