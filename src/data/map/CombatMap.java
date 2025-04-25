package data.map;

import java.util.ArrayList;
import java.util.Random;

import data.item.Projectile;
import data.player.Antagonist;
import gui.GameDisplay;

public class CombatMap extends Map {
	private int centerStartLine;
	private int centerStartCol;
	private int bridgeCol;
	private ArrayList<Antagonist> antagonists = new ArrayList<>();
	private ArrayList<Projectile> projectiles = new ArrayList<>();
	private ArrayList<Block> finalBridgeBlocks = new ArrayList<>();
	private ArrayList<Block> entranceBridgeBlocks = new ArrayList<>();
	private ArrayList<Block> cageZoneBlocks = new ArrayList<>();
	private boolean finaleRevealed = false;


    

	public CombatMap(int lineCount, int columnCount) {
		super(lineCount, columnCount);
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

	    for (int line = 0; line < getLineCount(); line++) {
	        for (int col = 0; col < getColumnCount(); col++) {
	            Block block = getBlock(line, col);
	            staticTerrain.put(block, "black");
	            setTerrainBlocked(block, true);
	        }
	    }

	    for (int line = centerStartLine; line < centerStartLine + 15; line++) {
	        for (int col = centerStartCol; col < centerStartCol + 15; col++) {
	            if (isInsideMap(line, col)) {
	                Block block = getBlock(line, col);
	                staticTerrain.put(block, "floorCave");
	                setTerrainBlocked(block, false);
	            }
	        }
	    }

	    for (int i = 0; i < 15; i++) {
	        if (isInsideMap(centerStartLine, centerStartCol + i)) {
	            Block top = getBlock(centerStartLine, centerStartCol + i);
	            staticTerrain.put(top, "horizontalBorder");
	            setTerrainBlocked(top, true);
	        }

	        if (i != 7 && isInsideMap(centerStartLine + 14, centerStartCol + i)) {
	            Block bottom = getBlock(centerStartLine + 14, centerStartCol + i);
	            staticTerrain.put(bottom, "horizontalBorder");
	            setTerrainBlocked(bottom, true);
	        }

	        if (isInsideMap(centerStartLine + i, centerStartCol)) {
	            Block left = getBlock(centerStartLine + i, centerStartCol);
	            staticTerrain.put(left, "verticalBorder");
	            setTerrainBlocked(left, true);
	        }

	        if (isInsideMap(centerStartLine + i, centerStartCol + 14)) {
	            Block right = getBlock(centerStartLine + i, centerStartCol + 14);
	            staticTerrain.put(right, "verticalBorder");
	            setTerrainBlocked(right, true);
	        }
	    }
	    Block bridgeExit = getBlock(centerStartLine + 7, centerStartCol + 14);
	    setTerrainBlocked(bridgeExit, false); 
	    
	    entranceBridgeBlocks.clear();
	    for (int l = centerStartLine + 15; l < getLineCount(); l++) {
	        Block b = getBlock(l, bridgeCol);
	        staticTerrain.put(b, "bridge");
	        setTerrainBlocked(b, false);
	        entranceBridgeBlocks.add(b);
	    }

	    
	    
	    finalBridgeBlocks.clear();
	    int finalBridgeLine = centerStartLine + 7;
	    int finalBridgeStartCol = centerStartCol + 15;
	    for (int col = finalBridgeStartCol; col < finalBridgeStartCol + 4; col++) {
	        if (isInsideMap(finalBridgeLine, col)) {
	            Block b = getBlock(finalBridgeLine, col);
	            finalBridgeBlocks.add(b); 
	        }
	    }

	    cageZoneBlocks.clear();
	    if (isInsideMap(finalBridgeLine - 1, finalBridgeStartCol + 4))
	        cageZoneBlocks.add(getBlock(finalBridgeLine - 1, finalBridgeStartCol + 4));
	    if (isInsideMap(finalBridgeLine, finalBridgeStartCol + 4))
	        cageZoneBlocks.add(getBlock(finalBridgeLine, finalBridgeStartCol + 4));
	    if (isInsideMap(finalBridgeLine + 1, finalBridgeStartCol + 4))
	        cageZoneBlocks.add(getBlock(finalBridgeLine + 1, finalBridgeStartCol + 4));

	    generatePlatforms();
	}

	private boolean isInsideMap(int line, int col) {
	    return line >= 0 && line < getLineCount() && col >= 0 && col < getColumnCount();
	}



    public void generatePlatforms() {
        Random rand = new Random();
        int islands = 10 + rand.nextInt(3);
        int attempts = 0;
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
            if (i >= centerStartLine - 1 && i <= centerStartLine + 15 &&
                j >= centerStartCol - 1 && j <= centerStartCol + 15) {
                attempts++;
                continue;
            }
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

    public void revealFinaleZone() {
        if (finaleRevealed) return;
        finaleRevealed = true;

        // ✅ Tous les blocs du pont restent sur fond "black" (non modifié)
        for (Block b : finalBridgeBlocks) {
            staticTerrain.put(b, "endBridge");       // Objet pont
            setTerrainBlocked(b, false);             // On débloque pour marcher
        }

        // ✅ Sur le dernier bloc uniquement : fond visible + cage_with_princess
        if (!finalBridgeBlocks.isEmpty()) {
            Block finalBlock = finalBridgeBlocks.get(finalBridgeBlocks.size() - 1);

            staticTerrain.put(finalBlock, "platformCave");      // Fond visible pour cage
            staticObjects.put(finalBlock, "cage_with_princess");// Objet cage
            setTerrainBlocked(finalBlock, false);               // On débloque
        }
    }





    
    public ArrayList<Antagonist> getAntagonists() {
		return antagonists;
	}


    public void setAntagonists(ArrayList<Antagonist> list) {
        this.antagonists = list;
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
	
	public void clearAntagonists() {
	    antagonists.clear();
	}
	
	public boolean areAllEnemiesDead() {
	    return antagonists.stream().allMatch(Antagonist::isDead);
	}
	
	public ArrayList<Projectile> getProjectiles() {
	    return projectiles;
	}
	
	public static void main(String[] args) {
	    javax.swing.SwingUtilities.invokeLater(() -> {
	        CombatMap combatMap = new CombatMap(23, 40);

	        // 🔁 Génération du pont final et de la cage
	        combatMap.revealFinaleZone();

	        // 🔁 Libération immédiate de la princesse (remplacement de la cage)
	        if (!combatMap.getFinalBridgeBlocks().isEmpty()) {
	            Block finalBlock = combatMap.getFinalBridgeBlocks().get(combatMap.getFinalBridgeBlocks().size() - 1);
	            combatMap.getStaticTerrain().put(finalBlock, "platformCave"); // fond visible
	            combatMap.getStaticObjects().put(finalBlock, "princess");     // image libérée
	            combatMap.setTerrainBlocked(finalBlock, false);              // libre d’accès
	        }

	        // 🎮 Affichage
	        GameDisplay display = new GameDisplay();
	        display.setCombatMap(combatMap);
	        display.setMap(combatMap);
	        display.setInShop(false);
	        display.setHero(new data.player.Hero(combatMap.getArenaEntryPosition(), 100));

	        javax.swing.JFrame frame = new javax.swing.JFrame("Test CombatMap - Princess Libre");
	        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	        frame.getContentPane().add(display);
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	    });
	}


	public ArrayList<Block> getFinalBridgeBlocks() {
		return finalBridgeBlocks;
	}


	public void setFinalBridgeBlocks(ArrayList<Block> finalBridgeBlocks) {
		this.finalBridgeBlocks = finalBridgeBlocks;
	}




}
