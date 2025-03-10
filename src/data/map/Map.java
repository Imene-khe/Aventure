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
    private int maxChests;

    public Map(int lineCount, int columnCount, int maxChest) {
        this.lineCount = lineCount;
        this.columnCount = columnCount;
        this.blocks = new Block[lineCount][columnCount];
        this.chestManager = new ChestManager(); // Initialisation du ChestManager
        this.maxChests = maxChest;

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
        
        generateObjects(); // Générez les objets (arbres, maisons, coffres)

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
    }

    public HashMap<Block, String> getStaticTerrain() {
        return staticTerrain;
    }

    private void generateObjects() {
        int generatedChests = 0;

        // Générez les arbres et les maisons
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Block block = blocks[lineIndex][columnIndex];

                String terrainType = staticTerrain.get(block);

                if (terrainType.equals("grass")) {
                    double rand = Math.random();
                    if (rand < 0.05) {
                        staticObjects.put(block, "tree");   // Arbre
                        setTerrainBlocked(block, true);
                    } else if (rand < 0.08) {
                        staticObjects.put(block, "house");  // Maison
                        setTerrainBlocked(block, true);
                    }
                }
            }
        }

        // Générez les coffres de manière aléatoire tout en respectant le nombre maximal
        while (generatedChests < maxChests) {
            // Sélectionner un bloc aléatoire de la carte
            int randomLine = (int) (Math.random() * lineCount);   // Ligne aléatoire
            int randomColumn = (int) (Math.random() * columnCount); // Colonne aléatoire
            Block block = blocks[randomLine][randomColumn];

            // Vérifier que le bloc est soit "path" ou "grass" et qu'il n'a pas déjà un objet statique
            String terrainType = staticTerrain.getOrDefault(block, "grass"); // Terrain par défaut "grass"
            if ((terrainType.equals("path") || terrainType.equals("grass")) && !staticObjects.containsKey(block)) {
                double rand = Math.random();
                if (rand < 0.1) { // 10% de chance d'ajouter un coffre sur ce bloc
                    // Ajouter le coffre dans chestManager
                    chestManager.addChest(block, "chest");
                    
                    // Ajouter également le coffre à staticObjects pour que getNearbyChestPosition() le trouve
                    staticObjects.put(block, "chest");
                    
                    // Bloque le terrain pour ce bloc
                    setTerrainBlocked(block, true);

                    generatedChests++;  // Incrémenter le compteur de coffres générés
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
    
    public ChestManager getChestManager() {
        return chestManager;
    } 
    
    public static void main(String[] args) {
        // Création d'une carte de taille 10x10 avec un max de 5 coffres
        Map map = new Map(10, 10, 5);

        // Affichage du nombre total de coffres générés
        System.out.println("Nombre de coffres générés : " + map.getChestManager().getChests().size());

        // Vérification des ennemis ajoutés (si applicable)
        if (map.getEnemies() != null) {
            System.out.println("Nombre total d'ennemis : " + map.getEnemies().size());
        } else {
            System.out.println("Aucun ennemi généré.");
        }

        // Vérification du nombre de blocs libres
        ArrayList<Block> freeBlocks = map.getFreeBlocks();
        System.out.println("Nombre de blocs libres : " + freeBlocks.size());
    }
}
