package gui.animation;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteAnimator {

    private final Image[] frames;
    private final int frameCount;
    private int currentFrame = 0;
    private final int frameDelay; // en ms

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

        startAnimationThread();
    }
    
    public SpriteAnimator(String[] imagePaths, int frameDelay) throws IOException {
        this.frameCount = imagePaths.length;
        this.frames = new Image[frameCount];
        this.frameDelay = frameDelay;

        for (int i = 0; i < frameCount; i++) {
            frames[i] = ImageIO.read(new File(imagePaths[i]));
        }

        startAnimationThread();
    }


    private void startAnimationThread() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(frameDelay);
                    currentFrame = (currentFrame + 1) % frameCount;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
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
    
 // 🔽 Main de test intégré à la classe SpriteAnimator
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // 💡 Test avec ton spritesheet flames.png (4 colonnes × 3 lignes)
                SpriteAnimator animator = new SpriteAnimator("src/images/outdoors/flames.png", 4, 3, 100);

                // 🎨 Création de la fenêtre d'affichage
                JFrame frame = new JFrame("Test SpriteAnimator");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(200, 200);
                frame.setLocationRelativeTo(null);

                // 🎥 JPanel qui dessine l’image animée
                JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Image frameImage = animator.getCurrentFrame();
                        if (frameImage != null) {
                            g.drawImage(frameImage, 50, 50, 64, 64, null); // Position fixe (x, y)
                        }
                    }
                };

                // ⏱️ Redessin toutes les 100ms
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(100);
                            panel.repaint();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                frame.add(panel);
                frame.setVisible(true);

            } catch (IOException e) {
                System.out.println("❌ Erreur de chargement du spritesheet : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

}