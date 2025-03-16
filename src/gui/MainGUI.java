package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import data.item.Inventory;
import data.item.InventoryManager;
import data.map.Block;
import data.player.Hero;
import gui.*;

/**
 * Classe principale de l'interface utilisateur du jeu.
 */
public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static MainGUI instance;

    private GameDisplay dashboard;
    private InventoryManager inventoryManager;
    private Inventory inventory;

    private JPanel bottomPanel;
    private JPanel inventoryPanel;
    private JLabel coinLabel;
    private int coinCount = 0;

    /**
     * Constructeur principal de l'interface.
     */
    public MainGUI() {
        super("Aventure - Déplacement du Héros");

        instance = this;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());

        // ✅ Instanciation correcte de GameDisplay
        this.dashboard = new GameDisplay();
        this.inventoryManager = new InventoryManager();
        this.inventory = inventoryManager.getInventory(); 

        add(dashboard, BorderLayout.CENTER);

        // 🔹 Barre du haut avec le compteur de pièces
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        coinLabel = new JLabel("💰 Pièces : " + coinCount);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coinLabel.setForeground(Color.WHITE);
        topPanel.add(coinLabel);
        add(topPanel, BorderLayout.NORTH);

        // 🔹 Panneau inférieur avec les boutons d'action
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(800, 60));
        bottomPanel.setBackground(new Color(80, 80, 80));

        JButton inventoryButton = new JButton("📦 Inventaire");
        inventoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryButton.setPreferredSize(new Dimension(150, 40));
        inventoryButton.addActionListener(e -> openInventory());

        JButton interactButton = new JButton("💬 Interagir");
        interactButton.setFont(new Font("Arial", Font.BOLD, 16));
        interactButton.setPreferredSize(new Dimension(150, 40));
        interactButton.addActionListener(e -> interactWithNPC());

        bottomPanel.add(inventoryButton);
        bottomPanel.add(interactButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // 🔹 Panneau d'affichage de l'inventaire
        inventoryPanel = new JPanel();
        inventoryPanel.setBackground(new Color(50, 50, 50));
        inventoryPanel.setPreferredSize(new Dimension(1000, 80));
        inventoryPanel.add(new JLabel("🔹 Inventaire :"));
        add(inventoryPanel, BorderLayout.SOUTH);

        setFocusable(true);
        setVisible(true);
        requestFocusInWindow();
    }

    public static MainGUI getInstance() {
        return instance;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void updateInventoryDisplay() {
        inventoryPanel.removeAll();
        inventoryPanel.add(new JLabel("🔹 Inventaire :"));
        for (int i = 0; i < inventory.getEquipments().size(); i++) {
            inventoryPanel.add(new JLabel("⚔ " + inventory.getEquipments().get(i).getName()));
        }
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    public void openInventory() {
        JOptionPane.showMessageDialog(this, "📦 Inventaire ouvert !");
        updateInventoryDisplay();
    }

    public void interactWithNPC() {
        JOptionPane.showMessageDialog(this, "💬 Interaction avec un PNJ !");
    }

    /**
     * Méthode principale qui lance le jeu en affichant l'écran de démarrage avant le jeu.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StartScreen(); // ✅ Correction : Vérifie bien que StartScreen existe et est bien codé
        });
    }
}

