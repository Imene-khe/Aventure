package data.item;

import data.map.Block;
import data.item.weapon.Axe;
import data.item.weapon.Sword;
import data.item.weapon.Weapon;

import java.util.HashMap;
import java.util.Random;

public class ChestManager {
    private HashMap<Block, Chest> chests; // Stocke les coffres par position

    public ChestManager() {
        chests = new HashMap<>();
    }

    // Ajoute un coffre à un bloc spécifique et y place des armes aléatoires
    public void addChest(Block block, String chestType) {
        if (chests.containsKey(block)) {
            System.out.println("⚠️ Tentative d'ajout d'un coffre sur un bloc déjà occupé : " + block);
        } else {
            // Création d'un nouveau coffre
            Chest newChest = new Chest();
            
            // Ajout d'armes aléatoires au coffre
            //addRandomWeapons(newChest);
            
            // Stockage du coffre et de son type
            chests.put(block, newChest);
            System.out.println("✅ Coffre ajouté à : " + block + " | Type : " + chestType);
        }
    }

    // Ajoute des armes aléatoires au coffre
    public void addRandomWeapons(Chest chest) {
        Weapon[] availableWeapons = {new Sword(), new Axe()};

        Random rand = new Random();
        int numWeapons = rand.nextInt(2) + 1; // 1 ou 2 armes par coffre

        for (int i = 0; i < numWeapons; i++) {
            Weapon randomWeapon = availableWeapons[rand.nextInt(availableWeapons.length)];
            chest.addItem(randomWeapon);
        }
    }

    // Ouvre un coffre et retourne son contenu
    public Inventory openChest(Block block) {
        Chest chest = chests.get(block);
        if (chest != null) {
            return chest.open(); // Récupère et vide le coffre
        }
        return new Inventory(); // Retourne un inventaire vide si pas de coffre
    }

    public HashMap<Block, Chest> getChests() {
        return chests;
    }

    public Chest getChestAt(Block block) {
        return chests.get(block); // Retourne le coffre s'il existe, sinon null
    }

    
    public static void main(String[] args) {
        // Création d'une instance de ChestManager
        ChestManager chestManager = new ChestManager();

        // Création de quelques blocs fictifs pour tester les ajouts
        Block block1 = new Block(2, 3);
        Block block2 = new Block(5, 7);
        Block block3 = new Block(1, 1);
        Block block4 = new Block(2, 3); // Bloc déjà occupé (pour tester le cas d'erreur)

        // Ajout de coffres aux blocs
        chestManager.addChest(block1, "golden_chest");
        chestManager.addChest(block2, "wooden_chest");
        chestManager.addChest(block3, "mystic_chest");
        chestManager.addChest(block4, "duplicate_chest"); // Devrait afficher un message d'avertissement

      
        
        // Affichage des coffres ajoutés
        System.out.println("📦 Liste des coffres ajoutés :");
        for (Block block : chestManager.getChests().keySet()) {
            System.out.println("   - Coffre à " + block);
        }

        // Ouverture de coffres et affichage de leur contenu
        System.out.println("\n🔑 Ouverture des coffres :");
        
        Inventory loot1 = chestManager.openChest(block1);
        System.out.println("Contenu du coffre à " + block1 + ": ");
        loot1.getEquipments().forEach(item -> System.out.println("   - " + item.getName()));

        Inventory loot2 = chestManager.openChest(block2);
        System.out.println("Contenu du coffre à " + block2 + ": ");
        loot2.getEquipments().forEach(item -> System.out.println("   - " + item.getName()));

        Inventory loot3 = chestManager.openChest(block3);
        System.out.println("Contenu du coffre à " + block3 + ": ");
        loot3.getEquipments().forEach(item -> System.out.println("   - " + item.getName()));

        // Tentative d'ouverture d'un coffre déjà ouvert
        Inventory loot4 = chestManager.openChest(block1); // Coffre déjà ouvert
        System.out.println("Tentative de réouverture du coffre à " + block1 + ": " + loot4.size() + " objet(s) (vide)");

        // Vérification après ouverture
        System.out.println("\n📊 État final des coffres :");
        for (Block block : chestManager.getChests().keySet()) {
            Chest chest = chestManager.getChests().get(block);
            System.out.println("   - Coffre à " + block + " | Ouvert : " + chest.isOpened());
        }
    }



}
