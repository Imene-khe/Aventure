package data.item;

import data.map.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class ChestManager {
    private HashMap<Block, String> chests; // Map pour stocker les coffres et leurs positions

    public ChestManager() {
        chests = new HashMap<>();
    }

    // Ajoutez un coffre à un bloc spécifique
    public void addChest(Block block, String chestType) {
        chests.put(block, chestType);
        System.out.println("Coffre ajouté à : " + block); // Affiche où le coffre est ajouté pour le debbogage
    }
 

    // Récupère les coffres (les blocs où il y a des coffres)
    public HashMap<Block, String> getChests() {
        return chests;
    }

    // Méthode pour ouvrir un coffre (le contenu serait géré ici)
    public ArrayList<String> openChest(String chestKey) {
        // Implémentation pour ouvrir le coffre et retourner son contenu
        return new ArrayList<>(); // Placeholder, à implémenter selon la logique du jeu
    }
    
    public static void main(String[] args) {
        ChestManager chestManager = new ChestManager();

        // Création de blocs fictifs pour le test
        Block block1 = new Block(2, 3);
        Block block2 = new Block(5, 7);
        Block block3 = new Block(1, 1);

        // Ajout de coffres à ces blocs
        chestManager.addChest(block1, "golden_chest");
        chestManager.addChest(block2, "wooden_chest");
        chestManager.addChest(block3, "mystic_chest");

        // Affichage des coffres stockés
        System.out.println("Coffres stockés : " + chestManager.getChests());

        // Vérification de l'accès aux coffres
        System.out.println("Nombre de coffres : " + chestManager.getChests().size());
    }


}
