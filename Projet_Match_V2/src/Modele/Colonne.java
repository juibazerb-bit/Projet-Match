package Modele;

import java.util.ArrayList;
import java.util.Random;

/**
 * Représente une colonne du plateau : une liste ordonnée de tuiles, de l'indice
 * 0 (bas) au dernier indice (haut).
 *
 * Quand des tuiles sont supprimées, les tuiles restantes descendent et de
 * nouvelles tuiles aléatoires sont ajoutées en haut.
 */
public class Colonne {

    private ArrayList<Tuile> tuiles;
    private int nbTypes;

    /**
     * Constructeur vide — utilisé uniquement pour la copie de plateau.
     */
    public Colonne() {
        this.tuiles = new ArrayList<>();
    }

    /**
     * Constructeur standard avec graine aléatoire non fixée.
     */
    public Colonne(int nbLignes, int nbTypes) {
        this.nbTypes = nbTypes;
        this.tuiles = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < nbLignes; i++) {
            tuiles.add(new Tuile(nbTypes, rand));
        }
    }

    /**
     * Constructeur avec graine fixe — même seed = même colonne.
     */
    public Colonne(int nbLignes, int nbTypes, Random rand) {
        this.nbTypes = nbTypes;
        this.tuiles = new ArrayList<>();
        for (int i = 0; i < nbLignes; i++) {
            tuiles.add(new Tuile(rand.nextInt(nbTypes)));
        }
    }

    // --- Accesseurs ---
    public Tuile getTuile(int ligne) {
        return tuiles.get(ligne);
    }

    public int getNbTypes() {
        return nbTypes;
    }

    public void setNbTypes(int n) {
        this.nbTypes = n;
    }

    public ArrayList<Tuile> getTuiles() {
        return tuiles;
    }

    /**
     * Ajoute une tuile en haut de la colonne (utilisé lors de la copie).
     */
    public void ajouterTuile(Tuile tuile) {
        tuiles.add(tuile);
    }

    /**
     * Remplace la tuile à la ligne donnée par null. Utilisé par
     * GestionIA.simulerMatchsDeterministe() pour marquer une case vide sans
     * modifier la taille de la liste.
     */
    public void setTuileNull(int ligne) {
        tuiles.set(ligne, null);
    }

    /**
     * Supprime les tuiles aux lignes indiquées (triées par ordre croissant),
     * puis remplace chacune par une nouvelle tuile aléatoire ajoutée en haut.
     */
    public void supprimerTuiles(ArrayList<Integer> lignes, Random rand) {
        // Suppression de bas en haut pour ne pas décaler les indices
        for (int i = lignes.size() - 1; i >= 0; i--) {
            tuiles.remove((int) lignes.get(i));
        }
        // Ajout de nouvelles tuiles en haut
        for (int i = 0; i < lignes.size(); i++) {
            tuiles.add(new Tuile(rand.nextInt(nbTypes)));
        }
    }
}
