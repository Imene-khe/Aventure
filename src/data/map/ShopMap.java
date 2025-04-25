package data.map;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import data.item.chest.ChestManager;
import gui.GameDisplay;

public class ShopMap extends Map {

    public ShopMap(int lineCount, int columnCount) {
        super(lineCount, columnCount);
        this.setEnemies(new HashMap<>());
        this.setCoins(new ArrayList<>());
        this.setChestManager(new ChestManager());
        this.setFlames(new ArrayList<>());

        setupShopLayout();
    }

    public void setupShopLayout() {
        generateWallsAndFloor();
        placeMerchantAndCounter();
        placeBookshelves();
        placeCarpet();
        placeTables(6);
        placePeopleAroundTables(2, 3);
        placeMusicStageWithMusicians();        
    }

    public void generateWallsAndFloor() {
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Block block = blocks[i][j];
                if (i == 0 || i == lineCount - 1 || j == 0 || j == columnCount - 1) {
                    staticTerrain.put(block, "lightWall");
                } else {
                    staticTerrain.put(block, "shopFloor");
                }
            }
        }
    }
    
    private void placeMerchantAndCounter() {
        int barRow = 3;
        int merchantRow = barRow - 1;
        int merchantCol = columnCount / 2;

        Block barLeft = blocks[barRow][merchantCol - 1];
        Block barCenter = blocks[barRow][merchantCol];
        Block barRight = blocks[barRow][merchantCol + 1];
        Block merchantBlock = blocks[merchantRow][merchantCol];

        staticObjects.put(barLeft, "bar");
        staticObjects.put(barCenter, "bar");
        staticObjects.put(barRight, "bar");
        staticObjects.put(merchantBlock, "merchant");
    }

    private void placeBookshelves() {
        int barRow = 3;
        int merchantRow = barRow - 1;
        int merchantCol = columnCount / 2;

        Block merchantBlock = blocks[merchantRow][merchantCol];
        Block barLeft = blocks[barRow][merchantCol - 1];
        Block barCenter = blocks[barRow][merchantCol];
        Block barRight = blocks[barRow][merchantCol + 1];

        for (int i = 1; i < lineCount - 1; i++) {
            for (int j = 1; j < columnCount - 1; j++) {
                Block block = blocks[i][j];
                boolean isEdgeInner = i == 1 || i == lineCount - 2 || j == 1 || j == columnCount - 2;
                boolean isEntry = i == lineCount - 2 && j >= 6 && j <= 8;
                boolean isMerchantZone = block.equals(merchantBlock) || block.equals(barLeft)
                        || block.equals(barCenter) || block.equals(barRight);

                if (isEdgeInner && !isEntry && !isMerchantZone) {
                    staticObjects.put(block, "bookshelf");
                }
            }
        }
    }

    
    private void placeCarpet() {
        int carpetStartRow = (lineCount - 7) / 2;
        int carpetStartCol = (columnCount - 7) / 2;

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Block block = blocks[carpetStartRow + i][carpetStartCol + j];
                if (!staticObjects.containsKey(block) && (i + j) % 2 == 0) {
                    staticTerrain.put(block, "carpet");
                }
            }
        }
    }

    
    private void placeTables(int count) {
        ArrayList<Block> candidates = new ArrayList<>();

        for (int i = 2; i < lineCount - 2; i++) {
            for (int j = 2; j < columnCount - 2; j++) {
                Block b = blocks[i][j];
                String terrain = staticTerrain.getOrDefault(b, "");
                boolean libre = !staticObjects.containsKey(b)
                             && !terrain.equals("carpet")
                             && !terrain.equals("lightWall");
                if (libre) candidates.add(b);
            }
        }

        Collections.shuffle(candidates, new Random());
        for (int i = 0; i < Math.min(count, candidates.size()); i++) {
            staticObjects.put(candidates.get(i), "table");
        }
    }
    
    private void placePeopleAroundTables(int min, int max) {
        Random rand = new Random();

        for (int i = 1; i < lineCount - 1; i++) {
            for (int j = 1; j < columnCount - 1; j++) {
                Block table = blocks[i][j];
                if (!"table".equals(staticObjects.get(table))) continue;

                Block[] adjacents = new Block[8];
                int index = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        Block b = blocks[i + dx][j + dy];

                        String terrain = staticTerrain.getOrDefault(b, "");
                        boolean libre = staticObjects.get(b) == null &&
                                        !"lightWall".equals(terrain) &&
                                        !"carpet".equals(terrain);

                        if (libre) {
                            adjacents[index++] = b;
                        }
                    }
                }

                // 🛡️ Protection anti-crash ici
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
                        staticObjects.put(chosen, type);

                        adjacents[r] = adjacents[--index];
                        placed++;
                    }
                }
            }
        }
    }

    private void placeMusicStageWithMusicians() {
        int startRow = lineCount - 4;     // Ligne 11 si 15 lignes
        int startCol = columnCount - 5;   // Colonne 10 si 15 colonnes

        // 1. 🎤 Sol musical (2 lignes x 3 colonnes)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                Block b = blocks[startRow + i][startCol + j];
                staticTerrain.put(b, "music_stage");
                staticObjects.remove(b); // au cas où un autre objet est là
            }
        }

        // 2. 👨‍🎤 Musiciens sur les tuiles du bas
        String[] musicians = { "musician1", "musician2", "musician3" };

        for (int j = 0; j < 3; j++) {
            Block b = blocks[startRow + 1][startCol + j]; // ligne du bas
            staticObjects.put(b, musicians[j]);
        }
    }


   
}
