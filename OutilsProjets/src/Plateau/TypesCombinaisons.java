/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package Plateau;

import java.awt.Image;

/**
 *
 * @author fpauvert
 */
public enum TypesCombinaisons {
    // Définition des constantes avec taille et symbole
    CARRE(4, "Carre"),
    QUATRELIGNE(4, "QuatreLigne"),
    QUATRECOLONNE(4, "QuatreColonne"),
    CINQLIGNE(5, "CinqLigne"),
    CINQCOLONNE(5, "CingColonne"),
    LBASGAUCHE(5, "LBasGauche"),
    LBASDROIT(5, "LBasDroit"),
    LHAUTGAUCHE(5, "LHautGauche"),
    LHAUTDROIT(5, "LHautDroit"),
    TGAUCHE(5, "TGauche"),
    TDROIT(5, "TDroit"),
    THAUT(5, "THaut"),
    TBAS(5, "TBas");

    private int taille;
    private String symbole;

    // Constructeur de l'énumération
    TypesCombinaisons(int taille, String symbole) {
        this.taille = taille;
        this.symbole = symbole;
    }

    public int getTaille() {
        return taille;
    }

    public String getSymbole() {
        return symbole;
    }

    /**
     * Calcule un score de base en fonction de la taille de la combinaison.
     */
    public int getScoreBase() {
        return this.taille * 100;
    }
}

    

