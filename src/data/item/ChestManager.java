package data.item;

import data.map.Block;

import java.util.ArrayList;
import java.util.HashMap;

public class ChestManager {
    private HashMap<Block, String> chests; // Map pour stocker les coffres et leurs positions

    public ChestManager() {
        chests = new HashMap<>();
    }

    // Ajoutez un coffre à un bloc spécifique
    public void addChest(Block block, String chestType) {
        if (chests.containsKey(block)) {
            System.out.println("⚠️ Tentative d'ajout d'un coffre sur un bloc déjà occupé : " + block);
        } else {
            chests.put(block, chestType);
            System.out.println("✅ Coffre ajouté à : " + block + " | Type : " + chestType);
        }
    }

    // Récupère les coffres (les blocs où il y a des coffres)
    public HashMap<Block, String> getChests() {
        System.out.println("🔍 Nombre total de coffres stockés : " + chests.size());
        for (Block block : chests.keySet()) {
            System.out.println("   - Coffre en : " + block + " | Type : " + chests.get(block));
        }
        return chests;
    }

    // Méthode pour ouvrir un coffre (le contenu serait géré ici)
    public ArrayList<String> openChest(String chestKey) {
        System.out.println("📦 Tentative d'ouverture du coffre : " + chestKey);
        return new ArrayList<>(); // Placeholder, à implémenter selon la logique du jeu
    }

    public static void main(String[] args) {
        ChestManager chestManager = new ChestManager();

        // Création de blocs fictifs pour le test
        Block block1 = new Block(2, 3);
        Block block2 = new Block(5, 7);
        Block block3 = new Block(1, 1);
        Block block4 = new Block(2, 3); // Test d'ajout d'un coffre sur un bloc déjà occupé

        // Ajout de coffres à ces blocs
        chestManager.addChest(block1, "golden_chest");
        chestManager.addChest(block2, "wooden_chest");
        chestManager.addChest(block3, "mystic_chest");
        chestManager.addChest(block4, "duplicate_chest"); // Devrait afficher un message d'avertissement

        // Affichage des coffres stockés
        chestManager.getChests();

        // Vérification de l'accès aux coffres
        System.out.println("📊 Nombre final de coffres enregistrés : " + chestManager.getChests().size());
    }
}
