package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import control.GameController;
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
    private static final int SHOP_SIZE = 40; //Taille de la boutique
    private Map map; // Instance de la carte du jeu
    private Map shopMap;
	private Hero hero; // Instance du héros
    private EnemyImageManager enemyImageManager; // Gestionnaire des images des ennemis
    private HashMap<String, Image> tileset; // Dictionnaire des images de terrain et objets
    private boolean canTakeDamage = true; //  Contrôle si le héros peut prendre des dégâts
    private boolean isGameOver = false; //  Empêche l'affichage multiple du message de Game Over
    private boolean isInShop = false; //  Indique si on est dans la boutique
    private SpriteAnimator flameAnimator;
    private SpriteAnimator coinAnimator;
    private PaintStrategy paintStrategy = new DefaultPaintStrategy();
    private GameController controller;
    private HashMap<String, Image> hostileTileset; //ajout

    

    public boolean isInShop() {
		return isInShop;
	}

	public void setInShop(boolean isInShop) {
		this.isInShop = isInShop;
	}
	
	public HashMap<String, Image> getHostileTileset() {
	    return hostileTileset;
	}




	/**
     * Constructeur de la classe. Initialise la carte, le héros et les images.
     */
	public GameDisplay() {
	    try {
	        int numberOfChests = 5; // Ajustable selon besoins
	        this.enemyImageManager = new EnemyImageManager();
	        this.map = new Map(GRID_SIZE, GRID_SIZE, numberOfChests, false);
	        this.shopMap = new Map(SHOP_SIZE, SHOP_SIZE, 0, true);    // Boutique plus petite
	        this.shopMap.setupStaticShop(); // Configuration de la boutique
	        this.hero = new Hero(map.getBlock(GRID_SIZE / 2, GRID_SIZE / 2), 100);
	        this.tileset = new HashMap<>();

	        this.controller = new GameController(this); // nouveau contrôleur

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

	        //  Thread collision déplacé vers le contrôleur
	        new Thread(() -> {
	            while (true) {
	                try {
	                    Thread.sleep(100); // Vérification toutes les 100 ms
	                    controller.checkEnemyCollision(); // via GameController
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }).start();

	        // Thread pour rafraîchir l'affichage des animations (ex: pièces en rotation)
	        new Thread(() -> {
	            while (true) {
	                try {
	                    Thread.sleep(100);
	                    repaint(); // Force le redessin de la fenêtre
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
            hostileTileset.put("deadTree1", loadImage("src/images/outdoor/hostile/deadTree1.png"));
            hostileTileset.put("deadTree2", loadImage("src/images/outdoor/hostile/deadTree2.png"));
            hostileTileset.put("deadTree3", loadImage("src/images/outdoor/hostile/deadTree3.png"));



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

	    Map mapToDraw = isInShop ? shopMap : map;

	    if (mapToDraw == null || tileset == null || tileset.isEmpty()) {
	        System.out.println("❌ Erreur: la map ou le tileset est null ou vide");
	        return;
	    }

	    // ✅ 1. Fond de carte (grass, water, etc.)
	    paintStrategy.paintTerrain(mapToDraw, g, this);

	    // ✅ 2. Pièces (sous les objets statiques)
	    if (!isInShop) {
	        paintStrategy.paintCoins(map, g, this);
	    }

	    // ✅ 3. Objets statiques (arbres, coffres, meubles...)
	    paintStrategy.paintStaticObjects(mapToDraw, g, this);

	    // ✅ 4. Cas particuliers : maisons en feu, bâtiment shop, marchand
	    if (!isInShop) {
	        paintStrategy.paintBurningHouse(map, g, this);
	        paintStrategy.paintShopBuilding(map, g, this);
	    } else {
	        paintStrategy.paintMerchant(shopMap, g, this);
	    }

	    // ✅ 5. Ennemis + barre de vie (map principale uniquement)
	    if (!isInShop) {
	        paintStrategy.paintEnemies(map, g, this);
	        paintStrategy.paintHealthBar(hero, g, this);
	    }

	    // ✅ 6. Héros (au-dessus de tout)
	    paintStrategy.paintHero(hero, g, this);
	    
	    
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
        hero.setPosition(shopMap.getBlock(13, 7)); // ✅ Placer le héros dans l’entrée centrale du shop
        repaint(); // 🔄 Mise à jour de l'affichage
    }


    
    /**
     * ✅ Permet au héros de sortir du shop et de retourner sur `currentMap`.
     */
    public void exitShop() {
        returnToMainMap(); // ✅ Appelle returnToMainMap() une seule fois sans boucle infinie
        
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
	    this.map = new data.map.HostileMap(35, 35, 0);
	    this.hero.setPosition(map.getBlock(17, 5));
	    this.repaint();
	    this.setFocusable(true); // 🟢 Important
	    this.requestFocusInWindow(); // 🟢 Focus ici et pas ailleurs
	    System.out.println("🌋 Passage à la HostileMap !");
	}




    
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        JFrame frame = new JFrame("Aventure - Vue complète");

	        GameDisplay gameDisplay = new GameDisplay();

	        gameDisplay.setFocusable(true);
	        gameDisplay.requestFocusInWindow();

	        frame.add(gameDisplay); // Pas de JScrollPane
	        frame.setSize(gameDisplay.getPreferredSize()); // ⬅️ taille exacte selon la carte
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	    });
	}


}
