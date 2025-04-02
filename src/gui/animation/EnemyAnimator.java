package gui.animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EnemyAnimator {
    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int frameCount;
    private int currentFrame = 0;
    private int frameWidth;
    private int frameHeight;

    public EnemyAnimator(String path, int columns, int rows) throws IOException {
        spriteSheet = ImageIO.read(new File(path));
        this.frameWidth = spriteSheet.getWidth() / columns;
        this.frameHeight = spriteSheet.getHeight() / rows;
        this.frameCount = columns;
        this.frames = new BufferedImage[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = spriteSheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }
    }

    public void updateFrame() {
        currentFrame = (currentFrame + 1) % frameCount;
    }

    public void draw(Graphics g, int x, int y, int size) {
        g.drawImage(frames[currentFrame], x, y, size, size, null);
    }

    // ✅ Nouvelle méthode pour récupérer la frame courante
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }
}
