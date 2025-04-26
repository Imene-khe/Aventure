package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import log.LoggerUtility;
import data.item.Flame;
import data.item.inventory.InventoryManager;
import data.map.Block;
import data.map.Map;
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
   

	private boolean piecesRemisesAuMarchand = false;
    private JScrollPane scrollPane;
    private String currentDialogueEvent = "intro";
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

        initDashboard();
        initSidePanel();
        initBottomPanel();
        initQuests();
        
        addKeyListener(new KeyControls());
        updateDialoguePanel(currentDialogueEvent);

        setFocusable(true);
        setVisible(true);
        dashboard.setFocusable(true);
        dashboard.requestFocusInWindow();
        logger.info("🖥️ Fenêtre affichée avec succès.");
        requestFocusInWindow();
    }

    
    public void initDashboard() {
        this.dashboard = new GameDisplay();
        this.dashboard.addKeyListener(new KeyControls());
        logger.info("🎮 GameDisplay attaché au centre.");
        add(dashboard, BorderLayout.CENTER);
    }
    
    private void initSidePanel() {
        this.inventory = new InventoryManager();
        
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

        add(sidePanel, BorderLayout.EAST);
        logger.info("📐 SidePanel attaché.");
    }

    
    private void initBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setPreferredSize(new Dimension(1000, 65));
        bottomPanel.setMaximumSize(new Dimension(1000, 60));
        bottomPanel.setBackground(new Color(80, 80, 80));

        JPanel leftBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBottomPanel.setOpaque(false);
        coinLabel = new JLabel("💰 Pièces : " + coinCount);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coinLabel.setForeground(Color.WHITE);
        leftBottomPanel.add(coinLabel);
        leftBottomPanel.add(inventory);
        leftBottomPanel.setMaximumSize(new Dimension(800, 60));

        JPanel rightBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBottomPanel.setOpaque(false);

        interactButton = new JButton("Interagir");
        interactButton.setFont(new Font("Arial", Font.BOLD, 16));
        interactButton.setPreferredSize(new Dimension(120, 30));
        interactButton.addActionListener(e -> interactWithNPC());
        rightBottomPanel.add(interactButton);

        JButton missionButton = new JButton("Mission");
        missionButton.setFont(new Font("Arial", Font.BOLD, 16));
        missionButton.setPreferredSize(new Dimension(120, 30));
        missionButton.addActionListener(e -> showMissions());
        rightBottomPanel.add(missionButton);
        rightBottomPanel.setMaximumSize(new Dimension(800, 60));

        bottomPanel.add(leftBottomPanel, BorderLayout.WEST);
        bottomPanel.add(rightBottomPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
        logger.info("📐 BottomPanel attaché.");
    }

    private void initQuests() {
        questManager.addQuest(new Quest("Collecte pour le marchand", "Récoltez 10 pièces d'or", Quest.TYPE_COLLECT, 10, 0));
        int flameCount = dashboard.getMap().getFlames().size();
        questManager.addQuest(new Quest("Eteindre les flammes", "Éteindre toutes les maisons en feu", Quest.TYPE_KILL, flameCount, 0));
        questManager.addQuest(new Quest("L'orbe sacré", "Trouvez l'orbe légendaire", Quest.TYPE_FIND, 1, 0));
    }

    private void showMissions() {
        StringBuilder message = new StringBuilder("📜 Missions en cours :\n\n");

        for (Quest quest : questManager.getActiveQuests()) {
            if (!quest.isCompleted()) {
                if ("Eteindre les flammes".equals(quest.getName())) {
                    Map map = MainGUI.getGameDisplay().getMap();
                    ArrayList<Flame> flames = map.getFlames();
                    int totalFlames = flames.size();
                    int extinguished = (int) flames.stream().filter(f -> !f.isActive()).count();
                    quest.setRequiredAmount(totalFlames);
                    quest.setCurrentAmount(extinguished);
                }

                message.append("• ").append(quest.getName())
                       .append(" (").append(quest.getStatusText()).append(")\n")
                       .append("  ➤ ").append(quest.getDescription()).append("\n")
                       .append("  ➤ Progression : ").append(quest.getCurrentAmount())
                       .append(" / ").append(quest.getRequiredAmount()).append("\n\n");
            }
        }

        JOptionPane.showMessageDialog(this, message.toString(), "🎯 Objectifs", JOptionPane.INFORMATION_MESSAGE);
        requestFocusInWindow();
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
    	logger.info("📣 triggerDialogue() appelé avec eventKey = " + eventKey);
        logger.info("📢 triggerDialogue() appelé avec eventKey = " + eventKey);
        if (!dialogueManager.hasDialogue(eventKey)) {
            logger.warn("❌ Aucun dialogue trouvé pour l’événement : " + eventKey);
            return;
        }
        logger.info("💬 Dialogue trouvé, préparation de l'affichage pour : " + eventKey);
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
            dashboard.getController().enterShop(this); 
        }

    }


    public void updateDialoguePanel(String eventKey) {
    	logger.info("📄 updateDialoguePanel() appelé avec eventKey = " + eventKey);
        String dialogueText = dialogueManager.getCurrent(eventKey);
        if (dialogueText == null) {
            logger.warn("🔕 Aucun texte de dialogue pour : " + eventKey);
            return;
        }
        logger.info("📝 Texte du dialogue actuel = " + dialogueText);
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

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        SwingUtilities.invokeLater(() -> verticalBar.setValue(verticalBar.getMaximum()));
    }

    public void moveHero(int keyCode) {
        if (dialogueActive) return; 

        dashboard.getController().moveHero(keyCode, this); 
    }

    public void interactWithNPC() {
        if (!dashboard.getController().tryInteractWithNPC(this)) {
            dashboard.getController().tryOpenChest(this);
            logger.debug("👤 Interaction avec un PNJ ou un coffre tentée.");
            dashboard.getController().tryExtinguishFlame(this); 
            logger.debug("👤 Interaction avec maison en feu tentée.");

        }

        requestFocusInWindow(); 
    }

    public boolean isHeroNearShop() {
        Block heroPos = dashboard.getHero().getPosition();
        int heroLine = heroPos.getLine();
        int heroColumn = heroPos.getColumn();

        for (int deltaLine = -1; deltaLine <= 1; deltaLine++) {
            for (int deltaColumn = -1; deltaColumn <= 1; deltaColumn++) {
                if (deltaLine == 0 && deltaColumn == 0) continue; 
                int newLine = heroLine + deltaLine;
                int newColumn = heroColumn + deltaColumn;
                if (newLine >= 0 && newLine < dashboard.getMap().getLineCount() && newColumn >= 0 && newColumn < dashboard.getMap().getColumnCount()) {
                    Block adjacentBlock = dashboard.getMap().getBlock(newLine, newColumn);
                    if (dashboard.getMap().getStaticObjects().containsKey(adjacentBlock) && dashboard.getMap().getStaticObjects().get(adjacentBlock).equals("shop")) {
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

   
    
    public void requestFocusOnGame() {
        dashboard.setFocusable(true);
        dashboard.requestFocusInWindow(); 
    }
    
   

    
    private class KeyControls implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (dialogueActive) {
                advanceDialogue();
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && dashboard.isInShop()) {
            	dashboard.getController().exitShop(MainGUI.this);
                logger.info("🚪 Sortie de la boutique !");
                logger.info("🔙 Touche ESC pressée : tentative de sortie de boutique.");
                triggerDialogue("exit_shop_1");
                requestFocusInWindow();
            } else {
                moveHero(e.getKeyCode()); 
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    
    
    
    public int getCoinCount() {
        return coinCount;
    }

    public InventoryManager getInventoryManager() {
        return inventory;
    }
    
    public void setDialogueActive(boolean active) {
        this.dialogueActive = active;
    }
    
    public boolean hasEnoughCoinsForShop() {
        return coinCount >= 10;
    }
    
    public void resetCoinCount() {
        coinCount = 0;
        coinLabel.setText("💰 Pièces : 0");
        logger.info("🔁 Les pièces ont été remises au marchand.");
    }
    
    public void setPiecesRemises(boolean status) {
        this.piecesRemisesAuMarchand = status;
    }

    public boolean havePiecesBeenReturned() {
        return piecesRemisesAuMarchand;
    }
    
    public boolean isDialogueActive() {
		return dialogueActive;
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

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
