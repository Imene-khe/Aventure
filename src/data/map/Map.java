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
    private boolean isStatic; // ✅ Ajout d'un booléen pour indiquer si la carte est fixe


    public Map(int lineCount, int columnCount, int maxChest, boolean isStatic) {
        this.lineCount = lineCount;
        this.columnCount = columnCount;
        this.blocks = new Block[lineCount][columnCount];
        this.chestManager = new ChestManager();
        this.maxChests = maxChest;
        this.enemies = new HashMap<>();
        this.coins = new ArrayList<>();
        this.isStatic = isStatic; // ✅ Définition du mode statique

        // Création des blocs
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                blocks[lineIndex][columnIndex] = new Block(lineIndex, columnIndex);
            }
        }

        // ✅ Si la carte est statique, on ne génère pas de terrain aléatoire
        if (!isStatic) {
            // Génération aléatoire des terrains
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

            generateObjects();  // Générer les objets (arbres, maisons, coffres)
            generateEnemies();  // Générer les ennemis
            generateCoins(10);  // Générer des pièces
            placeShopOnMap();   // ✅ Placer le shop après la génération des objets
        } else {
            setupStaticShop(); // ✅ Configuration spéciale pour la boutique
        }
    }

 // ✅ Méthode pour configurer une boutique avec un carré de 8x8 entouré de noir, torches et tables autour du marchand
    public void setupStaticShop() {
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Block block = blocks[i][j];

                // ✅ Définition des murs noirs (2 blocs d'épaisseur)
                if (i < 2 || i >= lineCount - 2 || j < 2 || j >= columnCount - 2) {
                    staticTerrain.put(block, "blackWall"); // ✅ Contour noir
                } else {
                    staticTerrain.put(block, "shopFloor"); // ✅ Sol de la boutique
                }
            }
        }

        // ✅ Ajouter des torches dans les coins du carré 8x8
        staticObjects.put(blocks[2][2], "torch"); // Coin haut-gauche
        staticObjects.put(blocks[2][lineCount - 3], "torch"); // Coin haut-droit
        staticObjects.put(blocks[lineCount - 3][2], "torch"); // Coin bas-gauche
        staticObjects.put(blocks[lineCount - 3][lineCount - 3], "torch"); // Coin bas-droit

        // ✅ Placement fixe du marchand au centre de la boutique
        int merchantRow = lineCount / 2;
        int merchantCol = columnCount / 2;
        Block merchantBlock = blocks[merchantRow][merchantCol];
        staticObjects.put(merchantBlock, "merchant");

        // ✅ Ajouter des tables autour du marchand
        staticObjects.put(blocks[merchantRow - 1][merchantCol], "bar"); // Haut
        staticObjects.put(blocks[merchantRow + 1][merchantCol], "bar"); // Bas
        staticObjects.put(blocks[merchantRow][merchantCol - 1], "bar"); // Gauche
        staticObjects.put(blocks[merchantRow][merchantCol + 1], "bar"); // Droite

        System.out.println("✅ Boutique statique configurée avec torches et tables !");
    }



    
    
    public void generateEnemies() {
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

    public void generateCoins(int coinCount) {
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
    
    /**
     * ✅ Place une maison spéciale "Shop" à un emplacement fixe et sécurisé sur `currentMap`.
     */
    /**
     * ✅ Place la maison spéciale "Shop" à un emplacement aléatoire mais sécurisé.
     */
    public void placeShopOnMap() {
        Random random = new Random();
        int maxAttempts = 100; // ✅ Évite une boucle infinie si la carte est très remplie
        int attempts = 0;

        while (attempts < maxAttempts) {
            int shopRow = random.nextInt(lineCount - 2) + 1; // ✅ Évite les bords
            int shopCol = random.nextInt(columnCount - 2) + 1;
            Block shopBlock = blocks[shopRow][shopCol];

            // ✅ Vérifier que le bloc est libre (ni eau, ni maison, ni coffre, ni obstacle)
            String terrainType = staticTerrain.getOrDefault(shopBlock, "grass");
            if (!terrainType.equals("water") &&
                !staticObjects.containsKey(shopBlock) &&
                !chestManager.getChests().containsKey(shopBlock) &&
                !enemies.containsKey(shopBlock)) {

                // ✅ Placer la maison "Shop" ici
                staticObjects.put(shopBlock, "shop");
                setTerrainBlocked(shopBlock, true);
                System.out.println("✅ Shop placé en position : " + shopBlock);
                return;
            }

            attempts++; // ✅ Incrémentation du compteur de tentatives
        }

        System.out.println("⚠ Impossible de placer le shop après " + maxAttempts + " essais !");
    }





    
    public ArrayList<Coin> getCoins(){
    	return coins;
    }
    
    
	public HashMap<Block, String> getStaticTerrain() {
        return staticTerrain;
    }

    public void generateObjects() {
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
        System.out.println("🔄 Initialisation de la boutique...");

        // ✅ Création d'une boutique statique
        Map shopMap = new Map(10, 10, 0, true);

        // ✅ Vérification des terrains générés
        System.out.println("\n📌 Vérification du sol et des murs de la boutique :");
        for (int i = 0; i < shopMap.getLineCount(); i++) {
            for (int j = 0; j < shopMap.getColumnCount(); j++) {
                Block block = shopMap.getBlock(i, j);
                String terrain = shopMap.getStaticTerrain().getOrDefault(block, "shopFloor");

                if (terrain.equals("blackWall")) {
                    System.out.print("⬛ "); // Contour noir
                } else {
                    System.out.print("⬜ "); // Sol de la boutique
                }
            }
            System.out.println();
        }

        // ✅ Vérification des objets statiques
        System.out.println("\n🏠 Objets placés dans la boutique :");
        for (Block block : shopMap.getStaticObjects().keySet()) {
            System.out.println("📍 " + block + " → " + shopMap.getStaticObjects().get(block));
        }

        // ✅ Vérification du placement du marchand
        System.out.println("\n🧑‍🦳 Position du marchand :");
        for (Block block : shopMap.getStaticObjects().keySet()) {
            if (shopMap.getStaticObjects().get(block).equals("merchant")) {
                System.out.println("✅ Marchand positionné à : " + block);
            }
        }

        System.out.println("\n✅ Test terminé ! Vérifie que la boutique est toujours la même à chaque exécution.");
    }



}
