package Colone;

import Tuile.Tuile;
import java.util.ArrayList;
import java.util.Random;

public class Colonne {

    private ArrayList<Tuile> colonne;
    private int nbTypes;

    // Constructeur vide — utilisé pour la copie du plateau
    public Colonne() {
        this.colonne = new ArrayList<Tuile>();
    }

    // Constructeur aléatoire standard
    public Colonne(int nbLignes, int nbTypes) {
        this.nbTypes = nbTypes;
        this.colonne = new ArrayList<>();
        for (int i = 0; i < nbLignes; i++) {
            this.colonne.add(new Tuile(nbTypes, new Random()));
        }
    }

    // Constructeur avec graine fixe — même seed = même plateau
    public Colonne(int nbLignes, int nbTypes, Random rand) {
        this.nbTypes = nbTypes;
        this.colonne = new ArrayList<>();
        for (int i = 0; i < nbLignes; i++) {
            this.colonne.add(new Tuile(rand.nextInt(nbTypes)));
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

    // Version avec graine fixe — utilisée lors de la construction avec seed
    public void supprimerTuiles(ArrayList<Integer> lignes, Random rand) {
        for (int i = lignes.size() - 1; i >= 0; i--) {
            this.colonne.remove((int) lignes.get(i));
        }
        for (int i = 0; i < lignes.size(); i++) {
            this.colonne.add(new Tuile(rand.nextInt(this.nbTypes)));
        }
    }
}