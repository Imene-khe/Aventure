package generator;

import data.map.ShopMap;
import data.map.Block;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ShopMapGenerator {

    public static void generateLayout(ShopMap shopMap) {
        generateWallsAndFloor(shopMap);
        placeMerchantAndCounter(shopMap);
        placeBookshelves(shopMap);
        placeCarpet(shopMap);
        placeTables(shopMap, 6);
        placePeopleAroundTables(shopMap, 2, 3);
        placeMusicStageWithMusicians(shopMap);
    }

    public static void generateWallsAndFloor(ShopMap shopMap) {
        for (int i = 0; i < shopMap.getLineCount(); i++) {
            for (int j = 0; j < shopMap.getColumnCount(); j++) {
                Block block = shopMap.getBlock(i, j);
                if (i == 0 || i == shopMap.getLineCount() - 1 || j == 0 || j == shopMap.getColumnCount() - 1) {
                    shopMap.getStaticTerrain().put(block, "lightWall");
                } else {
                    shopMap.getStaticTerrain().put(block, "shopFloor");
                }
            }
        }
    }

    public static void placeMerchantAndCounter(ShopMap shopMap) {
        int barRow = 3;
        int merchantRow = barRow - 1;
        int merchantCol = shopMap.getColumnCount() / 2;

        Block barLeft = shopMap.getBlock(barRow, merchantCol - 1);
        Block barCenter = shopMap.getBlock(barRow, merchantCol);
        Block barRight = shopMap.getBlock(barRow, merchantCol + 1);
        Block merchantBlock = shopMap.getBlock(merchantRow, merchantCol);

        shopMap.getStaticObjects().put(barLeft, "bar");
        shopMap.getStaticObjects().put(barCenter, "bar");
        shopMap.getStaticObjects().put(barRight, "bar");
        shopMap.getStaticObjects().put(merchantBlock, "merchant");
    }

    public static void placeBookshelves(ShopMap shopMap) {
        int barRow = 3;
        int merchantRow = barRow - 1;
        int merchantCol = shopMap.getColumnCount() / 2;

        Block merchantBlock = shopMap.getBlock(merchantRow, merchantCol);
        Block barLeft = shopMap.getBlock(barRow, merchantCol - 1);
        Block barCenter = shopMap.getBlock(barRow, merchantCol);
        Block barRight = shopMap.getBlock(barRow, merchantCol + 1);

        for (int i = 1; i < shopMap.getLineCount() - 1; i++) {
            for (int j = 1; j < shopMap.getColumnCount() - 1; j++) {
                Block block = shopMap.getBlock(i, j);
                boolean isEdgeInner = i == 1 || i == shopMap.getLineCount() - 2 || j == 1 || j == shopMap.getColumnCount() - 2;
                boolean isEntry = i == shopMap.getLineCount() - 2 && j >= 6 && j <= 8;
                boolean isMerchantZone = block.equals(merchantBlock) || block.equals(barLeft)
                        || block.equals(barCenter) || block.equals(barRight);

                if (isEdgeInner && !isEntry && !isMerchantZone) {
                    shopMap.getStaticObjects().put(block, "bookshelf");
                }
            }
        }
    }

    public static void placeCarpet(ShopMap shopMap) {
        int carpetStartRow = (shopMap.getLineCount() - 7) / 2;
        int carpetStartCol = (shopMap.getColumnCount() - 7) / 2;

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Block block = shopMap.getBlock(carpetStartRow + i, carpetStartCol + j);
                if (!shopMap.getStaticObjects().containsKey(block) && (i + j) % 2 == 0) {
                    shopMap.getStaticTerrain().put(block, "carpet");
                }
            }
        }
    }

    public static void placeTables(ShopMap shopMap, int count) {
        ArrayList<Block> candidates = new ArrayList<>();

        for (int i = 2; i < shopMap.getLineCount() - 2; i++) {
            for (int j = 2; j < shopMap.getColumnCount() - 2; j++) {
                Block b = shopMap.getBlock(i, j);
                String terrain = shopMap.getStaticTerrain().getOrDefault(b, "");
                boolean libre = !shopMap.getStaticObjects().containsKey(b)
                             && !terrain.equals("carpet")
                             && !terrain.equals("lightWall");
                if (libre) candidates.add(b);
            }
        }

        Collections.shuffle(candidates, new Random());
        for (int i = 0; i < Math.min(count, candidates.size()); i++) {
            shopMap.getStaticObjects().put(candidates.get(i), "table");
        }
    }

    public static void placePeopleAroundTables(ShopMap shopMap, int min, int max) {
        Random rand = new Random();

        for (int i = 1; i < shopMap.getLineCount() - 1; i++) {
            for (int j = 1; j < shopMap.getColumnCount() - 1; j++) {
                Block table = shopMap.getBlock(i, j);
                if (!"table".equals(shopMap.getStaticObjects().get(table))) continue;

                Block[] adjacents = new Block[8];
                int index = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        Block b = shopMap.getBlock(i + dx, j + dy);

                        String terrain = shopMap.getStaticTerrain().getOrDefault(b, "");
                        boolean libre = shopMap.getStaticObjects().get(b) == null &&
                                        !"lightWall".equals(terrain) &&
                                        !"carpet".equals(terrain);

                        if (libre) {
                            adjacents[index++] = b;
                        }
                    }
                }

                if (index == 0) continue;

                int totalToPlace = min;
                if (index > min) {
                    totalToPlace += rand.nextInt(Math.min(max - min + 1, index - min + 1));
                }

                int placed = 0;
                for (int k = 0; k < 8 && placed < totalToPlace; k++) {
                    int r = rand.nextInt(index);
                    Block chosen = adjacents[r];

                    if (chosen != null) {
                        String type = "villager" + (1 + rand.nextInt(5));
                        shopMap.getStaticObjects().put(chosen, type);

                        adjacents[r] = adjacents[--index];
                        placed++;
                    }
                }
            }
        }
    }

    public static void placeMusicStageWithMusicians(ShopMap shopMap) {
        int startRow = shopMap.getLineCount() - 4;
        int startCol = shopMap.getColumnCount() - 5;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                Block b = shopMap.getBlock(startRow + i, startCol + j);
                shopMap.getStaticTerrain().put(b, "music_stage");
                shopMap.getStaticObjects().remove(b);
            }
        }

        String[] musicians = { "musician1", "musician2", "musician3" };

        for (int j = 0; j < 3; j++) {
            Block b = shopMap.getBlock(startRow + 1, startCol + j);
            shopMap.getStaticObjects().put(b, musicians[j]);
        }
    }
}
