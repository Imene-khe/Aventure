package data.map;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import data.item.ChestManager;
import data.item.Coin;

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
    private ArrayList<Coin> coins;


    public Map(int lineCount, int columnCount, int maxChest) {
        this.lineCount = lineCount;
        this.columnCount = columnCount;
        this.blocks = new Block[lineCount][columnCount];
        this.chestManager = new ChestManager();
        this.maxChests = maxChest;
        this.enemies = new HashMap<>();
        this.coins = new ArrayList<>(); // ✅ Initialisation de coins avant utilisation

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

        generateObjects(); // Générer les objets (arbres, maisons, coffres)
        generateEnemies(); // Générer les ennemis (sans images)
        generateCoins(10); // ✅ Plus d'erreur car coins est initialisé
    }

    
    private void generateEnemies() {
        ArrayList<Block> freeBlocks = getFreeBlocks();
        Random random = new Random();
        int maxEnemies = 10; // Nombre max d'ennemis sur la carte
        int generatedEnemies = 0;

        while (generatedEnemies < maxEnemies && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index); // Sélectionner un bloc libre

            double rand = Math.random();
            String enemyType = (rand < 0.5) ? "skeleton" : "slime";

            // Stocker uniquement la position et le type de l'ennemi
            enemies.put(block, enemyType);
            generatedEnemies++;
        }
    }

    private void generateCoins(int coinCount) {
        ArrayList<Block> freeBlocks = getFreeBlocks(); // Récupère les blocs libres
        Random random = new Random();

        int generatedCoins = 0;
        while (generatedCoins < coinCount && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.get(index);

            // Vérifier que le bloc ne contient PAS d'ennemi et que ce n'est PAS de l'eau
            if (!enemies.containsKey(block) && !staticTerrain.getOrDefault(block, "").equals("water")) {
                coins.add(new Coin(block)); // Ajouter une pièce sur ce bloc
                freeBlocks.remove(index); // Retirer pour éviter un double spawn
                generatedCoins++;
            }
        }
    }



    
    public ArrayList<Coin> getCoins(){
    	return coins;
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
     // ✅ Placer le marchand sur le bord gauche de la carte
        Block merchantBlock = getBlock(getLineCount() - 2, 1); // Bord bas gauche
        staticObjects.put(merchantBlock, "merchant");
        System.out.println("✅ Marchand ajouté à la position : " + merchantBlock);


        
  
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

    
    public ChestManager getChestManager() {
        return chestManager;
    } 
    
    public static void main(String[] args) {
        // ✅ Création d'une carte de test (10x10 avec 5 coffres)
        System.out.println("🔄 Initialisation de la carte...");
        Map map = new Map(10, 10, 5);

        // ✅ Vérification des terrains générés
        System.out.println("\n📌 Vérification des terrains générés :");
        for (int i = 0; i < map.getLineCount(); i++) {
            for (int j = 0; j < map.getColumnCount(); j++) {
                Block block = map.getBlock(i, j);
                String terrain = map.getStaticTerrain().getOrDefault(block, "grass");
                System.out.print(terrain.substring(0, 1).toUpperCase() + " "); // Affichage simplifié (G = grass, W = water, P = path)
            }
            System.out.println();
        }

        // ✅ Vérification des objets statiques
        System.out.println("\n🏠 Objets statiques générés (maisons, arbres, coffres) :");
        for (Block block : map.getStaticObjects().keySet()) {
            System.out.println("📍 " + block + " → " + map.getStaticObjects().get(block));
        }

        // ✅ Vérification du nombre total de coffres générés
        int nbCoffres = map.getChestManager().getChests().size();
        System.out.println("\n🗃 Nombre de coffres générés : " + nbCoffres);

        // ✅ Vérification des ennemis générés
        System.out.println("\n👿 Liste des ennemis générés :");
        if (!map.getEnemies().isEmpty()) {
            for (Block block : map.getEnemies().keySet()) {
                System.out.println("⚔ Ennemi " + map.getEnemies().get(block) + " positionné à " + block);
            }
        } else {
            System.out.println("❌ Aucun ennemi généré !");
        }

        // ✅ Vérification des pièces générées
        System.out.println("\n💰 Liste des pièces générées :");
        if (!map.getCoins().isEmpty()) {
            for (Coin coin : map.getCoins()) {
                System.out.println("🟡 Pièce placée à " + coin.getBlock());
            }
        } else {
            System.out.println("❌ Aucune pièce générée !");
        }

        // ✅ Vérification du nombre de blocs libres
        ArrayList<Block> freeBlocks = map.getFreeBlocks();
        System.out.println("\n🟢 Nombre de blocs libres (sans objets ni ennemis) : " + freeBlocks.size());

        // ✅ Vérification de l'affichage d'un bloc spécifique
        int testLine = 2, testColumn = 2;
        Block testBlock = map.getBlock(testLine, testColumn);
        System.out.println("\n📍 Vérification du bloc (" + testLine + "," + testColumn + ") :");
        System.out.println("🗺 Terrain : " + map.getStaticTerrain().getOrDefault(testBlock, "grass"));
        System.out.println("🏠 Objet : " + map.getStaticObjects().getOrDefault(testBlock, "Aucun"));
        System.out.println("👿 Ennemi : " + map.getEnemies().getOrDefault(testBlock, "Aucun"));
    }

}
