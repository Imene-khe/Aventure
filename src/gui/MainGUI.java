package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import data.item.Chest;
import data.item.InventoryManager;
import data.map.Block;
import data.player.Hero;

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static MainGUI instance;

    private GameDisplay dashboard;
    private InventoryManager inventory;

    // ✅ Panneau de narration
    private JPanel sidePanel;
    private JLabel characterImage;
    private JPanel dialoguePanel; // Panneau contenant plusieurs bulles de dialogue
    private String[] dialogues = {
            "Bienvenue, Raymond ! Le Royaume de Serre-Gy est en danger...",
            "Le Seigneur des Ombres a capturé Layla !",
            "Tu dois partir en quête pour la sauver et empêcher la catastrophe.",
            "Commence par avancer vers le nord et équipe-toi d'armes !",
            "Cherche le vieux sage dans la forêt, il t’aidera dans ta mission."
    };
    private int dialogueIndex = 0;
    private boolean dialogueActive = true;
    //Test
    // ✅ Panneau du bas (boutons d’inventaire et d’interaction)
    private JPanel bottomPanel;
    private JButton inventoryButton;
    private JButton interactButton;
    private JLabel coinLabel;
    private int coinCount = 0; // Nombre de pièces ramassées

    public MainGUI() {
        super("Aventure - Déplacement du Héros");

        instance = this;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());

        this.dashboard = new GameDisplay();
        this.inventory = new InventoryManager();

        // ✅ Panneau de narration à droite
        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(300, 800));
        sidePanel.setBackground(new Color(50, 50, 50));

        characterImage = new JLabel(new ImageIcon("src/images/narrator.png"));
        characterImage.setHorizontalAlignment(SwingConstants.CENTER);

        // ✅ Panel pour empiler les bulles de dialogue
        dialoguePanel = new JPanel();
        dialoguePanel.setLayout(new BoxLayout(dialoguePanel, BoxLayout.Y_AXIS));
        dialoguePanel.setBackground(new Color(50, 50, 50));

        // ✅ Initialisation du premier dialogue
        updateDialoguePanel();

        JScrollPane scrollPane = new JScrollPane(dialoguePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        sidePanel.add(characterImage, BorderLayout.NORTH);
        sidePanel.add(scrollPane, BorderLayout.CENTER);

        // ✅ Panneau du bas avec boutons et compteur de pièces
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(800, 60));
        bottomPanel.setBackground(new Color(80, 80, 80));

        inventoryButton = new JButton("📦 Inventaire");
        inventoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryButton.setPreferredSize(new Dimension(150, 40));
        inventoryButton.addActionListener(e -> openInventory());

        interactButton = new JButton("💬 Interagir");
        interactButton.setFont(new Font("Arial", Font.BOLD, 16));
        interactButton.setPreferredSize(new Dimension(150, 40));
        interactButton.addActionListener(e -> interactWithNPC());

        coinLabel = new JLabel("💰 Pièces : " + coinCount);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coinLabel.setForeground(Color.WHITE);

        bottomPanel.add(coinLabel);
        bottomPanel.add(inventoryButton);
        bottomPanel.add(interactButton);

        // ✅ Ajout des composants principaux
        add(dashboard, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH); // ✅ Boutons réajoutés en bas

        // ✅ KeyListener pour dialogues et déplacement
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (dialogueActive) {
                    advanceDialogue();
                } else {
                    moveHero(e.getKeyCode());
                }
            }
        });

        setFocusable(true);
        setVisible(true);
        requestFocusInWindow();
    }

    public static GameDisplay getGameDisplay() {
        return instance != null ? instance.dashboard : null;
    }

    /**
     * ✅ Gérer la progression du dialogue et empiler les bulles
     */
    private void advanceDialogue() {
        if (dialogueIndex < dialogues.length - 1) {
            dialogueIndex++;
            updateDialoguePanel();
        } else {
            dialogueActive = false;
            updateDialoguePanel();
        }
    }

    /**
     * ✅ Met à jour le panneau de dialogue avec une nouvelle bulle à chaque avancée
     */
    private void updateDialoguePanel() {
        JTextArea newDialogue = new JTextArea(dialogues[dialogueIndex]);
        newDialogue.setEditable(false);
        newDialogue.setLineWrap(true);
        newDialogue.setWrapStyleWord(true);
        newDialogue.setBackground(new Color(255, 255, 204)); // Fond jaune clair
        newDialogue.setFont(new Font("Arial", Font.BOLD, 14));
        newDialogue.setMargin(new Insets(10, 10, 10, 10));
        newDialogue.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 100), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        newDialogue.setMaximumSize(new Dimension(250, 80));

        dialoguePanel.add(newDialogue);
        dialoguePanel.revalidate();
        dialoguePanel.repaint();
    }

    /**
     * ✅ Gestion des mouvements après la fin des dialogues.
     */
    public void moveHero(int keyCode) {
        if (dialogueActive) return;

        Hero hero = dashboard.getHero();
        Block currentPos = hero.getPosition();
        Block newPos = currentPos;

        if (keyCode == KeyEvent.VK_LEFT) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() - 1);
            if (!dashboard.getMap().isBlocked(newPos)) hero.moveLeft();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() + 1);
            if (!dashboard.getMap().isBlocked(newPos)) hero.moveRight();
        } else if (keyCode == KeyEvent.VK_UP) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() - 1, currentPos.getColumn());
            if (!dashboard.getMap().isBlocked(newPos)) hero.moveUp();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() + 1, currentPos.getColumn());
            if (!dashboard.getMap().isBlocked(newPos)) hero.moveDown();
        }

        // ✅ Vérifier si une pièce est ramassée
        dashboard.checkHeroCoinCollision(this);

        dashboard.repaint();
    }

    /**
     * ✅ Ouvrir l’inventaire
     */
    private void openInventory() {
        JOptionPane.showMessageDialog(this, "📦 Inventaire ouvert ! (À implémenter)");
    }

    /**
     * ✅ Interagir avec un NPC
     */
    private void interactWithNPC() {
        JOptionPane.showMessageDialog(this, "💬 Tu veux interagir avec quelqu'un ? (À implémenter)");
    }

    /**
     * ✅ Mettre à jour l'affichage des pièces
     */
    public void incrementCoinCount() {
        coinCount++;
        coinLabel.setText("💰 Pièces : " + coinCount);
    }

    public int getCoinCount() {
        return coinCount;
    }

    public InventoryManager getInventoryManager() {
        return inventory;
    }

    /**
     * ✅ Main pour tester l'affichage
     */
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(MainGUI::new);
    }
}

