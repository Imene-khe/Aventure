package generator;

import org.apache.log4j.Logger;
import log.LoggerUtility;


import java.util.ArrayList;
import java.util.Random;

import data.map.Block;
import data.map.HostileMap;
import data.player.Antagonist;

public class HostileMapGenerator {
	
	private static final Logger logger = LoggerUtility.getLogger(HostileMapGenerator.class, "text");


    public static void generateTerrain(HostileMap map) {
        for (int line = 0; line < map.getLineCount(); line++) {
            for (int col = 0; col < map.getColumnCount(); col++) {
                Block block = map.getBlock(line, col);
                map.getStaticTerrain().put(block, "floor1");
                map.setTerrainBlocked(block, false);
            }
        }

        for (int col = 0; col < map.getColumnCount(); col++) {
            Block top = map.getBlock(0, col);
            Block bottom = map.getBlock(map.getLineCount() - 1, col);
            map.getStaticTerrain().put(top, "lava");
            map.getStaticTerrain().put(bottom, "lava");
            map.setTerrainBlocked(top, true);
            map.setTerrainBlocked(bottom, true);
        }

        for (int line = 0; line < map.getLineCount(); line++) {
            Block left = map.getBlock(line, 0);
            Block right = map.getBlock(line, map.getColumnCount() - 1);
            map.getStaticTerrain().put(left, "lava");
            map.getStaticTerrain().put(right, "lava");
            map.setTerrainBlocked(left, true);
            map.setTerrainBlocked(right, true);
        }

        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            int line = rand.nextInt(map.getLineCount());
            int col = rand.nextInt(map.getColumnCount());

            if (line <= 2 || line >= map.getLineCount() - 3 || col <= 2 || col >= map.getColumnCount() - 3) {
                Block block = map.getBlock(line, col);
                map.getStaticTerrain().put(block, "lava");
                map.setTerrainBlocked(block, true);
            }
        }
    }

    public static void generateObjects(HostileMap map) {
        Random rng = new Random();
        int numBosquets = 10;

        for (int b = 0; b < numBosquets; b++) {
            int centerLine = rng.nextInt(map.getLineCount() - 6) + 3;
            int centerCol = rng.nextInt(map.getColumnCount() - 6) + 3;
            int radius = rng.nextInt(2) + 2;

            for (int i = centerLine - radius; i <= centerLine + radius; i++) {
                for (int j = centerCol - radius; j <= centerCol + radius; j++) {
                    if (i < 0 || j < 0 || i >= map.getLineCount() || j >= map.getColumnCount()) continue;

                    Block block = map.getBlock(i, j);
                    String terrainType = map.getStaticTerrain().get(block);

                    double dx = j - centerCol;
                    double dy = i - centerLine;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    double prob = 1.0 - (distance / radius);

                    if (terrainType != null && terrainType.startsWith("floor") &&
                        !map.getStaticObjects().containsKey(block) &&
                        !map.getShelterBlocks().contains(block) &&
                        rng.nextDouble() < prob * 0.8) {

                        int type = rng.nextInt(3) + 1;
                        map.getStaticObjects().put(block, "deadTree" + type);
                        map.setTerrainBlocked(block, true);
                    }
                }
            }
        }

        for (int i = 0; i < map.getLineCount(); i++) {
            for (int j = 0; j < map.getColumnCount(); j++) {
                Block block = map.getBlock(i, j);
                String terrainType = map.getStaticTerrain().get(block);

                if (terrainType != null && terrainType.startsWith("floor") &&
                    !map.getStaticObjects().containsKey(block) &&
                    !map.getShelterBlocks().contains(block) &&
                    Math.random() < 0.03) {

                    map.getStaticObjects().put(block, "rock");
                    map.setTerrainBlocked(block, true);
                }
            }
        }
    }

    public static void generateCave(HostileMap map) {
        int baseLine = 14;
        int baseCol = 17;

        Block top = map.getBlock(baseLine, baseCol + 1);
        Block shadow = map.getBlock(baseLine + 1, baseCol + 1);
        Block leftTop = map.getBlock(baseLine, baseCol);
        Block leftBottom = map.getBlock(baseLine + 1, baseCol);
        Block rightTop = map.getBlock(baseLine, baseCol + 2);
        Block rightBottom = map.getBlock(baseLine + 1, baseCol + 2);

        map.getStaticObjects().put(leftTop, "cave_left");
        map.getStaticObjects().put(top, "cave_top");
        map.getStaticObjects().put(rightTop, "cave_right");

        map.getStaticObjects().put(leftBottom, "cave_bottom");
        map.getStaticObjects().put(shadow, "cave_shadow");
        map.getStaticObjects().put(rightBottom, "cave_bottom");

        map.setTerrainBlocked(leftTop, true);
        map.setTerrainBlocked(top, true);
        map.setTerrainBlocked(rightTop, true);
        map.setTerrainBlocked(leftBottom, true);
        map.setTerrainBlocked(shadow, true);
        map.setTerrainBlocked(rightBottom, true);

        map.setCaveEntry(shadow); 
    }

    public static void generateSafeShelter(HostileMap map) {
        int centerLine = 4;
        int centerCol = map.getColumnCount() - 6;
        int radius = 2;

        for (int i = centerLine - radius - 1; i <= centerLine + radius + 1; i++) {
            for (int j = centerCol - radius - 1; j <= centerCol + radius + 1; j++) {
                if (i < 0 || j < 0 || i >= map.getLineCount() || j >= map.getColumnCount()) continue;
                Block block = map.getBlock(i, j);
                String terrain = map.getStaticTerrain().get(block);
                if (terrain == null || terrain.equals("lava")) continue;
                map.getStaticObjects().remove(block);
                double dx = j - centerCol;
                double dy = i - centerLine;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                    if (i == centerLine + radius && j == centerCol) continue;

                    map.getStaticObjects().put(block, "toprock");
                    map.setTerrainBlocked(block, true);
                    map.getShelterBlocks().add(block);
                }
            }
        }

        Block entry = map.getBlock(centerLine + radius, centerCol);
        map.getStaticObjects().remove(entry);
        map.getStaticTerrain().put(entry, "floor1");
        map.setTerrainBlocked(entry, false);
        map.getShelterBlocks().add(entry);

        Block center = map.getBlock(centerLine, centerCol);
        map.getStaticObjects().put(center, "campfire_off");
        map.setTerrainBlocked(center, true);
        map.getShelterBlocks().add(center);
    }

    public static void generateEnemies(HostileMap map) {
        ArrayList<Block> freeBlocks = map.getFreeBlocks();

        ArrayList<Block> toRemove = new ArrayList<>();
        for (Block block : freeBlocks) {
            for (Block shelter : map.getShelterBlocks()) {
                int dx = Math.abs(block.getLine() - shelter.getLine());
                int dy = Math.abs(block.getColumn() - shelter.getColumn());
                if ((dx + dy) <= 1) {
                    toRemove.add(block);
                    break;
                }
            }
        }
        freeBlocks.removeAll(toRemove);

        Random random = new Random();
        int maxEnemies = 10;

        map.getAntagonistList().clear();
        map.getAntagonistTypes().clear();

        for (int i = 0; i < maxEnemies && !freeBlocks.isEmpty(); i++) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index);
            String type = Math.random() < 0.5 ? "skeleton" : "slime";

            Antagonist enemy = new Antagonist(block, type, null);
            map.getAntagonistList().add(enemy);
            map.getAntagonistTypes().put(enemy, type);
        }
    }


    public static void generateSymbols(HostileMap map) {
        Random random = new Random();
        int runeCount = 3;
        int placed = 0;

        while (placed < runeCount) {
            int line = random.nextInt(map.getLineCount());
            int col = random.nextInt(map.getColumnCount());
            Block block = map.getBlock(line, col);

            boolean isFree = map.getStaticTerrain().get(block) != null &&
                             map.getStaticTerrain().get(block).startsWith("floor") &&
                             !map.getStaticObjects().containsKey(block);

            if (isFree) {
                String runeName = "rune" + (placed + 1);
                map.getStaticObjects().put(block, runeName);
                map.getRuneBlocks().add(block);
                map.setTerrainBlocked(block, false);
                placed++;
                logger.info(runeName + " placée sur le bloc " + block);
            }
        }
    }
}
