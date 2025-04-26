package data.item.inventory;

import javax.swing.*;

import data.item.Equipment;

import java.awt.*;

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
                    button.setIconTextGap(10); 
                    button.setBackground(new Color(60, 63, 65));  
                    button.setForeground(Color.WHITE);          
                    button.setFont(new Font("Verdana", Font.BOLD, 12));
                    button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                }
            }

            button.setPreferredSize(new Dimension(120, 40));
            button.addActionListener(e -> {
                gui.MainGUI.getInstance().requestFocusInWindow();
            });

            add(button);
        }

        revalidate();
        repaint();
    }

    public ImageIcon loadImage(String itemName) {
        String path = "src/images/items/" + itemName.toLowerCase() + ".png";
        ImageIcon icon = new ImageIcon(path);
        return icon.getIconWidth() > 0 ? icon : null;
    }

    public Inventory getInventory() {
        return inventory;
    }

    
}
