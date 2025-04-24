package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import control.GameController;
import data.map.CombatMap;
import data.map.HostileMap;
import data.map.Map;
import data.player.EnemyImageManager;
import data.player.Hero;
import gui.animation.SpriteAnimator;
import viewstrategy.PaintStrategy;


/**
 * Classe représentant l'affichage du jeu. Elle gère le rendu graphique de la CARTE, des ennemis, du héros
 * et de la barre de vie. Elle permet également de déplacer le héros et de gérer les interactions avec les objets.
 */
public class GameDisplay extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int GRID_SIZE = 35;  
    public static int BLOCK_SIZE = 32; // Taille inchangée
    private static final int SHOP_SIZE = 20; //Taille de la boutique
    private Map map; // Instance de la carte du jeu
    private Map shopMap;
    private Map hostileMap;
    private CombatMap combatMap;
	private Hero hero; // Instance du héros
    private EnemyImageManager enemyImageManager; // Gestionnaire des images des ennemis
    private HashMap<String, Image> tileset; // Dictionnaire des images de terrain et objets
    private boolean isGameOver = false; //  Empêche l'affichage multiple du message de Game Over
    private boolean isInShop = false; //  Indique si on est dans la boutique
    private boolean isInHostileMap = false;
    private boolean isInCombatMap = false;
    private SpriteAnimator flameAnimator;
    private SpriteAnimator coinAnimator;
    private PaintStrategy paintStrategy = new DefaultPaintStrategy();
    private GameController controller;
    private HashMap<String, Image> hostileTileset;
    private HashMap<String, Image> combatTileset; // Dictionnaire des images de terrain et objets


	/**
     * Constructeur de la classe. Initialise la carte, le héros et les images.
     */
	public GameDisplay() {
	    try {
	        int numberOfChests = 5; // Ajustable selon besoins
	        this.enemyImageManager = new EnemyImageManager();
	        this.map = new Map(23, 40, numberOfChests, false);
	        this.shopMap = new Map(SHOP_SIZE, SHOP_SIZE, 0, true);    // Boutique plus petite
	        this.shopMap.setupStaticShop(); // Configuration de la boutique
	        
	        this.hostileMap = new HostileMap(23, 40, 0); // a adapte le temps que l'on trouve la solution pour la map rectangulaire

	        this.hero = new Hero(map.getBlock(GRID_SIZE / 2, GRID_SIZE / 2), 100);
	        this.tileset = new HashMap<>();
	        this.controller = new GameController(this); 
	        this.addMouseListener(new MouseControls());

	        try {
	            String[] coinPaths = new String[8];
	            for (int i = 0; i < 8; i++) {
	                coinPaths[i] = "src/images/items/coins/coin" + (i + 1) + ".png";
	            }
	            coinAnimator = new SpriteAnimator(coinPaths, 100); //  100 ms entre les frames
	            System.out.println("✅ coinAnimator (8 images) chargé avec succès !");
	        } catch (IOException e) {
	            System.out.println("❌ Impossible de charger les images d’animation des pièces !");
	            e.printStackTrace();
	        }
	        new Thread(() -> {
	            while (true) {
	                try {
	                    Thread.sleep(100);
	                    controller.onRepaintTick(); 
	                    controller.checkEnemyCollision();
	                    repaint(); 
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }).start();


	        loadImages(); // Chargement des images
	        try {
	            flameAnimator = new SpriteAnimator("src/images/outdoors/flames.png", 4, 3, 100);
	        } catch (IOException e) {
	            System.out.println("❌ Impossible de charger l'animation des flammes !");
	            e.printStackTrace();
	        }

	        System.out.println("✅ GameDisplay créé avec succès !");
	    } catch (Exception e) {
	        System.out.println("❌ ERREUR : Impossible d'initialiser GameDisplay !");
	        e.printStackTrace();
	    }
	}

   
	public void resetMouseListener() {
	    for (MouseListener ml : this.getMouseListeners()) {
	        this.removeMouseListener(ml);
	    }
	    this.addMouseListener(new MouseControls());
	}
 
    public Map getMap() {
		return map;
	}
    @Override
    public java.awt.Dimension getPreferredSize() {
        return new java.awt.Dimension(750, 740); // Ajustement fixe
    }



    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    //public java.awt.Dimension getPreferredSize() {
     ////   Map currentMap = isInShop ? shopMap : map;
     //   int width = currentMap.getCols() * BLOCK_SIZE;
      //  int height = currentMap.getRows() * BLOCK_SIZE;

      //  return new java.awt.Dimension(width, height);
    //}
    


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
	
	public Map getShopMap() {
        return shopMap;
    }

	/**
     * Charge toutes les images nécessaires pour le rendu du jeu (terrains, objets, ennemis).
     */
	public void loadImages() {
        try {
            System.out.println(" Chargement des images...");

            // Chargement des terrains
            tileset.put("grass", loadImage("src/images/outdoors/Grass_Middle.png"));
            tileset.put("water", loadImage("src/images/outdoors/Water_Middle.png"));
            tileset.put("path", loadImage("src/images/outdoors/Path_Middle.png"));
            
            //Chargement des terrains du shop
            tileset.put("shopFloor", loadImage("src/images/shop/shopfloor.png"));
            tileset.put("lightWall", loadImage("src/images/shop/lightwall.png")); 
            tileset.put("torch", loadImage("src/images/shop/torch.png")); 
            tileset.put("bar", loadImage("src/images/shop/bar.png")); 
            tileset.put("merchant", loadImage("src/images/shop/merchant.png")); 
            tileset.put("carpet", loadImage("src/images/shop/carpet.png")); 
            tileset.put("bookshelf", loadImage("src/images/shop/bookshelf.png")); 

            // Chargement des obstacles
            tileset.put("house", loadImage("src/images/outdoors/House.png"));
            tileset.put("tree", loadImage("src/images/outdoors/Oak_Tree.png"));
            tileset.put("shop", loadImage("src/images/shop/shop.png")); 

            // Chargement des objets
            tileset.put("chest", loadImage("src/images/outdoors/Chest.png"));
            
         // ✅ Chargement des tuiles hostiles
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

            System.out.println(" Toutes les images sont chargées !");
        } catch (Exception e) {
            System.out.println(" ERREUR : Impossible de charger les images !");
            e.printStackTrace();
        }
    }

    /**
     * Charge une image depuis un chemin spécifié.
     * @param path Chemin vers le fichier image
     * @return L'image chargée, ou null si l'image n'existe pas
     */
	public Image loadImage(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("❌ L'image n'a pas été trouvée : " + path);
            return null;
        }
        return ImageIO.read(file);
    }

    /**
     * Méthode de rendu graphique. Elle dessine la carte, les ennemis, le héros et la barre de vie.
     * @param g L'objet Graphics utilisé pour dessiner
     */
    
    
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);

	    Map mapToDraw = (map instanceof CombatMap) ? map : (isInHostileMap ? hostileMap : (isInShop ? shopMap : map));

	    if (mapToDraw == null || (isInHostileMap ? hostileTileset : tileset) == null) {
	        System.out.println("❌ Erreur: map ou tileset null");
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
    /**
     * Dessine la barre de vie du héros.
     * @param g L'objet Graphics utilisé pour dessiner la barre de vie
     */
    public void drawHealthBar(Graphics g) {
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

    /**
     * ✅ Active l'affichage de `shopMap`
     */
    public void enterShop() {
        isInShop = true;
        hero.setPosition(shopMap.getBlock(13, 7)); 
        repaint(); // 🔄 Mise à jour de l'affichage
    }

    /**
     * ✅ Permet au héros de sortir du shop et de retourner sur `currentMap`.
     */
    public void exitShop() {
        returnToMainMap(); 
        
    }
    
    public void returnToMainMap() {
        if (isInShop) {  // ✅ Vérifie qu'on est bien dans la boutique avant de quitter
            isInShop = false; // ✅ Désactive la boutique
            hero.setPosition(map.getBlock(5, 5)); // ✅ Replace le héros sur la carte principale (ajuste la position si nécessaire)

            map.setAllHousesOnFire(); // 🔥 Met le feu à toutes les maisons après la sortie

            repaint(); // ✅ Met à jour l'affichage
            requestFocusInWindow(); // ✅ Récupère le focus pour permettre les déplacements
            System.out.println("🚪 Sortie de la boutique, retour à la carte principale !");
        }
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
	
	public void enterHostileMap() {
	    this.isInHostileMap = true;
	    this.hero.setPosition(hostileMap.getBlock(17, 5));
	    this.repaint();
	    this.setFocusable(true);
	    this.requestFocusInWindow();
	    resetMouseListener();
	    controller.setupHostileQuests();
	    this.setFocusable(true);
	    this.requestFocusInWindow(); // déjà présent ? ajoute aussi :
	    this.requestFocus(); // pour forcer en dernier recours

	    System.out.println("🌋 Passage à la HostileMap !");
	}
	
	public void enterCombatMap() {
	    this.combatMap = new CombatMap(23, 40); 
	    this.map = this.combatMap;
	    this.hero.setPosition(combatMap.getArenaEntryPosition());
	    this.isInHostileMap = false;
	    this.isInShop = false;
	    this.isInCombatMap = true;

	    MainGUI.getInstance().getQuestManager().clearQuests();
	    MainGUI.getInstance().getQuestManager().loadCombatMapQuests();

	    repaint();
	    setFocusable(true);
	    requestFocusInWindow();
	}
	
	public Map getActiveMap() {
	    if (isInCombatMap) return combatMap;
	    if (isInHostileMap) return hostileMap;
	    if (isInShop) return shopMap;
	    return map;
	}






	public Map getHostileMap() {
		return hostileMap;
	}

	public void setHostileMap(Map hostileMap) {
		this.hostileMap = hostileMap;
	}

	public void setHostileMap(HostileMap hostileMap) {
		this.hostileMap = hostileMap;
	}

	public boolean isInHostileMap() {
	    return isInHostileMap;
	}
	
	private class MouseControls extends MouseAdapter {
	    @Override
	    public void mouseClicked(MouseEvent e) {
	        if (SwingUtilities.isLeftMouseButton(e)) {
	            if (controller != null && controller.getCombatController() != null) {
	                System.out.println("🖱️ Clic détecté !");
	                controller.getCombatController().handleClick(e.getPoint());
	            }
	        }
	    }
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
