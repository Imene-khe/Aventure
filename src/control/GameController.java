package control;

import data.item.Coin;
import data.item.Equipment;
import data.item.Flame;
import data.item.Projectile;
import data.item.chest.Chest;
import data.map.Block;
import data.map.CombatMap;
import data.map.HostileMap;
import data.map.Map;
import data.player.Antagonist;
import data.player.Hero;
import data.quest.Quest;
import data.quest.QuestManager;
import gui.ChestUIManager;
import gui.GameDisplay;
import gui.MainGUI;
import gui.StartScreen;
import gui.animation.EndCreditsPanel;
import log.LoggerUtility;
import org.apache.log4j.Logger;
import javax.swing.*;
import java.awt.Point;
import java.util.ArrayList;

public class GameController {

    private static final Logger logger = LoggerUtility.getLogger(GameController.class, "text");
	private final GameDisplay display;
    private final Hero hero;
    private Map map;
    private Map hostileMap;
    MovementController movementController;
    private boolean canTakeDamage = true;
	private CombatController combatController;
    private ArrayList<Block> activatedRunes = new ArrayList<>();
    private int repaintCounter = 0;


    public GameController(GameDisplay display) {
        this.display = display;
        this.hero = display.getHero();
        this.map = display.getMap();
        display.getShopMap();
        this.hostileMap = display.getHostileMap(); 
        this.combatController = new CombatController(display, this);
        this.movementController = new MovementController(this.hero, this.display);
    }

    public void checkRuneActivation() {
        if (!(hostileMap instanceof HostileMap hMap)) return;

        // ✅ On vérifie que MainGUI existe AVANT
        if (gui.MainGUI.getInstance() == null) {
            return;
        }

        Block heroPos = hero.getPosition();
        ArrayList<Block> runeBlocks = hMap.getRuneBlocks();
        QuestManager qm = MainGUI.getInstance().getQuestManager();

        for (Block rune : runeBlocks) {
            if (rune.equals(heroPos) && !activatedRunes.contains(rune)) {
                activatedRunes.add(rune);
                qm.updateQuest("Activer les runes", 1);
                System.out.println("🔮 Rune activée sur " + rune);
                Quest runeQuest = qm.getActiveQuests().stream()
                    .filter(q -> q.getName().equals("Activer les runes"))
                    .findFirst().orElse(null);

                if (runeQuest != null && runeQuest.isCompleted()) {
                    JOptionPane.showMessageDialog(display, "✨ Les runes anciennes sont activées.\nLa grotte est maintenant accessible !");
                }
                break;
            }
        }
    }

    public void moveHero(Block newPosition, MainGUI mainGUI) {
        if (display.isGameOver()) return;

        Map activeMap = display.getActiveMap();

        if (activeMap.isBlocked(newPosition)) return;

        hero.setPosition(newPosition);

        if (!display.isInShop()) {
            checkCoinCollection(mainGUI);
            checkEnemyCollision();
        }

        display.repaint();
    }
    
    public void moveHero(int keyCode, MainGUI gui) {
        if (display.isGameOver()) return;

        movementController.moveHero(keyCode);

        if (!display.isInShop()) {
            if (gui != null) {
                checkCoinCollection(gui); // ✅ Appelle seulement si gui n'est pas null
            }
            checkEnemyCollision();
        }

        if (display.isInHostileMap()) {
            checkRuneActivation();
        }

        display.repaint();
    }



    public void checkCoinCollection(MainGUI mainGUI) {
    	Map activeMap = display.getActiveMap();

        ArrayList<Coin> collectedCoins = new ArrayList<>();

        for (Coin coin : activeMap.getCoins()) {
            if (!coin.isCollected() && coin.getBlock().equals(hero.getPosition())) {
                coin.collect();
                collectedCoins.add(coin);
                mainGUI.incrementCoinCount();
            }
        }

        activeMap.getCoins().removeAll(collectedCoins);
    }

    public Chest tryOpenNearbyChest(MainGUI gui) {
        Map activeMap = display.getActiveMap();
        Block heroPos = hero.getPosition();
        int heroLine = heroPos.getLine();
        int heroColumn = heroPos.getColumn();

        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dl == 0 && dc == 0) continue;

                int newL = heroLine + dl;
                int newC = heroColumn + dc;

