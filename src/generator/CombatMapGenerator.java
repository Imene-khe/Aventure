package generator;

import data.map.CombatMap;
import data.map.Block;
import java.util.Random;

public class CombatMapGenerator {

    public static void generateTerrain(CombatMap map) {
        int centerStartLine = (map.getLineCount() - 15) / 2;
        int centerStartCol = (map.getColumnCount() - 15) / 2;
        int bridgeCol = centerStartCol + 7;

        map.setCenterStartLine(centerStartLine);
        map.setCenterStartCol(centerStartCol);
        map.setBridgeCol(bridgeCol);

        for (int line = 0; line < map.getLineCount(); line++) {
            for (int col = 0; col < map.getColumnCount(); col++) {
                Block block = map.getBlock(line, col);
                map.getStaticTerrain().put(block, "black");
                map.setTerrainBlocked(block, true);
            }
        }

        for (int line = centerStartLine; line < centerStartLine + 15; line++) {
            for (int col = centerStartCol; col < centerStartCol + 15; col++) {
                if (isInsideMap(map, line, col)) {
                    Block block = map.getBlock(line, col);
                    map.getStaticTerrain().put(block, "floorCave");
                    map.setTerrainBlocked(block, false);
                }
            }
        }

        for (int i = 0; i < 15; i++) {
            if (isInsideMap(map, centerStartLine, centerStartCol + i)) {
                Block top = map.getBlock(centerStartLine, centerStartCol + i);
                map.getStaticTerrain().put(top, "horizontalBorder");
                map.setTerrainBlocked(top, true);
            }
            if (i != 7 && isInsideMap(map, centerStartLine + 14, centerStartCol + i)) {
                Block bottom = map.getBlock(centerStartLine + 14, centerStartCol + i);
                map.getStaticTerrain().put(bottom, "horizontalBorder");
                map.setTerrainBlocked(bottom, true);
            }
            if (isInsideMap(map, centerStartLine + i, centerStartCol)) {
                Block left = map.getBlock(centerStartLine + i, centerStartCol);
                map.getStaticTerrain().put(left, "verticalBorder");
                map.setTerrainBlocked(left, true);
            }
            if (isInsideMap(map, centerStartLine + i, centerStartCol + 14)) {
                Block right = map.getBlock(centerStartLine + i, centerStartCol + 14);
                map.getStaticTerrain().put(right, "verticalBorder");
                map.setTerrainBlocked(right, true);
            }
        }

        Block bridgeExit = map.getBlock(centerStartLine + 7, centerStartCol + 14);
        map.setTerrainBlocked(bridgeExit, false);

        map.getEntranceBridgeBlocks().clear();
        for (int l = centerStartLine + 15; l < map.getLineCount(); l++) {
            Block b = map.getBlock(l, bridgeCol);
            map.getStaticTerrain().put(b, "bridge");
            map.setTerrainBlocked(b, false);
            map.getEntranceBridgeBlocks().add(b);
        }

        map.getFinalBridgeBlocks().clear();
        int finalBridgeLine = centerStartLine + 7;
        int finalBridgeStartCol = centerStartCol + 15;
        for (int col = finalBridgeStartCol; col < finalBridgeStartCol + 4; col++) {
            if (isInsideMap(map, finalBridgeLine, col)) {
                Block b = map.getBlock(finalBridgeLine, col);
                map.getFinalBridgeBlocks().add(b);
            }
        }

        map.getCageZoneBlocks().clear();
        if (isInsideMap(map, finalBridgeLine - 1, finalBridgeStartCol + 4))
            map.getCageZoneBlocks().add(map.getBlock(finalBridgeLine - 1, finalBridgeStartCol + 4));
        if (isInsideMap(map, finalBridgeLine, finalBridgeStartCol + 4))
            map.getCageZoneBlocks().add(map.getBlock(finalBridgeLine, finalBridgeStartCol + 4));
        if (isInsideMap(map, finalBridgeLine + 1, finalBridgeStartCol + 4))
            map.getCageZoneBlocks().add(map.getBlock(finalBridgeLine + 1, finalBridgeStartCol + 4));
    }

    public static void generatePlatforms(CombatMap map) {
        Random rand = new Random();
        int islands = 10 + rand.nextInt(3);
        int attempts = 0;
        int centerStartLine = map.getCenterStartLine();
        int centerStartCol = map.getCenterStartCol();
        int bridgeCol = map.getBridgeCol();

        while (islands > 0 && attempts < 200) {
            int i = rand.nextInt(map.getLineCount());
            int j = rand.nextInt(map.getColumnCount());
            Block block = map.getBlock(i, j);
            if (!"black".equals(map.getStaticTerrain().get(block))) {
                attempts++;
                continue;
            }
            if (i >= centerStartLine - 1 && i <= centerStartLine + 15 &&
                j >= centerStartCol - 1 && j <= centerStartCol + 15) {
                attempts++;
                continue;
            }
            if (Math.abs(j - bridgeCol) <= 1 && i >= centerStartLine + 14) {
                attempts++;
                continue;
            }
            map.getStaticTerrain().put(block, "platformCave");
            map.setTerrainBlocked(block, false);
            islands--;
            attempts++;
        }
    }

    public static void revealFinaleZone(CombatMap map) {
        if (map.isFinaleRevealed()) return;
        map.setFinaleRevealed(true);

        for (Block b : map.getFinalBridgeBlocks()) {
            map.getStaticTerrain().put(b, "endBridge");
            map.setTerrainBlocked(b, false);
        }

        if (!map.getFinalBridgeBlocks().isEmpty()) {
            Block finalBlock = map.getFinalBridgeBlocks().get(map.getFinalBridgeBlocks().size() - 1);
            map.getStaticTerrain().put(finalBlock, "platformCave");
            map.getStaticObjects().put(finalBlock, "cage_with_princess");
            map.setTerrainBlocked(finalBlock, false);
        }
    }

    private static boolean isInsideMap(CombatMap map, int line, int col) {
        return line >= 0 && line < map.getLineCount() && col >= 0 && col < map.getColumnCount();
    }
}
