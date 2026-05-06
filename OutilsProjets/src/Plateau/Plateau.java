/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plateau;

import Colone.Colonne;
import Coordonnees.Coord;
import Tuile.Tuile;
import Clavier.Clavier;
import FenetreGraphique.FenetreGraphique;
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
    private GestionMatchs gestionMatchs = new GestionMatchs();
    private GestionGraphique gestionGraphique = new GestionGraphique();

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
        gestionMatchs.supprimerTousLesMatchs(this,new Random());
        System.out.println("Plateau pret !");
        this.score=0;
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
        gestionMatchs.supprimerTousLesMatchs(this,rand);
        this.score=0;// on passe le même rand
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

    public GestionMatchs getGestionMatchs() {
        return gestionMatchs;
    }

    public GestionGraphique getGestionGraphique() {
        return gestionGraphique;
    }

    public void setGestionMatchs(GestionMatchs gestionMatchs) {
        this.gestionMatchs = gestionMatchs;
    }

    public void setGestionGraphique(GestionGraphique gestionGraphique) {
        this.gestionGraphique = gestionGraphique;
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

    // Demande au joueur les coordonnées de deux tuiles à échanger et effectue l'échange
    public void jouerUnCoup() {
        System.out.println("Entrez les coordonnees de la premiere tuile :");

        Coord c1 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");
        Coord c2 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");

        boolean echangeOk = this.echangerTuiles(c1, c2);

        if (echangeOk) {
            if (gestionMatchs.existeUnMatch(this)) {
                System.out.println("Echange effectue !");
                gestionMatchs.supprimerTousLesMatchs(this,new Random());
                System.out.println("Score total : " + this.score);
            } else {
                System.out.println("Cet echange ne cree pas de match, annulation.");
                this.echangerTuiles(c2, c1);
            }
        }
    }

    public void jouerUnCoup(Coord c1, Coord c2) {
        boolean echangeOk = this.echangerTuiles(c1, c2);
        if (echangeOk) {
            if (gestionMatchs.existeUnMatch(this)) {
                System.out.println("echange effectue ! ");
                gestionMatchs.supprimerTousLesMatchs(this,new Random());
                System.out.println("Score total : " + this.score);
            } else {
                System.out.println("Cet echange ne cree pas de match, annulation.");
                this.echangerTuiles(c2, c1);
            }
        }
    }

    // Dessine un bouton et retourne true si le clic est dessus
    // Attend un clic sur un bouton et retourne le choix (1, 2, 3 ou 4)
    

    // -------------------------------------------------------------------------
    // AFFICHAGE GRAPHIQUE PLATEAU
    // -------------------------------------------------------------------------
    public void echangerTuile(FenetreGraphique fenetre, int margeX, int margeY) {
        System.out.println("Cliquez sur la premiere tuile...");
        Coord c1 = gestionGraphique.attendreClicOuBouton(this, fenetre, margeX, margeY);
        System.out.println("Premier clic : " + c1);

        System.out.println("Cliquez sur la deuxieme tuile...");
        Coord c2 = gestionGraphique.attendreClicOuBouton(this, fenetre, margeX, margeY);
        System.out.println("Deuxième clic : " + c2);

        this.jouerUnCoup(c1, c2);

        fenetre.effacer();
        gestionGraphique.afficherPlateau(this,fenetre, margeX, margeY);
    }
    // -------------------------------------------------------------------------
    // CHUTTE TUILE (REELLE)
    // -------------------------------------------------------------------------

    public void jouerUnCoup(FenetreGraphique fenetre, int margeX, int margeY) {
        System.out.println("Entrez les coordonnees de la premiere tuile :");

        Coord c1 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");
        Coord c2 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");

        boolean echangeOk = this.echangerTuiles(c1, c2);

        if (echangeOk) {
            if (gestionMatchs.existeUnMatch(this)) {
                System.out.println("Echange effectue !");

                // 1. On calcule la suppression et le remplissage (logique interne)
                gestionMatchs.supprimerTousLesMatchs(this,new Random());

                // 2. On lance l'animation visuelle de la chute
                // (Assure-toi d'avoir accès à 'fenetre', 'margeX' et 'margeY' ici)
                gestionGraphique.animerChute(this,fenetre, margeX, margeY);

                System.out.println("Score total : " + this.score);
            } else {
                System.out.println("Cet echange ne cree pas de match, annulation.");
                this.echangerTuiles(c2, c1);
            }
        }
    }

}
