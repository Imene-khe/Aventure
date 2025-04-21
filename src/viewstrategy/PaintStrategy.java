package viewstrategy;
import java.awt.Graphics;

import data.map.Map;
import data.player.Hero;
import gui.GameDisplay;
public interface PaintStrategy {
	
	// Terrain de base (grass, shopFloor...)
	void paintTerrain(Map map, Graphics g, GameDisplay display);
    // Objets statiques normaux (arbres, maisons, meubles...)
	void paintStaticObjects(Map map, Graphics g, GameDisplay display);

    // Objets spéciaux avec animation (maison en feu)
    void paintBurningHouse(Map map, Graphics g, GameDisplay display);

    // Shop (dans map principale)
    void paintShopBuilding(Map map, Graphics g, GameDisplay display);

    // Merchant (dans shopMap)
    void paintMerchant(Map map, Graphics g, GameDisplay display);

    // Pièce (coin)
    void paintCoins(Map map, Graphics g, GameDisplay display);


    // Ennemi
    void paintEnemies(Map map, Graphics g, GameDisplay display);

    // Héros
    void paintHero(Hero hero, Graphics g, GameDisplay display);

    // Barre de vie du héros
    void paintHealthBar(Hero hero, Graphics g, GameDisplay display);
    
    void paintMobileAntagonists(Map map, Graphics g, GameDisplay display);
    
    }
