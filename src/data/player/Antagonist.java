package data.player;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import data.map.Block;

public class Antagonist extends Person implements Runnable {
    private int currentFrame = 0;  // Frame actuelle de l'animation
    private ArrayList<Image> enemyFrames;  // Stocke les frames de l'ennemi
    private String enemyType;
    private static final int ANIMATION_DELAY = 150; // Temps entre chaque frame (ms)

    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;
        this.enemyFrames = imageManager.getEnemyImages(enemyType);  // Récupère les frames du sprite sheet

        // Si l'ennemi est un slime violet, on démarre l'animation
        if (shouldAnimate()) {
            Thread animationThread = new Thread(this);
            animationThread.start();
        }
    }

    private boolean shouldAnimate() {
        return enemyType.equals("slime"); // Seuls les slimes violets doivent être animés
    }

    @Override
    public void draw(Graphics g, int blockSize) {
        int drawX = getPosition().getColumn() * blockSize;
        int drawY = getPosition().getLine() * blockSize;

        // Vérifier que l'ennemi a des frames valides
        if (enemyFrames != null && !enemyFrames.isEmpty()) {
            g.drawImage(enemyFrames.get(currentFrame), drawX, drawY, blockSize, blockSize, null);
        } else {
            System.out.println("❌ Aucune image trouvée pour l'ennemi : " + enemyType);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(ANIMATION_DELAY); // Changer de frame toutes les 150 ms
                currentFrame = (currentFrame + 1) % enemyFrames.size(); // Boucle sur les frames de saut

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
