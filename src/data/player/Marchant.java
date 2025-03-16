package data.player;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import data.map.Block;
import data.map.Map;

public class Marchant {
    private Block position;
    private Map map;
    private BufferedImage spriteSheet; // Image complète (spritesheet)
    private int currentFrame;
    private int currentDirection; // 0 = bas, 1 = gauche, 2 = droite, 3 = haut

    public Marchant(Map map, int line, int column) {
        this.map = map;
        this.position = map.getBlock(line, column);
        
        // ✅ Chargement correct du sprite sheet
        try {
            this.spriteSheet = ImageIO.read(new File("src/images/merchant_sprite.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ ERREUR : Impossible de charger l'image du marchand !");
        }
        
        this.currentFrame = 0; // Débute à la première frame
        this.currentDirection = 0; // Par défaut, face au joueur
    }

    // ✅ Retourne la bonne frame du sprite (corrigé)
    public BufferedImage getCurrentSprite() {
        int frameWidth = spriteSheet.getWidth() / 6;  // 6 frames en largeur
        int frameHeight = spriteSheet.getHeight() / 2; // 2 lignes (marche & idle)
        
        int x = (currentFrame % 6) * frameWidth;  // Sélectionne la bonne colonne (frame)
        int y = currentDirection * frameHeight;   // Sélectionne la bonne ligne (0 = idle, 1 = marche)

        return spriteSheet.getSubimage(x, y, frameWidth, frameHeight);
    }


    // ✅ Fait avancer l'animation du marchand
    public void nextFrame() {
        currentFrame = (currentFrame + 1) % 4; // Passe à la frame suivante (0 → 3)
    }

    // ✅ Gère les déplacements et met à jour la direction
    public void move(int dx, int dy) {
        int newLine = position.getLine() + dy;
        int newColumn = position.getColumn() + dx;

        // Vérifie si le marchand reste dans la boutique (5x5)
        if (newLine >= 0 && newLine < 5 && newColumn >= 0 && newColumn < 5) {
            Block newPos = map.getBlock(newLine, newColumn);
            if (!map.isBlocked(newPos)) {
                this.position = newPos;
                this.nextFrame(); // Change la frame pour animer
                this.currentDirection = 1; // Passe en mode "marche"
            }
        }
    }


    public Block getPosition() {
        return position;
    }
}

