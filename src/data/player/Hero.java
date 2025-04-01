package data.player;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import data.map.Block;

public class Hero extends Person {
    private int spriteX = 0;  // Frame d'animation
    private int spriteY = 0;  // Ligne du sprite pour la direction
    private boolean isFlipped = false;  // Gère l'inversion pour la gauche
    private Image heroSprite;  // Image du sprite sheet

    private static final int SPRITE_WIDTH = 32;  // Largeur d'un sprite
    private static final int SPRITE_HEIGHT = 32; // Hauteur d'un sprite
    private static final int SPRITE_FRAMES = 3;  // Nombre de frames pour l'animation

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

    // Charger l’image du héros
    public void loadHeroSprite() {
        try {
            heroSprite = ImageIO.read(new File("src/images/player/Player.png"));
            System.out.println("✅ Image du héros chargée !");
        } catch (IOException e) {
            System.out.println("❌ ERREUR : Impossible de charger l’image du héros !");
            e.printStackTrace();
        }
    }

    // Mise à jour de l'animation (fait avancer le sprite)
    public void updateAnimationFrame() {
        spriteX = (spriteX + SPRITE_WIDTH) % (SPRITE_WIDTH * SPRITE_FRAMES);
        System.out.println("🔄 Animation mise à jour : spriteX = " + spriteX);
    }

    

    // Dessin du héros avec effet miroir pour la gauche
    public void draw(Graphics g, int blockSize) {
        int drawX = getPosition().getColumn() * blockSize;
        int drawY = getPosition().getLine() * blockSize;

        if (heroSprite != null) {
            if (isFlipped) {
                // 🔄 Effet miroir : Inverse l'image pour afficher le héros vers la gauche
                g.drawImage(heroSprite, drawX + blockSize, drawY, drawX, drawY + blockSize,
                        spriteX, spriteY, spriteX + SPRITE_WIDTH, spriteY + SPRITE_HEIGHT, null);
            } else {
                // 🎯 Affichage normal (vers la droite, le bas, ou le haut)
                g.drawImage(heroSprite, drawX, drawY, drawX + blockSize, drawY + blockSize,
                        spriteX, spriteY, spriteX + SPRITE_WIDTH, spriteY + SPRITE_HEIGHT, null);
            }
        } else {
            System.out.println("⚠ BUG : Sprite du héros non chargé !");
        }
    }

}

