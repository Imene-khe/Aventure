package gui;

import javax.swing.*;
import data.item.Chest;
import data.item.Equipment;
import data.item.EquipmentImageManager;
import data.item.Inventory;
import java.awt.*;

public class ChestUIManager {
    private JFrame chestWindow;
    private EquipmentImageManager imageManager;
    private MainGUI mainGUI; // Référence à MainGUI pour modifier l'inventaire

    public ChestUIManager(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.imageManager = new EquipmentImageManager();
    }

    public void displayChestContents(Chest chest) {
        chestWindow = new JFrame("Contenu du coffre");
        chestWindow.setSize(500, 400);
        chestWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chestWindow.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        Inventory chestInventory = chest.getInventory();

        if (chestInventory.size() == 0) {
            JOptionPane.showMessageDialog(chestWindow, "Le coffre est vide !");
            chestWindow.dispose();
            mainGUI.requestFocusInWindow(); // ✅ Redonner le focus après la fermeture
            return;
        }

        int row = 0, col = 0;
        for (Equipment equipment : chestInventory.getEquipments()) {
            String equipmentType = equipment.getName().toLowerCase();
            Image equipmentImage = imageManager.getEquipmentImage(equipmentType);

            if (equipmentImage != null) {
                Image resizedImg = equipmentImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(resizedImg);

                JLabel label = new JLabel(icon);
                label.setToolTipText(equipment.getName());

                JButton addButton = new JButton("Ajouter");
                addButton.addActionListener(e -> {
                    // ✅ Ajout à l'inventaire
                    mainGUI.getInventoryManager().getInventory().addEquipment(equipment);
                    
                    // ✅ Mise à jour de l'affichage de l'inventaire graphique
                    mainGUI.getInventoryManager().updateInventoryDisplay();

                    // ✅ Retrait de l'objet du coffre
                    chestInventory.getEquipments().remove(equipment);
                    
                    // ✅ Désactiver le bouton après l'ajout
                    addButton.setEnabled(false);
                    
                    JOptionPane.showMessageDialog(chestWindow, equipment.getName() + " ajouté à l’inventaire !");
                    
                    // ✅ Redonner le focus après action
                    mainGUI.requestFocusInWindow();
                });

                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.add(label, BorderLayout.CENTER);
                itemPanel.add(addButton, BorderLayout.SOUTH);

                constraints.gridx = col;
                constraints.gridy = row;
                panel.add(itemPanel, constraints);

                col++;
                if (col > 2) {
                    col = 0;
                    row++;
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        chestWindow.add(scrollPane);
        chestWindow.setVisible(true);
    }
}
