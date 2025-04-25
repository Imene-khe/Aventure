package gui.animation;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteAnimator {

    private final Image[] frames;
    private final int frameCount;
    private int currentFrame = 0;
    private final int frameDelay; // en ms
    private int elapsedTime = 0;

    public SpriteAnimator(String spritePath, int columns, int rows, int frameDelay) throws IOException {
        this.frameDelay = frameDelay;
        BufferedImage sheet = ImageIO.read(new File(spritePath));
        int frameWidth = sheet.getWidth() / columns;
        int frameHeight = sheet.getHeight() / rows;

        this.frameCount = columns * rows;
        this.frames = new Image[frameCount];

        for (int i = 0; i < frameCount; i++) {
            int x = (i % columns) * frameWidth;
            int y = (i / columns) * frameHeight;
            frames[i] = sheet.getSubimage(x, y, frameWidth, frameHeight);
        }
    }

    public SpriteAnimator(Image[] frames, int frameDelay) {
        this.frames = frames;
        this.frameCount = frames.length;
        this.frameDelay = frameDelay;
    }

    public SpriteAnimator(String[] imagePaths, int frameDelay) throws IOException {
        this.frameCount = imagePaths.length;
        this.frames = new Image[frameCount];
        this.frameDelay = frameDelay;

        for (int i = 0; i < frameCount; i++) {
            frames[i] = ImageIO.read(new File(imagePaths[i]));
        }
    }

    // 💡 Appelé par GameLoopManager à chaque tick
    public void update(int deltaMs) {
        elapsedTime += deltaMs;
        if (elapsedTime >= frameDelay) {
            elapsedTime = 0;
            currentFrame = (currentFrame + 1) % frameCount;
        }
    }

    public Image getCurrentFrame() {
        return frames[currentFrame];
    }

    public Image[] getAllFrames() {
        return frames;
    }

    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    public int getFrameCount() {
        return frameCount;
    }
}
