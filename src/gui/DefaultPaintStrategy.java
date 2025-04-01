package gui;

import java.awt.Graphics;
import java.awt.Image;

import data.map.Block;
import data.map.Map;
import data.player.Hero;
import viewstrategy.PaintStrategy;

public class DefaultPaintStrategy implements PaintStrategy{

	@Override
	public void paintTerrain( Map map, Graphics g, GameDisplay display) {
		// TODO Auto-generated method stub
		Block[][] blocks = map.getBlocks();
	    boolean isShop = display.isInShop();

	    for (int line = 0; line < map.getLineCount(); line++) {
	        for (int col = 0; col < map.getColumnCount(); col++) {
	            Block block1 = blocks[line][col];

	            String terrainType = map.getStaticTerrain().getOrDefault(block1, isShop ? "shopFloor" : "grass");
	            Image terrainImage = display.getTileset().get(terrainType);

	            if (terrainImage != null) {
	                g.drawImage(terrainImage, block1.getColumn() * 32, block1.getLine() * 32, 32, 32, null);
	            }
	        }
	    }
		
	}

	@Override
	public void paintStaticObjects(Map map, Graphics g, GameDisplay display) {
	    for (Block block : map.getStaticObjects().keySet()) {
	        String objectType = map.getStaticObjects().get(block);

	        // 🔥 Cas spécial : maison en feu
	        if ("house_burning".equals(objectType)) {
	            if (display.getTileset().containsKey("house")) {
	                g.drawImage(display.getTileset().get("house"),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }

	            if (display.getFlameAnimator() != null) {
	                g.drawImage(display.getFlameAnimator().getCurrentFrame(),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }

	            continue; // ✅ on passe à l’objet suivant
	        }

	        // 💡 Cas générique : arbre, meuble, torche, coffre, etc.
	        if (objectType != null && display.getTileset().containsKey(objectType)) {
	            g.drawImage(display.getTileset().get(objectType),
	                    block.getColumn() * 32, block.getLine() * 32,
	                    32, 32, null);
	        }
	    }

	    // 🔹 Affichage du bâtiment shop dans la map principale
	    if (!display.isInShop() && display.getTileset().containsKey("shop")) {
	        for (Block block : display.getMap().getStaticObjects().keySet()) {
	            if ("shop".equals(display.getMap().getStaticObjects().get(block))) {
	                g.drawImage(display.getTileset().get("shop"),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }
	        }
	    }

	    // 🔹 Affichage du marchand dans la boutique
	    if (display.isInShop() && display.getTileset().containsKey("merchant")) {
	        for (Block block : display.getShopMap().getStaticObjects().keySet()) {
	            if ("merchant".equals(display.getShopMap().getStaticObjects().get(block))) {
	                g.drawImage(display.getTileset().get("merchant"),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }
	        }
	    }
	}


	

	@Override
	public void paintCoins(Map map, Graphics g, GameDisplay display) {
	    for (data.item.Coin coin : map.getCoins()) {
	        if (!coin.isCollected()) {
	            Block block = coin.getBlock();
	            int x = block.getColumn() * 32;
	            int y = block.getLine() * 32;

	            Image frame = display.getCoinAnimator().getCurrentFrame();

	            int coinSize = (int) (32 * 0.5);
	            int offset = (32 - coinSize) / 2;

	            g.drawImage(frame, x + offset, y + offset, coinSize, coinSize, null);
	        }
	    }
	}


	@Override
	public void paintEnemies(Map map, Graphics g, GameDisplay display) {
	    for (Block block : map.getEnemies().keySet()) {
	        String enemyType = map.getEnemies().get(block);

	        Image enemyImage = display.getEnemyImageManager().getEnemyImage(enemyType, 0);
	        if (enemyImage != null) {
	            int x = block.getColumn() * 32;
	            int y = block.getLine() * 32;
	            g.drawImage(enemyImage, x, y, 32, 32, null);
	        } else {
	            System.out.println("⚠ BUG: Ennemi " + enemyType + " non affiché !");
	        }
	    }
	}


	@Override
	public void paintHero(Hero hero, Graphics g, GameDisplay display) {
	    hero.draw(g, 32); // ou display.getBlockSize() si tu veux le rendre dynamique
	}


	@Override
	public void paintHealthBar(Hero hero, Graphics g, GameDisplay display) {
	    int maxHealth = 100;
	    int currentHealth = hero.getHealth();

	    g.setColor(java.awt.Color.RED);
	    g.fillRect(10, 10, 200, 20);
	    g.setColor(java.awt.Color.GREEN);
	    g.fillRect(10, 10, (currentHealth * 200) / maxHealth, 20);
	    g.setColor(java.awt.Color.BLACK);
	    g.drawRect(10, 10, 200, 20);
	    g.drawString("Vie : " + currentHealth + "%", 90, 25);
	}

	@Override
	public void paintBurningHouse(Map map, Graphics g, GameDisplay display) {
	    for (Block block : map.getStaticObjects().keySet()) {
	        String objectType = map.getStaticObjects().get(block);
	        if ("house_burning".equals(objectType)) {
	            // Maison de fond
	            if (display.getTileset().containsKey("house")) {
	                g.drawImage(display.getTileset().get("house"),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }
	            // Flammes au-dessus
	            if (display.getFlameAnimator() != null) {
	                g.drawImage(display.getFlameAnimator().getCurrentFrame(),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }
	        }
	    }
	}


	@Override
	public void paintShopBuilding(Map map, Graphics g, GameDisplay display) {
	    if (!display.isInShop() && display.getTileset().containsKey("shop")) {
	        for (Block block : map.getStaticObjects().keySet()) {
	            if ("shop".equals(map.getStaticObjects().get(block))) {
	                g.drawImage(display.getTileset().get("shop"),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }
	        }
	    }
	}


	@Override
	public void paintMerchant(Map map, Graphics g, GameDisplay display) {
	    if (display.isInShop() && display.getTileset().containsKey("merchant")) {
	        for (Block block : map.getStaticObjects().keySet()) {
	            if ("merchant".equals(map.getStaticObjects().get(block))) {
	                g.drawImage(display.getTileset().get("merchant"),
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }
	        }
	    }
	}


	
}
