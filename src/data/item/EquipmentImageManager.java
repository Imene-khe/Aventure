package data.item;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class EquipmentImageManager {

    private HashMap<String, Image> equipmentImages; // Stocke une image par type d'équipement

    public EquipmentImageManager() {
        equipmentImages = new HashMap<>();
        loadImages();
    }

    private void loadImages() {
        try {
            System.out.println("Chargement des images des équipements...");
            
            // Chargement des images d'équipement
            equipmentImages.put("axe", loadImage("src/images/items/axe.png"));
            equipmentImages.put("woodsword", loadImage("src/images/items/woodsword.png"));
            equipmentImages.put("woodstick", loadImage("src/images/items/woodstick.png"));
            
            System.out.println("✅ Images des équipements chargées !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR lors du chargement des images des équipements !");
            e.printStackTrace();
        }
    }

    private Image loadImage(String path) throws IOException {
        File imageFile = new File(path);
        Image image = ImageIO.read(imageFile);

        if (image == null) {
            System.out.println("❌ ERREUR : Impossible de charger l'image depuis : " + path);
        }

        return image;
    }

    public Image getEquipmentImage(String equipmentType) {
        return equipmentImages.get(equipmentType);
    }

    public static void main(String[] args) {
        // Création du gestionnaire d'images d'équipement
        EquipmentImageManager manager = new EquipmentImageManager();

        // Test de récupération d'images
        System.out.println("✅ Image trouvée pour : axe -> " + (manager.getEquipmentImage("axe") != null));
        System.out.println("✅ Image trouvée pour : woodsword -> " + (manager.getEquipmentImage("woodsword") != null));
        System.out.println("✅ Image trouvée pour : woodstick -> " + (manager.getEquipmentImage("woodstick") != null));
    }
}
