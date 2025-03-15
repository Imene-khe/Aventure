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
import data.quest.QuestManager;

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // Affichage et gestion de la carte du jeu
    private GameDisplay dashboard;

    // Gestionnaire de l'inventaire
    private InventoryManager inventory;

    // Gestionnaire des quêtes
    private QuestManager questManager;

    // Ajout du bouton d'interaction (dialogue, ouverture...)
    private JButton interactionButton;
    private JButton questButton;
    
    private boolean isInteracting;
    private int coinCount = 0; // Nombre de pièces ramassées
    private JLabel coinLabel;  // Label pour afficher le compteur
    private JLabel questLabel; // Label pour afficher le nombre de quêtes actives

    /**
     * Constructeur de la classe. Il initialise la fenêtre, les composants graphiques
     * et les actions du clavier.
     */
    public MainGUI() {
        super("Aventure - Déplacement du Héros");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        this.dashboard = new GameDisplay();
        this.inventory = new InventoryManager();
        this.questManager = dashboard.getQuestManager(); // Récupérer le QuestManager de GameDisplay
        isInteracting = false;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dashboard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inventory, BorderLayout.CENTER);

        // ✅ Bouton d'interaction avec les coffres
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

        // ✅ Bouton pour afficher les quêtes en cours
        questButton = new JButton("📜 Voir les quêtes");
        questButton.addActionListener(e -> questManager.displayQuests());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(interactionButton);
        buttonPanel.add(questButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                moveHero(e.getKeyCode());
            }
        });

        // ✅ Ajout du compteur de pièces
        coinLabel = new JLabel("💰 Pièces : " + coinCount);
        JPanel coinPanel = new JPanel();
        coinPanel.add(coinLabel);

        // ✅ Ajout du compteur de quêtes
        questLabel = new JLabel("📜 Quêtes : " + questManager.getActiveQuests().size());
        JPanel questPanel = new JPanel();
        questPanel.add(questLabel);

        // ✅ Placement des compteurs en haut de l'interface
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(coinPanel);
        topPanel.add(questPanel);

        // Ajouter les compteurs à l'interface
        mainPanel.add(topPanel, BorderLayout.NORTH);

        setFocusable(true);
        setVisible(true);
    }

    /**
     * Incrémente le compteur de pièces et met à jour l'affichage.
     */
    public void incrementCoinCount() {
        coinCount++;
        coinLabel.setText("💰 Pièces : " + coinCount);
    }

    /**
     * Met à jour l'affichage du nombre de quêtes en cours.
     */
    public void updateQuestDisplay() {
        questLabel.setText("📜 Quêtes : " + questManager.getActiveQuests().size());
    }

    public int getCoinCount() {
        return coinCount;
    }

    /**
     * Déplace le héros en fonction de la touche pressée.
     * @param keyCode Le code de la touche pressée
     */
    public void moveHero(int keyCode) {
        Block currentPos = dashboard.getHero().getPosition();
        Block newPos = currentPos;

        if (keyCode == KeyEvent.VK_LEFT && currentPos.getColumn() > 0) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() - 1);
        } else if (keyCode == KeyEvent.VK_RIGHT && currentPos.getColumn() < dashboard.getMap().getColumnCount() - 1) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() + 1);
        } else if (keyCode == KeyEvent.VK_UP && currentPos.getLine() > 0) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() - 1, currentPos.getColumn());
        } else if (keyCode == KeyEvent.VK_DOWN && currentPos.getLine() < dashboard.getMap().getLineCount() - 1) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() + 1, currentPos.getColumn());
        }

        if (!dashboard.getMap().isBlocked(newPos)) {
            dashboard.moveHero(newPos, this);
            updateQuestDisplay(); // ✅ Met à jour l'affichage des quêtes après un déplacement
            System.out.println("🚶 Héros déplacé à : " + newPos.getLine() + ", " + newPos.getColumn());
        } else {
            System.out.println("❌ Déplacement bloqué !");
        }
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
