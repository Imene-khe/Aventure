package gui.animation;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gère l’animation du héros via SpriteAnimator, sans modifier la classe Hero.
 */
public class HeroAnimator {

    private final Map<Integer, SpriteAnimator> animatorsByLine = new HashMap<>();
    private final BufferedImage fullSheet;

    // Largeur/hauteur d’un sprite
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;

    public HeroAnimator(String pathToPlayerSpriteSheet) throws IOException {
        fullSheet = ImageIO.read(new File(pathToPlayerSpriteSheet));
        initAnimators();
    }

    private void initAnimators() {
        try {
            for (int row = 0; row < 10; row++) {
                int frameCount = (row < 6) ? 6 : 4;
                Image[] frames = new Image[frameCount];

                for (int i = 0; i < frameCount; i++) {
                    int x = i * SPRITE_WIDTH;
                    int y = row * SPRITE_HEIGHT;
                    frames[i] = fullSheet.getSubimage(x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
                }

                SpriteAnimator animator = new SpriteAnimator(frames, 120); // 120ms entre frames
                animatorsByLine.put(row, animator);
            }

            System.out.println("✅ HeroAnimator initialisé avec 10 lignes animées.");

        } catch (Exception e) {
            System.out.println("❌ Erreur dans l’initialisation des animations du héros");
            e.printStackTrace();
        }
    }

    /**
     * Dessine le héros à l’écran.
     * @param g contexte graphique
     * @param x position X en pixels
     * @param y position Y en pixels
     * @param spriteRow ligne à utiliser dans le spritesheet (0 à 9)
     * @param flipped vrai si on doit inverser l’image (vers la gauche)
     * @param blockSize taille d’un bloc à l’écran
     */
    public void draw(Graphics g, int x, int y, int spriteRow, boolean flipped, int blockSize) {
        SpriteAnimator animator = animatorsByLine.get(spriteRow);
        if (animator == null) return;

        Image frame = animator.getCurrentFrame();
        if (frame == null) return;

        if (flipped) {
            // 🔄 Affichage inversé (gauche)
            g.drawImage(frame, x + blockSize, y, x, y + blockSize, 
                0, 0, SPRITE_WIDTH, SPRITE_HEIGHT, null);
        } else {
            // ➡️ Affichage normal (droite, haut, bas)
            g.drawImage(frame, x, y, x + blockSize, y + blockSize, 
                0, 0, SPRITE_WIDTH, SPRITE_HEIGHT, null);
        }
    }
}
