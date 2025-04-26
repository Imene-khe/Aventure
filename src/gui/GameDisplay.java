package gui;

import org.apache.log4j.Logger;
import log.LoggerUtility;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import control.GameController;
import control.GameLoopManager;
import data.map.Block;
import data.map.CombatMap;
import data.map.HostileMap;
import data.map.Map;
import data.map.ShopMap;
import data.player.EnemyImageManager;
import data.player.Hero;
import gui.animation.SpriteAnimator;
import viewstrategy.PaintStrategy;


/**
 * Classe représentant l'affichage du jeu. Elle gère le rendu graphique de la CARTE, des ennemis, du héros
 * et de la barre de vie. Elle permet également de déplacer le héros et de gérer les interactions avec les objets.
 */
public class GameDisplay extends JPanel {
	
	private static final Logger logger = LoggerUtility.getLogger(GameDisplay.class, "text");
    private static final long serialVersionUID = 1L;
    private static final int GRID_SIZE = 35; 
    private static final int MAPS_LENGTH = 40; 
    private static final int MAPS_WIDTH = 22;  static int BLOCK_SIZE = 32; 
    private Map map; 
    private ShopMap shopMap;
    private HostileMap hostileMap;
    private CombatMap combatMap;
	private Hero hero; 
    private EnemyImageManager enemyImageManager; 
    private HashMap<String, Image> tileset; 
    private boolean isGameOver = false; 
    private boolean isInShop = false; 
    private boolean isInHostileMap = false;
    private boolean isInCombatMap = false;
    private SpriteAnimator flameAnimator;
    private SpriteAnimator coinAnimator;
    private PaintStrategy paintStrategy = new DefaultPaintStrategy();
    private GameController controller;
    private HashMap<String, Image> hostileTileset;
    private HashMap<String, Image> combatTileset; 


	/**
     * Constructeur de la classe. Initialise la carte, le héros et les images.
     */
	public GameDisplay() {
	    try {
	        int numberOfChests = 5;
	        this.enemyImageManager = new EnemyImageManager();
	        this.map = new Map(MAPS_WIDTH, MAPS_LENGTH, numberOfChests, false);
	        this.shopMap = new ShopMap(MAPS_WIDTH, MAPS_LENGTH);
	        this.hostileMap = new HostileMap(MAPS_WIDTH, MAPS_LENGTH, 0); 
	        this.combatMap = new CombatMap(MAPS_WIDTH, MAPS_LENGTH);

	        this.hero = new Hero(map.getBlock(GRID_SIZE / 2, GRID_SIZE / 2), 100);
	        this.tileset = new HashMap<>();
	        this.controller = new GameController(this); 
	        this.addMouseListener(new MouseControls());

	        try {
	            String[] coinPaths = new String[8];
	            for (int i = 0; i < 8; i++) {
	                coinPaths[i] = "src/images/items/coins/coin" + (i + 1) + ".png";
	            }
	            coinAnimator = new SpriteAnimator(coinPaths, 100); 
	            logger.info("coinAnimator (8 images) chargé avec succès.");
	            } catch (IOException e) {
	            	logger.error("Impossible de charger les images d'animation des pièces.");
	            	e.printStackTrace();
	        }
	        GameLoopManager.getInstance().setGameDisplay(this);
	        GameLoopManager.getInstance().setGameController(controller);
	        GameLoopManager.getInstance().start();


	        loadImages(); 
	        
	        try {
	            flameAnimator = new SpriteAnimator("src/images/outdoors/flames.png", 4, 3, 100);
	        } catch (IOException e) {
	            logger.error("Impossible de charger l'animation des flammes !");
	            e.printStackTrace();
	        }

	        logger.info("GameDisplay créé avec succès.");
	        } catch (Exception e) {
	        	logger.error("Erreur lors de l'initialisation de GameDisplay.");
	        	e.printStackTrace();
	    }
	}

   
	public void resetMouseListener() {
	    for (MouseListener ml : this.getMouseListeners()) {
	        this.removeMouseListener(ml);
	    }
	    this.addMouseListener(new MouseControls());
	}
 
   


