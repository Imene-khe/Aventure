package data.player;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class EnemyImageManager {

    private HashMap<String, ArrayList<Image>> enemyImages;

    public EnemyImageManager() {
        enemyImages = new HashMap<>();
        loadImages();
    }

    private void loadImages() {
        try {
            System.out.println("🔄 Chargement des images des ennemis...");
            enemyImages.put("skeleton", loadSpriteSheet("src/images/enemies/Skeleton.png", 32, 32, 0, 3));
            enemyImages.put("slime_green", loadSpriteSheet("src/images/enemies/slime_green2.png", 32, 32, 0, 1));
            enemyImages.put("slime", loadSpriteSheet("src/images/enemies/slime.png", 32, 32, 0, 3));
            enemyImages.put("boss", loadSingleImage("src/images/enemies/boss/boss.png")); // ✅ une seule image
            System.out.println("✅ Toutes les images des ennemis ont été chargées !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR lors du chargement des images des ennemis !");
            e.printStackTrace();
        }
    }

    private ArrayList<Image> loadSpriteSheet(String path, int frameWidth, int frameHeight, int startFrame, int endFrame) throws IOException {
        ArrayList<Image> frames = new ArrayList<>();
        File spriteSheetFile = new File(path);

        if (!spriteSheetFile.exists()) {
            System.out.println("❌ ERREUR : Le fichier n'existe pas : " + path);
            return frames;
        }

        BufferedImage spriteSheet = ImageIO.read(spriteSheetFile);
        int spriteSheetWidth = spriteSheet.getWidth();
        int framesPerRow = spriteSheetWidth / frameWidth;

        for (int i = startFrame; i <= endFrame; i++) {
            int x = (i % framesPerRow) * frameWidth;
            int y = (i / framesPerRow) * frameHeight;
            frames.add(spriteSheet.getSubimage(x, y, frameWidth, frameHeight));
        }

        return frames;
    }

    public ArrayList<Image> getEnemyImages(String enemyType) {
        return enemyImages.getOrDefault(enemyType, new ArrayList<>());
    }

    public Image getEnemyImage(String enemyType, int frameIndex) {
        ArrayList<Image> frames = enemyImages.get(enemyType);
        if (frames == null || frames.isEmpty()) {
            System.out.println("⚠ Aucune image trouvée pour l'ennemi : " + enemyType);
            return null;
        }
        return frames.get(frameIndex % frames.size());
    }
    
    public Image getEnemyImageFor(Antagonist enemy) {
     
        return getEnemyImage(enemy.getType(), 0);
    }

    
    private ArrayList<Image> loadSingleImage(String path) throws IOException {
        ArrayList<Image> frames = new ArrayList<>();
        File imageFile = new File(path);
        
        if (!imageFile.exists()) {
            System.out.println("❌ ERREUR : Le fichier du boss est introuvable : " + path);
            return frames;
        }

        BufferedImage image = ImageIO.read(imageFile);
        frames.add(image); // ✅ Une seule frame
        return frames;
    }


    
    


}

