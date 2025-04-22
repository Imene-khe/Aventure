package viewstrategy;

import java.awt.Graphics;
import data.map.Map;
import data.player.Hero;
import gui.GameDisplay;

public interface PaintStrategy {
	
	void paintTerrain(Map map, Graphics g, GameDisplay display);
	void paintStaticObjects(Map map, Graphics g, GameDisplay display);
    void paintBurningHouse(Map map, Graphics g, GameDisplay display);
    void paintShopBuilding(Map map, Graphics g, GameDisplay display);
    void paintMerchant(Map map, Graphics g, GameDisplay display);
    void paintCoins(Map map, Graphics g, GameDisplay display);
    void paintEnemies(Map map, Graphics g, GameDisplay display);
    void paintHero(Hero hero, Graphics g, GameDisplay display);
    void paintHealthBar(Hero hero, Graphics g, GameDisplay display);
    void paintMobileAntagonists(Map map, Graphics g, GameDisplay display);
    }
