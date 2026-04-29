/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tuile;

import TypeTuile.TypeTuile;

/**
 *
 * @author fpauvert
 */
public class Tuile {

    private int type;

    public Tuile(int typeTuile) {
        this.type = typeTuile;
    }

    // Constructeur pour créer une tuile aléatoire
    public Tuile(int nbTuiles, boolean aleatoire) {
        this.type = (int) (Math.random() * nbTuiles);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        TypeTuile[] valeurs = TypeTuile.values();
        return valeurs[this.type].afficher();
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
        final Tuile other = (Tuile) obj;
        return this.type == other.type;
    }

}
