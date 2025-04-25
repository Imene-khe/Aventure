package gui;

import org.apache.log4j.Logger;
import log.LoggerUtility;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import data.item.Projectile;
import data.map.Block;
import data.map.CombatMap;
import data.map.HostileMap;
import data.map.Map;
import data.player.Antagonist;
import data.player.Hero;
import gui.animation.EndCreditsPanel;
import viewstrategy.PaintStrategy;

public class DefaultPaintStrategy implements PaintStrategy{
	private static final Logger logger = LoggerUtility.getLogger(DefaultPaintStrategy.class, "text");

	@Override
	public void paintTerrain(Map map, Graphics g, GameDisplay display) {
	    Block[][] blocks = map.getBlocks();
	    boolean isShop = display.isInShop();
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
	    HashMap<String, Image> tileset;

	    if (map instanceof CombatMap) {
	        tileset = display.getCombatTileset();
	    } else if (map instanceof HostileMap) {
	        tileset = display.getHostileTileset();
	    } else {
	        tileset = display.getTileset();
	    }

	    for (Block block : map.getStaticObjects().keySet()) {
	        String objectType = map.getStaticObjects().get(block);

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

	            continue;
	        }

	        if (objectType != null && tileset.containsKey(objectType)) {
	            Image img = tileset.get(objectType);
	            


	            if ("cage_with_princess".equals(objectType) || "princess".equals(objectType)) {
	                int size = 26;
	                int offset = (32 - size) / 2;
	                g.drawImage(img,
	                        block.getColumn() * 32 + offset,
	                        block.getLine() * 32 + offset,
	                        size, size, null);
	            } else {
	                // 💡 Cas générique
	                g.drawImage(img,
	                        block.getColumn() * 32, block.getLine() * 32,
	                        32, 32, null);
	            }
	        }

	    }

	    if (!display.isInShop() && !display.isInHostileMap() && display.getTileset().containsKey("shop")) {
	        for (Block block : display.getMap().getStaticObjects().keySet()) {
	            if ("shop".equals(display.getMap().getStaticObjects().get(block))) {
	                g.drawImage(display.getTileset().get("shop"),
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
	        	logger.warn("⚠ BUG d'affichage : image manquante pour l'ennemi de type '" + enemyType + "'");
	        }
	    }
	}


	@Override
	public void paintHero(Hero hero, Graphics g, GameDisplay display) {
	    int blockSize = 32;
	    int heroSize = (int)(blockSize * 0.70); // 70% du blockSize

	    int drawX = hero.getPosition().getColumn() * blockSize + (blockSize - heroSize) / 2;
	    int drawY = hero.getPosition().getLine() * blockSize + (blockSize - heroSize) / 2;

	    Image heroSprite = display.getTileset().get("hero");
	    if (heroSprite != null) {
	        g.drawImage(heroSprite,
	                drawX, drawY, drawX + heroSize, drawY + heroSize,
	                0, 0, heroSprite.getWidth(null), heroSprite.getHeight(null),
	                null);
	    } else {
	        System.out.println("⚠ BUG : Image du héros non trouvée dans le tileset !");
	    }
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
	    int size = display.getBlockSize();
	    ArrayList<Antagonist> enemies = new ArrayList<>();
	    if (map instanceof HostileMap hMap) {
	        enemies = hMap.getAntagonistList();
	    } else if (map instanceof CombatMap cMap) {
	        enemies = cMap.getAntagonists();
	    }

	    for (Antagonist enemy : enemies) {
	        Block block = enemy.getPosition();
	        Image image = display.getEnemyImageManager().getEnemyImageFor(enemy);
	        if (image != null) {
	            int x = block.getColumn() * size;
	            int y = block.getLine() * size;
	            g.drawImage(image, x, y, size, size, null);
	            if ("boss".equals(enemy.getType())) {
	                EnemyHealthBar.draw(g, enemy.getHealth(), enemy.getMaxHealth(), x, y);
	            }
	        } else {
	        	logger.warn("⚠ Image manquante pour ennemi mobile de type '" + enemy.getType() + "' à " + enemy.getPosition());
	        }
	    }

	    // ✅ Dessin des projectiles uniquement dans la CombatMap
	    if (map instanceof CombatMap cMap) {
	        paintProjectiles(cMap, g, display);
	    }
	}

	
	public void paintProjectiles(CombatMap map, Graphics g, GameDisplay display) {
	    int size = display.getBlockSize();
	    for (Projectile p : map.getProjectiles()) {
	        if (p.isActive()) {
	            Block b = p.getPosition();
	            int x = b.getColumn() * size;
	            int y = b.getLine() * size;

	            String key = "projectile_" + p.getDirectionName();
	            Image img = display.getCombatTileset().get(key);

	            if (img != null) {
	                g.drawImage(img, x + 8, y + 8, size / 2, size / 2, null);
	            } else {
	                g.setColor(java.awt.Color.MAGENTA);
	                g.fillOval(x + 8, y + 8, size / 2, size / 2);
	            }
	        }
	    }
	}
	
	@Override
	public void paintEndCredits(Graphics g, EndCreditsPanel panel) {
	    int y = panel.getYPosition();
	    int width = panel.getWidth();

	    g.setColor(Color.BLACK);
	    g.fillRect(0, 0, width, panel.getHeight());

	    g.setColor(Color.WHITE);
	    g.setFont(new Font("Arial", Font.BOLD, 24));

	    String line1 = "Jeu réalisé par :";
	    String line2 = "Mathis Albrun - Imene Khelil";
	    String line3 = "Dans le cadre de l'UE Génie Logiciel, année 2024-2025";
	    String line4 = "Remerciements à notre enseignant, M.LIU";

	    FontMetrics fm = g.getFontMetrics();

	    g.drawString(line1, (width - fm.stringWidth(line1)) / 2, y);
	    g.drawString(line2, (width - fm.stringWidth(line2)) / 2, y + 40);
	    g.drawString(line3, (width - fm.stringWidth(line3)) / 2, y + 80);
	    g.drawString(line4, (width - fm.stringWidth(line4)) / 2, y + 120);
	}






}

	

