package data.item;

import data.map.Block;

public class Flame {
    private final Block position;
    private boolean active;

    public Flame(Block position) {
        this.position = position;
        this.active = true;
    }

    public Block getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public void extinguish() {
        this.active = false;
    }
}