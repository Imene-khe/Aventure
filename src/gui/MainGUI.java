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
    private JPanel dialoguePanel;
    private String[] dialogues = {
            "Bienvenue, Raymond ! Le Royaume de Serre-Gy est en danger...",
            "Le Seigneur des Ombres a capturé Layla !",
            "Tu dois partir en quête pour la sauver et empêcher la catastrophe.",
            "Commence par avancer vers le nord et équipe-toi d'armes !",
            "Cherche le vieux sage dans la forêt, il t’aidera dans ta mission."
    };
    private int dialogueIndex = 0;
    private boolean dialogueActive = true;

    // ✅ Panneau du bas
    private JPanel bottomPanel;
    private JButton interactButton;
    private JLabel coinLabel;
    private int coinCount = 0;

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
        sidePanel.setPreferredSize(new Dimension(250, getHeight()));
        sidePanel.setBackground(new Color(50, 50, 50));

        characterImage = new JLabel(new ImageIcon("src/images/narrator.png"));
        characterImage.setHorizontalAlignment(SwingConstants.CENTER);

        dialoguePanel = new JPanel();
        dialoguePanel.setLayout(new BoxLayout(dialoguePanel, BoxLayout.Y_AXIS));
        dialoguePanel.setBackground(new Color(50, 50, 50));

        updateDialoguePanel();

        JScrollPane scrollPane = new JScrollPane(dialoguePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        sidePanel.add(characterImage, BorderLayout.NORTH);
        sidePanel.add(scrollPane, BorderLayout.CENTER);

        // ✅ Panneau du bas
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(800, 60));
        bottomPanel.setBackground(new Color(80, 80, 80));

        interactButton = new JButton("💬 Interagir");
        interactButton.setFont(new Font("Arial", Font.BOLD, 16));
        interactButton.setPreferredSize(new Dimension(150, 40));
        interactButton.addActionListener(e -> interactWithNPC());

        coinLabel = new JLabel("💰 Pièces : " + coinCount);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coinLabel.setForeground(Color.WHITE);

        bottomPanel.add(coinLabel);
        bottomPanel.add(interactButton);

        // ✅ Ajouter 5 boutons vides pour l'inventaire rapide
        for (int i = 0; i < 5; i++) {
            JButton itemSlot = new JButton("Vide");
            itemSlot.setFont(new Font("Arial", Font.BOLD, 14));
            itemSlot.setPreferredSize(new Dimension(80, 40));
            bottomPanel.add(itemSlot);
        }

        dashboard.setPreferredSize(new Dimension(getWidth() - sidePanel.getPreferredSize().width, getHeight()));
        add(dashboard, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

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

    private void advanceDialogue() {
        if (dialogueIndex < dialogues.length - 1) {
            dialogueIndex++;
            updateDialoguePanel();
        } else {
            dialogueActive = false;
            updateDialoguePanel();
        }
    }

    private void updateDialoguePanel() {
        JTextArea newDialogue = new JTextArea(dialogues[dialogueIndex]);
        newDialogue.setEditable(false);
        newDialogue.setLineWrap(true);
        newDialogue.setWrapStyleWord(true);
        newDialogue.setBackground(new Color(255, 255, 204));
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

        dashboard.checkHeroCoinCollision(this);
        dashboard.repaint();
    }

    /**
     * ✅ Interaction avec un coffre
     */
    private void interactWithNPC() {
        Chest chest = dashboard.openNearbyChest(); // Vérifier si un coffre est proche
        if (chest != null) {
            System.out.println("🔓 Coffre trouvé, ouverture...");
            ChestUIManager chestUI = new ChestUIManager(this);
            chestUI.displayChestContents(chest);
        } else {
            JOptionPane.showMessageDialog(this, "💬 Il n'y a rien à interagir ici !");
        }

        // ✅ Redonner le focus à la fenêtre pour que le héros puisse se déplacer
        this.requestFocusInWindow();
    }


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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
