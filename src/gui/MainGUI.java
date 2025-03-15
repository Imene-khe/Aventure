package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.item.Chest;
import data.item.Inventory;
import data.item.InventoryManager;
import data.map.Block;
import data.player.Hero;

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private static MainGUI instance; // Stocke l'instance pour accéder à getGameDisplay()

    // Affichage et gestion de la carte du jeu
    private GameDisplay dashboard;

    // Gestionnaire de l'inventaire
    private InventoryManager inventory;

    // Ajout du bouton d'interaction (dialogue, ouverture...)
    private JButton interactionButton;
    private boolean isInteracting;
    private int coinCount = 0; // Nombre de pièces ramassées
    private JLabel coinLabel;  // Label pour afficher le compteur

    /**
     * Constructeur de la classe. Il initialise la fenêtre, les composants graphiques
     * et les actions du clavier.
     */
    public MainGUI() {
        super("Aventure - Déplacement du Héros");

        instance = this; // Stocker l'instance actuelle pour l'accès statique

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        this.dashboard = new GameDisplay();
        this.inventory = new InventoryManager();
        isInteracting = false;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dashboard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inventory, BorderLayout.CENTER);

        interactionButton = new JButton("Interagir");
        interactionButton.addActionListener(e -> {
            if (isInteracting) return;

            isInteracting = true;
            Chest chest = dashboard.openNearbyChest();

            if (chest != null) {
                ChestUIManager chestUIManager = new ChestUIManager(this);
                chestUIManager.displayChestContents(chest);
            } else {
                JOptionPane.showMessageDialog(this, "Aucun coffre à proximité !");
            }

            isInteracting = false;
            requestFocusInWindow();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(interactionButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                moveHero(e.getKeyCode());
            }
        });

        coinLabel = new JLabel("💰 Pièces : " + coinCount);
        JPanel coinPanel = new JPanel();
        coinPanel.add(coinLabel);

        // Placer le compteur en haut à gauche
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(coinPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        setFocusable(true);
        setVisible(true);
        requestFocusInWindow(); // ✅ S'assure que le KeyListener reste actif
    }

    public static GameDisplay getGameDisplay() {
        return instance != null ? instance.dashboard : null;
    }

    public void incrementCoinCount() {
        coinCount++;
        coinLabel.setText("💰 Pièces : " + coinCount);
    }

    public int getCoinCount() {
        return coinCount;
    }

    /**
     * Déplace le héros en fonction de la touche pressée.
     * @param keyCode Le code de la touche pressée
     */
    public void moveHero(int keyCode) {
        Hero hero = dashboard.getHero();
        Block currentPos = hero.getPosition();
        Block newPos = currentPos;

        if (keyCode == KeyEvent.VK_LEFT) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() - 1);
            if (!dashboard.getMap().isBlocked(newPos)) {
                hero.moveLeft();
                hero.setPosition(newPos);
            }
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() + 1);
            if (!dashboard.getMap().isBlocked(newPos)) {
                hero.moveRight();
                hero.setPosition(newPos);
            }
        } else if (keyCode == KeyEvent.VK_UP) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() - 1, currentPos.getColumn());
            if (!dashboard.getMap().isBlocked(newPos)) {
                hero.moveUp();
                hero.setPosition(newPos);
            }
        } else if (keyCode == KeyEvent.VK_DOWN) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() + 1, currentPos.getColumn());
            if (!dashboard.getMap().isBlocked(newPos)) {
                hero.moveDown();
                hero.setPosition(newPos);
            }
        }

        // ✅ Vérifier si le héros marche sur une pièce
        dashboard.checkHeroCoinCollision(this);

        // 🔄 Rafraîchir l'affichage après le mouvement
        dashboard.repaint();
    }


    public Inventory getInventory() {
        return inventory.getInventory();
    }

    public InventoryManager getInventoryManager() {
        return inventory;
    }

    /**
     * Méthode principale pour démarrer l'application et afficher l'interface graphique.
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}

