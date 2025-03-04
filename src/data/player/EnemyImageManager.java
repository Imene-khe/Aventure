package data.player;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class EnemyImageManager {

    private HashMap<String, ArrayList<Image>> enemyImages; // Maintenant, on stocke une liste de frames par ennemi.

    public EnemyImageManager() {
        enemyImages = new HashMap<>();
        loadImages();
    }

    private void loadImages() {
        try {
            System.out.println("Chargement des images ennemies...");
            // Exemple pour charger les sprites de squelettes et autres ennemis
            enemyImages.put("skeleton", loadSpriteSheet("src/images/enemies/skeleton.png", 32, 32, 4));  // 4 frames par ligne
            enemyImages.put("slime", loadSpriteSheet("src/images/enemies/slime.png", 32, 32, 4));  // 4 frames par ligne
            System.out.println("✅ Images ennemies chargées !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR lors du chargement des images ennemies !");
            e.printStackTrace();
        }
    }

    private ArrayList<Image> loadSpriteSheet(String path, int frameWidth, int frameHeight, int framesPerRow) throws IOException {
        ArrayList<Image> frames = new ArrayList<>();
        File spriteSheetFile = new File(path);
        Image spriteSheet = ImageIO.read(spriteSheetFile);

        if (spriteSheet == null) {
            System.out.println("❌ ERREUR : Impossible de charger la spritesheet depuis : " + path);
            return frames;
        }

        int spriteSheetWidth = spriteSheet.getWidth(null);
        int spriteSheetHeight = spriteSheet.getHeight(null);

        // Découpe de la spritesheet en fonction des dimensions des frames
        for (int row = 0; row < spriteSheetHeight / frameHeight; row++) {
            for (int col = 0; col < framesPerRow; col++) {
                int x = col * frameWidth;
                int y = row * frameHeight;
                frames.add(((BufferedImage) spriteSheet).getSubimage(x, y, frameWidth, frameHeight));
            }
        }

        return frames;
    }

    public ArrayList<Image> getEnemyImages(String enemyType) {
        return enemyImages.get(enemyType);
    }
}
