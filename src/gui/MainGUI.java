package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import data.item.InventoryManager;
import data.map.Block;
import data.dialogue.DialogueManager;

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static MainGUI instance;

    private GameDisplay dashboard;
    private InventoryManager inventory;

    // ✅ Panneau de narration
    private JPanel sidePanel;
    private JLabel characterImage;	
    private JPanel dialoguePanel;
    private DialogueManager dialogueManager = new DialogueManager();
    private boolean dialogueActive = true;
    private JScrollPane scrollPane;

    private String currentDialogueEvent = "intro"; // Par défaut au lancement

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

        scrollPane = new JScrollPane(dialoguePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        sidePanel.add(characterImage, BorderLayout.NORTH);
        sidePanel.add(scrollPane, BorderLayout.CENTER);

        // ✅ Panneau du bas
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setPreferredSize(new Dimension(1000, 65));
        bottomPanel.setMaximumSize(new Dimension(1000, 60));
        bottomPanel.setBackground(new Color(80, 80, 80));


        // ➤ Sous-panel gauche (pièces + boutons inventaire)
        JPanel leftBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBottomPanel.setOpaque(false);

        coinLabel = new JLabel("💰 Pièces : " + coinCount);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coinLabel.setForeground(Color.WHITE);
        leftBottomPanel.add(coinLabel);

        for (int i = 0; i < 5; i++) {
            JButton itemSlot = new JButton("Vide");
            itemSlot.setFont(new Font("Arial", Font.BOLD, 14));
            itemSlot.setPreferredSize(new Dimension(80, 30));
            itemSlot.addActionListener(e -> {
                System.out.println("🎒 Bouton d'inventaire cliqué : " + itemSlot.getText());
                requestFocusInWindow();
            });
            leftBottomPanel.add(itemSlot);
        }

        // ➤ Sous-panel droit (boutons Interagir + Mission côte à côte)
        JPanel rightBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBottomPanel.setOpaque(false);

        // ➤ Bouton Interagir
        interactButton = new JButton("Interagir");
        interactButton.setFont(new Font("Arial", Font.BOLD, 16));
        interactButton.setPreferredSize(new Dimension(120, 30));
        interactButton.addActionListener(e -> interactWithNPC());
        rightBottomPanel.add(interactButton);

        // ➤ Nouveau bouton Mission
        JButton missionButton = new JButton("Mission");
        missionButton.setFont(new Font("Arial", Font.BOLD, 16));
        missionButton.setPreferredSize(new Dimension(120, 30));
        missionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "📜 Objectif : Éteins les flammes des maisons !");
        });
        rightBottomPanel.add(missionButton);

        bottomPanel.add(leftBottomPanel, BorderLayout.WEST);
        bottomPanel.add(rightBottomPanel, BorderLayout.EAST);
        
        leftBottomPanel.setMaximumSize(new Dimension(800, 60));
        rightBottomPanel.setMaximumSize(new Dimension(800, 60));


        add(dashboard, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        addKeyListener(new KeyControls());



        updateDialoguePanel(currentDialogueEvent);

        setFocusable(true);
        //pack(); 
        setVisible(true);
        requestFocusInWindow();
    }



    public static GameDisplay getGameDisplay() {
        return instance != null ? instance.dashboard : null;
    }

    public void advanceDialogue() {
        if (dialogueManager.hasNext(currentDialogueEvent)) {
            dialogueManager.next(currentDialogueEvent);
            updateDialoguePanel(currentDialogueEvent);
        } else {
            dialogueActive = false;
            dialoguePanel.revalidate();
            dialoguePanel.repaint();
        }
    }
    
    public void triggerDialogue(String eventKey) {
        if (!dialogueManager.hasDialogue(eventKey)) return;

        currentDialogueEvent = eventKey;
        dialogueManager.reset(eventKey);
        dialogueActive = true;
        updateDialoguePanel(eventKey);
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
            "💬 Le vieux marchand t’atta dans sa boutique...\nVeux-tu entrer ?", 
            "Marchand", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            enterShop();
        }
    }


    public void updateDialoguePanel(String eventKey) {
        String dialogueText = dialogueManager.getCurrent(eventKey);
        if (dialogueText == null) return;

        JTextArea newDialogue = new JTextArea(dialogueText);
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

        // ✅ Force le scroll tout en bas à chaque nouveau message
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        SwingUtilities.invokeLater(() -> verticalBar.setValue(verticalBar.getMaximum()));
    }




    /**
     * Gère les déplacements du héros en fonction de la carte active (`currentMap` ou `shopMap`).
     */
    public void moveHero(int keyCode) {
        if (dialogueActive) return; // ✅ Bloque les déplacements si un dialogue est actif

        dashboard.getController().moveHero(keyCode, this); // ✅ délégation complète
    }



    /**
     * ✅ Vérifie l’interaction avec un coffre ou l’entrée du shop.
     */
    public void interactWithNPC() {
        // Exemple : priorité à un PNJ plus tard, sinon tente un coffre
        if (!dashboard.getController().tryInteractWithNPC(this)) {
            dashboard.getController().tryOpenChest(this);
        }

        requestFocusInWindow(); // ✅ Redonne le focus clavier après interaction
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
    
    public void requestFocusOnGame() {
        dashboard.requestFocusInWindow(); // dashboard est ton GameDisplay
    }
    
    private class KeyControls implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (dialogueActive) {
                advanceDialogue();
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && dashboard.isInShop()) {
                dashboard.exitShop();
                System.out.println("🚪 Sortie de la boutique !");
                triggerDialogue("exit_shop_1");
                requestFocusInWindow();
            } else {
                moveHero(e.getKeyCode()); // ⬅️ délégué au GameController
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }



    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
