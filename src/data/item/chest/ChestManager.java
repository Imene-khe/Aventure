package data.item.chest;

import org.apache.log4j.Logger;
import log.LoggerUtility;



import data.item.inventory.Inventory;
import data.map.Block;

import java.util.HashMap;

public class ChestManager {

    private static final Logger logger = LoggerUtility.getLogger(ChestManager.class, "text");
    private final HashMap<Block, Chest> chests;

    public ChestManager() {
        this.chests = new HashMap<>();
    }

    public void addChest(Block block, String chestType) {
        if (chests.containsKey(block)) {
           logger.info("⚠️ Tentative d'ajout d'un coffre sur un bloc déjà occupé : " + block);
            return;
        }

        Chest newChest;
        if ("orbe_chest".equalsIgnoreCase(chestType)) {
            newChest = ChestFactory.createChestWithOrbe();
        } else {
            newChest = ChestFactory.createChestWithRandomWeapons();
        }

        chests.put(block, newChest);
        logger.info("✅ Coffre ajouté à : " + block + " | Type : " + chestType);
    }

    public Inventory openChest(Block block) {
        Chest chest = chests.get(block);
        if (chest != null) {
            chest.openChest();
            return chest.getInventory();
        }
        return new Inventory();
    }

    public HashMap<Block, Chest> getChests() {
        return chests;
    }

    public Chest getChestAt(Block block) {
        return chests.get(block);
    }
}
