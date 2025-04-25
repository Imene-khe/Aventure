package data.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import data.player.Antagonist;

public class HostileMap extends Map {

    private ArrayList<Antagonist> antagonistList = new ArrayList<>();
    private HashMap<Antagonist, String> antagonistTypes = new HashMap<>();
    private ArrayList<Block> shelterBlocks = new ArrayList<>();
    private ArrayList<Block> runeBlocks = new ArrayList<>();
    private Block caveEntry; // ajouté tout en haut

   

    public HostileMap(int lineCount, int columnCount, int maxChest) {
    	super(lineCount, columnCount); // ⬅️ appelle le constructeur neutre        
    	staticObjects.clear();
        staticTerrain.clear();
        enemies.clear();
        terrainBlocked.clear();

        generateTerrain();
        generateObjects();
        generateCave();
        generateSafeShelter();
        generateEnemies();
        generateSymbols();

    }

    @Override
    public void generateTerrain() {
        for (int line = 0; line < getLineCount(); line++) {
            for (int col = 0; col < getColumnCount(); col++) {
                Block block = getBlock(line, col);
                staticTerrain.put(block, "floor1");
                setTerrainBlocked(block, false);
            }
        }

        for (int col = 0; col < getColumnCount(); col++) {
            Block top = getBlock(0, col);
            Block bottom = getBlock(getLineCount() - 1, col);
            staticTerrain.put(top, "lava");
            staticTerrain.put(bottom, "lava");
            setTerrainBlocked(top, true);
            setTerrainBlocked(bottom, true);
        }

        for (int line = 0; line < getLineCount(); line++) {
            Block left = getBlock(line, 0);
            Block right = getBlock(line, getColumnCount() - 1);
            staticTerrain.put(left, "lava");
            staticTerrain.put(right, "lava");
            setTerrainBlocked(left, true);
            setTerrainBlocked(right, true);
        }

        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            int line = rand.nextInt(getLineCount());
            int col = rand.nextInt(getColumnCount());

            if (line <= 2 || line >= getLineCount() - 3 || col <= 2 || col >= getColumnCount() - 3) {
                Block block = getBlock(line, col);
                staticTerrain.put(block, "lava");
                setTerrainBlocked(block, true);
            }
        }
    }

    @Override
    public void generateEnemies() {
        ArrayList<Block> freeBlocks = getFreeBlocks();

        ArrayList<Block> toRemove = new ArrayList<>();
        for (Block block : freeBlocks) {
            for (Block shelter : getShelterBlocks()) {
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

        antagonistList.clear();
        antagonistTypes.clear();

        for (int i = 0; i < maxEnemies && !freeBlocks.isEmpty(); i++) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index);
            String type = Math.random() < 0.5 ? "skeleton" : "slime";
            Antagonist enemy = new Antagonist(block, type, null);
            antagonistList.add(enemy);
            antagonistTypes.put(enemy, type);
        }
    }

    @Override
    public void generateObjects() {
        Random rng = new Random();
        int numBosquets = 10;
        for (int b = 0; b < numBosquets; b++) {
            int centerLine = rng.nextInt(getLineCount() - 6) + 3;
            int centerCol = rng.nextInt(getColumnCount() - 6) + 3;
            int radius = rng.nextInt(2) + 2;

            for (int i = centerLine - radius; i <= centerLine + radius; i++) {
                for (int j = centerCol - radius; j <= centerCol + radius; j++) {
                    if (i < 0 || j < 0 || i >= getLineCount() || j >= getColumnCount()) continue;

                    Block block = getBlock(i, j);
                    String terrainType = getStaticTerrain().get(block);

                    double dx = j - centerCol;
                    double dy = i - centerLine;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    double prob = 1.0 - (distance / radius);

                    if (terrainType != null &&
                    	    terrainType.startsWith("floor") &&
                    	    !getStaticObjects().containsKey(block) &&
                    	    !shelterBlocks.contains(block) &&   // ✅ bloc exclu si partie du refuge
                    	    rng.nextDouble() < prob * 0.8) {



                        int type = rng.nextInt(3) + 1;
                        getStaticObjects().put(block, "deadTree" + type);
                        setTerrainBlocked(block, true);
                    }
                }
            }
        }

        for (int i = 0; i < getLineCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                Block block = getBlock(i, j);
                String terrainType = getStaticTerrain().get(block);
                if (terrainType != null &&
                	    terrainType.startsWith("floor") &&
                	    !getStaticObjects().containsKey(block) &&
                	    !shelterBlocks.contains(block) &&   // ✅ idem ici
                	    Math.random() < 0.03) {


                    getStaticObjects().put(block, "rock");
                    setTerrainBlocked(block, true);
                }
            }
        }
    }

    public void generateCave() {
        int baseLine = 14;
        int baseCol = 17;

        Block top = getBlock(baseLine, baseCol + 1);
        Block shadow = getBlock(baseLine + 1, baseCol + 1);

        Block leftTop = getBlock(baseLine, baseCol);
        Block leftBottom = getBlock(baseLine + 1, baseCol);

        Block rightTop = getBlock(baseLine, baseCol + 2);
        Block rightBottom = getBlock(baseLine + 1, baseCol + 2);

        staticObjects.put(leftTop, "cave_left");
        staticObjects.put(top, "cave_top");
        staticObjects.put(rightTop, "cave_right");

        staticObjects.put(leftBottom, "cave_bottom");
        staticObjects.put(shadow, "cave_shadow");
        staticObjects.put(rightBottom, "cave_bottom");

        setTerrainBlocked(leftTop, true);
        setTerrainBlocked(top, true);
        setTerrainBlocked(rightTop, true);
        setTerrainBlocked(leftBottom, true);
        setTerrainBlocked(shadow, true);
        setTerrainBlocked(rightBottom, true);

        // ✅ Ajout dynamique de l'entrée de la cave
        this.caveEntry = shadow;
    }

    
    public void generateSafeShelter() {
        int centerLine = 4;
        int centerCol = getColumnCount() - 6;
        int radius = 2;

        for (int i = centerLine - radius - 1; i <= centerLine + radius + 1; i++) {
            for (int j = centerCol - radius - 1; j <= centerCol + radius + 1; j++) {
                if (i < 0 || j < 0 || i >= getLineCount() || j >= getColumnCount()) continue;
                Block block = getBlock(i, j);
                String terrain = staticTerrain.get(block);
                if (terrain == null || terrain.equals("lava")) continue;
                staticObjects.remove(block);
                double dx = j - centerCol;
                double dy = i - centerLine;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                    if (i == centerLine + radius && j == centerCol) continue; 
                    String rockType;
                    if (i == centerLine - radius && j == centerCol) {
                        rockType = "toprock";
                    } else if (j == centerCol - radius && i == centerLine) {
                        rockType = "rightborderrock"; 
                    } else if (j == centerCol + radius && i == centerLine) {
                        rockType = "leftborderrock"; 
                    } else if (j < centerCol && i < centerLine) {
                        rockType = "topleftrock";
                    } else if (j > centerCol && i < centerLine) {
                        rockType = "toprightrock";
                    } else if (j < centerCol && i > centerLine) {
                        rockType = "topleftrock";
                    } else if (j > centerCol && i > centerLine) {
                        rockType = "toprightrock";
                    } else {
                        rockType = "toprock";
                    }

                    staticObjects.put(block, rockType);
                    setTerrainBlocked(block, rockType.contains("rock")); // ✅ bloque seulement les rochers
                    shelterBlocks.add(block); 
                }
            }
        }

        Block entry = getBlock(centerLine + radius, centerCol);
    
        staticObjects.remove(entry);
        staticTerrain.put(entry, "floor1"); // ✅ Remet un terrain normal si besoin
        setTerrainBlocked(entry, false);    // ✅ Sûr que ce bloc sera accessible
        shelterBlocks.add(entry);


        Block center = getBlock(centerLine, centerCol);
        String centerTerrain = staticTerrain.get(center);
        if (centerTerrain != null && !centerTerrain.equals("lava")) {
            staticObjects.remove(center); 
            staticObjects.put(center, "campfire_off");
            setTerrainBlocked(center, true); // ✅ feu bloquant
            shelterBlocks.add(center); 
        }
    }

    
    public void generateSymbols() {
        Random random = new Random();
        int runeCount = 3;
        int placed = 0;

        while (placed < runeCount) {
            int line = random.nextInt(getLineCount());
            int col = random.nextInt(getColumnCount());
            Block block = getBlock(line, col);

            boolean isFree = getStaticTerrain().get(block) != null &&
                             getStaticTerrain().get(block).startsWith("floor") &&
                             !getStaticObjects().containsKey(block);

            if (isFree) {
                String runeName = "rune" + (placed + 1);
                getStaticObjects().put(block, runeName);
                runeBlocks.add(block);
                setTerrainBlocked(block, false);
                placed++;
                System.out.println("📜 " + runeName + " placée sur : " + block);
            }
        }
    }

    public ArrayList<Block> getRuneBlocks() {
        return runeBlocks;
    }
    public ArrayList<Block> getShelterBlocks() {
        return shelterBlocks;
    }
    
    public ArrayList<Antagonist> getAntagonistList() {
        return antagonistList;
    }
    public HashMap<Antagonist, String> getAntagonistTypes() {
        return antagonistTypes;
    }
    
    public Block getCaveEntry() {
        return caveEntry;
    }
}
