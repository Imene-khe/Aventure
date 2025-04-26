package data.map;

import java.util.ArrayList;

import data.item.Projectile;
import data.player.Antagonist;
import generator.CombatMapGenerator; // 👉 import du generator

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
		CombatMapGenerator.generateTerrain(this);
		CombatMapGenerator.generatePlatforms(this);
	}

	public Block getArenaEntryPosition() {
		return getBlock(centerStartLine + 15, bridgeCol); 
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

	public ArrayList<Block> getFinalBridgeBlocks() {
		return finalBridgeBlocks;
	}

	public void setFinalBridgeBlocks(ArrayList<Block> finalBridgeBlocks) {
		this.finalBridgeBlocks = finalBridgeBlocks;
	}

	public int getBridgeCol() {
		return bridgeCol;
	}

	public void setBridgeCol(int bridgeCol) {
		this.bridgeCol = bridgeCol;
	}

	public ArrayList<Block> getEntranceBridgeBlocks() {
		return entranceBridgeBlocks;
	}

	public void setEntranceBridgeBlocks(ArrayList<Block> entranceBridgeBlocks) {
		this.entranceBridgeBlocks = entranceBridgeBlocks;
	}

	public ArrayList<Block> getCageZoneBlocks() {
		return cageZoneBlocks;
	}

	public void setCageZoneBlocks(ArrayList<Block> cageZoneBlocks) {
		this.cageZoneBlocks = cageZoneBlocks;
	}

	public boolean isFinaleRevealed() {
		return finaleRevealed;
	}

	public void setFinaleRevealed(boolean finaleRevealed) {
		this.finaleRevealed = finaleRevealed;
	}

	public void setProjectiles(ArrayList<Projectile> projectiles) {
		this.projectiles = projectiles;
	}
}
