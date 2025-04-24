package data.item;

import org.apache.log4j.Logger;
import log.LoggerUtility;

import data.map.Block;
import data.map.Map;

public class Projectile {
	private static final Logger logger = LoggerUtility.getLogger(Projectile.class, "text");
    private Block position;
    private int dx;
    private int dy;
    private boolean active = true;
    private String directionName;

    public Projectile(Block startPosition, int dx, int dy) {
        this.position = startPosition;
        this.dx = dx;
        this.dy = dy;
        
        if (dx > 0) directionName = "right";
        else if (dx < 0) directionName = "left";
        else if (dy > 0) directionName = "down";
        else directionName = "up";
        logger.info("🛠️ Projectile initialisé à " + position + " avec direction '" + directionName + "'");
    }

    public void move(Map map) {
        if (!active) return;

        int nextLine = position.getLine() + dy;
        int nextCol = position.getColumn() + dx;
        if (nextLine < 0 || nextLine >= map.getLineCount() ||
            nextCol < 0 || nextCol >= map.getColumnCount()) {
            active = false;
            logger.info("⛔ Projectile désactivé (hors limites) à " + nextLine + "," + nextCol);
            return;
        }

        Block nextBlock = map.getBlock(nextLine, nextCol);

        if (map.isBlocked(nextBlock)) {
            active = false;
        } 
        else {
            position = nextBlock;
        }
    }


    public Block getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
    public String getDirectionName() {
        return directionName;
    }
}
