/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package TypeTuile;

/**
 *
 * @author flo66
 */
public enum TypeTuile {
    ROUGE("\u001B[31m", "o"),
    VERT("\u001B[32m", "o"),
    JAUNE("\u001B[33m", "o"),
    BLEU("\u001B[34m", "o"),
    MAGENTA("\u001B[35m", "o"),
    CYAN("\u001B[36m", "o"),
    BLANC("\u001B[37m", "o");

    private String RESET = "\u001B[0m";
    private String couleur;
    private String symbole;
    private int type;

    TypeTuile(String couleur, String symbole) {
        this.couleur = couleur;
        this.symbole = symbole;
    }

    public String afficher() {
        return this.couleur + this.symbole + RESET + " ";
    }
    
}
