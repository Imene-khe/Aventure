package data.item.chest;

import data.item.Equipment;
import data.item.inventory.Inventory;
import data.map.Block;

import java.util.HashMap;

public class ChestManager {
    private HashMap<Block, Chest> chests; // Stocke les coffres par position

    public ChestManager() {
        chests = new HashMap<>();
    }

    // Ajoute un coffre à un bloc spécifique via la factory
    public void addChest(Block block, String chestType) {
        if (chests.containsKey(block)) {
            System.out.println("⚠️ Tentative d'ajout d'un coffre sur un bloc déjà occupé : " + block);
        } else {
            Chest newChest;

            if ("orbe_chest".equalsIgnoreCase(chestType)) {
                // 🔮 Création manuelle d'un coffre avec uniquement l'orbe
                newChest = new Chest();
                newChest.clearItems(); // au cas où
                newChest.addItem(new Equipment("orbe"));
                System.out.println("💠 Orbe ajouté dans le coffre à " + block);
            } else {
                // 🗡️ Création classique via la factory
                newChest = ChestFactory.createChestWithRandomWeapons();
            }

            chests.put(block, newChest);
            System.out.println("✅ Coffre ajouté à : " + block + " | Type : " + chestType);
        }
    }

    // Ouvre un coffre et retourne son contenu
    public Inventory openChest(Block block) {
        Chest chest = chests.get(block);
        if (chest != null) {
            return chest.open(); // Récupère le contenu du coffre
        }
        return new Inventory(); // Retourne un inventaire vide si aucun coffre
    }

    public HashMap<Block, Chest> getChests() {
        return chests;
    }

    public Chest getChestAt(Block block) {
        return chests.get(block); // Retourne le coffre à cette position (ou null)
    }

    // === Main interne de test ===
    public static void main(String[] args) {
        ChestManager chestManager = new ChestManager();

        Block block1 = new Block(2, 3);
        Block block2 = new Block(5, 7);
        Block block3 = new Block(1, 1);
        Block blockOrbe = new Block(0, 0); // 👉 Position du coffre à orbe

        chestManager.addChest(block1, "golden_chest");
        chestManager.addChest(block2, "wooden_chest");
        chestManager.addChest(block3, "mystic_chest");
        chestManager.addChest(blockOrbe, "orbe_chest");

        System.out.println("📦 Liste des coffres ajoutés :");
        for (Block block : chestManager.getChests().keySet()) {
            System.out.println("   - Coffre à " + block);
        }

        System.out.println("\n🔑 Ouverture des coffres :");
        for (Block block : new Block[]{block1, block2, block3, blockOrbe}) {
            Inventory loot = chestManager.openChest(block);
            System.out.println("Contenu du coffre à " + block + ": ");
            loot.getEquipments().forEach(item -> System.out.println("   - " + item.getName()));
        }

        Inventory loot4 = chestManager.openChest(block1); // déjà ouvert
        System.out.println("Tentative de réouverture du coffre à " + block1 + ": " + loot4.size() + " objet(s)");

        System.out.println("\n📊 État final des coffres :");
        for (Block block : chestManager.getChests().keySet()) {
            Chest chest = chestManager.getChests().get(block);
            System.out.println("   - Coffre à " + block + " | Ouvert : " + chest.isOpened());
        }
    }
}
