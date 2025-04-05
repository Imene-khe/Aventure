package data.item;

import javax.swing.*;
import java.awt.*;

/**
 * Classe InventoryManager - Gère l'affichage de l'inventaire du joueur sous forme de boutons.
 * Cette classe hérite de JPanel et affiche toujours au moins 5 emplacements dans l'inventaire.
 */
public class InventoryManager extends JPanel {

    private static final long serialVersionUID = 1L;
    private Inventory inventory;

    public InventoryManager() {
        this.inventory = new Inventory();
        setLayout(new FlowLayout(FlowLayout.CENTER));
        updateInventoryDisplay();
    }

    public void updateInventoryDisplay() {
        removeAll();

        for (int i = 0; i < 5; i++) {
            JButton button = new JButton("Vide");

            if (i < inventory.size()) {
                Equipment item = inventory.getEquipmentAt(i);
                button.setToolTipText(item.getName());
                button.setText(item.getName());

                ImageIcon itemImage = loadImage(item.getName());
                if (itemImage != null) {
                    Image resized = itemImage.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(resized));
                    button.setHorizontalTextPosition(SwingConstants.RIGHT);
                    button.setIconTextGap(10); // espace entre l'icône et le texte
                    button.setBackground(new Color(60, 63, 65));  // fond gris foncé
                    button.setForeground(Color.WHITE);           // texte blanc
                    button.setFont(new Font("Verdana", Font.BOLD, 12));
                    button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                }
            }

            button.setPreferredSize(new Dimension(120, 40));

            // 🔁 Redonne le focus à la fenêtre principale après clic
            button.addActionListener(e -> {
                gui.MainGUI.getInstance().requestFocusInWindow();
            });

            add(button);
        }

        revalidate();
        repaint();
    }

    private ImageIcon loadImage(String itemName) {
        String path = "src/images/items/" + itemName.toLowerCase() + ".png";
        ImageIcon icon = new ImageIcon(path);
        return icon.getIconWidth() > 0 ? icon : null;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test InventoryManager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 200);

            InventoryManager inventoryManager = new InventoryManager();

            // Ajout manuel de 3 équipements
            inventoryManager.getInventory().addEquipment(new Equipment("woodsword"));
            inventoryManager.getInventory().addEquipment(new Equipment("axe"));
            inventoryManager.getInventory().addEquipment(new Equipment("orbe"));

            // Mise à jour de l'affichage
            inventoryManager.updateInventoryDisplay();

            frame.add(inventoryManager);
            frame.setVisible(true);
        });
    }
}
