package gui;

import javax.swing.*;

import data.item.Chest;
import data.item.Equipment;
import data.item.EquipmentImageManager;
import data.item.Inventory;

import java.awt.*;
import java.util.ArrayList;

public class ChestUIManager {

    private JFrame chestWindow; // Fenêtre pour afficher le contenu du coffre
    private EquipmentImageManager imageManager; // Gestionnaire d'images des équipements

    public ChestUIManager() {
        // Initialiser le gestionnaire d'images des équipements
        imageManager = new EquipmentImageManager();
    }

    /**
     * Méthode pour afficher le contenu du coffre dans une fenêtre dédiée.
     * @param inventory L'inventaire du coffre.
     */
    public void displayChestContents(Inventory inventory) {
        // Crée une nouvelle fenêtre pour afficher le contenu du coffre
        chestWindow = new JFrame("Contenu du coffre");
        chestWindow.setSize(400, 300); // Taille de la fenêtre
        chestWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crée une ArrayList pour stocker les icônes des équipements
        ArrayList<ImageIcon> itemIcons = new ArrayList<>();

        // Parcours des équipements dans l'inventaire du coffre
        for (int i = 0; i < inventory.size(); i++) {
            Equipment equipment = inventory.getEquipmentAt(i);

            // Obtenir l'image de l'équipement via le gestionnaire d'images
            String equipmentType = equipment.getName().toLowerCase(); // Récupérer le type (par exemple "sword", "shield", etc.)
            Image equipmentImage = imageManager.getEquipmentImage(equipmentType); // Charger l'image de l'équipement

            if (equipmentImage != null) {
                // Redimensionner l'image pour l'adapter à l'interface
                Image resizedImg = equipmentImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Redimensionner l'image
                ImageIcon icon = new ImageIcon(resizedImg); // Créer une nouvelle ImageIcon avec l'image redimensionnée

                // Ajouter l'icône à la liste
                itemIcons.add(icon);
            }
        }

        // Crée un tableau de données avec une seule colonne pour afficher les icônes
        Object[][] data = new Object[itemIcons.size()][1];

        // Remplir les données avec les icônes
        for (int i = 0; i < itemIcons.size(); i++) {
            data[i][0] = itemIcons.get(i);
        }

        // Crée un tableau pour afficher les équipements
        JTable table = new JTable(data, new String[]{});  // Pas d'en-têtes de colonnes
        table.setRowHeight(100);  // Hauteur de chaque ligne
        table.setCellSelectionEnabled(true); // Permet la sélection des cellules

        // Ajoute le tableau dans un panneau avec un défilement
        JScrollPane scrollPane = new JScrollPane(table);
        chestWindow.add(scrollPane);  // Ajoute le panneau avec le tableau à la fenêtre

        // Affiche la fenêtre
        chestWindow.setVisible(true); // Affiche la fenêtre
    }

    /**
     * Ferme la fenêtre du contenu du coffre si elle est ouverte.
     */
    public void closeChestContents() {
        if (chestWindow != null) {
            chestWindow.dispose(); // Ferme la fenêtre
        }
    }

    // Méthode pour afficher le contenu du coffre dans une fenêtre graphique
    public void showChestContents(Inventory inventory) {
        displayChestContents(inventory);
    }

    // === Main interne pour tester la classe ===
    public static void main(String[] args) {
        // Création du coffre et de l'inventaire
        Chest chest = new Chest();
        System.out.println("=== Test du coffre ===");
        System.out.println("Contenu initial du coffre (avant ouverture) : " + chest.getInventory());

        // Ajout d'un nouvel objet au coffre
        chest.addItem(new Equipment("axe"));
        chest.addItem(new Equipment("woodSword"));
        chest.addItem(new Equipment("woodstick"));
        System.out.println("Ajout des objets 'axe', 'woodSword' et 'woodstick'.");

        // Ouverture du coffre
        Inventory loot = chest.open();
        System.out.println("Coffre ouvert. Contenu du coffre : " + loot);

        // Test de ChestUIManager : afficher les objets du coffre
        ChestUIManager uiManager = new ChestUIManager();
        System.out.println("=== Affichage du contenu du coffre ===");
        uiManager.showChestContents(loot); // Affiche les objets dans la fenêtre

        // Tentative de réouverture du coffre (doit retourner un inventaire vide)
        Inventory emptyLoot = chest.open();
        System.out.println("Tentative de réouverture du coffre. Contenu après réouverture : " + emptyLoot);
    }
}
