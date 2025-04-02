package data.player;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import data.map.Block;

public class Antagonist extends Person implements Runnable {

    private int currentFrame = 0;
    private ArrayList<Image> enemyFrames;
    private String enemyType;
    private static final int ANIMATION_DELAY = 150;

    private int health;
    private int maxHealth;

    private int x, y; // ✅ Coordonnées pixels pour CombatMap

    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;
        this.enemyFrames = imageManager.getEnemyImages(enemyType);

        switch (enemyType) {
            case "small":  this.maxHealth = 50; break;
            case "medium": this.maxHealth = 80; break;
            case "large":  this.maxHealth = 120; break;
            default:       this.maxHealth = 60; break;
        }

        this.health = maxHealth;

        // ✅ Conversion position logique en coordonnées pixels (pour CombatMap)
        if (startPosition != null) {
            this.x = startPosition.getColumn() * 50;
            this.y = startPosition.getLine() * 50;
        }
        
        
        
        

        if (enemyFrames != null && !enemyFrames.isEmpty()) {
            Thread animationThread = new Thread(this);
            animationThread.start();
        } else {
            System.out.println("❌ Aucune frame chargée pour l'ennemi : " + enemyType);
        }
    }

    @Override
    public void draw(Graphics g, int blockSize) {
        if (enemyFrames != null && !enemyFrames.isEmpty()) {
            g.drawImage(enemyFrames.get(currentFrame), x, y, blockSize, blockSize, null);
        } else {
            System.out.println("❌ Aucune image trouvée pour l'ennemi : " + enemyType);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(ANIMATION_DELAY);
                currentFrame = (currentFrame + 1) % enemyFrames.size();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    public Image getCurrentImage() {
        if (enemyFrames != null && !enemyFrames.isEmpty()) {
            return enemyFrames.get(currentFrame);
        }
        return null;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}

