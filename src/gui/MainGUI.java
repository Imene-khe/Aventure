package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;
import log.LoggerUtility;

import data.item.InventoryManager;
import data.map.Block;
import data.quest.Quest;
import data.quest.QuestManager;
import data.dialogue.DialogueManager;

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerUtility.getLogger(MainGUI.class, "text");
    private static MainGUI instance;
    private GameDisplay dashboard;
    private InventoryManager inventory;
    private JPanel sidePanel;
    private JLabel characterImage;	
    private JPanel dialoguePanel;
    private DialogueManager dialogueManager = new DialogueManager();
    private boolean dialogueActive = true;
    private JScrollPane scrollPane;
    private String currentDialogueEvent = "intro"; // Par défaut au lancement
    private JPanel bottomPanel;
    private JButton interactButton;
    private JLabel coinLabel;
    private int coinCount = 0;
    private QuestManager questManager = new QuestManager();

    public MainGUI() {
        super("Aventure - Déplacement du Héros");
        logger.info("🟢 Initialisation de l'IHM MainGUI...");
        instance = this;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        this.dashboard = new GameDisplay();
        this.dashboard.addKeyListener(new KeyControls()); // ✅ Pour capter les touches même si GameDisplay a le focus
        logger.info("🎮 GameDisplay attaché au centre.");
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
        logger.info("📐 Panneaux UI ajoutés à la fenêtre.");

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
        logger.info("📐 Panneaux UI ajoutés à la fenêtre.");
        leftBottomPanel.add(inventory); // inventory est une instance de InventoryManager

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
            StringBuilder message = new StringBuilder("📜 Missions en cours :\n\n");
            for (Quest quest : questManager.getActiveQuests()) {
                if (!quest.isCompleted()) {
                    message.append("• ").append(quest.getName())
                           .append(" (").append(quest.getStatusText()).append(")\n")
                           .append("  ➤ ").append(quest.getDescription()).append("\n")
                           .append("  ➤ Progression : ").append(quest.getCurrentAmount())
                           .append(" / ").append(quest.getRequiredAmount()).append("\n\n");
                }
            }
            JOptionPane.showMessageDialog(this, message.toString(), "🎯 Objectifs", JOptionPane.INFORMATION_MESSAGE);
            requestFocusInWindow();
        });

        rightBottomPanel.add(missionButton);

        bottomPanel.add(leftBottomPanel, BorderLayout.WEST);
        bottomPanel.add(rightBottomPanel, BorderLayout.EAST);
        
        leftBottomPanel.setMaximumSize(new Dimension(800, 60));
        rightBottomPanel.setMaximumSize(new Dimension(800, 60));


        add(dashboard, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        questManager.addQuest(new Quest("Collecte pour le marchand", "Récoltez 10 pièces d'or", Quest.TYPE_COLLECT, 10, 0));
        questManager.addQuest(new Quest("Eteindre les flammes", "Éteindre 3 maisons en feu", Quest.TYPE_KILL, 3, 0)); // ou TYPE_FIND si tu préfères
        questManager.addQuest(new Quest("L'orbe sacré", "Trouvez l'orbe légendaire", Quest.TYPE_FIND, 1, 0));


        addKeyListener(new KeyControls());
        updateDialoguePanel(currentDialogueEvent);

        setFocusable(true);
        //pack(); 
        setVisible(true);
        dashboard.setFocusable(true); // 🟢 Assure que GameDisplay peut recevoir les touches
        dashboard.requestFocusInWindow(); // 🟢 Force le focus
        logger.info("🖥️ Fenêtre affichée avec succès.");
        requestFocusInWindow();
    }



    public QuestManager getQuestManager() {
		return questManager;
	}



	public void setQuestManager(QuestManager questManager) {
		this.questManager = questManager;
	}
	
	public static MainGUI getInstance() {
	    return instance;
	}




	public static GameDisplay getGameDisplay() {
        return instance != null ? instance.dashboard : null;
    }

    public void advanceDialogue() {
        if (dialogueManager.hasNext(currentDialogueEvent)) {
        	logger.debug("➡️ Dialogue avancé : " + currentDialogueEvent);
            dialogueManager.next(currentDialogueEvent);
            updateDialoguePanel(currentDialogueEvent);
        } else {
            dialogueActive = false;
            dialoguePanel.revalidate();
            dialoguePanel.repaint();
            logger.info("📭 Fin du dialogue pour : " + currentDialogueEvent);
        }
    }
    
    public void triggerDialogue(String eventKey) {
        if (!dialogueManager.hasDialogue(eventKey)) {
        	logger.warn("❌ Aucun dialogue trouvé pour l’événement : " + eventKey);
        	return;
        }

        logger.info("💬 Début du dialogue : " + eventKey);
        
        // ✅ Nettoyer les anciens dialogues affichés
        dialoguePanel.removeAll();
        dialoguePanel.revalidate();
        dialoguePanel.repaint();
        currentDialogueEvent = eventKey;
        dialogueManager.reset(eventKey);
        dialogueActive = true;
        updateDialoguePanel(eventKey);
    }



    
    public void interactWithMerchant() {
        if (coinCount < 10) {
            JOptionPane.showMessageDialog(this, "💬 Il te faut 10 pièces pour entrer dans la boutique !");
            logger.info("❌ Tentative d'entrée dans le shop sans assez de pièces.");
            return;
        }

        int choix = JOptionPane.showConfirmDialog(this, 
            "💬 Le vieux marchand t’atta dans sa boutique...\nVeux-tu entrer ?", 
            "Marchand", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            dashboard.getController().enterShop(this); // Appel du GameController
        }

    }


    public void updateDialoguePanel(String eventKey) {
        String dialogueText = dialogueManager.getCurrent(eventKey);
        if (dialogueText == null) {
        	logger.warn("🔕 Aucun texte de dialogue pour : " + eventKey);
        	return;
        }
        logger.debug("📝 Affichage dialogue : " + dialogueText);
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
        if (!dashboard.getController().tryInteractWithNPC(this)) {
            dashboard.getController().tryOpenChest(this);
            logger.debug("👤 Interaction avec un PNJ ou un coffre tentée.");
            dashboard.getController().tryExtinguishFlame(this); 
            logger.debug("👤 Interaction avec maison en feu tentée.");

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
        logger.info("💰 Pièce ajoutée. Total = " + coinCount);
        coinLabel.setText("💰 Pièces : " + coinCount);
        questManager.updateQuest("Collecte pour le marchand", 1);

        
        if (coinCount == 10) {
        	logger.info("🎯 Objectif atteint : 10 pièces.");
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
        dashboard.setFocusable(true);
        dashboard.requestFocusInWindow(); // ✅ C'est ça qu'on veut rappeler après l'inventaire
    }
    
    public void setDialogueActive(boolean active) {
        this.dialogueActive = active;
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
                logger.info("🚪 Sortie de la boutique !");
                logger.info("🔙 Touche ESC pressée : tentative de sortie de boutique.");
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
