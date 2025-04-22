package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

import data.map.Block;
import data.map.HostileMap;
import data.map.Map;
import data.player.Antagonist;
import data.player.Hero;
import viewstrategy.PaintStrategy;

public class DefaultPaintStrategy implements PaintStrategy{

	@Override
	public void paintTerrain(Map map, Graphics g, GameDisplay display) {
	    Block[][] blocks = map.getBlocks();
	    boolean isShop = display.isInShop();

	    // ✅ Tileset sélectionné dynamiquement selon le type de map
	    HashMap<String, Image> tileset =
	            (map instanceof data.map.CombatMap) ? display.getCombatTileset() :
	            (map instanceof data.map.HostileMap) ? display.getHostileTileset() :
	            display.getTileset();

	    for (int line = 0; line < map.getLineCount(); line++) {
	        for (int col = 0; col < map.getColumnCount(); col++) {
	            Block block = blocks[line][col];

	            String terrainType = map.getStaticTerrain().getOrDefault(block,
	                    isShop ? "shopFloor" : "grass");

	            if ("black".equals(terrainType)) {
	                // ✅ On affiche quand même si "black" est dans le tileset
	                Image terrainImage = tileset.get("black");
	                if (terrainImage != null) {
	                    g.drawImage(terrainImage, block.getColumn() * 32, block.getLine() * 32, 32, 32, null);
	                }
	                continue; // pas besoin de double affichage
	            }

	            Image terrainImage = tileset.get(terrainType);
	            if (terrainImage != null) {
	                g.drawImage(terrainImage, block.getColumn() * 32, block.getLine() * 32, 32, 32, null);
	            }
	        }
	    }
	}



	@Override
	public void paintStaticObjects(Map map, Graphics g, GameDisplay display) {
	    // ✅ Sélection dynamique du bon tileset (hostile ou normal)
	    HashMap<String, Image> tileset = 
	        map instanceof HostileMap ? display.getHostileTileset() : display.getTileset();

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
	        if (objectType != null && tileset.containsKey(objectType)) {
	            g.drawImage(tileset.get(objectType),
	                    block.getColumn() * 32, block.getLine() * 32,
	                    32, 32, null);
	        }
	    }

	    /// 🔹 Affichage du bâtiment shop UNIQUEMENT dans la carte principale
	    if (!display.isInShop() && !display.isInHostileMap() && display.getTileset().containsKey("shop")) {
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

	
	@Override
	public void paintMobileAntagonists(Map map, Graphics g, GameDisplay display) {
	    if (map instanceof HostileMap hMap) {
	        int size = display.getBlockSize();

	        for (Antagonist enemy : hMap.getAntagonistList()) {
	            Block block = enemy.getPosition();
	            String type = hMap.getAntagonistTypes().get(enemy); // 🔍 récupérer le type aléatoire

	            if (type != null) {
	                Image image = display.getEnemyImageManager().getEnemyImage(type, 0);
	                if (image != null) {
	                    int x = block.getColumn() * size;
	                    int y = block.getLine()   * size;
	                    g.drawImage(image, x, y, size, size, null);
	                } else {
	                    System.out.println("⚠️ Image non trouvée pour l'ennemi : " + type);
	                }
	            }
	        }
	    }
	}

	
}
