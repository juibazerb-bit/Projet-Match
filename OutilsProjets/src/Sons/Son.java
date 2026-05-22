package Sons;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum Son {

    // Définition de tes bruitages (nom du fichier .wav)
    CLIC("clic.wav"),
    MATCH_SIMPLE("match.wav"),
    EXPLOSION("explosion.wav"),
    BONUS_FUSEE("fusee.wav"),
    PERDU("game_over.wav"),
    GAGNE("gagne.wav"),
    HIROSHIMA("hiroshima_alert.wav"),
    RACISME("Racisme.wav");

    private Clip clip;

    // Le constructeur charge le son une seule fois au démarrage du jeu
    Son(String nomFichier) {
        try {
            // Chemin vers ton dossier de sons (à la racine de ton projet NetBeans)
            File fichierSon = new File("sons/" + nomFichier);

            if (fichierSon.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(fichierSon);
                this.clip = AudioSystem.getClip();
                this.clip.open(audioIn);
            } else {
                System.out.println("Fichier son introuvable : " + fichierSon.getAbsolutePath());
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erreur de chargement pour le son : " + nomFichier);
            e.printStackTrace();
        }
    }

    /**
     * Joue le son depuis le début. Si le son est déjà en train de jouer, il est
     * réinitialisé et rejoué immédiatement.
     */
    public void jouer() {
        if (clip != null) {
            // Si le son est déjà en cours de lecture, on l'arrête
            if (clip.isRunning()) {
                clip.stop();
            }
            // On replace le curseur de lecture au tout début (0)
            clip.setFramePosition(0);
            // On lance la lecture
            clip.start();
        }
    }

    /**
     * Permet de jouer un son en boucle (utile pour une musique de fond par
     * exemple)
     */
    public void jouerEnBoucle() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void jouerNsecondes(double secondes) {
        if (clip != null) {
            // On lance le son normalement (rembobinage + lecture)
            jouer();

            // On crée un processus en arrière-plan pour gérer la coupure
            new Thread(() -> {
                try {
                    // Convertit les secondes en millisecondes (ex: 1.5s -> 1500ms)
                    long millisecondes = (long) (secondes * 1000);

                    // Le processus s'endort pendant la durée demandée
                    Thread.sleep(millisecondes);

                    // Une fois le temps écoulé, on coupe le son
                    stopper();

                } catch (InterruptedException e) {
                    // En cas d'interruption, on s'assure de couper le son par sécurité
                    stopper();
                    Thread.currentThread().interrupt();
                }
            }).start(); // /!\ Ne pas oublier le .start() pour lancer le processus parallèle
        }
    }

    /**
     * Arrête de force la lecture du son
     */
    public void stopper() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

}
