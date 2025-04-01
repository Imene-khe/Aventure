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

    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;
        this.enemyFrames = imageManager.getEnemyImages(enemyType);

        // Initialisation des points de vie
        switch (enemyType) {
            case "small":  this.maxHealth = 50; break;
            case "medium": this.maxHealth = 80; break;
            case "large":  this.maxHealth = 120; break;
            default:       this.maxHealth = 60; break;
        }

        this.health = maxHealth;

        // ✅ On ne lance l'animation que si la liste d'images est non vide
        if (enemyFrames != null && !enemyFrames.isEmpty()) {
            Thread animationThread = new Thread(this);
            animationThread.start();
        } else {
            System.out.println("❌ Aucune frame chargée pour l'ennemi : " + enemyType);
        }

        this.health = maxHealth;

        if (shouldAnimate()) {
            Thread animationThread = new Thread(this);
            animationThread.start();
        }
    }

    // ✅ Convertit le type générique en nom d’image réel
    private String resolveType(String type) {
        return switch (type.toLowerCase()) {
            case "small" -> "SmallSlime_Green";
            case "medium" -> "MediumSlime_Blue";
            case "large" -> "LargeSlime_Red";
            default -> type;
        };
    }

    private boolean shouldAnimate() {
        return true; // tu peux filtrer ici si tu veux animer uniquement certains types
    }

    @Override
    public void draw(Graphics g, int blockSize) {
        int drawX = getPosition().getColumn() * blockSize;
        int drawY = getPosition().getLine() * blockSize;

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

    public boolean isDead() {
        return health <= 0;
    }
    
    
}
