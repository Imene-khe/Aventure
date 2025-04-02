package data.player;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import data.map.Block;
import gui.animation.EnemyAnimator;

public class Antagonist extends Person {

    private EnemyAnimator animator;
    private String enemyType;
    private int health;
    private int maxHealth;

    private int x, y; // Coordonnées pixels pour CombatMap

    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;

        switch (enemyType) {
            case "small" -> this.maxHealth = 50;
            case "medium" -> this.maxHealth = 80;
            case "large" -> this.maxHealth = 120;
            default -> this.maxHealth = 60;
        }
        this.health = maxHealth;

        // Position en pixels
        if (startPosition != null) {
            this.x = startPosition.getColumn() * 50;
            this.y = startPosition.getLine() * 50;
        }

        // Chargement des sprites animés
        try {
            String spritePath = switch (enemyType) {
                case "small" -> "src/images/enemies/SmallSlime_Green.png";
                case "medium" -> "src/images/enemies/MediumSlime_Blue.png";
                case "large" -> "src/images/enemies/LargeSlime_Grey.png";
                default -> throw new IllegalArgumentException("Taille inconnue");
            };
            this.animator = new EnemyAnimator(spritePath, 5, 6); // 5 colonnes x 6 lignes
        } catch (IOException e) {
            System.out.println("❌ Erreur chargement animator pour " + enemyType);
            e.printStackTrace();
        }
    }

    public void draw(Graphics g, int blockSize) {
        if (animator != null) {
            animator.updateFrame();
            animator.draw(g, x, y, blockSize);
        }
    }

    public Image getCurrentImage() {
        return animator != null ? animator.getCurrentFrame() : null;
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

    public int getX() { return x; }

    public int getY() { return y; }
}