	public void loadImages() {
        try {
        	logger.info("Chargement des images...");
        	
        	tileset.put("hero", loadImage("src/images/player/Player.png"));
            tileset.put("grass", loadImage("src/images/outdoors/Grass_Middle.png"));
            tileset.put("water", loadImage("src/images/outdoors/Water_Middle.png"));
            tileset.put("path", loadImage("src/images/outdoors/Path_Middle.png"));
            tileset.put("shopFloor", loadImage("src/images/shop/shopfloor.png"));
            tileset.put("lightWall", loadImage("src/images/shop/lightwall.png")); 
            tileset.put("bar", loadImage("src/images/shop/bar.png")); 
            tileset.put("merchant", loadImage("src/images/shop/merchant.png")); 
            tileset.put("carpet", loadImage("src/images/shop/carpet.png")); 
            tileset.put("bookshelf", loadImage("src/images/shop/bookshelf.png"));
            tileset.put("table", loadImage("src/images/shop/table.png"));
            tileset.put("villager1", loadImage("src/images/shop/people/villager1.png")); 
            tileset.put("villager2", loadImage("src/images/shop/people/villager2.png")); 
            tileset.put("villager3", loadImage("src/images/shop/people/villager3.png")); 
            tileset.put("villager4", loadImage("src/images/shop/people/villager4.png")); 
            tileset.put("villager5", loadImage("src/images/shop/people/villager5.png"));
            tileset.put("music_stage", loadImage("src/images/shop/music_stage.png"));
            tileset.put("musician1", loadImage("src/images/shop/people/musician/musician1.png"));
            tileset.put("musician2", loadImage("src/images/shop/people/musician/musician2.png"));
            tileset.put("musician3", loadImage("src/images/shop/people/musician/musician3.png"));
            tileset.put("house", loadImage("src/images/outdoors/House.png"));
            tileset.put("tree", loadImage("src/images/outdoors/Oak_Tree.png"));
            tileset.put("shop", loadImage("src/images/shop/shop.png")); 
            tileset.put("chest", loadImage("src/images/outdoors/Chest.png"));
            
            hostileTileset = new HashMap<>();
            hostileTileset.put("lava", loadImage("src/images/outdoor/hostile/lava.png"));
            hostileTileset.put("floor1", loadImage("src/images/outdoor/hostile/floor1.png"));
            hostileTileset.put("floor2", loadImage("src/images/outdoor/hostile/floor2.png"));
            hostileTileset.put("floor3", loadImage("src/images/outdoor/hostile/floor3.png"));
            hostileTileset.put("rock", loadImage("src/images/outdoor/hostile/rock.png")); 
            hostileTileset.put("toprock", loadImage("src/images/outdoor/hostile/shelter/toprock.png"));
            hostileTileset.put("rightborderrock", loadImage("src/images/outdoor/hostile/shelter/rightborderrock.png"));
            hostileTileset.put("leftborderrock", loadImage("src/images/outdoor/hostile/shelter/leftborderrock.png"));
            hostileTileset.put("topleftrock", loadImage("src/images/outdoor/hostile/shelter/topleftrock.png"));
            hostileTileset.put("toprightrock", loadImage("src/images/outdoor/hostile/shelter/toprightrock.png"));
            hostileTileset.put("campfire_off", loadImage("src/images/outdoor/hostile/shelter/CampFireOff.png"));
            hostileTileset.put("campfire_on", loadImage("src/images/outdoor/hostile/shelter/CampFireOn.png"));
            hostileTileset.put("deadTree1", loadImage("src/images/outdoor/hostile/deadTree1.png"));
            hostileTileset.put("deadTree2", loadImage("src/images/outdoor/hostile/deadTree2.png"));
            hostileTileset.put("deadTree3", loadImage("src/images/outdoor/hostile/deadTree3.png"));
            hostileTileset.put("cave_left", loadImage("src/images/outdoor/hostile/cave/cave_left.png"));
            hostileTileset.put("cave_right", loadImage("src/images/outdoor/hostile/cave/cave_right.png"));
            hostileTileset.put("cave_top", loadImage("src/images/outdoor/hostile/cave/cave_top.png"));
            hostileTileset.put("cave_bottom", loadImage("src/images/outdoor/hostile/cave/cave_bottom.png")); 
            hostileTileset.put("cave_shadow", loadImage("src/images/outdoor/hostile/cave/shadow.png")); 
            hostileTileset.put("rune1", loadImage("src/images/outdoor/hostile/symbol/rune1.png"));
            hostileTileset.put("rune2", loadImage("src/images/outdoor/hostile/symbol/rune2.png"));
            hostileTileset.put("rune3", loadImage("src/images/outdoor/hostile/symbol/rune3.png"));
            
            combatTileset = new HashMap<>();
            combatTileset.put("floorCave", loadImage("src/images/outdoor/combat/mapComponent/floorcave.png"));
            combatTileset.put("platformCave", loadImage("src/images/outdoor/combat/mapComponent/platformCave.png"));
            combatTileset.put("black", loadImage("src/images/outdoor/combat/mapComponent/black.png"));
            combatTileset.put("bridge", loadImage("src/images/outdoor/combat/mapComponent/bridge.png"));
            combatTileset.put("verticalBorder", loadImage("src/images/outdoor/combat/mapComponent/verticalBorder.png"));
            combatTileset.put("horizontalBorder", loadImage("src/images/outdoor/combat/mapComponent/horizontalBorder.png"));
            combatTileset.put("projectile_right", loadImage("src/images/outdoor/combat/projectile/projectileRight.png"));
            combatTileset.put("projectile_left", loadImage("src/images/outdoor/combat/projectile/projectileLeft.png"));
            combatTileset.put("projectile_down", loadImage("src/images/outdoor/combat/projectile/projectileDown.png"));
            combatTileset.put("projectile_up", loadImage("src/images/outdoor/combat/projectile/projectileTop.png"));
            combatTileset.put("cage", loadImage("src/images/outdoor/combat/mapComponent/cage.png"));
            combatTileset.put("endBridge", loadImage("src/images/outdoor/combat/mapComponent/endBridge.png"));
            combatTileset.put("cage_with_princess", loadImage("src/images/player/cagewithprincess.png"));
            combatTileset.put("princess", loadImage("src/images/player/princess.png"));

            logger.info("Toutes les images sont chargées.");
        } catch (Exception e) {
        	logger.error("Erreur lors du chargement des images.");
        	e.printStackTrace();
        }
    }

