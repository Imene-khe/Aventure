package data.item.chest;

import java.util.Random;

import data.item.Equipment;
import data.item.inventory.Inventory;

public class Chest {

    private Inventory inventory; // Inventaire du coffre
    private boolean opened; // Indique si le coffre a été ouvert

    // Tableau d'objets Equipment
    private static final Equipment[] possibleItems = {
        new Equipment("axe"),
        new Equipment("woodsword"),
        new Equipment("woodstick"),
    };
    
    public Chest() {
        this.inventory = new Inventory(); // Création d'un inventaire pour stocker les objets
        this.opened = false;
        fillChestWithRandomItems(); // Remplir le coffre avec des objets aléatoires
    }

    /**
     * Remplir le coffre avec des objets aléatoires.
     */
    private void fillChestWithRandomItems() {
        Random random = new Random();
        int numberOfItems = random.nextInt(3) + 1;

        for (int i = 0; i < numberOfItems; i++) {
            Equipment item = new Equipment(possibleItems[random.nextInt(possibleItems.length)].getName());
            addItem(item);
        }
    }



    /**
     * Ouvre le coffre et retourne son contenu sous forme d'inventaire.
     * @return L'inventaire du coffre si ce n'est pas déjà fait, sinon un inventaire vide.
     */
    public Inventory open() {
        if (!opened) {
            opened = true;
            return inventory; // Retourne le contenu sans vider le coffre
        }
        return inventory; // Retourne le même inventaire à chaque ouverture
    }


    /**
     * Vérifie si le coffre a été ouvert.
     * @return true si le coffre a été ouvert, sinon false.
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Ajoute un équipement au coffre.
     * @param item L'équipement à ajouter au coffre.
     */
    public void addItem(Equipment item) {
    	
    	if (item != null) {
            inventory.getEquipments().add(item);
            System.out.println("[LOG] Ajouté au coffre : " + item.getName());
        } else {
            System.out.println("[LOG] Tentative d'ajout d'équipement nul.");
        }
    }

    /**
     * Retourne l'inventaire du coffre.
     * @return L'inventaire du coffre.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Vide le coffre.
     */
    public void clearItems() {
        inventory = new Inventory(); // Réinitialise l'inventaire du coffre
    }

 // === Main interne pour tester la classe ===
    public static void main(String[] args) {
        System.out.println("=== Test de la classe Chest ===");

        // Création d'un coffre
        Chest chest = new Chest();
        System.out.println("Coffre créé avec les objets :");
        if (chest.getInventory().size() > 0) {
            for (Equipment item : chest.getInventory().getEquipments()) {
                System.out.println("  - " + item.getName());
            }
        } else {
            System.out.println("  Le coffre est vide.");
        }

       

        // Vérification de l'état du coffre
        System.out.println("\nLe coffre est-il ouvert ? " + chest.isOpened());

        // Ouverture du coffre
        Inventory loot = chest.open();
        System.out.println("\nContenu du coffre lors de l'ouverture :");
        if (loot.size() > 0) {
            for (Equipment item : loot.getEquipments()) {
                System.out.println("  - " + item.getName());
            }
        } else {
            System.out.println("  L'inventaire est vide.");
        }

        // Vérification après ouverture
        System.out.println("\nLe coffre est-il ouvert ? " + chest.isOpened());
        System.out.println("Contenu du coffre après ouverture :");
        if (chest.getInventory().size() > 0) {
            for (Equipment item : chest.getInventory().getEquipments()) {
                System.out.println("  - " + item.getName());
            }
        } else {
            System.out.println("  Le coffre est vide.");
        }

        // Tentative de réouverture d'un coffre déjà ouvert
        Inventory emptyLoot = chest.open();
        System.out.println("\nTentative de réouverture :");
        if (emptyLoot.size() > 0) {
            for (Equipment item : emptyLoot.getEquipments()) {
                System.out.println("  - " + item.getName());
            }
        } else {
            System.out.println("  L'inventaire est vide.");
        }

        // Nettoyage du coffre
        chest.clearItems();
        System.out.println("\nContenu du coffre après nettoyage :");
        if (chest.getInventory().size() > 0) {
            for (Equipment item : chest.getInventory().getEquipments()) {
                System.out.println("  - " + item.getName());
            }
        } else {
            System.out.println("  Le coffre est vide.");
        }
    }



}
