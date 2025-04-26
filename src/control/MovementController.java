package control;

import data.map.Block;
import data.map.CombatMap;
import data.map.HostileMap;
import data.map.Map;
import data.player.Antagonist;
import data.player.Hero;
import gui.GameDisplay;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MovementController {

    private final GameDisplay display;
    private final Hero hero;

    public MovementController(Hero hero, GameDisplay display) {
        this.hero = hero;
        this.display = display;
    }
    
    public void moveHero(int keyCode) {
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

        Map activeMap = display.getActiveMap();
        int blockSize = display.getBlockSize();
        int visibleHeight = display.getHeight();
        int maxVisibleLines = visibleHeight / blockSize;

        if (newLine < 0 || newCol < 0 ||
            newLine >= Math.min(activeMap.getLineCount(), maxVisibleLines) ||
            newCol >= activeMap.getColumnCount()) {
            return;
        }

        Block newBlock = activeMap.getBlock(newLine, newCol);
        moveHeroTo(newBlock); 
    }

    public void moveHeroTo(Block newPosition) {
        Map activeMap = display.getActiveMap();
        if (activeMap.isBlocked(newPosition)) return;
        hero.setPosition(newPosition);
        display.repaint();
    }

    public boolean isAdjacent(Block a, Block b) {
        int dx = Math.abs(a.getLine() - b.getLine());
        int dy = Math.abs(a.getColumn() - b.getColumn());
        return (dx + dy) == 1;
    }

    public void moveEnemiesTowardsHero() {
        if (!display.isInHostileMap() && !display.isInCombatMap()) return;

        Block heroPos = hero.getPosition();
        ArrayList<Block> occupiedBlocks = new ArrayList<>();
        Map activeMap = display.getActiveMap();

        if (activeMap instanceof HostileMap hMap) {
            int safeCenterLine = 4;
            int safeCenterCol = hMap.getColumnCount() - 6;
            int safeRadius = 2;

            for (Antagonist enemy : hMap.getAntagonistList()) {
                occupiedBlocks.add(enemy.getPosition());
            }

            for (Antagonist enemy : hMap.getAntagonistList()) {
                Block current = enemy.getPosition();
                int dx = Math.abs(current.getLine() - safeCenterLine);
                int dy = Math.abs(current.getColumn() - safeCenterCol);
                if ((dx + dy) <= safeRadius) continue;

                Block nextBlock = computeNextEnemyBlock(enemy, heroPos, hMap, occupiedBlocks);
                if (!nextBlock.equals(current)) {
                    enemy.setPosition(nextBlock);
                    occupiedBlocks.add(nextBlock);
                }
            }

        } else if (activeMap instanceof CombatMap cMap) {
            for (Antagonist enemy : cMap.getAntagonists()) {
                occupiedBlocks.add(enemy.getPosition());
            }

            for (Antagonist enemy : cMap.getAntagonists()) {
                Block current = enemy.getPosition();
                Block nextBlock = computeNextEnemyBlock(enemy, heroPos, cMap, occupiedBlocks);
                if (!nextBlock.equals(current)) {
                    enemy.setPosition(nextBlock);
                    occupiedBlocks.add(nextBlock);
                }
            }
        }

        display.repaint();
    }

    public Block computeNextEnemyBlock(Antagonist enemy, Block heroPos, Map map, ArrayList<Block> occupied) {
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

        return current;
    }


}
