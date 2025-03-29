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

    private int health;      // Vie actuelle de l'ennemi
    private int maxHealth;   // Vie maximale

    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;
        this.enemyFrames = imageManager.getEnemyImages(enemyType);

        // Initialisation des points de vie en fonction du type
        switch (enemyType) {
            case "small":
                this.maxHealth = 50;
                break;
            case "medium":
                this.maxHealth = 80;
                break;
            case "large":
                this.maxHealth = 120;
                break;
            default:
                this.maxHealth = 60;
                break;
        }

        this.health = maxHealth;

        // Lancement de l'animation uniquement pour les slimes violets
        if (shouldAnimate()) {
            Thread animationThread = new Thread(this);
            animationThread.start();
        }
    }

    private boolean shouldAnimate() {
        return enemyType.equals("slime");
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

    // On va leurs rajouter une barre de vie

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }
}
