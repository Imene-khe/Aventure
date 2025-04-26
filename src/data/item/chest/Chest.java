package data.item.chest;

import data.item.inventory.Inventory;

public class Chest {

    private final Inventory inventory;
    private boolean opened;

    public Chest() {
        this.inventory = new Inventory();
        this.opened = false;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isOpened() {
        return opened;
    }

    public void openChest() {
        this.opened = true;
    }
}
