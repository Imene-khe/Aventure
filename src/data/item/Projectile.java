package data.item;

import data.map.Block;
import data.map.Map;

public class Projectile {
    private Block position;
    private int dx;
    private int dy;
    private boolean active = true;
    private String directionName;

    public Projectile(Block startPosition, int dx, int dy) {
        this.position = startPosition;
        this.dx = dx;
        this.dy = dy;

        // ✅ Déduire la direction
        if (dx > 0) directionName = "right";
        else if (dx < 0) directionName = "left";
        else if (dy > 0) directionName = "down";
        else directionName = "up"; // dx == 0 && dy < 0
    }

    public void move(Map map) {
        if (!active) return;

        int nextLine = position.getLine() + dy;
        int nextCol = position.getColumn() + dx;

        // ✅ Vérifie les bornes de la carte
        if (nextLine < 0 || nextLine >= map.getLineCount() ||
            nextCol < 0 || nextCol >= map.getColumnCount()) {
            active = false; // ❌ Stoppe le projectile s’il sort de la carte
            return;
        }

        Block nextBlock = map.getBlock(nextLine, nextCol);

        if (map.isBlocked(nextBlock)) {
            active = false; // ❌ Collision => projectile arrêté
        } else {
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
