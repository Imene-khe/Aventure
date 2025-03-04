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
        chests.put(block, chestType);
        //System.out.println("Coffre ajouté à : " + block); // Affiche où le coffre est ajouté pour le debbogage
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
}
