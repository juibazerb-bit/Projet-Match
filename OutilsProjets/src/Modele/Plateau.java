/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import Affichage.DessinPlateau;
import FenetreGraphique.FenetreGraphique;
import Controleur.GestionClics;
import Controleur.GestionPartie;
import LogiqueJeu.DetectionMatchs;
import LogiqueJeu.SuppressionMatchs;
import java.util.Random;

/**
 *
 * @author fpauvert
 */
public class Plateau {

    private Colonne[] lesColonnes;
    private int nbCol;
    private int nbLig;
    private int nbTypesTuile;
    private int score;
    private DetectionMatchs detectionMatchs = new DetectionMatchs();
    private SuppressionMatchs suppressionMatchs = new SuppressionMatchs();
    private GestionPartie gestionPartie = new GestionPartie();
    private GestionClics gestionClics = new GestionClics();
    private DessinPlateau dessinPlateau = new DessinPlateau();

    public Plateau(int nbColonnes, int nbLignes, int nbTypes) {
        this.nbCol = nbColonnes;
        this.nbLig = nbLignes;
        this.nbTypesTuile = nbTypes;
        this.score = 0;
        this.lesColonnes = new Colonne[nbColonnes];
        for (int i = 0; i < nbColonnes; i++) {
            this.lesColonnes[i] = new Colonne(nbLignes, nbTypes);
        }
        System.out.println("Colonnes creees, suppression des matchs...");
        suppressionMatchs.supprimerTousLesMatchs(this, new Random());
        System.out.println("Plateau pret !");
        this.score = 0;
    }

    public Plateau copy() {
        Plateau copy = new Plateau(this.nbCol, this.nbLig, this.nbTypesTuile);
        Colonne[] copyColonne = new Colonne[nbCol];
        for (int i = 0; i < nbCol; i++) {
            Colonne copyCol = new Colonne();
            copyCol.setNbTypes(this.nbTypesTuile);
            for (int j = 0; j < nbLig; j++) {
                int type = this.lesColonnes[i].getTuile(j).getType();
                copyCol.setTuile(new Tuile(type));
            }
            copyColonne[i] = copyCol;
        }
        copy.setLesColonnes(copyColonne);
        return copy;
    }
    // -------------------------------------------------------------------------
    // PLATEAU ALEATOIRE MAIS PAS VRAIMENT
    // -------------------------------------------------------------------------

    public Plateau(int nbColonnes, int nbLignes, int nbTypes, long seed) {
        this.nbCol = nbColonnes;
        this.nbLig = nbLignes;
        this.nbTypesTuile = nbTypes;
        this.score = 0;
        this.lesColonnes = new Colonne[nbColonnes];
        Random rand = new Random(seed);
        for (int i = 0; i < nbColonnes; i++) {
            this.lesColonnes[i] = new Colonne(nbLignes, nbTypes, rand);
        }
        suppressionMatchs.supprimerTousLesMatchs(this, rand);
        this.score = 0;// on passe le même rand
    }

    // -------------------------------------------------------------------------
    // GETTER ET SETTER
    // -------------------------------------------------------------------------
    public Colonne[] getLesColonnes() {
        return lesColonnes;
    }

    public int getNbCol() {
        return nbCol;
    }

    public int getNbLig() {
        return nbLig;
    }

    public void ajouterScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public DetectionMatchs getDetectionMatchs() {
        return detectionMatchs;
    }

    public SuppressionMatchs getSuppressionMatchs() {
        return suppressionMatchs;
    }

    public void setLesColonnes(Colonne[] lesColonnes) {
        this.lesColonnes = lesColonnes;
    }

    public void setNbCol(int nbCol) {
        this.nbCol = nbCol;
    }

    public void setNbLig(int nbLig) {
        this.nbLig = nbLig;
    }

    public void setNbTypesTuile(int nbTypesTuile) {
        this.nbTypesTuile = nbTypesTuile;
    }

    public int getNbTypesTuile() {
        return nbTypesTuile;
    }

    public Tuile getTuile(int colonne, int ligne) {
        return this.lesColonnes[colonne].getTuile(ligne);
    }

    // -------------------------------------------------------------------------
    // AFFICHAGE
    // -------------------------------------------------------------------------
    public String afficher() {
        String res = "";
        for (int lig = this.nbLig - 1; lig >= 0; lig--) {
            res += lig + "\t| ";
            for (int col = 0; col < this.nbCol; col++) {
                res += this.getTuile(col, lig).toString();
            }
            res += "\n";
        }
        res += "\t  ";
        for (int col = 0; col < this.nbCol; col++) {
            res += "- ";
        }
        res += "\n\t  ";
        for (int col = 0; col < this.nbCol; col++) {
            res += col + " ";
        }
        return res;
    }

    // -------------------------------------------------------------------------
    // ECHANGE DE TUILES
    // -------------------------------------------------------------------------
    // Échange deux tuiles voisines désignées par leurs coordonnées
    public boolean echangerTuiles(Coord c1, Coord c2) {
        if (!c1.estVoisine(c2)) {
            System.out.println("Ces deux tuiles ne sont pas voisines !");
            return false;
        }

        int col1 = c1.getAbscisse();
        int lig1 = c1.getOrdonnee();
        int col2 = c2.getAbscisse();
        int lig2 = c2.getOrdonnee();

        // On sauvegarde le type de la première tuile
        int typeTemp = getTuile(col1, lig1).getType();
        this.lesColonnes[col1].getTuile(lig1).setType(getTuile(col2, lig2).getType());
        this.lesColonnes[col2].getTuile(lig2).setType(typeTemp);

        return true;
    }

    // Dessine un bouton et retourne true si le clic est dessus
    // Attend un clic sur un bouton et retourne le choix (1, 2, 3 ou 4)
    // -------------------------------------------------------------------------
    // AFFICHAGE GRAPHIQUE PLATEAU
    // -------------------------------------------------------------------------
    public void echangerTuile(FenetreGraphique fenetre, int margeX, int margeY) {
        System.out.println("Cliquez sur la premiere tuile...");
        Coord c1 = gestionClics.attendreClicOuBouton(this, fenetre, margeX, margeY);
        System.out.println("Premier clic : " + c1);

        System.out.println("Cliquez sur la deuxieme tuile...");
        Coord c2 = gestionClics.attendreClicOuBouton(this, fenetre, margeX, margeY);
        System.out.println("Deuxième clic : " + c2);

        gestionPartie.jouerUnCoup(this, c1, c2);

        fenetre.effacer();
        dessinPlateau.afficherPlateau(this, fenetre, margeX, margeY);
    }
    // -------------------------------------------------------------------------
    // CHUTTE TUILE (REELLE)
    // -------------------------------------------------------------------------

}
