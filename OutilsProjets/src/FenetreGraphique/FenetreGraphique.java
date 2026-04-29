package FenetreGraphique;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Classe de dessin 2D
 *
 * @author guillaume.laurent
 * @date 2024-04-09
 */
public class FenetreGraphique extends JFrame implements MouseListener, KeyListener {

    private BufferedImage image;
    private BufferedImage buffer;
    private Graphics2D graphicsImage;
    private Graphics2D graphicsBuffer;
    private long tempsDerniereActualisation;
    private JLabel jLabel;
    private boolean unClicAEuLieu;
    private boolean uneToucheAEtePressee;
    private MouseEvent dernierClic;
    private KeyEvent derniereTouche;

    public FenetreGraphique(String titre, int largeur, int hauteur) {
        this(titre, largeur, hauteur, 0, 0);
    }

    public FenetreGraphique(String titre, int largeur, int hauteur, int positionX, int positionY) {
        super(titre);

        if (largeur <= 0 || hauteur <= 0) {
            throw new IllegalArgumentException("La largeur et la hauteur de la fenetre doivent être positives");
        }

        // initialisation de la fenetre
        setSize(largeur, hauteur);
        setLocation(positionX, positionY);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jLabel = new JLabel();
        this.jLabel.setPreferredSize(new java.awt.Dimension(largeur, hauteur));
        setContentPane(jLabel);
        pack();

        // Creation du buffer pour l'affichage et recuperation du contexte graphique
        buffer = new BufferedImage(this.jLabel.getWidth(), this.jLabel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.jLabel.setIcon(new ImageIcon(buffer));
        graphicsBuffer = buffer.createGraphics();

        // Creation d'un image pour dessiner et recuperation du contexte graphique
        image = new BufferedImage(this.jLabel.getWidth(), this.jLabel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphicsImage = image.createGraphics();
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphicsImage.addRenderingHints(rh);
        graphicsImage.setColor(Color.WHITE);
        graphicsImage.fillRect(0, 0, this.jLabel.getWidth(), this.jLabel.getHeight());
        graphicsImage.setColor(Color.BLACK);

        // Ajoute des écouteurs souris et clavier
        this.jLabel.addMouseListener(this);
        this.addKeyListener(this);
        unClicAEuLieu = false;
        uneToucheAEtePressee = false;

        setVisible(true);
        tempsDerniereActualisation = System.nanoTime();
    }

    public Graphics2D getGraphics2D() {
        return graphicsImage;
    }

    public void actualiser() {
        unClicAEuLieu = false;
        uneToucheAEtePressee = false;
        graphicsBuffer.drawImage(image, 0, 0, null);
        this.jLabel.repaint();
        tempsDerniereActualisation = System.nanoTime();
    }

    public void actualiser(double secondes) {
        unClicAEuLieu = false;
        uneToucheAEtePressee = false;
        long nanosecondes = (long) (secondes * 1_000_000_000.0);
        while (System.nanoTime() - tempsDerniereActualisation < nanosecondes) {
        }
        tempsDerniereActualisation = System.nanoTime();
        graphicsBuffer.drawImage(image, 0, 0, null);
        this.jLabel.repaint();
    }

    public void attendre(double secondes) {
        long millisecondes = (long) (secondes * 1000.0);
        try {
            Thread.sleep(millisecondes);
        } catch (InterruptedException ex) {
            Logger.getLogger(FenetreGraphique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fermer() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void effacer() {
        effacer(Color.WHITE);
    }

    public void effacer(Color couleur) {
        Color couleurCourante = graphicsImage.getColor();
        graphicsImage.setColor(couleur);
        graphicsImage.fillRect(0, 0, this.jLabel.getWidth(), this.jLabel.getHeight());
        graphicsImage.setColor(couleurCourante);
    }

    public void enregistrerImage(String nomDuFichierPNG) {
        if (nomDuFichierPNG == null || nomDuFichierPNG.equals("")) {
            throw new IllegalArgumentException("Le nom du fichier ne peut être vide.");
        }
        String suffix = nomDuFichierPNG.substring(nomDuFichierPNG.lastIndexOf('.') + 1);

        if (suffix.toLowerCase().equals("png")) {
            try {
                File file = new File(nomDuFichierPNG);
                ImageIO.write(image, suffix, file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Le format du fichier doit être png.");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        unClicAEuLieu = true;
        dernierClic = e;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        uneToucheAEtePressee = true;
        derniereTouche = e;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public int getXDernierClic() {
        return dernierClic.getX();
    }

    public int getYDernierClic() {
        return dernierClic.getY();
    }
    
    public int getBouttonDernierClic() {
        return dernierClic.getButton();
    }

    public int getCodeDerniereTouche() {
        return derniereTouche.getKeyCode();
    }

    public boolean unClicAEuLieu() {
        return unClicAEuLieu;
    }

    public boolean uneToucheAEtePressee() {
        return uneToucheAEtePressee;
    }
    
    
}
