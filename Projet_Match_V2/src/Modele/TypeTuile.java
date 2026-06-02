package Modele;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * Enumération de tous les types de tuiles disponibles. Chaque type connait son
 * symbole console, sa couleur ANSI, et son image.
 *
 * Les images sont chargées une seule fois au démarrage depuis le dossier
 * "images gemmes/" à la racine du projet.
 */
public enum TypeTuile {

    ROUGE("\u001B[31m", "R", "ruby.png"),
    VERT("\u001B[32m", "E", "emerald.png"),
    JAUNE("\u001B[33m", "G", "gem.png"),
    BLEU("\u001B[34m", "T", "tourmaline.png"),
    MAGENTA("\u001B[35m", "A", "amethyst.png"),
    CYAN("\u001B[36m", "X", "diamond.png"),
    BLANC("\u001B[37m", "O", "gem1.png"),
    LOUP("", "", "loup.png"),
    CHAT("", "", "chat.png"),
    DAUPHIN("", "", "dauphin.png"),
    GRENOUILLE("", "", "grenouille.png"),
    PERROQUET("", "", "perroquet.png"),
    RENARD("", "", "renard.png"),
    POULPE("", "", "poulpe.png");

    private static final String RESET = "\u001B[0m";

    private final String couleur;
    private final String symbole;
    private final Image image;

    TypeTuile(String couleur, String symbole, String fichier) {
        this.couleur = couleur;
        this.symbole = symbole;
        this.image = new ImageIcon("images gemmes/" + fichier).getImage();
    }

    public Image getImage() {
        return image;
    }

    /**
     * Retourne le symbole coloré pour l'affichage console.
     */
    public String afficher() {
        return couleur + symbole + RESET + " ";
    }
}
