package data.item.weapon;

import data.item.Equipment;

public abstract class Armor extends Equipment {
    private int defensePower;

    public Armor(String name, int durability, int defensePower) {
        super(name, durability);
        this.defensePower = defensePower;
    }

    public int getDefensePower() {
        return defensePower;
    }
}

