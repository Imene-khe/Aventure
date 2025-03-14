package data.item;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.map.Block;

public class Coin implements Runnable {
    private Block block;
    private boolean collected;
    private Image[] images;
    private int currentFrame;
    private static final int FRAME_DELAY = 100; // Temps entre chaque frame en millisecondes

    public Coin(Block block) {
        this.block = block;
        this.collected = false;
        this.images = new Image[8]; // 8 images pour l'animation
        this.currentFrame = 0;

        loadImages();
        
        // Démarrer le thread d'animation
        Thread animationThread = new Thread(this);
        animationThread.start();
    }

    private void loadImages() {
        try {
            for (int i = 0; i < 8; i++) {
                images[i] = ImageIO.read(new File("src/images/items/coins/coin" + (i + 1) + ".png"));
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur lors du chargement des images des pièces !");
            e.printStackTrace();
        }
    }

    public void draw(Graphics g, int blockSize) {
        if (!collected && images[currentFrame] != null) {
            int x = block.getColumn() * blockSize;
            int y = block.getLine() * blockSize;
            
            // Définir une taille réduite (ex: 60% de la taille d'un bloc)
            int coinSize = (int) (blockSize * 0.5); // 60% de BLOCK_SIZE

            // Centrer la pièce dans le bloc
            int offset = (blockSize - coinSize) / 2;

            g.drawImage(images[currentFrame], x + offset, y + offset, coinSize, coinSize, null);
        }
    }


    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void run() {
        while (!collected) {
            try {
                Thread.sleep(FRAME_DELAY); // Attendre avant de changer d'image
                currentFrame = (currentFrame + 1) % images.length; // Passer à la frame suivante
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
 // === TEST MAIN ===
    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Animation Coin");
        frame.setSize(200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            private Coin coin = new Coin(new Block(2, 2)); // Coin à une position arbitraire

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                coin.draw(g, 50); // Dessine la pièce avec une taille de 50 pixels
                repaint(); // Rafraîchir pour animer
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }
}
