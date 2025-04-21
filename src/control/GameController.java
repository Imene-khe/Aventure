package control;

import data.item.Chest;
import data.item.Coin;
import data.item.Equipment;
import data.item.Flame;
import data.map.Block;
import data.map.HostileMap;
import data.map.Map;
import data.player.Antagonist;
import data.player.Hero;
import gui.ChestUIManager;
import gui.GameDisplay;
import gui.MainGUI;
import log.LoggerUtility;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameController {

    private static final Logger logger = LoggerUtility.getLogger(GameController.class, "text");

    private final GameDisplay display;
    private final Hero hero;
    private Map map;
    private final Map shopMap;
    private Map hostileMap;
    private boolean canTakeDamage = true;

    public GameController(GameDisplay display) {
        this.display = display;
        this.hero = display.getHero();
        this.map = display.getMap();
        this.shopMap = display.getShopMap();
        this.hostileMap = display.getHostileMap(); // ✅ Initialisé comme shopMap
   

    }


    public void moveHero(int keyCode, MainGUI gui) {
        Block current = hero.getPosition();
        int newLine = current.getLine();
        int newCol = current.getColumn();

        switch (keyCode) {
            case KeyEvent.VK_UP -> newLine--;
            case KeyEvent.VK_DOWN -> newLine++;
            case KeyEvent.VK_LEFT -> newCol--;
            case KeyEvent.VK_RIGHT -> newCol++;
            default -> { return; }
        }

        Map activeMap = display.isInShop() ? shopMap :
                        (display.isInHostileMap() ? hostileMap : map);

        int blockSize = display.getBlockSize();
        int visibleHeight = display.getHeight();
        int maxVisibleLines = visibleHeight / blockSize;

        if (newLine < 0 || newCol < 0 ||
            newLine >= Math.min(activeMap.getLineCount(), maxVisibleLines) ||
            newCol >= activeMap.getColumnCount()) {
            return;
        }

        Block newBlock = activeMap.getBlock(newLine, newCol);
        moveHero(newBlock, gui);
    }

    public void moveHero(Block newPosition, MainGUI mainGUI) {
        if (display.isGameOver()) return;

        Map activeMap = display.isInShop() ? shopMap :
                        (display.isInHostileMap() ? hostileMap : map);

        if (activeMap.isBlocked(newPosition)) return;

        hero.setPosition(newPosition);

        if (!display.isInShop()) {
            checkCoinCollection(mainGUI);
            checkEnemyCollision();
        }

        display.repaint();
    }

    public void checkCoinCollection(MainGUI mainGUI) {
        Map activeMap = display.isInShop() ? shopMap :
                        (display.isInHostileMap() ? hostileMap : map);

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

    public Chest tryOpenNearbyChest() {
        Map activeMap = display.isInShop() ? shopMap :
                        (display.isInHostileMap() ? hostileMap : map);

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
                                    display.enterHostileMap(); 
                                    this.hostileMap = display.getHostileMap();
                                    MainGUI.getInstance().setDialogueActive(false); // ✅ débloque les touches
                                    display.requestFocusInWindow();
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
        Map activeMap = display.isInHostileMap() ? hostileMap : map;

        if (activeMap instanceof HostileMap hMap) {
            for (Antagonist enemy : hMap.getAntagonistList()) {
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

    public void applyHeroDamage() {
        if (!canTakeDamage) return;

        hero.takeDamage(10);
        canTakeDamage = false;

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                canTakeDamage = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        if (hero.getHealth() <= 0) {
            display.setGameOver(true);
            logger.error("☠️ GAME OVER ! Le héros est mort.");
            JOptionPane.showMessageDialog(display, "☠️ GAME OVER ! Le héros est mort.");
            System.exit(0);
        }
    }


    public void tryOpenChest(MainGUI gui) {
        Chest chest = tryOpenNearbyChest();
        if (chest != null) {
            ChestUIManager chestUI = new ChestUIManager(gui);

            chestUI.setOnOrbTakenCallback(() -> {
                gui.getQuestManager().updateQuest("Trouver l'orbe", 1);
                display.enterHostileMap();
                this.hostileMap = display.getHostileMap();
                MainGUI.getInstance().setDialogueActive(false); // ✅ débloque les touches
            });


            chestUI.displayChestContents(chest);
            gui.requestFocusInWindow();
        } else {
            logger.warn("❌ Aucun coffre à proximité.");
        }
    }


    public boolean tryInteractWithNPC(MainGUI gui) {
        Block heroPos = hero.getPosition();

        for (int dl = -2; dl <= 2; dl++) {
            for (int dc = -2; dc <= 2; dc++) {
                if (dl == 0 && dc == 0) continue;

                int line = heroPos.getLine() + dl;
                int col = heroPos.getColumn() + dc;

                Map activeMap = display.isInShop() ? shopMap :
                                (display.isInHostileMap() ? hostileMap : map);

                if (line >= 0 && col >= 0 && line < activeMap.getLineCount() && col < activeMap.getColumnCount()) {
                    Block block = activeMap.getBlock(line, col);
                    String object = activeMap.getStaticObjects().get(block);

                    if ("merchant".equals(object)) {
                        logger.info("🗨️ Interaction avec le marchand détectée.");

                        String message = "👴 Le marchand te salue avec un sourire.\n\nQue veux-tu lui dire ?";
                        String[] options = {
                            "💰 Bonjour, j'ai cru comprendre que vous aviez perdu une bourse d'or...",
                            "👋 Bonjour l'ami. Alors vous êtes nouveau dans la région ?"
                        };

                        int choix = JOptionPane.showOptionDialog(
                            null,
                            message,
                            "Dialogue avec le marchand",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            options,
                            options[0]
                        );

                        if (choix == 0) gui.triggerDialogue("enter_shop_give_gold");
                        else if (choix == 1) gui.triggerDialogue("enter_shop_chat");

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


    public void enterShop(MainGUI gui) {
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
        Map activeMap = display.isInShop() ? shopMap :
                        (display.isInHostileMap() ? hostileMap : map);

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
    
    

    public boolean isAdjacent(Block a, Block b) {
        int dx = Math.abs(a.getLine() - b.getLine());
        int dy = Math.abs(a.getColumn() - b.getColumn());
        return (dx + dy) == 1;
    }
    
    
    public void moveEnemiesTowardsHero() {
        if (!display.isInHostileMap()) return;

        if (hostileMap instanceof HostileMap hMap) {
            Block heroPos = hero.getPosition();
            ArrayList<Block> occupied = new ArrayList<>();

            for (Antagonist enemy : hMap.getAntagonistList()) {
                Block next = computeNextEnemyBlock(enemy, heroPos, hostileMap, occupied);
                enemy.setPosition(next);
                occupied.add(next); // réserve la case
            }

            checkEnemyCollision();
            display.repaint();
        }
    }


    private Block computeNextEnemyBlock(Antagonist enemy, Block heroPos, Map map, ArrayList<Block> occupied) {
        Block current = enemy.getPosition();
        int dLine = heroPos.getLine() - current.getLine();
        int dCol = heroPos.getColumn() - current.getColumn();
        int nextLine = current.getLine();
        int nextCol = current.getColumn();

        if (Math.abs(dLine) > Math.abs(dCol)) {
            nextLine += Integer.compare(dLine, 0);
        } else if (dCol != 0) {
            nextCol += Integer.compare(dCol, 0);
        }

        Block nextBlock = map.getBlock(nextLine, nextCol);
        if (!map.isBlocked(nextBlock) && !occupied.contains(nextBlock)) {
            return nextBlock;
        }

        return current; // reste en place
    }



}
