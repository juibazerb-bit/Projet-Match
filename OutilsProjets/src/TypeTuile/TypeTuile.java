/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package TypeTuile;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author flo66
 */
public enum TypeTuile {

    ROUGE("\u001B[31m", "R", "ruby.png"),
    VERT("\u001B[32m", "E", "emerald.png"),
    JAUNE("\u001B[33m", "G", "gem.png"),
    BLEU("\u001B[34m", "T", "tourmaline.png"),
    MAGENTA("\u001B[35m", "A", "amethyst.png"),
    CYAN("\u001B[36m", "X", "diamond.png"),
    BLANC("\u001B[37m", "O", "gem1.png");

    private final String RESET = "\u001B[0m";
    private final String couleur;
    private final String symbole;
    private final Image image;

    TypeTuile(String couleur, String symbole, String fichier) {
        this.couleur = couleur;
        this.symbole = symbole;
        String chemin = "images gemmes/" + fichier;
        this.image = new ImageIcon(chemin).getImage();

    }

    public Image getImage() {
        return image;
    }

    public String afficher() {
        try {
            // Force la console à interpréter les caractères en UTF-8
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this.couleur + this.symbole + RESET + " ";
    }

}
