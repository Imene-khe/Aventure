package data.item.weapon;

import data.item.Equipment;

/**
 * Classe représentant une arme.
 */
public abstract class Weapon extends Equipment {
    private int attackPower;

    public Weapon(String name, int durability, int attackPower) {
        super(name, durability);
        this.attackPower = attackPower;
    }

    public int getAttackPower() {
        return attackPower;
    }
}

