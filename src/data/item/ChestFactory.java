package data.item;

import data.item.weapon.Axe;
import data.item.weapon.Sword;

public class ChestFactory {
    public static Chest createChestWithRandomWeapons() {
        Chest chest = new Chest();
        chest.addItem(new Sword());
        chest.addItem(new Axe());
        return chest;
    }

    public static Chest createChestWithOrbe() {
        Chest chest = new Chest();
        chest.addItem(new Equipment("orbe"));
        return chest;
    }
}