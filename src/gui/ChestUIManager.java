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

    public void displayChestContents(Chest chest) {
        System.out.println("[LOG] Affichage du contenu du coffre...");
        
        chestWindow = new JFrame("Contenu du coffre");
        chestWindow.setSize(500, 400);
        chestWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chestWindow.setLocationRelativeTo(null); // Centrer la fenêtre

        // Panel principal avec un Layout GridBagLayout pour plus de flexibilité
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);  // Fond gris clair

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);  // Espacement entre les icônes

        ArrayList<Equipment> equipmentList = new ArrayList<>();

        Inventory inventory = chest.getInventory();  // On récupère l'inventaire du coffre via la méthode du coffre
        System.out.println("[LOG] Inventaire du coffre contient " + inventory.size() + " éléments.");

        // Récupération des équipements et des icônes
        for (int i = 0; i < inventory.size(); i++) {
            Equipment equipment = inventory.getEquipmentAt(i);
           
            System.out.println("[LOG] Récupération de l'équipement : " + equipment.getName());

            String equipmentType = equipment.getName().toLowerCase();
            Image equipmentImage = imageManager.getEquipmentImage(equipmentType);
            

            if (equipmentImage != null) {
                System.out.println("[LOG] Image trouvée pour " + equipmentType);
                equipmentList.add(equipment);
            } else {
                System.out.println("[LOG] Aucune image trouvée pour " + equipmentType);
            }
        }

        // Si le coffre est vide, affichage d'un message
        if (equipmentList.isEmpty()) {
            System.out.println("[LOG] Le coffre est vide !");
            JOptionPane.showMessageDialog(chestWindow, "Le coffre est vide !");
            return;
        }

        // Affichage des icônes et des boutons dans le panneau
        int row = 0, col = 0;
        for (Equipment equipment : equipmentList) {
            String equipmentType = equipment.getName().toLowerCase();
            Image equipmentImage = imageManager.getEquipmentImage(equipmentType);
            Image resizedImg = equipmentImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);  // Taille ajustée
            ImageIcon icon = new ImageIcon(resizedImg);

            // Label pour afficher l'image
            JLabel label = new JLabel(icon);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Bordure noire autour des images
            label.setToolTipText("Cliquez pour ajouter à l'inventaire");

            // Bouton "Ajouter"
            JButton addButton = new JButton("Ajouter");
            addButton.addActionListener(e -> {
                System.out.println("[LOG] L'équipement " + equipment.getName() + " ajouté à l'inventaire.");
                // Ajout de l'équipement à l'inventaire du héros
                // Vous devez ici faire appel à la méthode d'ajout d'équipement dans l'inventaire du héros
            });

            // Ajouter l'image et le bouton sous forme de GridBagLayout
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BorderLayout());
            itemPanel.add(label, BorderLayout.CENTER);
            itemPanel.add(addButton, BorderLayout.SOUTH);  // Le bouton sous l'image

            constraints.gridx = col;
            constraints.gridy = row;
            panel.add(itemPanel, constraints);

            col++;
            if (col > 2) { // Limite à 3 icônes par ligne
                col = 0;
                row++;
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        chestWindow.add(scrollPane);
        chestWindow.setVisible(true);
        System.out.println("[LOG] Fenêtre du coffre affichée avec succès !");
    }


    public static void main(String[] args) {
        System.out.println("[LOG] Début du test du ChestUIManager...");

        // Test de la classe avec des éléments
        Chest chest = new Chest();
        System.out.println("[LOG] Création d'un coffre...");
       
        System.out.println("[LOG] Contenu du coffre : " + chest.getInventory().size());


        System.out.println("[LOG] Coffre ouvert. Contenu récupéré.");

        // Affichage du contenu du coffre
        ChestUIManager uiManager = new ChestUIManager();
        uiManager.displayChestContents(chest);

        System.out.println("[LOG] Fin du test.");
    }
}
