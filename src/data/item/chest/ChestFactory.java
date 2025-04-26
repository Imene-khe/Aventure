package data.item.chest;

import data.item.Equipment;

import java.util.Random;

public class ChestFactory {

    private static final Equipment[] possibleItems = {
        new Equipment("axe"),
        new Equipment("woodsword"),
        new Equipment("woodstick"),
    };

    public static Chest createChestWithRandomWeapons() {
        Chest chest = new Chest();
        Random random = new Random();
        int numberOfItems = random.nextInt(3) + 1;

        for (int i = 0; i < numberOfItems; i++) {
            Equipment item = new Equipment(possibleItems[random.nextInt(possibleItems.length)].getName());
            chest.getInventory().getEquipments().add(item);
        }

        return chest;
    }

    public static Chest createChestWithOrbe() {
        Chest chest = new Chest();
        chest.getInventory().getEquipments().add(new Equipment("orbe"));
        return chest;
    }
}
