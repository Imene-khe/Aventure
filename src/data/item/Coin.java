package data.item;

import data.map.Block;

public class Coin {
    private final Block block;
    private boolean collected;

    public Coin(Block block) {
        this.block = block;
        this.collected = false;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
    }
}
