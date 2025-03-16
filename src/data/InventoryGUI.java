package data;

import javax.swing.*;
import data.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryGUI extends JPanel {
    private static final long serialVersionUID = 1L;
    private Inventory inventory;

    public InventoryGUI() {
        this.inventory = new Inventory();

        // Définir le layout du panel pour afficher les éléments horizontalement
        setLayout(new FlowLayout(FlowLayout.CENTER)); // Centrer l'inventaire dans le panneau

        // Mettre à jour l'affichage des éléments de l'inventaire
        updateInventoryDisplay();
    }

    // Méthode pour mettre à jour l'affichage des éléments dans l'inventaire
    public void updateInventoryDisplay() {
        removeAll(); // Réinitialiser le panneau

        // Ajouter les éléments de l'inventaire au panneau
        for (int i = 0; i < 4; i++) {
            Equipment item = inventory.getEquipmentAt(i);
            JButton button = new JButton();
            if (item != null) {
                button.setToolTipText(item.getName()); // Afficher le nom de l'objet en info-bulle
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Vous avez sélectionné: " + item.getName());
                    }
                });
            } else {
                button.setText("Vide"); // Si aucune case n'est remplie
            }
            add(button); // Ajouter le bouton au panneau
        }

        revalidate(); // Revalider le panneau pour qu'il se réaffiche correctement
        repaint();    // Redessiner le panneau
    }
}
