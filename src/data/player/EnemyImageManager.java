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

            // Ennemis existants
            enemyImages.put("skeleton", loadSpriteSheet("src/images/enemies/Skeleton.png", 32, 32, 0, 3));
            enemyImages.put("slime_green", loadSpriteSheet("src/images/enemies/slime_green2.png", 32, 32, 0, 1));
            enemyImages.put("slime", loadSpriteSheet("src/images/enemies/slime.png", 32, 32, 0, 3));

            // Ajout des types small, medium, large (⚠ nom exact des fichiers)
            enemyImages.put("small", loadSpriteSheet("src/images/enemies/SmallSlime_Green.png", 32, 32, 0, 0));
            enemyImages.put("medium", loadSpriteSheet("src/images/enemies/MediumSlime_Orange.png", 32, 32, 0, 0));
            enemyImages.put("large", loadSpriteSheet("src/images/enemies/LargeSlime_Purple.png", 32, 32, 0, 0));

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
    
    public Image getEnemyImage(String enemyType, int frameIndex) {
        ArrayList<Image> frames = enemyImages.get(enemyType);

        if (frames == null || frames.isEmpty()) {
            System.out.println("⚠ Avertissement : Aucune image trouvée pour l'ennemi '" + enemyType + "'");
            return null; // Retourne `null` si l'ennemi n'a pas d'image chargée
        }

        // Assurer que l'index demandé est valide
        return frames.get(frameIndex % frames.size()); // Utilisation de `%` pour boucler en cas de dépassement
    }

}
