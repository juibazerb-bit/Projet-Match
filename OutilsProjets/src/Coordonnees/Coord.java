/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Coordonnees;

/**
 *
 * @author fpauvert
 */
public class Coord {

    private int abscisse;
    private int ordonnee;

    public Coord(int abscisse, int ordonnee) {
        this.abscisse = abscisse;
        this.ordonnee = ordonnee;
    }

    public int getAbscisse() {
        return abscisse;
    }

    public int getOrdonnee() {
        return ordonnee;
    }

    public void setAbscisse(int abscisse) {
        this.abscisse = abscisse;
    }

    public void setOrdonnee(int ordonnee) {
        this.ordonnee = ordonnee;
    }

    public boolean estVoisine(Coord c) {
        // Calcul de la différence entre les abscisses et les ordonnées
        int diffabs = Math.abs(this.abscisse - c.getAbscisse());
        int difford = Math.abs(this.ordonnee - c.getOrdonnee());

        // Deux cases sont voisines si :
        // (différence de X est 1 ET différence de Y est 0)
        // OU (différence de X est 0 ET différence de Y est 1)
        return (diffabs == 1 && difford == 0) || (diffabs == 0 && difford == 1);
    }

    @Override
    public String toString() {
        return "(" + this.abscisse + ", " + this.ordonnee + ")";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coord other = (Coord) obj;
        if (this.abscisse != other.abscisse) {
            return false;
        }
        return this.ordonnee == other.ordonnee;
    }
    
    

}
