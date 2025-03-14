package data.player;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class EnemyImageManager {

    private HashMap<String, ArrayList<Image>> enemyImages; // Stocke une liste de frames pour chaque ennemi.

    public EnemyImageManager() {
        enemyImages = new HashMap<>();
        loadImages();
    }

    private void loadImages() {
        try {
            System.out.println("🔄 Chargement des images des ennemis...");

            // Chargement des ennemis normaux
            enemyImages.put("skeleton", loadSpriteSheet("src/images/enemies/skeleton.png", 32, 32, 0, 3));
            enemyImages.put("slime_green", loadSpriteSheet("src/images/enemies/slime_green2.png", 32, 32, 0, 1));

            // 🔥 Charger uniquement les frames de saut pour le slime violet (exclut l'explosion)
            enemyImages.put("slime", loadSpriteSheet("src/images/enemies/slime.png", 32, 32, 0, 3));

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
            System.out.println("❌ ERREUR : Le fichier de spritesheet n'existe pas : " + path);
            return frames;
        }

        BufferedImage spriteSheet = ImageIO.read(spriteSheetFile);
        int spriteSheetWidth = spriteSheet.getWidth();
        int framesPerRow = spriteSheetWidth / frameWidth;

        System.out.println("📌 Découpe de " + path + " en frames de " + startFrame + " à " + endFrame);

        // Charger uniquement les frames spécifiées
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
}
