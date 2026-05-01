/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plateau;

import Colone.Colonne;
import Coordonnées.Coord;
import Tuile.Tuile;
import java.util.ArrayList;
import Clavier.Clavier;
import FenetreGraphique.FenetreGraphique;
import java.awt.Color;

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
        this.nbCol = nbColonnes;
        this.nbLig = nbLignes;
        this.nbTypesTuile = nbTypes;
        this.lesColonnes = new Colonne[nbColonnes];
        for (int i = 0; i < nbColonnes; i++) {
            this.lesColonnes[i] = new Colonne(nbLignes, nbTypes);
        }
        // On s'assure qu'il n'y a pas de match dans l'état initial
        this.supprimerTousLesMatchs();
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
    // DETECTION DE MATCHS
    // -------------------------------------------------------------------------
    // Vérifie s'il existe un match vertical à partir de la coordonnée donnée
    public boolean existeMatchVertical(Coord coordonnee) {
        int col = coordonnee.getAbscisse();
        int lig = coordonnee.getOrdonnee();

        if (lig + 2 < this.nbLig) {
            int typeSource = getTuile(col, lig).getType();
            int typeSuivant1 = getTuile(col, lig + 1).getType();
            int typeSuivant2 = getTuile(col, lig + 2).getType();
            return (typeSource == typeSuivant1 && typeSource == typeSuivant2);
        }
        return false;
    }

    // Vérifie s'il existe un match horizontal à partir de la coordonnée donnée
    public boolean existeMatchHorizontal(Coord coordonnee) {
        int col = coordonnee.getAbscisse();
        int lig = coordonnee.getOrdonnee();

        if (col + 2 < this.nbCol) {
            int typeSource = getTuile(col, lig).getType();
            int typeSuivant1 = getTuile(col + 1, lig).getType();
            int typeSuivant2 = getTuile(col + 2, lig).getType();
            return (typeSource == typeSuivant1 && typeSource == typeSuivant2);
        }
        return false;
    }

    // Retourne la position du premier match vertical trouvé, ou (-1,-1) si aucun
    public Coord posMatchVertical() {
        Coord pos = new Coord(-1, -1);
        int col = 0;
        boolean trouve = false;
        while (col < this.nbCol && !trouve) {
            int lig = 0;
            while (lig < this.nbLig - 2 && !trouve) {
                if (this.existeMatchVertical(new Coord(col, lig))) {
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

    // Retourne la position du premier match horizontal trouvé, ou (-1,-1) si aucun
    public Coord posMatchHorizontal() {
        Coord pos = new Coord(-1, -1);
        int lig = 0;
        boolean trouve = false;
        while (lig < this.nbLig && !trouve) {
            int col = 0;
            while (col < this.nbCol - 2 && !trouve) {
                if (this.existeMatchHorizontal(new Coord(col, lig))) {
                    trouve = true;
                    pos = new Coord(col, lig);
                } else {
                    col++;
                }
            }
            lig++;
        }
        return pos;
    }

    // Retourne true s'il existe au moins un match (vertical ou horizontal) sur le plateau
    public boolean existeUnMatch() {
        return this.posMatchVertical().getAbscisse() != -1
                || this.posMatchHorizontal().getAbscisse() != -1;
    }

    // -------------------------------------------------------------------------
    // SUPPRESSION DES MATCHS ET CHUTE DES TUILES
    // -------------------------------------------------------------------------
    public int supprimerMatchVertical(Coord pos) {
        int col = pos.getAbscisse();
        int lig = pos.getOrdonnee();
        int typeMatch = getTuile(col, lig).getType();

        // On collecte toutes les lignes à supprimer dans cette colonne
        ArrayList<Integer> lignesASupprimer = new ArrayList<>();

        // On remonte pour trouver toutes les tuiles du même type alignées
        //On regarde d'abord si il y a des tuiles de meme type en dessous 
        //(normalement non car posMatchVertical revoir la position du premier en bas) 
        int debut = lig;
        while (debut > 0 && getTuile(col, debut - 1).equals(getTuile(col, lig))) {
            debut--;
        }
        // On regarde ensuite les Tuiles du dessus
        int fin = lig + 2;
        while (fin + 1 < this.nbLig && getTuile(col, fin + 1).equals(getTuile(col, lig))) {
            fin++;
        }
        // on ajoute les lignes a supprimer dans la liste des lignes a supprimer
        for (int i = debut; i <= fin; i++) {
            lignesASupprimer.add(i);
        }

        // On supprime ces lignes
        this.lesColonnes[col].supprimerTuiles(lignesASupprimer);
        return lignesASupprimer.size();
    }

    // Supprime toutes les tuiles qui font partie d'un match horizontal à partir de pos
    public int supprimerMatchHorizontal(Coord pos) {
        int col = pos.getAbscisse();
        int lig = pos.getOrdonnee();
        int typeMatch = getTuile(col, lig).getType();

        // On remonte à gauche pour trouver le début du match
        int debut = col;
        while (debut > 0 && getTuile(debut - 1, lig).equals(getTuile(col, lig))) {
            debut--;
        }
        int fin = col + 2;
        while (fin + 1 < this.nbCol && getTuile(col, fin + 1).equals(getTuile(col, lig))) {
            fin++;
        }

        // Pour chaque colonne concernée, on supprime la tuile à la ligne lig
        for (int c = debut; c <= fin; c++) {
            ArrayList<Integer> lignesASupprimer = new ArrayList<>();
            lignesASupprimer.add(lig);
            this.lesColonnes[c].supprimerTuiles(lignesASupprimer);
        }
        return fin - debut + 1;
    }

    // Supprime tous les matchs en cascade jusqu'à ce qu'il n'y en ait plus
    public int supprimerTousLesMatchs() {
        int totalSupprimees = 0;
        boolean matchTrouve = true;
        while (matchTrouve) {
            matchTrouve = false;

            Coord posV = this.posMatchVertical();
            if (posV.getAbscisse() != -1) {
                totalSupprimees += this.supprimerMatchVertical(posV);
                matchTrouve = true;
            }

            Coord posH = this.posMatchHorizontal();
            if (posH.getAbscisse() != -1) {
                totalSupprimees += this.supprimerMatchHorizontal(posH);
                matchTrouve = true;
            }
        }
        return totalSupprimees;
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
//        System.out.print("  Colonne : ");
        Coord c1 = Clavier.getCoord();
//        int col1 = Clavier.getInt();
//        System.out.print("  Ligne   : ");
//        int lig1 = Clavier.getInt();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");
//        System.out.print("  Colonne : ");
        Coord c2 = Clavier.getCoord();
//        int col2 = Clavier.getInt();
//        System.out.print("  Ligne   : ");
//        int lig2 = Clavier.getInt();

//        Coord c1 = new Coord(col1, lig1);
//        Coord c2 = new Coord(col2, lig2);
        boolean echangeOk = this.echangerTuiles(c1, c2);

        if (echangeOk) {
            if (this.existeUnMatch()) {
                System.out.println("echange effectue ! ");
                int nbPoints = this.supprimerTousLesMatchs() * 100;
                System.out.println("nombre de points:" + nbPoints);
            } else {
                // Pas de match créé : on annule l'échange
                System.out.println("Cet échange ne crée pas de match, annulation.");
                this.echangerTuiles(c2, c1);
            }
        }
    }

    // -------------------------------------------------------------------------
    // AFFICHAGE GRAPHIQUE
    // -------------------------------------------------------------------------
    public void afficherPlateau(FenetreGraphique fenetre) {
     

        for (int lig = this.nbLig - 1; lig >= 0; lig--) {
            for (int col = 0; col < this.nbCol; col++) {
                Tuile t = this.lesColonnes[col].getTuile(lig);

                if (t != null) {
                    // Calcul de la position : on utilise la TAILLE de la tuile 
                    int posX = 200+ col * Tuile.TAILLE;
                    int posY =200+this.nbLig*Tuile.TAILLE - lig * Tuile.TAILLE;

                    // On met à jour les coordonnées internes de la tuile si besoin
                    t.setCoordTuile(new Coord(posX, posY));

                    // On dessine
                    t.dessiner(fenetre, posX, posY);
                }

            }
        }
        fenetre.actualiser();

    }

}
