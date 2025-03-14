package data.item;

import javax.swing.*;
import java.awt.*;

/**
 * Classe InventoryManager - Gère l'affichage de l'inventaire du joueur sous forme de boutons.
 * Cette classe hérite de JPanel et affiche toujours au moins 5 emplacements dans l'inventaire.
 */
public class InventoryManager extends JPanel {

    private static final long serialVersionUID = 1L; // Identifiant de version pour la sérialisation
    private Inventory inventory; // Inventaire du joueur

    /**
     * Constructeur par défaut de InventoryManager.
     * Initialise un inventaire vide et configure l'affichage.
     */
    public InventoryManager() {
        this.inventory = new Inventory(); // Initialisation de l'inventaire
        setLayout(new FlowLayout(FlowLayout.CENTER)); // Centrer les éléments dans le panneau
        updateInventoryDisplay(); // Met à jour l'affichage initial de l'inventaire
    }

    /**
     * Met à jour l'affichage de l'inventaire.
     * Affiche les objets de l'inventaire sous forme de boutons et complète avec "Vide" si nécessaire.
     */
    public void updateInventoryDisplay() {
        removeAll(); // Supprime les éléments existants pour rafraîchir l'affichage

        for (int i = 0; i < 5; i++) { // Toujours 5 emplacements visibles
            JButton button = new JButton("Vide"); // Bouton par défaut

            if (i < inventory.size()) { // Si un objet est présent
                Equipment item = inventory.getEquipmentAt(i);
                button.setToolTipText(item.getName());
                button.setText(item.getName());

                ImageIcon itemImage = loadImage(item.getName());
                if (itemImage != null) {
                    button.setIcon(itemImage);
                }
            }

            add(button); // Ajoute le bouton mis à jour
        }

        revalidate();
        repaint();
    }



    /**
     * Charge une image d'équipement à partir du répertoire des ressources.
     * @param itemName Nom de l'objet dont l'image doit être chargée.
     * @return Une ImageIcon si l'image est trouvée, sinon null.
     */
    private ImageIcon loadImage(String itemName) {
        String path = "src/images/items/" + itemName.toLowerCase() + ".png"; // Chemin de l'image
        ImageIcon icon = new ImageIcon(path);
        return icon.getIconWidth() > 0 ? icon : null; // Vérifie si l'image est valide
    }

	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return inventory;
	}
}
