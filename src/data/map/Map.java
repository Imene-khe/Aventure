package data.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.Logger;

import data.item.Coin;
import data.item.Equipment;
import data.item.Flame;
import data.item.chest.Chest;
import data.item.chest.ChestFactory;
import data.item.chest.ChestManager;
import data.player.Antagonist;
import log.LoggerUtility;

public class Map {
    protected Block[][] blocks;
    private HashMap<Block, Obstacle> obstacles = new HashMap<>();
    protected HashMap<Block, Boolean> terrainBlocked = new HashMap<>();
    protected HashMap<Block, String> staticObjects = new HashMap<>();
    protected HashMap<Block, String> staticTerrain = new HashMap<>();
    protected HashMap<Block, String> enemies = new HashMap<>();
    private ChestManager chestManager;   
    protected int lineCount;
    protected int columnCount;
    private int maxChests;
    private ArrayList<Coin> coins;
    private ArrayList<Flame> flames = new ArrayList<>();
    private static final Logger logger = LoggerUtility.getLogger(Map.class, "text");

    public Map(int lineCount, int columnCount, int maxChest, boolean isStatic) {
        this.lineCount = lineCount;
        this.columnCount = columnCount;
        this.blocks = new Block[lineCount][columnCount];
        this.chestManager = new ChestManager();
        this.maxChests = maxChest;
        this.enemies = new HashMap<>();
        this.coins = new ArrayList<>();
        this.flames = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                blocks[lineIndex][columnIndex] = new Block(lineIndex, columnIndex);
            }
        }
        generateTerrain();
        generateObjects();  
        generateEnemies();  
        generateCoins(25); 
        placeShopOnMap();   
        
    }
    
    public Map(int lineCount, int columnCount) {
        this.lineCount = lineCount;
        this.columnCount = columnCount;
        this.blocks = new Block[lineCount][columnCount];
        this.chestManager = new ChestManager();
        this.enemies = new HashMap<>();
        this.coins = new ArrayList<>();
        this.flames = new ArrayList<>();

        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                blocks[lineIndex][columnIndex] = new Block(lineIndex, columnIndex);
            }
        }
    }

    public void generateTerrain() {
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
    }

    public void generateEnemies() {
        ArrayList<Block> freeBlocks = getFreeBlocks();
        Random random = new Random();
        int maxEnemies = 10; 
        int generatedEnemies = 0;

        while (generatedEnemies < maxEnemies && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index);

            double rand = Math.random();
            String enemyType = (rand < 0.5) ? "skeleton" : "slime";
            enemies.put(block, enemyType);
            generatedEnemies++;
        }
    }

    public void generateCoins(int coinCount) {
        ArrayList<Block> freeBlocks = getFreeBlocks(); 
        Random random = new Random();

        int generatedCoins = 0;
        while (generatedCoins < coinCount && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.get(index);
            if (!enemies.containsKey(block) && !staticTerrain.getOrDefault(block, "").equals("water")) {
                coins.add(new Coin(block)); 
                freeBlocks.remove(index); 
                generatedCoins++;
            }
        }
    }
    
    public void placeShopOnMap() {
        Random random = new Random();
        int maxAttempts = 100; 
        int attempts = 0;

        while (attempts < maxAttempts) {
            int shopRow = random.nextInt(lineCount - 2) + 1; 
            int shopCol = random.nextInt(columnCount - 2) + 1;
            Block shopBlock = blocks[shopRow][shopCol];
            String terrainType = staticTerrain.getOrDefault(shopBlock, "grass");
            if (!terrainType.equals("water") &&!staticObjects.containsKey(shopBlock) &&!chestManager.getChests().containsKey(shopBlock) &&
                !enemies.containsKey(shopBlock)) {
                staticObjects.put(shopBlock, "shop");
                setTerrainBlocked(shopBlock, true);
                logger.info("Shop placé en position : " + shopBlock);
                return;
            }

            attempts++; 
        }

        logger.warn("Impossible de placer le shop après " + maxAttempts + " essais !");
        }

	public void generateObjects() {
	    int generatedChests = 0;
	    int generatedHouses = 0;
	    boolean orbePlaced = false;
	    int maxHouses = 8;

	    ArrayList<Block> freeGrassBlocks = new ArrayList<>();
	    for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
	        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
	            Block block = blocks[lineIndex][columnIndex];
	            String terrainType = staticTerrain.get(block);

	            if ("grass".equals(terrainType)) {
	                freeGrassBlocks.add(block);
	            }
	        }
	    }
	    Random random = new Random();
	    while (generatedHouses < maxHouses && !freeGrassBlocks.isEmpty()) {
	        int index = random.nextInt(freeGrassBlocks.size());
	        Block block = freeGrassBlocks.remove(index);

	        if (!staticObjects.containsKey(block)) {
	            staticObjects.put(block, "house");
	            setTerrainBlocked(block, true);
	            generatedHouses++;
	        }
	    }
	    for (Block block : freeGrassBlocks) {
	        if (Math.random() < 0.05) { 
	            staticObjects.put(block, "tree");
	            setTerrainBlocked(block, true);
	        }
	    }
	    while (generatedChests < maxChests) {
	        int randomLine = random.nextInt(lineCount);
	        int randomColumn = random.nextInt(columnCount);
	        Block block = blocks[randomLine][randomColumn];

	        String terrainType = staticTerrain.getOrDefault(block, "grass");
	        if ((terrainType.equals("path") || terrainType.equals("grass")) && !staticObjects.containsKey(block)) {
	            if (Math.random() < 0.1) {
	                Chest chest = ChestFactory.createChestWithRandomWeapons();

	                if (!orbePlaced) {
	                    chest.getInventory().addEquipment(new Equipment("orbe"));
	                    orbePlaced = true;
	                    logger.info("Orbe inséré dans le coffre à : " + block);	                }

	                chestManager.getChests().put(block, chest);
	                staticObjects.put(block, "chest");
	                setTerrainBlocked(block, true);
	                generatedChests++;
	            }
	        }
	    }

	    if (!orbePlaced) {
	    	logger.warn("Aucun coffre contenant l’orbe n’a pu être placé !");		    }
	}

	public Block getShopPosition() {
	    for (Block block : staticObjects.keySet()) {
	        if ("shop".equals(staticObjects.get(block))) {
	            return block;
	        }
	    }
	    return null;
	}

	public ArrayList<Block> getFreeBlocks() {
	    ArrayList<Block> freeBlocks = new ArrayList<>();

	    for (int i = 0; i < lineCount; i++) {
	        for (int j = 0; j < columnCount; j++) {
	            Block block = blocks[i][j];

	            boolean isOccupied = staticObjects.containsKey(block) || enemies.containsKey(block);
	            boolean isBlocked = isBlocked(block); 
	            if (!isOccupied && !isBlocked) {
	                freeBlocks.add(block);
	            }
	        }
	    }

	    return freeBlocks;
	}

	public boolean isBlocked(Block block) {
	    if (obstacles.containsKey(block)) return true;
	    if (terrainBlocked.getOrDefault(block, false)) return true;
	    if ("water".equals(staticTerrain.get(block))) return true;

	    if (staticObjects.containsKey(block)) {
	        String object = staticObjects.get(block);
	        if (object.endsWith("rock") || object.startsWith("rune") || object.equals("campfire") || object.equals("entry")) {
	            return false;
	        }
	        return true;
	    }

	    return false;
	}
	
	public void setAllHousesOnFire() {
        flames.clear();

        for (Block block : staticObjects.keySet()) {
            String value = staticObjects.get(block);
            if ("house".equals(value)) {
                staticObjects.put(block, "house_burning");
                flames.add(new Flame(block));
            }
        }

        int totalFlames = flames.size();
        data.quest.QuestManager questManager = gui.MainGUI.getInstance().getQuestManager();
        questManager.setRequiredAmount("Éteindre les flammes", totalFlames);

        logger.info(flames.size() + " maisons mises en feu !");
    }
	
	 public ArrayList<Coin> getCoins(){
	    	return coins;
	    }
	    
	    
	public HashMap<Block, String> getStaticTerrain() {
	    return staticTerrain;
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
   
	public int getRows() {
	    return lineCount;
	}

	public int getCols() {
	    return columnCount;
	}
	
	public void setBlocks(Block[][] blocks) {
		this.blocks = blocks;
	}


	public void setEnemies(HashMap<Block, String> enemies) {
		this.enemies = enemies;
	}


	public void setChestManager(ChestManager chestManager) {
		this.chestManager = chestManager;
	}


	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}


	public void setCoins(ArrayList<Coin> coins) {
		this.coins = coins;
	}
	
    public ArrayList<Flame> getFlames() {
		return flames;
	}
    public ArrayList<Antagonist> getMobileAntagonists() {
        return new ArrayList<>();
    }

	public void setFlames(ArrayList<data.item.Flame> flames) {
		this.flames = flames;
	}
	
	public boolean isShelterBlock(Block block) {
	    return false; 
	}
	

	public boolean supportsFinalZoneReveal() {
	    return false;
	}

	public void revealFinalZone() {
	    // par défaut, rien
	}

	public boolean supportsWaves() {
	    return false;
	}

	public boolean areAllEnemiesDead() {
	    return false;
	}



}
