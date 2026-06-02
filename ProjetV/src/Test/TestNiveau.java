/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Test;

import FenetreGraphique.FenetreGraphique;
import LogiqueJeu.GestionIA;
import Modele.Coord;
import Modele.Plateau;
import Modele.Tuile;
import Plateau.Niveau;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author flo66
 */
public class TestNiveau {

    public static void main(String[] args) {
        int margeX = 100;
        int margeY = 100;

        int numeroNiveau = 1;
        boolean continuer = true;

        while (continuer) {
            // --- Initialisation du niveau ---
            Niveau niveau = new Niveau(numeroNiveau);
            System.out.println("=== " + niveau + " ===");

            Plateau plateau = new Plateau(
                    niveau.getNbColonnes(),
                    niveau.getNbLignes(),
                    niveau.getNbTypes()
            );

            int largeur = niveau.getNbColonnes() * Tuile.TAILLE + 300;
            int hauteur = niveau.getNbLignes() * Tuile.TAILLE + 300;
            FenetreGraphique fenetre = new FenetreGraphique(
                    "CandyCrush - Niveau " + numeroNiveau, largeur, hauteur);

            GestionIA ia = new GestionIA();
            plateau.getDessinPlateau().afficherPlateau(plateau, fenetre, margeX, margeY);

            int coupsJoues = 0;
            Coord premierClic = null;
            boolean niveauEnCours = true;

            // --- Boucle du niveau ---
            while (niveauEnCours) {

                // Afficher les infos en haut à droite
                plateau.getDessinPlateau().afficherInfosNiveau(
                        plateau, fenetre, niveau, coupsJoues, margeX);
                fenetre.actualiser();

                Coord clic = plateau.getGestionClics()
                        .attendreClicOuBouton(plateau, fenetre, margeX, margeY);

                if (clic.getAbscisse() == -2) {
                    System.out.println(ia.listMatchs(plateau));
                    premierClic = null;

                } else if (clic.getAbscisse() == -3) {
                    // Nouvelle partie = recommencer le niveau actuel
                    plateau = new Plateau(
                            niveau.getNbColonnes(),
                            niveau.getNbLignes(),
                            niveau.getNbTypes()
                    );
                    coupsJoues = 0;
                    premierClic = null;
                    plateau.getDessinPlateau().afficherPlateau(
                            plateau, fenetre, margeX, margeY);

                } else if (clic.getAbscisse() == -4) {
                    continuer = false;
                    niveauEnCours = false;
                    fenetre.dispose();

                } else if (clic.getAbscisse() == -5) {
                    // Afficher le meilleur coup
//                ArrayList<Coord> meilleurCoup = ia.aideOrdi(plateau);
                    ArrayList<Coord> coupDeMonteCarlo = plateau.getGestionIA().obtenirMeilleurCoupStatistique(plateau, 200);
                    if (coupDeMonteCarlo.isEmpty()) {
                        System.out.println("Aucun coup possible !");
                    } else {
                        plateau.getDessinPlateau().afficherPlateauAvecAide(plateau, fenetre, margeX, margeY, coupDeMonteCarlo);
                        System.out.println("Meilleur coup : " + coupDeMonteCarlo.get(0) + " <-> " + coupDeMonteCarlo.get(1));
                    }
                    premierClic = null;
                } else if (clic.getAbscisse() == -10) {
                    // ignoré en mode niveau (taille fixée par le niveau)
                    premierClic = null;

                } else if (clic.getAbscisse() == -11) {
                    // ignoré en mode niveau
                    premierClic = null;

                } else {
                    // Clic sur une tuile
                    if (premierClic == null) {
                        premierClic = clic;
                    } else {
                        plateau.getAnimation()
                                .fixerPositionsActuelles(plateau, margeY);

                        boolean echangeOk = plateau.echangerTuiles(premierClic, clic);

                        if (echangeOk && plateau.getDetectionMatchs().existeUnMatch(plateau)) {
                            coupsJoues++;

                            // Boucle suppression + animation
                            boolean encoreDesMatchs = true;
                            while (encoreDesMatchs) {
                                java.util.ArrayList<Coord> aSupprimer
                                        = plateau.getSuppressionMatchs()
                                                .collecterToutesLesTuilesASupprimer(plateau);

                                if (aSupprimer.isEmpty()) {
                                    encoreDesMatchs = false;
                                } else {
                                    // Clignotement
                                    for (int i = 0; i < 3; i++) {
                                        plateau.getDessinPlateau().afficherPlateauClignotant(
                                                plateau, fenetre, margeX, margeY, aSupprimer, true);
                                        fenetre.attendre(0.2);
                                        plateau.getDessinPlateau().afficherPlateauClignotant(
                                                plateau, fenetre, margeX, margeY, aSupprimer, false);
                                        fenetre.attendre(0.2);
                                    }

                                    // Suppression
                                    java.util.Random rand = new java.util.Random();
                                    for (int col = 0; col < plateau.getNbCol(); col++) {
                                        java.util.ArrayList<Integer> lignes = new java.util.ArrayList<>();
                                        for (Coord c : aSupprimer) {
                                            if (c.getAbscisse() == col) {
                                                lignes.add(c.getOrdonnee());
                                            }
                                        }
                                        if (!lignes.isEmpty()) {
                                            lignes.sort((a, b) -> a - b);
                                            plateau.getLesColonnes()[col].supprimerTuiles(lignes, rand);
                                        }
                                    }

                                    // Animation chute
                                    plateau.getAnimation()
                                            .animerChute(plateau, fenetre, margeX, margeY);
                                    fenetre.attendre(0.5);
                                }
                            }
                            System.out.println("Score total =" + plateau.getScore());
                            // --- Vérification fin de niveau ---
                            if (niveau.objectifAtteint(plateau.getScore())) {
                                System.out.println("NIVEAU " + numeroNiveau + " REUSSI !");
                                afficherMessageFin(fenetre, plateau, margeX,
                                        "NIVEAU " + numeroNiveau + " REUSSI !",
                                        new Color(50, 200, 50));
                                fenetre.attendre(2);
                                numeroNiveau++;
                                niveauEnCours = false;
                                fenetre.dispose();

                            } else if (niveau.plusDeCoup(coupsJoues)) {
                                System.out.println("Plus de coups ! Score : "
                                        + plateau.getScore()
                                        + " / " + niveau.getScoreObjectif());
                                afficherMessageFin(fenetre, plateau, margeX,
                                        "PERDU ! Rejouer ?",
                                        new Color(200, 50, 50));
                                fenetre.attendre(2);
                                niveauEnCours = false;
                                fenetre.dispose();
                                // On reste au même niveau
                            }

                        } else if (echangeOk) {
                            System.out.println("Pas de match, annulation.");
                            plateau.echangerTuiles(clic, premierClic);
                            plateau.getDessinPlateau()
                                    .afficherPlateau(plateau, fenetre, margeX, margeY);
                        }

                        premierClic = null;
                    }
                }
            }
        }
    }

// Affiche un message de fin centré dans la fenêtre
    private static void afficherMessageFin(FenetreGraphique fenetre, Plateau plateau,
            int margeX, String message, Color couleur) {
        Graphics2D g = fenetre.getGraphics2D();
        int cx = margeX + (plateau.getNbCol() * Tuile.TAILLE) / 2;
        int cy = 150 + (plateau.getNbLig() * Tuile.TAILLE) / 2;

        // Fond semi-transparent
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(cx - 150, cy - 40, 300, 80, 20, 20);

        // Texte
        g.setColor(couleur);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString(message, cx - 130, cy + 10);

        fenetre.actualiser();
    }
}
