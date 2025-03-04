package data.player;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import data.map.Block;

public class Hero extends Person {
    private int spriteX = 0;  // Animation frame (horizontale)
    private int spriteY = 0;  // Direction (verticale)
    private boolean isFlipped = false;  //  Permet d'inverser l'image pour la gauche
    private Image heroSprite;  //  Stocke l'image du héros

    public Hero(Block startPosition, int health) {
        super(startPosition, health);
        loadHeroSprite();
    }
    
    public Hero(Block position) {
    	super(position);
    	loadHeroSprite();
    }

    public void takeDamage(int amount) {
        setHealth(Math.max(0, getHealth() - amount));
        System.out.println("💥 Héros touché ! Vie restante : " + getHealth() + "%");
    }

    // Charger l’image du hero
    private void loadHeroSprite() {
        try {
            heroSprite = ImageIO.read(new File("src/images/player/Player.png"));
        } catch (IOException e) {
            System.out.println("❌ ERREUR : Impossible de charger l’image du héros !");
            e.printStackTrace();
        }
    }

    // Déplacement à gauche (on inverse l’image)
    public void moveLeft() {
        spriteY = 32;  
        spriteX = (spriteX + 32) % (32 * 3);
        isFlipped = true;  // Active l'effet miroir car il n'y a pas d'image pour la gauche
    }

    // ✅ Déplacement à droite 
    public void moveRight() {
        spriteY = 32;  
        spriteX = (spriteX + 32) % (32 * 3);
        isFlipped = false;  
    }

    // ✅ Déplacement en haut
    public void moveUp() {
        spriteY = 64;  // 🔄 Ligne 3 -> Héros regarde en haut
        spriteX = (spriteX + 32) % (32 * 3);
    }

    // Déplacement en bas
    public void moveDown() {
        spriteY = 0;  // 🔄 Ligne 1 -> Héros regarde en bas
        spriteX = (spriteX + 32) % (32 * 3);
    }

    public void draw(Graphics g, int blockSize) {
        int drawX = super.getPosition().getColumn() * blockSize;
        int drawY = super.getPosition().getLine() * blockSize;

        if (isFlipped) {
            // Dessine l’image inversée pour la gauche
            g.drawImage(heroSprite, drawX + blockSize, drawY, drawX, drawY + blockSize,
                    spriteX, spriteY, spriteX + 32, spriteY + 32, null);
        } else {
            // Dessine l’image normalement
            g.drawImage(heroSprite, drawX, drawY, drawX + blockSize, drawY + blockSize,
                    spriteX, spriteY, spriteX + 32, spriteY + 32, null);
        }
    }
}
