package data;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import data.Block;

public class Antagonist extends Person {
	 private int spriteX = 0;  // Animation frame (horizontale)
	 private int spriteY = 0;  // Direction (verticale)
     private Image enemySprite;  //  Stocke l'image du héros

     public Antagonist(Block startPosition) {
        super(startPosition);
        loadSprite();
     }

    
     private void loadSprite() {
        try {
            enemySprite = ImageIO.read(new File("src/main/java/aventure/image/img/Enemies/Skeleton.png"));
        } catch (IOException e) {
            System.out.println("❌ ERREUR : Impossible de charger l’image !");
            e.printStackTrace();
        }
     }

     public void draw(Graphics g, int blockSize) {
		int drawX = getPosition().getColumn() * blockSize;
        int drawY = getPosition().getLine() * blockSize;
        g.drawImage(enemySprite, drawX + blockSize, drawY, drawX, drawY + blockSize, 0, 0, 32,32, null);
	}
    
}

