package data.map;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.Logger;
import log.LoggerUtility;


import data.item.ChestManager;
import data.item.Coin;
import data.item.Flame;
import gui.GameDisplay;

public class Map {
	private static final Logger logger = LoggerUtility.getLogger(Map.class, "text");
	
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
    private ArrayList<Flame> flames = new ArrayList<>();
    


    public Map(int lineCount, int columnCount, int maxChest, boolean isStatic) {
    	logger.info("Création de la carte " + (isStatic ? "statique (shop)" : "principale") + " : " + lineCount + "x" + columnCount);
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
            logger.debug("🌳 Objets générés (arbres, maisons, coffres)");
            generateEnemies();  // Générer les ennemis
            logger.debug("👾 Ennemis générés sur la carte");
            generateCoins(10);  // Générer des pièces
            logger.debug("🪙 Pièces générées");
            placeShopOnMap();   // ✅ Placer le shop après la génération des objets
        } else {
        	 setupStaticShop();
        	 logger.info("🏪 Carte boutique configurée (statique)");
        	 this.enemies.clear(); // ✅ Supprime les ennemis de `shopMap` mais pas sur du tout pour le retour sur la map classique
        	 this.coins.clear();   // ✅ Supprime les pièces de `shopMap` 	mais pas sur du tout pour le retour sur la map classique
        }
    }

 
    /**
     * ✅ Configure une boutique avec un contour de `blackWall` simple et un placement optimisé des torches.
     */
    public void setupStaticShop() {
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Block block = blocks[i][j];

                // ✅ Contour léger gris
                if (i == 0 || i == lineCount - 1 || j == 0 || j == columnCount - 1) {
                    staticTerrain.put(block, "lightWall");
                } else {
                    staticTerrain.put(block, "shopFloor");
                }
            }
        }

        // ✅ Placement du marchand en haut centré
        int merchantRow = 2;
        int merchantCol = columnCount / 2;
        Block merchantBlock = blocks[merchantRow][merchantCol];
        Block barLeft = blocks[merchantRow + 1][merchantCol - 1];
        Block barCenter = blocks[merchantRow + 1][merchantCol];
        Block barRight = blocks[merchantRow + 1][merchantCol + 1];

        staticObjects.put(merchantBlock, "merchant");
        staticObjects.put(barLeft, "bar");
        staticObjects.put(barCenter, "bar");
        staticObjects.put(barRight, "bar");

        // ✅ Bordure intérieure avec bookshelf (1 bloc vers l’intérieur)
        for (int i = 1; i < lineCount - 1; i++) {
            for (int j = 1; j < columnCount - 1; j++) {
                Block block = blocks[i][j];

                boolean isEdgeInner = i == 1 || i == lineCount - 2 || j == 1 || j == columnCount - 2;
                boolean isEntry = i == lineCount - 2 && j >= 6 && j <= 8;
                boolean isMerchantZone = block.equals(merchantBlock) || block.equals(barLeft) ||
                                         block.equals(barCenter) || block.equals(barRight);

                if (isEdgeInner && !isEntry && !isMerchantZone) {
                    staticObjects.put(block, "bookshelf");
                }
            }
        }

        // ✅ Tapis en damier au centre de la pièce (7x7)
        int carpetStartRow = (lineCount - 7) / 2;
        int carpetStartCol = (columnCount - 7) / 2;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Block block = blocks[carpetStartRow + i][carpetStartCol + j];

                // ✅ Ne pas écraser les bars ou le marchand
                if (!staticObjects.containsKey(block)) {
                    if ((i + j) % 2 == 0) { // Motif damier
                        staticTerrain.put(block, "carpet");
                    }
                }
            }
        }

        logger.info("✅ Boutique statique configurée.");
    }



    public void generateEnemies() {
        ArrayList<Block> freeBlocks = getFreeBlocks();
        Random random = new Random();
        int maxEnemies = 10; // Nombre max d'ennemis sur la carte
        int generatedEnemies = 0;
        logger.info("Début de la génération des ennemis...");
        while (generatedEnemies < maxEnemies && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index); // Sélectionner un bloc libre

            double rand = Math.random();
            String enemyType = (rand < 0.5) ? "skeleton" : "slime";

            // Stocker uniquement la position et le type de l'ennemi
            enemies.put(block, enemyType);
            generatedEnemies++;
        }
        logger.info("✅ " + generatedEnemies + " ennemis générés.");
    }

    public void generateCoins(int coinCount) {
        ArrayList<Block> freeBlocks = getFreeBlocks(); // Récupère les blocs libres
        Random random = new Random();

        int generatedCoins = 0;
        logger.info("🪙 Début de génération de " + coinCount + " pièces...");
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
        logger.info("✅ " + generatedCoins + " pièces placées.");
    }
    
    
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
                logger.info("🏪 Shop placé en position : " + shopBlock);
                return;
            }

            attempts++; // ✅ Incrémentation du compteur de tentatives
        }

        logger.warn("⚠️ Impossible de placer le shop après " + maxAttempts + " tentatives.");
        
   }





    
    public ArrayList<Coin> getCoins(){
    	return coins;
    }
    
    
	public HashMap<Block, String> getStaticTerrain() {
        return staticTerrain;
    }

    public void generateObjects() {
        int generatedChests = 0;

        logger.info("🌳 Génération des arbres et maisons...");
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

        logger.info("📦 Génération des coffres...");
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
        logger.info("✅ " + generatedChests + " coffres placés.");
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
        if (isStatic) {
            String object = staticObjects.get(block);
            if (object == null) return false;

            // ✅ Ne bloquer que les objets qui prennent de la place
            return object.equals("bookshelf") || object.equals("merchant") || object.equals("bar");
        }

        return obstacles.containsKey(block)
            || terrainBlocked.getOrDefault(block, false)
            || (staticTerrain.containsKey(block) && staticTerrain.get(block).equals("water"))
            || staticObjects.containsKey(block);
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
    
    public ArrayList<Flame> getFlames() {
        return flames;
    }
    
    public void addFlame(Block block) {
        flames.add(new Flame(block));
    }

    


    public void setAllHousesOnFire() {
        for (Block block : staticObjects.keySet()) {
            if ("house".equals(staticObjects.get(block))) {
                staticObjects.put(block, "house_burning");
                addFlame(block); // ✅ Ajoute une flamme
            }
        }
        logger.info("🔥 Toutes les maisons ont été incendiées.");
    }

    
    public void paintTerrain(Graphics g, GameDisplay display) {
        Block[][] blocks = this.getBlocks();
        boolean isInShop = display.isInShop();

        for (int line = 0; line < getLineCount(); line++) {
            for (int col = 0; col < getColumnCount(); col++) {
                Block block = blocks[line][col];

                String terrainType = this.getStaticTerrain().getOrDefault(block, isInShop ? "shopFloor" : "grass");
                Image terrainImage = display.getTileset().get(terrainType);
                if (terrainImage != null) {
                    g.drawImage(terrainImage, block.getColumn() * 32, block.getLine() * 32, 32, 32, null);
                }
            }
        }
    }


    public static void main(String[] args) {
        System.out.println("📚 Initialisation de la boutique 15x15 avec marchand derrière son comptoir en haut...");

        Map shopMap = new Map(15, 15, 0, true);

        int lineCount = shopMap.getLineCount();
        int columnCount = shopMap.getColumnCount();
        int centerCol = columnCount / 2;

        // ✅ Contour gris (lightWall)
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Block block = shopMap.getBlock(i, j);
                if (i == 0 || i == lineCount - 1 || j == 0 || j == columnCount - 1) {
                    shopMap.getStaticTerrain().put(block, "lightWall");
                } else {
                    shopMap.getStaticTerrain().put(block, "shopFloor");
                }
            }
        }

        // ✅ Placement du comptoir (3 barres) et du marchand derrière
        Block barLeft = shopMap.getBlock(3, centerCol - 1);
        Block barCenter = shopMap.getBlock(3, centerCol);
        Block barRight = shopMap.getBlock(3, centerCol + 1);
        Block merchant = shopMap.getBlock(2, centerCol); // derrière le comptoir

        shopMap.getStaticObjects().put(barLeft, "bar");
        shopMap.getStaticObjects().put(barCenter, "bar");
        shopMap.getStaticObjects().put(barRight, "bar");
        shopMap.getStaticObjects().put(merchant, "merchant");
        shopMap.getStaticTerrain().put(barCenter, "carpet");

        // ✅ Bordure intérieure : 1 bloc de bookshelf tout autour sauf entrée et comptoir/marchand
        for (int i = 1; i < lineCount - 1; i++) {
            for (int j = 1; j < columnCount - 1; j++) {
                Block block = shopMap.getBlock(i, j);

                boolean isEdgeInner = i == 1 || i == lineCount - 2 || j == 1 || j == columnCount - 2;
                boolean isEntry = i == lineCount - 2 && j >= 6 && j <= 8;
                boolean isComptoir = block.equals(barLeft) || block.equals(barCenter) || block.equals(barRight);
                boolean isMerchant = block.equals(merchant);

                if (isEdgeInner && !isEntry && !isComptoir && !isMerchant) {
                    shopMap.getStaticObjects().put(block, "bookshelf");
                }
            }
        }

        // ✅ Affichage ASCII
        System.out.println("\n🗺️ Affichage ASCII :");
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Block block = shopMap.getBlock(i, j);
                String terrain = shopMap.getStaticTerrain().getOrDefault(block, "??");
                String obj = shopMap.getStaticObjects().getOrDefault(block, " ");

                String symbol;
                switch (obj) {
                    case "merchant": symbol = "M"; break;
                    case "bar": symbol = "B"; break;
                    case "bookshelf": symbol = "📚"; break;
                    case " ": // pas d’objet
                        if ("lightWall".equals(terrain)) symbol = "░";
                        else if ("carpet".equals(terrain)) symbol = "◉";
                        else if ("shopFloor".equals(terrain)) symbol = ".";
                        else symbol = "?";
                        break;
                    default: symbol = "?"; break;
                }

                System.out.print(symbol + " ");
            }
            System.out.println();
        }

        System.out.println("\n✅ Boutique 15x15 avec marchand en haut derrière son comptoir !");
    }










}
