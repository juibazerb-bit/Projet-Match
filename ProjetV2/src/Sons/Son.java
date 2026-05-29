package Sons;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * Enumération de tous les effets sonores du jeu.
 * Chaque son est chargé une seule fois au démarrage depuis le dossier sons/.
 *
 * Utilisation :
 *   SonManager.jouer(Son.MATCH_SIMPLE);
 *   SonManager.jouerNsecondes(Son.HIROSHIMA, 2);
 */
public enum Son {

    CLIC          ("clic.wav"),
    MATCH_SIMPLE  ("match.wav"),
    EXPLOSION     ("explosion.wav"),
    BONUS_FUSEE   ("fusee.wav"),
    PERDU         ("game_over.wav"),
    GAGNE         ("gagne.wav"),
    HIROSHIMA     ("hiroshima_alert.wav"),
    RACISME       ("Racisme.wav");

    private Clip clip;

    Son(String nomFichier) {
        try {
            File fichier = new File("sons/" + nomFichier);
            if (fichier.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(fichier);
                this.clip = AudioSystem.getClip();
                this.clip.open(audioIn);
            } else {
                System.out.println("Son introuvable : " + fichier.getAbsolutePath());
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erreur chargement son : " + nomFichier);
        }
    }

    /** Joue le son depuis le début (repart du début si déjà en cours). */
    public void jouer() {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /** Joue le son en boucle infinie. */
    public void jouerEnBoucle() {
        if (clip == null) return;
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /** Joue le son pendant `secondes` secondes puis l'arrête. */
    public void jouerNsecondes(double secondes) {
        if (clip == null) return;
        jouer();
        new Thread(() -> {
            try {
                Thread.sleep((long) (secondes * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                stopper();
            }
        }).start();
    }

    /** Arrête le son. */
    public void stopper() {
        if (clip != null && clip.isRunning()) clip.stop();
    }
}
