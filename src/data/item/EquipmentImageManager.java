package data.item;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import log.LoggerUtility;

public class EquipmentImageManager {

    private HashMap<String, Image> equipmentImages; 
    private static final Logger logger = LoggerUtility.getLogger(EquipmentImageManager.class, "text");

    public EquipmentImageManager() {
        equipmentImages = new HashMap<>();
        loadImages();
    }

    private void loadImages() {
        try {
        	logger.info("Chargement des images des équipements...");            
            equipmentImages.put("axe", loadImage("src/images/items/axe.png"));
            equipmentImages.put("woodsword", loadImage("src/images/items/woodsword.png"));
            equipmentImages.put("woodstick", loadImage("src/images/items/woodstick.png"));
            equipmentImages.put("orbe", loadImage("src/images/items/orbe.png"));            
            logger.info("Images des équipements chargées !");        } 
        catch (Exception e) {
        	logger.error("ERREUR lors du chargement des images des équipements !");
        	e.printStackTrace();
        }
    }

    private Image loadImage(String path) {
        try {
            File imageFile = new File(path);
            if (!imageFile.exists()) {
            	logger.warn("ERREUR : Fichier introuvable : " + path);
            	return null;
            }
            return ImageIO.read(imageFile);
        } catch (IOException e) {
        	logger.error("ERREUR : Impossible de charger l'image : " + path);
        	return null;
        }
    }


    public Image getEquipmentImage(String equipmentType) {
        return equipmentImages.get(equipmentType);
    }

}
