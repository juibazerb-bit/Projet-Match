/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FenetreGraphique;

import java.awt.Color;
import static java.awt.event.KeyEvent.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author guillaume.laurent
 */
public class TestClavierSouris {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        FenetreGraphique fenetre = new FenetreGraphique("Nyan cat", 300, 250);

        BufferedImage image = ImageIO.read(new File("nyancat.png"));
        
        int x = 30;
        int y = 40;
        while (true) {
            
            if (fenetre.unClicAEuLieu()) {
                x = fenetre.getXDernierClic();
                y = fenetre.getYDernierClic();                           
            }
            
            if (fenetre.uneToucheAEtePressee()) {
                if (fenetre.getCodeDerniereTouche()==VK_RIGHT) {
                    x = x + 5;
                }
                if (fenetre.getCodeDerniereTouche()==VK_LEFT) {
                    x = x - 5;
                }
                if (fenetre.getCodeDerniereTouche()==VK_DOWN) {
                    y = y + 5;
                }
                if (fenetre.getCodeDerniereTouche()==VK_UP) {
                    y = y - 5;
                }
            }
              
            fenetre.effacer(new Color(0, 0, 100));
            fenetre.getGraphics2D().drawImage(image, x, y, null);           
            fenetre.actualiser(0.04);
        }

    }

}
