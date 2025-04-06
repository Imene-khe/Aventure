package control;
import data.item.Chest;
import data.item.Coin;
import data.map.Block;
import data.map.Map;
import data.player.Hero;
import gui.ChestUIManager;
import gui.GameDisplay;
import gui.MainGUI;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameController {

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

        int blockSize = display.getBlockSize();
        int visibleHeight = display.getHeight(); // hauteur réelle allouée au GameDisplay

        // ✅ Calcul du nombre de lignes visibles (dans GameDisplay uniquement)
        int maxVisibleLines = visibleHeight / blockSize;

        // 🔒 Empêche de sortir de l'écran ou de la map (en bas)
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

        Map activeMap = display.isInShop() ? shopMap : map;
        if (activeMap.isBlocked(newPosition)) return;

        hero.setPosition(newPosition);

        if (!display.isInShop()) {
            checkCoinCollection(mainGUI);
            checkEnemyCollision();
        }

        display.repaint();
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
                        return activeMap.getChestManager().getChests().get(adjacent);
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
            gui.requestFocusInWindow();
        } else {
            System.out.println("❌ Aucun coffre à proximité.");
        }
    }

    public boolean tryInteractWithNPC(MainGUI gui) {
        Block heroPos = hero.getPosition();

        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dl == 0 && dc == 0) continue;

                int line = heroPos.getLine() + dl;
                int col = heroPos.getColumn() + dc;

                Map activeMap = display.isInShop() ? shopMap : map;
                if (line >= 0 && col >= 0 && line < activeMap.getLineCount() && col < activeMap.getColumnCount()) {
                    Block block = activeMap.getBlock(line, col);
                    String object = activeMap.getStaticObjects().get(block);

                    if ("merchant".equals(object)) {
                        JOptionPane.showMessageDialog(display, "👴 Bienvenue dans ma boutique !");
                        return true;
                    }

                    if (!display.isInShop() && "shop".equals(object)) {
                        display.enterShop();
                        return true;
                    }
                }
            }
        }

        return false;
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
                        display.enterShop();
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