	public Image loadImage(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
        	logger.warn("Image non trouvée : " + path);
            return null;
        }
        return ImageIO.read(file);
    }

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);

	    Map mapToDraw = (map instanceof CombatMap) ? map : (isInHostileMap ? hostileMap : (isInShop ? shopMap : map));

	    if (mapToDraw == null || (isInHostileMap ? hostileTileset : tileset) == null) {
	    	logger.error("Erreur : map ou tileset null.");
	    	return;
	    }
	    paintStrategy.paintTerrain(mapToDraw, g, this);
	    if (!isInShop && !isInHostileMap) {
	        paintStrategy.paintCoins(mapToDraw, g, this);
	    }
	    paintStrategy.paintStaticObjects(mapToDraw, g, this);
	    if (!isInShop && !isInHostileMap) {
	    	paintStrategy.paintBurningHouse(mapToDraw, g, this);
	    	paintStrategy.paintShopBuilding(mapToDraw, g, this);

	    } else if (isInShop) {
	        paintStrategy.paintMerchant(shopMap, g, this);
	    }
	    if (!isInShop) {
	        paintStrategy.paintEnemies(mapToDraw, g, this);
	        paintStrategy.paintHealthBar(hero, g, this);
	    } 
	    if (isInHostileMap || isInCombatMap) {
	        paintStrategy.paintMobileAntagonists(mapToDraw, g, this);
	    }
	    paintStrategy.paintHero(hero, g, this);
	}

	
    /**
     * ✅ Active l'affichage de `shopMap`
     */
    public void enterShop() {
        isInShop = true;
        hero.setPosition(shopMap.getBlock(21, 6)); 
        repaint(); 
    }

    public void returnToMainMap(Block exitBlock) {
        isInShop = false;
        isInHostileMap = false;
        isInCombatMap = false;

        if (exitBlock != null) {
            hero.setPosition(exitBlock);
        }

        repaint();
        requestFocusInWindow();
        logger.info("Sortie de la boutique, retour à la carte principale.");
    }

    
	
	public void enterHostileMap() {
	    this.isInHostileMap = true;
	    this.isInShop = false;
	    this.isInCombatMap = false;
	    
	    this.hero.setPosition(hostileMap.getBlock(17, 5));
	    repaint();
	    resetMouseListener();
	    logger.info("Passage à la HostileMap.");
	}


	
	public void enterCombatMap() {
	    this.isInShop = false;
	    this.isInHostileMap = false;
	    this.isInCombatMap = true;
	    this.map = this.combatMap; 

	    this.hero.setPosition(combatMap.getArenaEntryPosition());

	    if (gui.MainGUI.getInstance() != null) { 
	        gui.MainGUI.getInstance().getQuestManager().clearQuests();
	        gui.MainGUI.getInstance().getQuestManager().loadCombatMapQuests();
	    } else {
	    	logger.warn("MainGUI non disponible (mode test manuel).");
	    }

	    repaint();
	    setFocusable(true);
	    requestFocusInWindow();
	}

	private class MouseControls extends MouseAdapter {
	    @Override
	    public void mouseClicked(MouseEvent e) {
	        if (SwingUtilities.isLeftMouseButton(e)) {
	            if (controller != null && controller.getCombatController() != null) {
	            	logger.info("Clic détecté.");
	            	controller.getCombatController().handleClick(e.getPoint());
	            }
	        }
	    }
	}
	
	public Map getActiveMap() {
	    if (isInCombatMap) return combatMap;
	    if (isInHostileMap) return hostileMap;
	    if (isInShop) return shopMap;
	    return map;
	}

	public HostileMap getHostileMap() {
		return hostileMap;
	}

	public void setHostileMap(HostileMap hostileMap) {
		this.hostileMap = hostileMap;
	}


	public boolean isInHostileMap() {
	    return isInHostileMap;
	}
	
	public CombatMap getCombatMap() {
		return combatMap;
	}
	
	public void setCombatMap(CombatMap combatMap) {
		this.combatMap = combatMap;
	}
	
	public HashMap<String, Image> getCombatTileset() {
		return combatTileset;
	}

	public void setCombatTileset(HashMap<String, Image> combatTileset) {
		this.combatTileset = combatTileset;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}
	
	 public Map getMap() {
			return map;
		}
	   
	    public int getBlockSize() {
	        return BLOCK_SIZE;
	    }

		public void setMap(Map map) {
			this.map = map;
		}

		public Hero getHero() {
			return hero;
		}

		public void setHero(Hero hero) {
			this.hero = hero;
		}

		public EnemyImageManager getEnemyImageManager() {
			return enemyImageManager;
		}

		public void setEnemyImageManager(EnemyImageManager enemyImageManager) {
			this.enemyImageManager = enemyImageManager;
		}

		public HashMap<String, Image> getTileset() {
			return tileset;
		}

		public void setTileset(HashMap<String, Image> tileset) {
			this.tileset = tileset;
		}
		
		public ShopMap getShopMap() {
		    return shopMap;
		}
    
    
		public SpriteAnimator getFlameAnimator() {
	        return flameAnimator;
	    }
	    
	    public SpriteAnimator getCoinAnimator() {
	        return coinAnimator;
	    }
	    
	    public GameController getController() {
			return controller;
		}

		public void setController(GameController controller) {
			this.controller = controller;
		}
	
    public boolean isInShop() {
		return isInShop;
	}
    public boolean isInCombatMap() {
        return isInCombatMap;
    }

	public void setInShop(boolean isInShop) {
		this.isInShop = isInShop;
	}
	
	public HashMap<String, Image> getHostileTileset() {
	    return hostileTileset;
	}


}
