/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controleur;


import Affichage.Animation;
import Affichage.DessinPlateau;
import Clavier.Clavier;
import FenetreGraphique.FenetreGraphique;
import LogiqueJeu.DetectionMatchs;
import LogiqueJeu.SuppressionMatchs;
import Modele.Coord;
import Modele.Plateau;
import java.util.Random;

/**
 *
 * @author flo66
 */
public class GestionPartie {
    
    private DetectionMatchs detectionMatchs = new DetectionMatchs();
    private SuppressionMatchs suppressionMatchs = new SuppressionMatchs();
    private GestionClics gestionClics = new GestionClics();
    private Animation animation = new Animation();

     // Demande au joueur les coordonnées de deux tuiles à échanger et effectue l'échange
    public void jouerUnCoup(Plateau plateau) {
        System.out.println("Entrez les coordonnees de la premiere tuile :");

        Coord c1 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");
        Coord c2 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");

        boolean echangeOk = plateau.echangerTuiles(c1, c2);

        if (echangeOk) {
            if (detectionMatchs.existeUnMatch(plateau)) {
                System.out.println("Echange effectue !");
                suppressionMatchs.supprimerTousLesMatchs(plateau,new Random());
                System.out.println("Score total : " + plateau.getScore());
            } else {
                System.out.println("Cet echange ne cree pas de match, annulation.");
                plateau.echangerTuiles(c2, c1);
            }
        }
    }

    public void jouerUnCoup(Plateau plateau,Coord c1, Coord c2) {
        boolean echangeOk = plateau.echangerTuiles(c1, c2);
        if (echangeOk) {
            if (detectionMatchs.existeUnMatch(plateau)) {
                System.out.println("echange effectue ! ");
                suppressionMatchs.supprimerTousLesMatchs(plateau,new Random());
                System.out.println("Score total : " + plateau.getScore());
            } else {
                System.out.println("Cet echange ne cree pas de match, annulation.");
                plateau.echangerTuiles(c2, c1);
            }
        }
    }
     public void jouerUnCoup(Plateau plateau,FenetreGraphique fenetre, int margeX, int margeY) {
        System.out.println("Entrez les coordonnees de la premiere tuile :");

        Coord c1 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");
        Coord c2 = Clavier.getCoord();

        System.out.println("Entrez les coordonnees de la deuxieme tuile :");

        boolean echangeOk =  plateau.echangerTuiles(c1, c2);

        if (echangeOk) {
            if (detectionMatchs.existeUnMatch( plateau)) {
                System.out.println("Echange effectue !");

                // 1. On calcule la suppression et le remplissage (logique interne)
                suppressionMatchs.supprimerTousLesMatchs( plateau,new Random());

                // 2. On lance l'animation visuelle de la chute
                // (Assure-toi d'avoir accès à 'fenetre', 'margeX' et 'margeY' ici)
                animation.animerChute( plateau,fenetre, margeX, margeY);

                System.out.println("Score total : " +  plateau.getScore());
            } else {
                System.out.println("Cet echange ne cree pas de match, annulation.");
                 plateau.echangerTuiles(c2, c1);
            }
        }
    }
}
