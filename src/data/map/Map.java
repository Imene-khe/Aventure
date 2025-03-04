package data.map;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import data.item.ChestManager;

public class Map {
    private Block[][] blocks;
    private HashMap<Block, Obstacle> obstacles = new HashMap<>();
    private HashMap<Block, Boolean> terrainBlocked = new HashMap<>();
    private HashMap<Block, String> staticObjects = new HashMap<>();
    private HashMap<Block, String> staticTerrain = new HashMap<>();
    private HashMap<Block, String> enemies = new HashMap<>();
    private ChestManager chestManager;   
    private int lineCount;
    private int columnCount;

    public Map(int lineCount, int columnCount) {
        this.lineCount = lineCount;
        this.columnCount = columnCount;
        blocks = new Block[lineCount][columnCount];
        chestManager = new ChestManager(); // Initialisation du ChestManager

        // Création des blocs
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                blocks[lineIndex][columnIndex] = new Block(lineIndex, columnIndex);
            }
        }

        // Initialisation aléatoire des terrains
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Block block = blocks[lineIndex][columnIndex];
                double random = Math.random();
                if (random < 0.15) {
                    staticTerrain.put(block, "water");
                } else if (random < 0.2) {
                    staticTerrain.put(block, "path");
                } else {
                    staticTerrain.put(block, "grass");
                }
            }
        }

        // Ajout des ennemis
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Block block = blocks[lineIndex][columnIndex];
                double random = Math.random();
                if (random < 0.12) {
                    enemies.put(block, "skeleton");
                } else if (random < 0.15) {
                    enemies.put(block, "slime");
                }
            }
        }

        generateObjects(); // Générez les objets (arbres, maisons, coffres)
    }

    public HashMap<Block, String> getStaticTerrain() {
        return staticTerrain;
    }

    private void generateObjects() {
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Block block = blocks[lineIndex][columnIndex];

                // Vérifie si la case a un terrain défini, sinon on lui met "grass" par défaut
                if (!staticTerrain.containsKey(block)) {
                    staticTerrain.put(block, "grass");
                }

                String terrainType = staticTerrain.get(block);

                if (terrainType.equals("grass")) {
                    double rand = Math.random();
                    if (rand < 0.05) {
                        staticObjects.put(block, "tree");   // Arbre
                        setTerrainBlocked(block, true);
                    } else if (rand < 0.08) {
                        staticObjects.put(block, "house");  // Maison
                        setTerrainBlocked(block, true);
                    } else if (rand < 0.10) {
                        staticObjects.put(block, "chest");  // Coffre
                        chestManager.addChest(block, "chest");  // Ajoute le coffre
                        setTerrainBlocked(block, true);  // Bloque le terrain pour ce bloc
                    }
                }

                // Générer des ennemis sur les blocs de terrain comme "path" et "grass"
                if (terrainType.equals("path") || terrainType.equals("grass")) {
                    double rand = Math.random();
                    if (rand < 0.05) {
                        enemies.put(block, "skeleton");   // Squelette
                    } else if (rand < 0.10) {
                        enemies.put(block, "slime");      // Slime
                    } else if (rand < 0.15) {
                        enemies.put(block, "slime_green"); // Slime vert
                    }
                }

                // Vérifie que le terrain est bien reconnu
                if (!terrainType.equals("grass") && !terrainType.equals("path") && !terrainType.equals("water")) {
                    System.out.println("Terrain inconnu corrigé : " + terrainType);
                }
            }
        }
    }

    public ArrayList<Block> getFreeBlocks() {
        ArrayList<Block> freeBlocks = new ArrayList<>();

        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Block block = blocks[i][j];
                // Vérifie que le bloc ne contient PAS d'obstacle et PAS d'ennemi
                if (!staticObjects.containsKey(block) && !enemies.containsKey(block)) {
                    freeBlocks.add(block);
                }
            }
        }
        return freeBlocks;
    }

    public boolean isBlocked(Block block) {
        return obstacles.containsKey(block) || terrainBlocked.getOrDefault(block, false) ||
               (staticTerrain.containsKey(block) && staticTerrain.get(block).equals("water")) ||
               staticObjects.containsKey(block);
    }

    public void setTerrainBlocked(Block block, boolean blocked) {
        terrainBlocked.put(block, blocked);
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getLineCount() {
        return lineCount;
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public Block getBlock(int line, int column) {
        return blocks[line][column];
    }

    public HashMap<Block, String> getStaticObjects() {
        return staticObjects;
    }

    public HashMap<Block, String> getEnemies() {
        return enemies;
    }

    public HashMap<String, Image> getCoins() {
        // TODO Auto-generated method stub
        return null;
    }
}
