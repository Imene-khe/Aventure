package control;

import data.item.Chest;
import data.item.Coin;
import data.item.Equipment;
import data.item.Flame;
import data.item.Inventory;
import data.map.Block;
import data.map.Map;
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
    private final Map map;
    private final Map shopMap;
    private boolean canTakeDamage = true;

    public GameController(GameDisplay display) {
        this.display = display;
        this.hero = display.getHero();
        this.map = display.getMap();
        this.shopMap = display.getShopMap();
    }

    public void moveHero(Block newPosition, MainGUI mainGUI) {
        if (display.isGameOver()) return;

        Map activeMap = display.isInShop() ? shopMap : map;
        if (activeMap.isBlocked(newPosition)) return;

        hero.setPosition(newPosition);

        if (!display.isInShop()) {
            checkCoinCollection(mainGUI);
            checkEnemyCollision();
        }

        display.repaint();
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

        Map activeMap = display.isInShop() ? shopMap : map;
        if (newLine < 0 || newCol < 0 || newLine >= activeMap.getLineCount() || newCol >= activeMap.getColumnCount()) return;

        Block newBlock = activeMap.getBlock(newLine, newCol);
        moveHero(newBlock, gui);
    }

    public void checkCoinCollection(MainGUI mainGUI) {
        Map activeMap = display.isInShop() ? shopMap : map;
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
        Map activeMap = display.isInShop() ? shopMap : map;
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

                        // ✅ Vérifie si le coffre contient l'orbe
                        if (chest != null && chest.getInventory() != null) {
                            boolean hasOrb = false;
                            for (Equipment eq : chest.getInventory().getEquipments()) {
                                if ("orb".equalsIgnoreCase(eq.getName())) {
                                    hasOrb = true;
                                    break;
                                }
                            }

                            if (hasOrb) {
                                // ✅ Fenêtre de confirmation
                                int response = JOptionPane.showConfirmDialog(
                                    display,
                                    "🌟 Vous avez trouvé l'Orbe légendaire !\nSouhaitez-vous poursuivre l'aventure ?",
                                    "Poursuivre l'aventure",
                                    JOptionPane.YES_NO_OPTION
                                );

                                if (response == JOptionPane.YES_OPTION) {
                                    display.enterHostileMap(); // 🌋 Passage à la map hostile
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

        for (Block enemyBlock : map.getEnemies().keySet()) {
            if (enemyBlock.equals(heroPos)) {
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
        }
    }

    public void tryOpenChest(MainGUI gui) {
        Chest chest = tryOpenNearbyChest();
        if (chest != null) {
            new ChestUIManager(gui).displayChestContents(chest);

            // 🔍 Vérifie si le coffre contient l’orbe légendaire
            Inventory chestInventory = chest.getInventory();
            for (Equipment eq : chestInventory.getEquipments()) {
                if ("orb".equalsIgnoreCase(eq.getName())) {
                    gui.getQuestManager().updateQuest("Trouver l'orbe", 1); // ✅ Mise à jour de la quête
                    break;
                }
            }

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

                Map activeMap = display.isInShop() ? shopMap : map;
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

                        if (choix == 0) {
                            gui.triggerDialogue("enter_shop_give_gold");
                        } else if (choix == 1) {
                            gui.triggerDialogue("enter_shop_chat");
                        }

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
        display.enterShop(); // Change de map
        gui.triggerDialogue("enter_shop"); // ✅ Dialogue automatique du marchand
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
                        enterShop(gui); // au lieu de display.enterShop()
                        return true;
                    }

                }
            }
        }

        return false;
    }
    
    public void tryExtinguishFlame(MainGUI gui) {
        Block heroPos = display.getHero().getPosition();
        Map map = display.getMap();

        for (Flame flame : map.getFlames()) {
            Block flamePos = flame.getPosition();

            int dx = Math.abs(heroPos.getLine() - flamePos.getLine());
            int dy = Math.abs(heroPos.getColumn() - flamePos.getColumn());

            if (dx <= 1 && dy <= 1 && flame.isActive()) {
                flame.extinguish();
                map.getStaticObjects().put(flamePos, "house");
                gui.repaint();
                gui.requestFocusInWindow();

                // ✅ Vérifie si toutes les flammes sont désormais éteintes
                boolean allExtinguished = map.getFlames().stream().noneMatch(Flame::isActive);
                if (allExtinguished) {
                    gui.getQuestManager().updateQuest("Eteindre les flammes", 1); // valide la quête
                }

                return;
            }
        }

        // Plus de JOptionPane ici, car tu ne veux pas de pop-up
    }


}
