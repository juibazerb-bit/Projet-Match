/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plateau;

import Colone.Colonne;
import Coordonnees.Coord;
import Tuile.Tuile;
import java.util.ArrayList;
import Clavier.Clavier;
import FenetreGraphique.FenetreGraphique;

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
    
    public Plateau copy(){
        Plateau copy= new Plateau(this.nbCol, this.nbLig, this.nbTypesTuile);
        copy.setLesColonnes(this.getLesColonnes());
        return copy;
    }

    public Colonne[] getLesColonnes() {
        return lesColonnes;
    }

    public int getNbCol() {
        return nbCol;
    }

    public int getNbLig() {
        return nbLig;
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
    // DETECTION DE MATCHS
    // -------------------------------------------------------------------------
    // Vérifie s'il existe un match vertical à partir de la coordonnée donnée
        public boolean existeMatchVertical(Coord coordonnee) {
        int col = coordonnee.getAbscisse();
        int lig = coordonnee.getOrdonnee();
        int typeSource = getTuile(col, lig).getType();
        int typeHaut1 = -1;
        int typeHaut2 = -1;
        int typeBas1 = -1;
        int typeBas2 = -1;
//      On attribut des types au 2 case au dessus et en dessous s'il existe
//      Sinon on leurs donne la valeur -1 
        if (lig + 2 < this.nbLig) {
            typeHaut1 = getTuile(col, lig + 1).getType();
            typeHaut2 = getTuile(col, lig + 2).getType();    
        } else if (lig + 1 < this.nbLig) {
            typeHaut1 = getTuile(col, lig + 1).getType();
        }
        if (lig - 2 >= 0) {
            typeBas1 = getTuile(col, lig - 1).getType();
            typeBas2 = getTuile(col, lig - 2).getType();
        }else if (lig - 1 >= 0) {
            typeBas1 = getTuile(col, lig - 1).getType();
        }
        return (typeSource == typeHaut1 && typeSource == typeHaut2 
             || typeSource == typeBas1 && typeSource == typeBas2
             || typeSource == typeBas1 && typeSource == typeHaut1);
    }
    
    
    // Vérifie s'il existe un match horizontal à partir de la coordonnée donnée
            public boolean existeMatchHorizontal(Coord coordonnee) {
        int col = coordonnee.getAbscisse();
        int lig = coordonnee.getOrdonnee();
        int typeSource = getTuile(col, lig).getType();
        int typeDroite1 = -1;
        int typeDroite2 = -1;
        int typeGauche1 = -1;
        int typeGauche2 = -1;
        
//      On attribut des types aux 2 case a droite et a gauche s'il existe
//      Sinon on leurs donne la valeur -1
        if (col + 2 < this.nbCol) {
            typeDroite1 = getTuile(col + 1, lig ).getType();
            typeDroite2 = getTuile(col + 2, lig ).getType();    
        } else if (col + 1 < this.nbCol) {
            typeDroite1 = getTuile(col + 1, lig ).getType();
        }
        if (col - 2 >= 0) {
            typeGauche1 = getTuile(col - 1, lig ).getType();
            typeGauche2 = getTuile(col - 2, lig ).getType();
        }else if (col - 1 >= 0) {
            typeGauche1 = getTuile(col - 1, lig ).getType();
        }
        return (typeSource == typeDroite1 && typeSource == typeDroite2 
             || typeSource == typeGauche1 && typeSource == typeGauche2
             || typeSource == typeGauche1 && typeSource == typeDroite1);
 
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

        Coord c1 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");

        Coord c2 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");


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

    public void jouerUnCoup(Coord c1, Coord c2) {

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
    // AIDE ORDINATEUR
    // -------------------------------------------------------------------------
    
    public String listMatchs(){
        ArrayList<Coord> matchs = this.listEchange();
        String res = "Liste des echanges possibles";
        if (matchs.isEmpty()) {
            return res += ": \n Aucun";
        }
        res+= " entre:";
        for (int i = 0; i < matchs.size(); i += 2) {
            res += " \n " + matchs.get(i) + " et " + matchs.get(i + 1);
        }

        return res;
    }
    
    
    public ArrayList<Coord> listEchange() { 
        ArrayList<Coord> matchs = new ArrayList<>();

        // Vérification de création de matchs par des echange verticale
        for (int ordonnee = 0; ordonnee < nbLig - 1; ordonnee++) {
            for (int abscisse = 0; abscisse < nbCol; abscisse++) {
                Coord coord1 = new Coord(abscisse, ordonnee);
                Coord coord2 = new Coord(abscisse, ordonnee + 1);
                echangerTuiles(coord1, coord2);

                // si matchs ajout a la liste
                if (this.existeMatchVertical(coord1)
                        || this.existeMatchVertical(coord2)
                        || this.existeMatchHorizontal(coord1)
                        || this.existeMatchHorizontal(coord2)) {
                    // Verification que la paire de coord n'est pas deja dans la liste
                    boolean paireDejaPresente = false;
                    int i = 0;
                    while (!paireDejaPresente && i < matchs.size()) {
                        if (matchs.get(i).equals(coord1) && matchs.get(i + 1).equals(coord2)) {
                            paireDejaPresente = true;
                        }
                        i += 2;
                    }
                    if (!paireDejaPresente) {
                        matchs.add(coord1);
                        matchs.add(coord2);
                    }
                }
                // on remet en place les Tuiles
                echangerTuiles(coord1, coord2);
            }
        }

        // Vérification de création de matchs par des echange horizontale
        for (int abscisse = 0; abscisse < nbCol - 1; abscisse++) {
            for (int ordonnee = 0; ordonnee < nbLig; ordonnee++) {
                Coord coord1 = new Coord(abscisse, ordonnee);
                Coord coord2 = new Coord(abscisse + 1, ordonnee);
                echangerTuiles(coord1, coord2);

                if (this.existeMatchVertical(coord1)
                        || this.existeMatchVertical(coord2)
                        || this.existeMatchHorizontal(coord1)
                        || this.existeMatchHorizontal(coord2)) {

                    boolean paireDejaPresente = false;
                    int i = 0;
                    while (!paireDejaPresente && i < matchs.size()) {
                        if (matchs.get(i).equals(coord1) && matchs.get(i + 1).equals(coord2)) {
                            paireDejaPresente = true;
                        }
                        i += 2;
                    }
                    if (!paireDejaPresente) {
                        matchs.add(coord1);
                        matchs.add(coord2);
                    }
                }
                // on remet en place les Tuiles
                echangerTuiles(coord1, coord2);
            }
        }
        return matchs;
    }
    
    // à faire (Ayoub) avec une boucle reccursif
    public ArrayList<Coord> aideOrdi(){
        ArrayList<Coord> matchs=this.listEchange();
        Plateau copy=this.copy();
        ArrayList<Coord> meilleurMatchs= new ArrayList<Coord>();
        int meilleurScore=0;
        for (int i=0;i< matchs.size();i+=2){
            copy.echangerTuiles(matchs.get(i), matchs.get(i+1));
            int scoreCopy= copy.supprimerTousLesMatchs() * 100;
            if (scoreCopy>meilleurScore){
                meilleurMatchs.clear();
                meilleurMatchs.add(matchs.get(i));
                meilleurMatchs.add(matchs.get(i+1));
                meilleurScore= scoreCopy;
            }
        }
        return meilleurMatchs;
    }

    // -------------------------------------------------------------------------
    // METHODES SUR CLIC
    // -------------------------------------------------------------------------
    private Coord clicVersCoord(int clicX, int clicY, int margeX, int margeY) {
        int col = (clicX - margeX) / Tuile.TAILLE;
        int basGrille = margeY + (this.nbLig + 1) * Tuile.TAILLE; // +1 pour le décalage
        int lig = (basGrille - clicY) / Tuile.TAILLE;

        System.out.println("Clic pixels : (" + clicX + ", " + clicY + ")");
        System.out.println("Converti en : col=" + col + ", lig=" + lig);

        if (col >= 0 && col < nbCol && lig >= 0 && lig < nbLig) {
            return new Coord(col, lig);
        }
        return null;
    }

    private Coord attendreClic(FenetreGraphique fenetre,int margeX,int margeY) {
        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int clicX = fenetre.getXDernierClic();
                int clicY = fenetre.getYDernierClic();
                fenetre.effacerDernierClic();
                Coord coord = clicVersCoord(clicX, clicY, margeX, margeY);
                if (coord != null) {
                    return coord;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    // -------------------------------------------------------------------------
    // AFFICHAGE GRAPHIQUE
    // -------------------------------------------------------------------------
    public void afficherPlateau(FenetreGraphique fenetre,int margeX,int margeY) {

        for (int lig = this.nbLig - 1; lig >= 0; lig--) {
            for (int col = 0; col < this.nbCol; col++) {
                Tuile t = this.lesColonnes[col].getTuile(lig);

                if (t != null) {
                    // Calcul de la position : on utilise la TAILLE de la tuile 
                    int posX = margeX + col * Tuile.TAILLE;
                    int posY = margeY + this.nbLig * Tuile.TAILLE - lig * Tuile.TAILLE;

                    // On met à jour les coordonnées internes de la tuile si besoin
                    t.setCoordTuile(new Coord(posX, posY));

                    // On dessine
                    t.dessiner(fenetre, posX, posY);
                }

            }
        }
        fenetre.actualiser();

    }

    public void echangerTuile(FenetreGraphique fenetre,int margeX,int margeY) {
        System.out.println("Cliquez sur la premiere tuile...");
        Coord c1 = attendreClic(fenetre,margeX,margeY);
        System.out.println("Premier clic : " + c1);

        System.out.println("Cliquez sur la deuxieme tuile...");
        Coord c2 = attendreClic(fenetre,margeX,margeY);
        System.out.println("Deuxième clic : " + c2);

        this.jouerUnCoup(c1, c2);

        fenetre.effacer();
        this.afficherPlateau(fenetre,margeX,margeY);
    }
}
