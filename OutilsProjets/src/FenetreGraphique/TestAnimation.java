/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FenetreGraphique;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author guillaume.laurent
 */
public class TestAnimation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        FenetreGraphique fenetre = new FenetreGraphique("Nyan cat", 300, 250);

        BufferedImage image = ImageIO.read(new File("nyancat.png"));
        
        int x = -image.getWidth(null);
        while (true) {
            fenetre.effacer(new Color(0, 0, 100));
            fenetre.getGraphics2D().drawImage(image, x, 100, null);
            fenetre.actualiser(0.04);
            x = x + 5;
            if (x > 300) {
                x = -image.getWidth(null);
            }
        }

    }

}
