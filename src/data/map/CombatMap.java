package data.map;

import java.util.ArrayList;
import java.util.Random;

import data.player.Antagonist;

public class CombatMap extends Map {
	
	private int centerStartLine;
	private int centerStartCol;
	


	private int bridgeCol;
	private ArrayList<Antagonist> antagonists = new ArrayList<>();

    

	public CombatMap(int lineCount, int columnCount) {
        super(lineCount, columnCount, 0, true);
        setStatic(false);

        staticTerrain.clear();
        staticObjects.clear();
        enemies.clear();
        terrainBlocked.clear();

        generateTerrain();
    }

    
    @Override
    public void generateTerrain() {
    	this.centerStartLine = (getLineCount() - 15) / 2;
    	this.centerStartCol = (getColumnCount() - 15) / 2;
    	this.bridgeCol = centerStartCol + 7;

        // Étape 1 : tout en noir bloqué
        for (int line = 0; line < getLineCount(); line++) {
            for (int col = 0; col < getColumnCount(); col++) {
                Block block = getBlock(line, col);
                staticTerrain.put(block, "black");
                setTerrainBlocked(block, true);
            }
        }

        // Étape 2 : zone centrale accessible
        for (int line = centerStartLine; line < centerStartLine + 15; line++) {
            for (int col = centerStartCol; col < centerStartCol + 15; col++) {
                Block block = getBlock(line, col);
                staticTerrain.put(block, "floorCave");
                setTerrainBlocked(block, false);
            }
        }

        // Étape 3 : bordures (bloquées sauf ouverture)
        for (int i = 0; i < 15; i++) {
            Block top = getBlock(centerStartLine, centerStartCol + i);
            staticTerrain.put(top, "horizontalBorder");
            setTerrainBlocked(top, true);

            if (i != 7) { // ✅ laisse l'ouverture au centre bas
                Block bottom = getBlock(centerStartLine + 14, centerStartCol + i);
                staticTerrain.put(bottom, "horizontalBorder");
                setTerrainBlocked(bottom, true);
            }

            Block left = getBlock(centerStartLine + i, centerStartCol);
            staticTerrain.put(left, "verticalBorder");
            setTerrainBlocked(left, true);

            Block right = getBlock(centerStartLine + i, centerStartCol + 14);
            staticTerrain.put(right, "verticalBorder");
            setTerrainBlocked(right, true);
        }

        // Étape 4 : pont (vertical vers le bas depuis l'ouverture)
        for (int line = centerStartLine + 15; line < getLineCount(); line++) {
            Block block = getBlock(line, bridgeCol);
            staticTerrain.put(block, "bridge");
            setTerrainBlocked(block, false);
        }

        // Étape 5 : plateformes aléatoires
        generatePlatforms();
 
    }

    public void generatePlatforms() {
        Random rand = new Random();
        int islands = 10 + rand.nextInt(3);

        int attempts = 0;

        // ↪️ Recalculer les coordonnées centrales localement
        int centerStartLine = (getLineCount() - 15) / 2;
        int centerStartCol  = (getColumnCount() - 15) / 2;
        int bridgeCol = centerStartCol + 7;

        while (islands > 0 && attempts < 200) {
            int i = rand.nextInt(getLineCount());
            int j = rand.nextInt(getColumnCount());
            Block block = getBlock(i, j);

            if (!"black".equals(staticTerrain.get(block))) {
                attempts++;
                continue;
            }

            // Évite les bords de l’arène
            if (i >= centerStartLine - 1 && i <= centerStartLine + 15 &&
                j >= centerStartCol - 1 && j <= centerStartCol + 15) {
                attempts++;
                continue;
            }

            // Évite le pont et ses bords
            if (Math.abs(j - bridgeCol) <= 1 && i >= centerStartLine + 14) {
                attempts++;
                continue;
            }

            staticTerrain.put(block, "platformCave");
            setTerrainBlocked(block, false);
            islands--;
            attempts++;
        }
    }
    
    public Block getArenaEntryPosition() {
        return getBlock(centerStartLine + 15, bridgeCol); // juste sous l'ouverture
    }

    @Override
    public void generateObjects() {
        // non utilisé pour l’instant
    }

    @Override
    public void generateEnemies() {
        // à faire plus tard
    }
    
    public ArrayList<Antagonist> getAntagonists() {
		return antagonists;
	}


	public void setAntagonists(ArrayList<Antagonist> antagonists) {
		this.antagonists = antagonists;
	}
	
	public int getCenterStartCol() {
		return centerStartCol;
	}

	public int getCenterStartLine() {
		return centerStartLine;
	}

	public void setCenterStartLine(int centerStartLine) {
		this.centerStartLine = centerStartLine;
	}


	public void setCenterStartCol(int centerStartCol) {
		this.centerStartCol = centerStartCol;
	}

}
