package gui;

import javax.swing.*;

import data.item.Chest;
import data.item.Equipment;
import data.item.EquipmentImageManager;
import data.item.Inventory;

import java.awt.*;
import java.util.ArrayList;

public class ChestUIManager {
    private JFrame chestWindow;
    private EquipmentImageManager imageManager;

    public ChestUIManager() {
        System.out.println("[LOG] Initialisation de ChestUIManager...");
        imageManager = new EquipmentImageManager();
    }

    public void displayChestContents(Inventory inventory) {
        System.out.println("[LOG] Affichage du contenu du coffre...");
        
        chestWindow = new JFrame("Contenu du coffre");
        chestWindow.setSize(400, 300);
        chestWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3, 10, 10));

        ArrayList<ImageIcon> itemIcons = new ArrayList<>();

        System.out.println("[LOG] Nombre d'éléments dans le coffre : " + inventory.size());

        for (int i = 0; i < inventory.size(); i++) {
            Equipment equipment = inventory.getEquipmentAt(i);
            System.out.println("[LOG] Récupération de l'équipement : " + equipment.getName());

            String equipmentType = equipment.getName().toLowerCase();
            Image equipmentImage = imageManager.getEquipmentImage(equipmentType);

            if (equipmentImage != null) {
                System.out.println("[LOG] Image trouvée pour " + equipmentType);
                Image resizedImg = equipmentImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(resizedImg);
                itemIcons.add(icon);
            } else {
                System.out.println("[LOG] Aucune image trouvée pour " + equipmentType);
            }
        }

        if (itemIcons.isEmpty()) {
            System.out.println("[LOG] Le coffre est vide !");
            JOptionPane.showMessageDialog(chestWindow, "Le coffre est vide !");
            return;
        }

        for (ImageIcon icon : itemIcons) {
            JLabel label = new JLabel(icon);
            label.setHorizontalAlignment(JLabel.CENTER);
            panel.add(label);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        chestWindow.add(scrollPane);
        chestWindow.setVisible(true);
        System.out.println("[LOG] Fenêtre du coffre affichée avec succès !");
    }

    public static void main(String[] args) {
        System.out.println("[LOG] Début du test du ChestUIManager...");

        Chest chest = new Chest();
        System.out.println("[LOG] Création d'un coffre...");

        chest.addItem(new Equipment("axe"));
        System.out.println("[LOG] Ajout de 'axe' au coffre.");

        chest.addItem(new Equipment("woodsword"));
        System.out.println("[LOG] Ajout de 'woodSword' au coffre.");
        
        chest.addItem(new Equipment("woodstick"));
        System.out.println("[LOG] Ajout de 'woodSword' au coffre.");

        Inventory loot = chest.open();
        System.out.println("[LOG] Coffre ouvert. Contenu récupéré.");

        ChestUIManager uiManager = new ChestUIManager();
        uiManager.displayChestContents(loot);

        System.out.println("[LOG] Fin du test.");
    }
}
