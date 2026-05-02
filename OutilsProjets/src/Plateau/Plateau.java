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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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
        this.supprimerTousLesMatchs(new Random());
        System.out.println("Plateau pret !");
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
        this.supprimerTousLesMatchs(rand); // on passe le même rand
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
        } else if (lig - 1 >= 0) {
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
            typeDroite1 = getTuile(col + 1, lig).getType();
            typeDroite2 = getTuile(col + 2, lig).getType();
        } else if (col + 1 < this.nbCol) {
            typeDroite1 = getTuile(col + 1, lig).getType();
        }
        if (col - 2 >= 0) {
            typeGauche1 = getTuile(col - 1, lig).getType();
            typeGauche2 = getTuile(col - 2, lig).getType();
        } else if (col - 1 >= 0) {
            typeGauche1 = getTuile(col - 1, lig).getType();
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
    // ------------------------------------------------------------------------
    // Collecte toutes les positions à supprimer (vertical + horizontal) sans rien supprimer
    public ArrayList<Coord> collecterToutesLesTuilesASupprimer() {
        ArrayList<Coord> aSupprimer = new ArrayList<>();

        // Matchs verticaux
        for (int col = 0; col < this.nbCol; col++) {
            for (int lig = 0; lig < this.nbLig - 2; lig++) {
                if (getTuile(col, lig).equals(getTuile(col, lig + 1))
                        && getTuile(col, lig).equals(getTuile(col, lig + 2))) {
                    // Trouve toute l'étendue du match
                    int fin = lig + 2;
                    while (fin + 1 < this.nbLig && getTuile(col, fin + 1).equals(getTuile(col, lig))) {
                        fin++;
                    }
                    for (int i = lig; i <= fin; i++) {
                        Coord c = new Coord(col, i);
                        if (!contient(aSupprimer, c)) {
                            aSupprimer.add(c);
                        }
                    }
                    lig = fin; // on saute les tuiles déjà traitées
                }
            }
        }

        // Matchs horizontaux
        for (int lig = 0; lig < this.nbLig; lig++) {
            for (int col = 0; col < this.nbCol - 2; col++) {
                if (getTuile(col, lig).equals(getTuile(col + 1, lig))
                        && getTuile(col, lig).equals(getTuile(col + 2, lig))) {
                    // Trouve toute l'étendue du match
                    int fin = col + 2;
                    while (fin + 1 < this.nbCol && getTuile(fin + 1, lig).equals(getTuile(col, lig))) {
                        fin++;
                    }
                    for (int c = col; c <= fin; c++) {
                        Coord coord = new Coord(c, lig);
                        if (!contient(aSupprimer, coord)) {
                            aSupprimer.add(coord);
                        }
                    }
                    col = fin;
                }
            }
        }

        return aSupprimer;
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

    public int supprimerTousLesMatchs(Random rand) {
        int totalSupprimees = 0;
        boolean matchTrouve = true;
        while (matchTrouve) {
            ArrayList<Coord> aSupprimer = collecterToutesLesTuilesASupprimer();
            if (aSupprimer.isEmpty()) {
                matchTrouve = false;
            } else {
                totalSupprimees += supprimerCoords(aSupprimer, rand);
            }
        }
        return totalSupprimees;
    }

    private int supprimerCoords(ArrayList<Coord> aSupprimer, Random rand) {
        for (int col = 0; col < this.nbCol; col++) {
            ArrayList<Integer> lignesASupprimer = new ArrayList<>();
            for (Coord c : aSupprimer) {
                if (c.getAbscisse() == col) {
                    lignesASupprimer.add(c.getOrdonnee());
                }
            }
            if (!lignesASupprimer.isEmpty()) {
                lignesASupprimer.sort((a, b) -> a - b);
                this.lesColonnes[col].supprimerTuiles(lignesASupprimer, rand);
            }
        }
        return aSupprimer.size();
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
                this.score += this.supprimerTousLesMatchs(new Random()) * 100;
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
            if (this.existeUnMatch()) {
                System.out.println("echange effectue ! ");
                this.score += this.supprimerTousLesMatchs(new Random()) * 100;
                System.out.println("Score total : " + this.score);
            } else {
                System.out.println("Cet echange ne cree pas de match, annulation.");
                this.echangerTuiles(c2, c1);
            }
        }
    }

    // -------------------------------------------------------------------------
    // AIDE ORDINATEUR
    // -------------------------------------------------------------------------
    public String listMatchs() {
        ArrayList<Coord> matchs = this.listEchange();
        String res = "Liste des echanges possibles";
        if (matchs.isEmpty()) {
            return res += ": \n Aucun";
        }
        res += " entre:";
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
    public ArrayList<Coord> aideOrdi() {
        ArrayList<Coord> matchs = this.listEchange();
        Plateau copy = this.copy();
        ArrayList<Coord> meilleurMatchs = new ArrayList<Coord>();
        int meilleurScore = 0;
        for (int i = 0; i < matchs.size(); i += 2) {
            copy.echangerTuiles(matchs.get(i), matchs.get(i + 1));
            int scoreCopy = copy.supprimerTousLesMatchs(new Random()) * 100;
            if (scoreCopy > meilleurScore) {
                meilleurMatchs.clear();
                meilleurMatchs.add(matchs.get(i));
                meilleurMatchs.add(matchs.get(i + 1));
                meilleurScore = scoreCopy;
            }
        }
        return meilleurMatchs;
    }

    // -------------------------------------------------------------------------
    // METHODES SUR CLIC
    // -------------------------------------------------------------------------
    public Coord clicVersCoord(int clicX, int clicY, int margeX, int margeY) {
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

    public Coord attendreClicOuBouton(FenetreGraphique fenetre, int margeX, int margeY) {
        // Valeurs spéciales retournées pour les boutons :
        // (-2, 0) = Coups possibles
        // (-3, 0) = Nouvelle partie  
        // (-4, 0) = Quitter
        // Coord valide = clic sur la grille
        int boutonX = margeX + this.nbCol * Tuile.TAILLE + 20;

        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int clicX = fenetre.getXDernierClic();
                int clicY = fenetre.getYDernierClic();
                fenetre.effacerDernierClic();

                if (boutonClique(clicX, clicY, boutonX, 60, 160, 30)) {
                    return new Coord(-2, 0);
                }
                if (boutonClique(clicX, clicY, boutonX, 100, 160, 30)) {
                    return new Coord(-3, 0);
                }
                if (boutonClique(clicX, clicY, boutonX, 140, 160, 30)) {
                    return new Coord(-4, 0);
                }

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
    // BOUTONS
    // -------------------------------------------------------------------------
    // Dessine un bouton et retourne true si le clic est dessus
    public boolean boutonClique(int clicX, int clicY, int x, int y, int largeur, int hauteur) {
        return clicX >= x && clicX <= x + largeur && clicY >= y && clicY <= y + hauteur;
    }

    // Dessine un bouton dans la fenêtre
    public void dessinerBouton(FenetreGraphique fenetre, String texte, int x, int y, int largeur, int hauteur) {
        Graphics2D g = fenetre.getGraphics2D();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, largeur, hauteur, 10, 10);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(texte, x + 10, y + hauteur / 2 + 5);
    }

    // Attend un clic sur un bouton et retourne le choix (1, 2, 3 ou 4)
    public int lireChoix(FenetreGraphique fenetre) {
        int boutonX = 20 + this.nbCol * Tuile.TAILLE + 20;

        while (true) {
            if (fenetre.unClicAEuLieu()) {
                int clicX = fenetre.getXDernierClic();
                int clicY = fenetre.getYDernierClic();
                fenetre.effacerDernierClic();

                if (boutonClique(clicX, clicY, boutonX, 20, 160, 30)) {
                    return 1; // Jouer
                }
                if (boutonClique(clicX, clicY, boutonX, 60, 160, 30)) {
                    return 2; // Coups possibles
                }
                if (boutonClique(clicX, clicY, boutonX, 100, 160, 30)) {
                    return 3; // Nouvelle partie
                }
                if (boutonClique(clicX, clicY, boutonX, 140, 160, 30)) {
                    return 4; // Quitter
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // AFFICHAGE GRAPHIQUE PLATEAU
    // -------------------------------------------------------------------------
    public void afficherPlateau(FenetreGraphique fenetre, int margeX, int margeY) {
        fenetre.effacer();
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

        // Dessin des boutons à droite de la grille
        int boutonX = 150 + this.nbCol * Tuile.TAILLE;
        dessinerBouton(fenetre, "Coups possibles", boutonX, 60, 160, 30);
        dessinerBouton(fenetre, "Nouvelle partie", boutonX, 100, 160, 30);
        dessinerBouton(fenetre, "Quitter", boutonX, 140, 160, 30);

        fenetre.actualiser();

    }

    public void echangerTuile(FenetreGraphique fenetre, int margeX, int margeY) {
        System.out.println("Cliquez sur la premiere tuile...");
        Coord c1 = attendreClicOuBouton(fenetre, margeX, margeY);
        System.out.println("Premier clic : " + c1);

        System.out.println("Cliquez sur la deuxieme tuile...");
        Coord c2 = attendreClicOuBouton(fenetre, margeX, margeY);
        System.out.println("Deuxième clic : " + c2);

        this.jouerUnCoup(c1, c2);

        fenetre.effacer();
        this.afficherPlateau(fenetre, margeX, margeY);
    }

}