                if (newL >= 0 && newL < activeMap.getLineCount() && newC >= 0 && newC < activeMap.getColumnCount()) {
                    Block adjacent = activeMap.getBlock(newL, newC);
                    if ("chest".equals(activeMap.getStaticObjects().get(adjacent))) {
                        Chest chest = activeMap.getChestManager().getChests().get(adjacent);

                        if (chest != null && chest.getInventory() != null) {
                            boolean hasOrb = false;
                            for (Equipment eq : chest.getInventory().getEquipments()) {
                                if ("orb".equalsIgnoreCase(eq.getName())) {
                                    hasOrb = true;
                                    break;
                                }
                            }

                            if (hasOrb) {
                                int response = JOptionPane.showConfirmDialog(
                                    display,
                                    "🌟 Vous avez trouvé l'Orbe légendaire !\nSouhaitez-vous poursuivre l'aventure ?",
                                    "Poursuivre l'aventure",
                                    JOptionPane.YES_NO_OPTION
                                );

                                if (response == JOptionPane.YES_OPTION) {
                                    this.hostileMap = display.getHostileMap();
                                    this.enterHostileMap(gui);

                                }

                            }
                        }

                        return chest;
                    }
                }
            }
        }
        return null;
    }



    public void checkEnemyCollision() {
        if (display.isGameOver() || display.isInShop()) return;

        Block heroPos = hero.getPosition();
        Map activeMap = display.getActiveMap();

        if (activeMap instanceof HostileMap hMap) {
            for (Antagonist enemy : hMap.getAntagonistList()) {
                if (enemy.getPosition().equals(heroPos)) {
                    applyHeroDamage();
                    return;
                }
            }
        } else if (activeMap instanceof CombatMap cMap) {
            for (Antagonist enemy : cMap.getAntagonists()) {
                if (enemy.getPosition().equals(heroPos)) {
                    applyHeroDamage();
                    return;
                }
            }
        } else {
            for (Block enemyBlock : activeMap.getEnemies().keySet()) {
                if (enemyBlock.equals(heroPos)) {
                    applyHeroDamage();
                    return;
                }
            }
        }
    }
    
    public void setCanTakeDamage(boolean value) {
        this.canTakeDamage = value;
    }



    public void applyHeroDamage() {
        if (!canTakeDamage) return;

        hero.takeDamage(10);
        canTakeDamage = false;

        GameLoopManager.getInstance().startHeroDamageCooldown();


        if (hero.getHealth() <= 0) {
            display.setGameOver(true);
            logger.error("☠️ GAME OVER ! Le héros est mort.");
            JOptionPane.showMessageDialog(display, "☠️ GAME OVER ! Le héros est mort.");
            System.exit(0);
        }
    }


    public void tryOpenChest(MainGUI gui) {
        Chest chest = tryOpenNearbyChest(gui);
        if (chest != null) {
            ChestUIManager chestUI = new ChestUIManager(gui);

            chestUI.setOnOrbTakenCallback(() -> {
                logger.info("📦 Orbe récupéré, lancement de l'entrée dans HostileMap...");
                gui.getQuestManager().updateQuest("Trouver l'orbe", 1);
                this.enterHostileMap(gui);
            });

            chestUI.displayChestContents(chest);
            gui.requestFocusInWindow();
        } else {
            logger.warn("❌ Aucun coffre à proximité.");
        }
    }



    public boolean tryInteractWithNPC(MainGUI gui) {
        if (display.isInHostileMap() && tryChopDeadTree(gui)) {
        	return true;
        }
        if (tryOpenPrincessCage(gui)) {
        	return true;
        }
        Chest chest = tryOpenNearbyChest(gui);
        if (chest != null) {
            tryOpenChest(gui); 
            return true;
        }
        if (tryMerchantOrShopInteraction(gui)) {
        	return true;
        }
        if (tryEnterCombatMap(gui)) {
        	return true;
        }
        display.getController().tryIgniteCampfire(gui);
        return false;
    }


    public boolean tryOpenPrincessCage(MainGUI gui) {
        Map activeMap = display.getActiveMap();
        Block heroPos = hero.getPosition();

        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dl == 0 && dc == 0) continue;

                int newL = heroPos.getLine() + dl;
                int newC = heroPos.getColumn() + dc;

                if (newL >= 0 && newL < activeMap.getLineCount() &&
                    newC >= 0 && newC < activeMap.getColumnCount()) {

                    Block adjacent = activeMap.getBlock(newL, newC);
                    String object = activeMap.getStaticObjects().get(adjacent);

                    if ("cage_with_princess".equals(object)) {
                        activeMap.getStaticObjects().put(adjacent, "princess");
                        activeMap.getStaticTerrain().put(adjacent, "platformCave");
                        activeMap.setTerrainBlocked(adjacent, false);

                        gui.repaint();
                        JOptionPane.showMessageDialog(gui, "👸 Tu as libéré la princesse !\nL’aventure touche à sa fin...");
                        GameLoopManager.getInstance().stop();
                      
                        int choix = JOptionPane.showOptionDialog(
                            gui,
                            "🎊 Félicitations !\nTu as vaincu le boss, sauvé ta femme,\net restauré la paix dans le royaume.\n\nSouhaites-tu rejouer ?",
                            "Fin du jeu",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"🔁 Revenir au menu", "❌ Quitter"},
                            "🔁 Revenir au menu"
                        );

                        if (choix == JOptionPane.YES_OPTION) {
                            gui.dispose(); 
                            SwingUtilities.invokeLater(StartScreen::new); 
                        } else {
                            gui.dispose(); 
                            SwingUtilities.invokeLater(() -> EndCreditsPanel.showInWindow()); 
                        }

                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean tryChopDeadTree(MainGUI gui) {
        Block heroPos = hero.getPosition();
        Map activeMap = hostileMap;

        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dl == 0 && dc == 0) continue;

                int line = heroPos.getLine() + dl;
                int col = heroPos.getColumn() + dc;

                if (line >= 0 && col >= 0 && line < activeMap.getLineCount() && col < activeMap.getColumnCount()) {
                    Block block = activeMap.getBlock(line, col);
                    String object = activeMap.getStaticObjects().get(block);

                    if (object != null && object.startsWith("deadTree")) {
                        activeMap.getStaticObjects().remove(block);
                        activeMap.setTerrainBlocked(block, false);
                        gui.getQuestManager().updateQuest("Trouve du bois sec", 1);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean tryMerchantOrShopInteraction(MainGUI gui) {
        Block heroPos = hero.getPosition();
        Map activeMap = display.getActiveMap();
        for (int dl = -2; dl <= 2; dl++) {
            for (int dc = -2; dc <= 2; dc++) {
                if (dl == 0 && dc == 0) continue;

                int line = heroPos.getLine() + dl;
                int col = heroPos.getColumn() + dc;

                if (line >= 0 && col >= 0 && line < activeMap.getLineCount() && col < activeMap.getColumnCount()) {
                    Block block = activeMap.getBlock(line, col);
                    String object = activeMap.getStaticObjects().get(block);

                    if ("merchant".equals(object)) {
                        openMerchantDialogue(gui);
                        return true;
                    }

                    if (!display.isInShop() && "shop".equals(object)) {
                        logger.info("🏪 Entrée dans la boutique détectée.");
                        enterShop(gui);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void openMerchantDialogue(MainGUI gui) {
        logger.info("🗨️ Interaction avec le marchand détectée.");
        String message = "👴 Le marchand te salue avec un sourire.\n\nQue veux-tu lui dire ?";
        String[] options = {
            "💰 Bonjour, j'ai cru comprendre que vous aviez perdu une bourse d'or...",
            "👋 Bonjour l'ami. Alors vous êtes nouveau dans la région ?"
        };

        JOptionPane.showOptionDialog(
            null,
            message,
            "Dialogue avec le marchand",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
            gui.triggerDialogue("enter_shop_give_gold");
            gui.resetCoinCount(); 
            gui.setPiecesRemises(true); 
        
    }



    public void enterHostileMap(MainGUI gui) {
        logger.info("🌋 GameController.enterHostileMap() appelé");
        
        display.enterHostileMap();         
        setupHostileQuests();               
        gui.setDialogueActive(true);      
        
        gui.triggerDialogue("enter_hostile_map"); 
        
        gui.requestFocusInWindow();        
        gui.requestFocusOnGame();           
    }


    public void enterShop(MainGUI gui) {
        if (!gui.hasEnoughCoinsForShop()) {
            logger.warn("🚫 Tentative d'entrée dans la boutique sans 10 pièces.");
            JOptionPane.showMessageDialog(gui, "💰 Il te faut 10 pièces pour entrer dans la boutique !");
            gui.requestFocusInWindow();
            return;
        }

        display.enterShop();
        gui.triggerDialogue("enter_shop");
    }


    public boolean tryEnterShop(MainGUI gui) {
        Block heroPos = hero.getPosition();

        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dl == 0 && dc == 0) continue;

                int line = heroPos.getLine() + dl;
                int col = heroPos.getColumn() + dc;

                if (line >= 0 && col >= 0 && line < map.getLineCount() && col < map.getColumnCount()) {
                    Block block = map.getBlock(line, col);
                    if ("shop".equals(map.getStaticObjects().get(block))) {
                        logger.info("🏪 Entrée dans la boutique.");
                        enterShop(gui);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void tryExtinguishFlame(MainGUI gui) {
        Block heroPos = display.getHero().getPosition();
        Map activeMap = display.getActiveMap();
        for (Flame flame : activeMap.getFlames()) {
            Block flamePos = flame.getPosition();

            int dx = Math.abs(heroPos.getLine() - flamePos.getLine());
            int dy = Math.abs(heroPos.getColumn() - flamePos.getColumn());

            if (dx <= 1 && dy <= 1 && flame.isActive()) {
                flame.extinguish();
                activeMap.getStaticObjects().put(flamePos, "house");
                gui.repaint();
                gui.requestFocusInWindow();

                boolean allExtinguished = activeMap.getFlames().stream().noneMatch(Flame::isActive);
                if (allExtinguished) {
                    gui.getQuestManager().updateQuest("Eteindre les flammes", 1);
                    gui.triggerDialogue("flames_extinguished"); 
                }

                return;
            }
        }
    }
    

    
    public void setupHostileQuests() {
        QuestManager questManager = MainGUI.getInstance().getQuestManager();
        questManager.clearQuests();
        questManager.addQuest(new Quest("Trouve du bois sec", "Atteinds le refuge et allume un feu", Quest.TYPE_FIND, 3, 0));
        questManager.addQuest(new Quest("Chasseur de têtes", "Élimine 5 monstres hostiles", Quest.TYPE_KILL, 5, 250));
        questManager.addQuest(new Quest("Activer les runes", "Marche sur les 3 runes anciennes", "rune", 3, 0));}
    
    public void tryIgniteCampfire(MainGUI gui) {
        if (!display.isInHostileMap()) return;

        Block heroPos = hero.getPosition();
        Map activeMap = hostileMap;

        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                int line = heroPos.getLine() + dl;
                int col = heroPos.getColumn() + dc;

                if (line < 0 || col < 0 || line >= activeMap.getLineCount() || col >= activeMap.getColumnCount())
                    continue;

                Block block = activeMap.getBlock(line, col);
                String object = activeMap.getStaticObjects().get(block);
                if ("campfire_off".equals(object)) {
                    QuestManager qm = MainGUI.getInstance().getQuestManager();
                    Quest quest = qm.getActiveQuests().stream()
                        .filter(q -> q.getName().equals("Trouve du bois sec"))
                        .findFirst().orElse(null);

                    if (quest != null && quest.getCurrentAmount() == 3 && quest.getRequiredAmount() == 3) {
                        activeMap.getStaticObjects().put(block, "campfire_on");  
                        qm.updateQuest("Trouve du bois sec", 1); 
                        JOptionPane.showMessageDialog(gui, "🔥 Tu as allumé le feu de camp !");
                        gui.triggerDialogue("campfire_lit"); 
                        display.repaint(); 
                    }

                    else {
                        JOptionPane.showMessageDialog(gui, "💨 Il te faut au moins 3 bois secs pour allumer le feu.");
                    }
                    return;
                }
            }
        }
    }
    
    public void applyProjectileHit() {
        hero.takeDamage(10);
        logger.warn("💥 Projectile touché ! Vie restante : " + hero.getHealth() + "%");

        if (hero.getHealth() <= 0) {
            display.setGameOver(true);
            logger.error("☠️ GAME OVER ! Le héros est mort.");
            javax.swing.JOptionPane.showMessageDialog(display, "☠️ GAME OVER ! Le héros est mort.");
            System.exit(0);
        }
    }


    public void setGameOver() {
        display.setGameOver(true);
        logger.error("☠️ GAME OVER ! Le héros est mort.");
        javax.swing.JOptionPane.showMessageDialog(display, "☠️ GAME OVER ! Le héros est mort.");
        System.exit(0);
    }
    
    public void moveEnemiesTowardsHero() {
        movementController.moveEnemiesTowardsHero();
    }
    
    public boolean isAdjacent(Block a, Block b) {
        return movementController.isAdjacent(a, b);
    }

    public void handleCombatClick(Point clickPoint) {
        if (combatController != null) {
            combatController.handleClick(clickPoint);
        }
    }
    
    public CombatController getCombatController() {
		return combatController;
	}


	public void setCombatController(CombatController combatController) {
		this.combatController = combatController;
	}
	
	public boolean tryEnterCombatMap(MainGUI gui) {
	    if (!display.isInHostileMap()) return false;
	    Block heroPos = hero.getPosition();
	    HostileMap hostileMap = (HostileMap) display.getHostileMap();
	    Block caveEntry = hostileMap.getCaveEntry();
	    if (caveEntry != null) {
	        int dx = Math.abs(heroPos.getLine() - caveEntry.getLine());
	        int dy = Math.abs(heroPos.getColumn() - caveEntry.getColumn());

	        if (dx + dy <= 1) {
	            display.enterCombatMap();
	            combatController.loadFirstWaveIfNeeded();
	            gui.triggerDialogue("enter_combat_map"); 
	            return true;
	        }

	    }

	    return false;
	}
	
	public void bossSpecialAttack() {
        Map activeMap = display.getActiveMap();

        if (!(activeMap instanceof CombatMap cMap)) return;

        Block heroPos = hero.getPosition();

        for (Antagonist enemy : cMap.getAntagonists()) {
            if (enemy.getType().equals("boss")) {
                Block bossPos = enemy.getPosition();

                int dx = Integer.compare(heroPos.getColumn(), bossPos.getColumn());
                int dy = Integer.compare(heroPos.getLine(), bossPos.getLine());

                Projectile p = new Projectile(bossPos, dx, dy);
                cMap.getProjectiles().add(p);
            }
        }
    }
	
	public void updateProjectiles() {
	    Map activeMap = display.getActiveMap();

	    if (!(activeMap instanceof CombatMap cMap)) return;

	    ArrayList<Projectile> toRemove = new ArrayList<>();
	    for (Projectile p : cMap.getProjectiles()) {
	        p.move(cMap);
	        if (!p.isActive()) {
	            toRemove.add(p);
	            continue;
	        }

	        if (p.getPosition().equals(hero.getPosition())) {
	            applyProjectileHit();
	            p.deactivate();
	            toRemove.add(p);
	        }
	    }

	    cMap.getProjectiles().removeAll(toRemove);
	}


	
	public GameDisplay getDisplay() {
		return display;
	}
	
	public void onRepaintTick() {
	    repaintCounter++;

	    if (repaintCounter % 25 == 0) {
	        updateProjectiles();
	    }
	}
	
	public void exitShop(MainGUI gui) {
	    Map map = display.getMap();
	    Block shopBlock = map.getShopPosition();
	    Block exitBlock = findAdjacentFreeBlock(shopBlock, map);
	    if (exitBlock == null) exitBlock = shopBlock;

	    map.setAllHousesOnFire();

	    display.returnToMainMap(exitBlock);
	}

	private Block findAdjacentFreeBlock(Block center, Map map) {
	    int line = center.getLine();
	    int col = center.getColumn();

	    for (int dl = -1; dl <= 1; dl++) {
	        for (int dc = -1; dc <= 1; dc++) {
	            if (dl == 0 && dc == 0) continue;
	            int newLine = line + dl;
	            int newCol = col + dc;

	            if (newLine >= 0 && newCol >= 0 &&
	                newLine < map.getLineCount() &&
	                newCol < map.getColumnCount()) {

	                Block adj = map.getBlock(newLine, newCol);
	                if (!map.isBlocked(adj)) {
	                    return adj;
	                }
	            }
	        }
	    }
	    return null;
	}
}
