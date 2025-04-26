package data.map;

import java.util.ArrayList;
import java.util.HashMap;
import data.player.Antagonist;
import generator.HostileMapGenerator; 

public class HostileMap extends Map {

    private ArrayList<Antagonist> antagonistList = new ArrayList<>();
    private HashMap<Antagonist, String> antagonistTypes = new HashMap<>();
    private ArrayList<Block> shelterBlocks = new ArrayList<>();
    private ArrayList<Block> runeBlocks = new ArrayList<>();
    private Block caveEntry; 

    public HostileMap(int lineCount, int columnCount, int maxChest) {
        super(lineCount, columnCount);
        staticObjects.clear();
        staticTerrain.clear();
        enemies.clear();
        terrainBlocked.clear();

        HostileMapGenerator.generateTerrain(this);
        HostileMapGenerator.generateObjects(this);
        HostileMapGenerator.generateCave(this);
        HostileMapGenerator.generateSafeShelter(this);
        HostileMapGenerator.generateEnemies(this);
        HostileMapGenerator.generateSymbols(this);
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
    
    public void setCaveEntry(Block caveEntry) {
        this.caveEntry = caveEntry;
    }
}
