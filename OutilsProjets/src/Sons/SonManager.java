/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Sons;

/**
 *
 * @author flo66
 */
public class SonManager {
     private static boolean actif = true;
    
    public static void activer()   { actif = true;  }
    public static void desactiver(){ actif = false; }
    public static boolean estActif(){ return actif; }
    
    // Joue le son seulement si le manager est actif
    public static void jouer(Son son) {
        if (actif) son.jouer();
    }
    
    public static void jouerEnBoucle(Son son) {
        if (actif) son.jouerEnBoucle();
    }
    
    public static void jouerNsecondes(Son son, double secondes) {
        if (actif) son.jouerNsecondes(secondes);
    }
    
    public static void stopper(Son son) {
        if (actif) son.stopper();
    }
}
