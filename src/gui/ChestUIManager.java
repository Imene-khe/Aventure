package gui;

import javax.swing.*;

import data.item.Equipment;
import data.item.EquipmentImageManager;
import data.item.chest.Chest;
import data.item.inventory.Inventory;

import java.awt.*;

public class ChestUIManager {
    private JFrame chestWindow;
    private EquipmentImageManager imageManager;
    private MainGUI mainGUI;
    private Runnable onOrbTakenCallback;

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

        for (Equipment item : chestInventory.getEquipments()) {
            if ("orbe".equalsIgnoreCase(item.getName())) {
                mainGUI.getQuestManager().updateQuest("Trouver l’orbe", 1);
                break;
            }
        }

        if (chestInventory.size() == 0) {
            JOptionPane.showMessageDialog(chestWindow, "Le coffre est vide !");
            chestWindow.dispose();
            mainGUI.requestFocusInWindow();
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
                    mainGUI.getInventoryManager().getInventory().addEquipment(equipment);
                    mainGUI.getInventoryManager().updateInventoryDisplay();
                    chestInventory.getEquipments().remove(equipment);
                    addButton.setEnabled(false);
                    JOptionPane.showMessageDialog(chestWindow, equipment.getName() + " ajouté à l’inventaire !");
                    mainGUI.requestFocusInWindow();
                    mainGUI.requestFocusOnGame(); 


                    if ("orbe".equalsIgnoreCase(equipment.getName())) {
                        int result = JOptionPane.showConfirmDialog(
                            chestWindow,
                            "💠 Tu as trouvé l'orbe sacré !\nSouhaites-tu poursuivre l'aventure ?",
                            "Nouvelle étape",
                            JOptionPane.YES_NO_OPTION
                        );

                        if (result == JOptionPane.YES_OPTION) {
                            chestWindow.dispose();
                            mainGUI.requestFocusOnGame();
                            
                            if (onOrbTakenCallback != null) {
                                onOrbTakenCallback.run();
                            }

                        }
                    }


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
    
    public void setOnOrbTakenCallback(Runnable callback) {
        this.onOrbTakenCallback = callback;
    }
}
