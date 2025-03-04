package data.player;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import data.map.Block;

public class Antagonist extends Person {
    private int currentFrame = 0;  // Indice de la frame actuelle
    private ArrayList<Image> enemyFrames;  // Stocke les frames de l'ennemi
    private String enemyType;

    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;
        this.enemyFrames = imageManager.getEnemyImages(enemyType);  // Récupère les frames de l'ennemi
    }

    public void draw(Graphics g, int blockSize) {
        int drawX = getPosition().getColumn() * blockSize;
        int drawY = getPosition().getLine() * blockSize;

        // Dessiner l'ennemi avec la frame actuelle
        if (enemyFrames != null && !enemyFrames.isEmpty()) {
            g.drawImage(enemyFrames.get(currentFrame), drawX, drawY, blockSize, blockSize, null);
            currentFrame = (currentFrame + 1) % enemyFrames.size();  // Passer à la frame suivante (cycle)
        }
    }

}
