package data.map;

import java.util.HashMap;
import java.util.ArrayList;

import data.item.chest.ChestManager;
import generator.ShopMapGenerator; 

public class ShopMap extends Map {

    public ShopMap(int lineCount, int columnCount) {
        super(lineCount, columnCount);
        this.setEnemies(new HashMap<>());
        this.setCoins(new ArrayList<>());
        this.setChestManager(new ChestManager());
        this.setFlames(new ArrayList<>());
        ShopMapGenerator.generateLayout(this); 
    }
}
