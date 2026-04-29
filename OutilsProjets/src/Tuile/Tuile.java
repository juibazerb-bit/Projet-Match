/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tuile;

import Coordonnées.Coord;

/**
 *
 * @author fpauvert
 */
public class Tuile {
    private int type;
    private Coord position;

    public Tuile(Coord position, int typeTuile) {
        this.position = position;
        this.type = typeTuile;
    }

    // Méthode statique pour créer une tuile aléatoire
    public Tuile(int nbTuiles) {
        this.type =(int) (Math.random() * nbTuiles);
    }

    public int getType() {
        return type;
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
