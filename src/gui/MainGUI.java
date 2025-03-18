package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import data.item.Chest;
import data.item.InventoryManager;
import data.map.Block;
import data.map.Map;
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

        // ✅ Ajouter 5 boutons d'inventaire rapide et restaurer le focus après un clic
        for (int i = 0; i < 5; i++) {
            JButton itemSlot = new JButton("Vide");
            itemSlot.setFont(new Font("Arial", Font.BOLD, 14));
            itemSlot.setPreferredSize(new Dimension(80, 40));

            itemSlot.addActionListener(e -> {
                System.out.println("🎒 Bouton d'inventaire cliqué : " + itemSlot.getText());
                requestFocusInWindow(); // ✅ Redonne le focus après un clic
            });

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

    public void advanceDialogue() {
        if (dialogueIndex < dialogues.length - 1) {
            dialogueIndex++;
            updateDialoguePanel();
        } else {
            dialogueActive = false;
            updateDialoguePanel();
        }
    }
    
    /**
     * ✅ Change l'affichage pour afficher `shopMap` dans `GameDisplay`
     */
    public void enterShop() {
        System.out.println("🏪 Le héros entre dans le shop !");
        dashboard.enterShop(); // ✅ Active la boutique dans GameDisplay
    }


    
    public void interactWithMerchant() {
        if (coinCount < 10) {
            JOptionPane.showMessageDialog(this, "💬 Il te faut 10 pièces pour entrer dans la boutique !");
            return;
        }

        int choix = JOptionPane.showConfirmDialog(this, 
            "💬 Le vieux marchand t’attend dans sa boutique...\nVeux-tu entrer ?", 
            "Marchand", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            enterShop();
        }
    }


    public void updateDialoguePanel() {
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

    /**
     * Gère les déplacements du héros en fonction de la carte active (`currentMap` ou `shopMap`).
     */
    public void moveHero(int keyCode) {
        if (dialogueActive) return; // ✅ Bloque les déplacements si un dialogue est actif

        Hero hero = dashboard.getHero();
        Block currentPos = hero.getPosition();
        Block newPos = currentPos;

        // ✅ Sélectionner la carte active (`currentMap` ou `shopMap`)
        Map activeMap = dashboard.isInShop() ? dashboard.getShopMap() : dashboard.getMap();

        int newLine = currentPos.getLine();
        int newColumn = currentPos.getColumn();

        if (keyCode == KeyEvent.VK_LEFT) {
            newColumn--;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            newColumn++;
        } else if (keyCode == KeyEvent.VK_UP) {
            newLine--;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            newLine++;
        }

        // ✅ Vérifier que le déplacement reste dans les limites de la carte
        if (newLine >= 0 && newLine < activeMap.getLineCount() && newColumn >= 0 && newColumn < activeMap.getColumnCount()) {
            newPos = activeMap.getBlock(newLine, newColumn);

            // ✅ Vérifier que le bloc n'est pas bloqué
            if (!activeMap.isBlocked(newPos)) {
                hero.setPosition(newPos);
                dashboard.repaint();
            } else {
                System.out.println("🚫 Mouvement bloqué par un obstacle !");
            }
        } else {
            System.out.println("🚫 Mouvement interdit : hors des limites !");
        }

        dashboard.checkHeroCoinCollision(this);
    }


    /**
     * ✅ Vérifie l’interaction avec un coffre ou l’entrée du shop.
     */
    public void interactWithNPC() {
        Chest chest = dashboard.openNearbyChest(); // Vérifier si un coffre est proche

        if (chest != null) {
            System.out.println("🔓 Coffre trouvé, ouverture...");
            ChestUIManager chestUI = new ChestUIManager(this);
            chestUI.displayChestContents(chest);
        } else if (isHeroNearShop()) {
            enterShop(); // ✅ Entrer dans la boutique si le héros est proche du shop
        } else {
            JOptionPane.showMessageDialog(this, "💬 Il n'y a rien à interagir ici !");
        }

        requestFocusInWindow(); // ✅ Redonner le focus après l’interaction
    }


    /**
     * ✅ Vérifie si le héros est proche du bâtiment `shop`
     */
    public boolean isHeroNearShop() {
        Block heroPos = dashboard.getHero().getPosition();
        int heroLine = heroPos.getLine();
        int heroColumn = heroPos.getColumn();

        // Vérifier les cases adjacentes autour du héros
        for (int deltaLine = -1; deltaLine <= 1; deltaLine++) {
            for (int deltaColumn = -1; deltaColumn <= 1; deltaColumn++) {
                if (deltaLine == 0 && deltaColumn == 0) continue; // Ignorer la case du héros lui-même

                int newLine = heroLine + deltaLine;
                int newColumn = heroColumn + deltaColumn;

                // Vérifier que les coordonnées sont valides
                if (newLine >= 0 && newLine < dashboard.getMap().getLineCount() &&
                    newColumn >= 0 && newColumn < dashboard.getMap().getColumnCount()) {

                    Block adjacentBlock = dashboard.getMap().getBlock(newLine, newColumn);

                    // Vérifier si le bloc adjacent est le shop
                    if (dashboard.getMap().getStaticObjects().containsKey(adjacentBlock) &&
                        dashboard.getMap().getStaticObjects().get(adjacentBlock).equals("shop")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void incrementCoinCount() {
        coinCount++;
        coinLabel.setText("💰 Pièces : " + coinCount);
        
        if (coinCount == 10) {
            JOptionPane.showMessageDialog(this, "💬 Bravo ! Tu as 10 pièces, va voir le marchand !");
        }
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
