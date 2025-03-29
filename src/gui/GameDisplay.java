package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.item.Chest;
import data.item.Coin;
import data.map.Block;
import data.map.Map;
import data.player.EnemyImageManager;
import data.player.Hero;
import gui.animation.SpriteAnimator;

/**
 * Classe représentant l'affichage du jeu. Elle gère le rendu graphique de la CARTE, des ennemis, du héros
 * et de la barre de vie. Elle permet également de déplacer le héros et de gérer les interactions avec les objets.
 */
public class GameDisplay extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int GRID_SIZE = 20;  // Réduire la taille à 20x20
    private static final int BLOCK_SIZE = 32; // Taille inchangée
    private static final int SHOP_SIZE = 15; //Taille de la boutique
    private Map map; // Instance de la carte du jeu
    private Map shopMap;
    private Hero hero; // Instance du héros
    private EnemyImageManager enemyImageManager; // Gestionnaire des images des ennemis
    private HashMap<String, Image> tileset; // Dictionnaire des images de terrain et objets
    private boolean canTakeDamage = true; // ✅ Contrôle si le héros peut prendre des dégâts
    private boolean isGameOver = false; // ✅ Empêche l'affichage multiple du message de Game Over
    private boolean isInShop = false; // ✅ Indique si on est dans la boutique
    private SpriteAnimator flameAnimator;
    private SpriteAnimator coinAnimator;




    public boolean isInShop() {
		return isInShop;
	}



	public void setInShop(boolean isInShop) {
		this.isInShop = isInShop;
	}



	/**
     * Constructeur de la classe. Initialise la carte, le héros et les images.
     */
    public GameDisplay() {
        try {
            int numberOfChests = 5; // Ajustable selon besoins
            this.enemyImageManager = new EnemyImageManager();
            this.map = new Map(GRID_SIZE, GRID_SIZE, numberOfChests,false);
            this.shopMap = new Map(SHOP_SIZE, SHOP_SIZE, 0,true);    // Boutique plus petite
            this.shopMap.setupStaticShop(); // ✅ Configuration de la boutique
            this.hero = new Hero(map.getBlock(GRID_SIZE / 2, GRID_SIZE / 2), 100);
            this.tileset = new HashMap<>();
            
            try {
                String[] coinPaths = new String[8];
                for (int i = 0; i < 8; i++) {
                    coinPaths[i] = "src/images/items/coins/coin" + (i + 1) + ".png";
                }
                coinAnimator = new SpriteAnimator(coinPaths, 100); // ⏱️ 100 ms entre les frames
                System.out.println("✅ coinAnimator (8 images) chargé avec succès !");
            } catch (IOException e) {
                System.out.println("❌ Impossible de charger les images d’animation des pièces !");
                e.printStackTrace();
            }

            
            
         // ✅ Thread pour vérifier en continu les collisions avec les ennemis
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(100); // 🔄 Vérification toutes les 100 ms
                        checkEnemyCollision(); // ✅ Vérifie si le héros touche un ennemi
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

            // Chargement des images a supprimer dans un second temps
            loadImages();
            try {
                flameAnimator = new SpriteAnimator("src/images/outdoors/flames.png", 4, 3, 100);
            } catch (IOException e) {
                System.out.println("❌ Impossible de charger l'animation des flammes !");
                e.printStackTrace();
            }

            /*merchantPosition = map.getBlock(10, 10); // Ajuste la position selon ta map
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        showMerchant = !showMerchant;
                        repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/


            System.out.println("✅ GameDisplay créé avec succès !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible d'initialiser GameDisplay !");
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * Vérifie si un coffre est à proximité du héros et l'ouvre si c'est le cas.
     * @return Le coffre ouvert, ou null si aucun coffre n'est proche.
     */
    public Chest openNearbyChest() {
        Block heroPos = hero.getPosition();  // Récupère la position actuelle du héros
        int heroLine = heroPos.getLine();
        int heroColumn = heroPos.getColumn();

        // Vérifier les cases adjacentes autour du héros
        for (int deltaLine = -1; deltaLine <= 1; deltaLine++) {
            for (int deltaColumn = -1; deltaColumn <= 1; deltaColumn++) {
                if (deltaLine == 0 && deltaColumn == 0) continue;  // Ignorer la case du héros lui-même

                int newLine = heroLine + deltaLine;
                int newColumn = heroColumn + deltaColumn;

                // Vérifier que les coordonnées sont valides
                if (newLine >= 0 && newLine < map.getLineCount() && newColumn >= 0 && newColumn < map.getColumnCount()) {
                    Block adjacentBlock = map.getBlock(newLine, newColumn);

                    // Vérifier si un coffre est présent
                    if (map.getStaticObjects().containsKey(adjacentBlock) && map.getStaticObjects().get(adjacentBlock).equals("chest")) {
                        Chest chest = map.getChestManager().getChests().get(adjacentBlock);
                        if (chest != null) {
                            System.out.println("🗃 Coffre trouvé à " + adjacentBlock + ", ouverture en cours...");
                            return chest;
                        }
                    }
                }
            }
        }

        System.out.println("❌ Aucun coffre à proximité !");
        return null;
    }
    
    /**
     * Vérifie si le héros est sur une pièce et la collecte.
     * Met à jour le compteur de pièces dans `MainGUI`.
     */
    public void checkHeroCoinCollision(MainGUI mainGUI) {
        ArrayList<Coin> collectedCoins = new ArrayList<>();

        for (Coin coin : map.getCoins()) {
            if (!coin.isCollected() && coin.getBlock().equals(hero.getPosition())) {
                coin.collect(); // Marquer la pièce comme collectée
                collectedCoins.add(coin);

                // ✅ Augmenter le compteur de pièces dans MainGUI
                mainGUI.incrementCoinCount();
                System.out.println("💰 Pièce ramassée ! Total : " + mainGUI.getCoinCount());
            }
        }

        // ✅ Supprimer les pièces collectées de la carte
        map.getCoins().removeAll(collectedCoins);
    }

    

    public Map getMap() {
		return map;
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


            System.out.println("✅ Toutes les images sont chargées !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger les images !");
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
     * Déplace le héros vers une nouvelle position.
     * Si la position contient un ennemi, le héros perd de la vie.
     * @param newPosition La nouvelle position du héros
     */
	public void moveHero(Block newPosition, MainGUI mainGUI) {
	    if (isGameOver) return; // 🔴 Si le jeu est terminé, empêcher les mouvements

	    System.out.println("➡️ Tentative de déplacement vers : " + newPosition);

	    // ✅ Déterminer sur quelle carte on joue actuellement
	    Map activeMap = isInShop ? shopMap : map;

	    // ✅ Vérifier si le bloc est bloqué avant de déplacer le héros
	    if (activeMap.isBlocked(newPosition)) {
	        System.out.println("🚫 Mouvement impossible, obstacle détecté !");
	        return; // 🔴 Arrêter le déplacement si bloqué
	    }

	    // ✅ Déplacer le héros
	    hero.setPosition(newPosition);
	    System.out.println("✅ Héros déplacé à : " + hero.getPosition());

	    // ✅ Vérifier si on est dans `currentMap` pour ramasser des pièces, ouvrir un coffre et rencontrer des ennemis
	    if (!isInShop) {
	        checkHeroCoinCollision(mainGUI);

	        Chest chest = openNearbyChest();
	        if (chest != null) {
	            ChestUIManager chestUI = new ChestUIManager(mainGUI);
	            chestUI.displayChestContents(chest);
	        }

	        // ✅ Vérifier si le héros touche un ennemi (uniquement en `currentMap`)
	        for (Block enemyPos : map.getEnemies().keySet()) {
	            if (enemyPos.equals(newPosition)) {
	                System.out.println("💀 COLLISION AVEC UN ENNEMI !");
	                hero.takeDamage(10); // ✅ Inflige 10 points de dégâts

	                // ✅ Vérifier si le héros est mort
	                if (hero.getHealth() <= 0) {
	                    System.out.println("☠️ GAME OVER ! Le héros est mort.");
	                    JOptionPane.showMessageDialog(this, "☠️ GAME OVER ! Le héros est mort.");
	                    isGameOver = true; // ✅ Empêcher tout nouveau déplacement
	                    return; // 🔴 Stopper la fonction immédiatement
	                }
	            }
	        }
	    }

	    repaint(); // ✅ Mise à jour de l'affichage
	}








    /**
     * Méthode de rendu graphique. Elle dessine la carte, les ennemis, le héros et la barre de vie.
     * @param g L'objet Graphics utilisé pour dessiner
     */
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ✅ Déterminer quelle carte dessiner (currentMap ou shopMap)
        Map mapToDraw = isInShop ? shopMap : map;

        if (mapToDraw == null || tileset == null || tileset.isEmpty()) {
            System.out.println("❌ Erreur: la map ou le tileset est null ou vide");
            return;
        }

        Block[][] blocks = mapToDraw.getBlocks();

        // 🔹 Dessiner la carte (terrains)
        for (int lineIndex = 0; lineIndex < mapToDraw.getLineCount(); lineIndex++) {
            for (int columnIndex = 0; columnIndex < mapToDraw.getColumnCount(); columnIndex++) {
                Block block = blocks[lineIndex][columnIndex];

                // ✅ Vérifier si on est dans `shopMap` et utiliser `blackWall` pour le contour
                String terrainType;
                terrainType = mapToDraw.getStaticTerrain().getOrDefault(block, isInShop ? "shopFloor" : "grass");


                Image terrainImage = tileset.get(terrainType);

                if (terrainImage != null) {
                    g.drawImage(terrainImage, block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }

                // 🔹 Dessiner les objets statiques (arbres, maisons, coffres, meubles, torches, tables, shop)
                String objectType = mapToDraw.getStaticObjects().get(block);
             // 🔥 Cas spécial : maison en feu (dessinée par-dessus la maison normale)
                if ("house_burning".equals(objectType)) {
                    // 1. Dessiner la maison normale
                    if (tileset.containsKey("house")) {
                        g.drawImage(tileset.get("house"), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                    }

                    // 2. Dessiner les flammes animées par-dessus
                    if (flameAnimator != null) {
                        g.drawImage(flameAnimator.getCurrentFrame(), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                    }

                    continue; // ✅ Ne pas dessiner ce bloc à nouveau dans le bloc générique
                }

                if (objectType != null && tileset.containsKey(objectType)) {
                    g.drawImage(tileset.get(objectType), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }


            }
        }

        // 🔹 Dessiner le bâtiment shop (`shop`) uniquement dans `currentMap`
        if (!isInShop && tileset.containsKey("shop")) {
            for (Block block : map.getStaticObjects().keySet()) {
                if ("shop".equals(map.getStaticObjects().get(block))) {
                    g.drawImage(tileset.get("shop"), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }
            }
        }

        // 🔹 Dessiner le marchand (`merchant`) uniquement dans `shopMap`
        if (isInShop && tileset.containsKey("merchant")) {
            for (Block block : shopMap.getStaticObjects().keySet()) {
                if ("merchant".equals(shopMap.getStaticObjects().get(block))) {
                    g.drawImage(tileset.get("merchant"), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }
            }
        }

        // 🔹 Dessiner les pièces (coins) uniquement si on est dans currentMap
        if (!isInShop) {
            for (Coin coin : map.getCoins()) {
                if (!coin.isCollected()) {
                    Block block = coin.getBlock();
                    int x = block.getColumn() * BLOCK_SIZE;
                    int y = block.getLine() * BLOCK_SIZE;

                    Image frame = coinAnimator.getCurrentFrame();
                    

                    int coinSize = (int) (BLOCK_SIZE * 0.5);
                    int offset = (BLOCK_SIZE - coinSize) / 2;

                    g.drawImage(frame, x + offset, y + offset, coinSize, coinSize, null);
                }
            }
        }

        // 🔥 Dessiner les ennemis uniquement si on est dans currentMap
        if (!isInShop) {
            for (Block block : map.getEnemies().keySet()) {
                String enemyType = map.getEnemies().get(block);
                Image enemyImage = enemyImageManager.getEnemyImage(enemyType, 0);

                if (enemyImage != null) {
                    int x = block.getColumn() * BLOCK_SIZE;
                    int y = block.getLine() * BLOCK_SIZE;
                    g.drawImage(enemyImage, x, y, BLOCK_SIZE, BLOCK_SIZE, null);
                } else {
                    System.out.println("⚠ BUG: Ennemi " + enemyType + " non affiché !");
                }
            }
        }

        // 🔹 Dessiner le héros
        if (hero != null) {
            hero.draw(g, BLOCK_SIZE);
        }

        // 🔹 Dessiner la barre de vie uniquement si on est dans currentMap
        if (!isInShop) {
            drawHealthBar(g);
        }
    }







    
    
    /**
     * ✅ Vérifie si le héros est sur un ennemi et applique un délai avant de reprendre des dégâts.
     */
    public void checkEnemyCollision() {
        if (isGameOver) return; // 🔴 Si le jeu est fini, ne rien faire

        Block heroPosition = hero.getPosition(); // 📌 Position actuelle du héros

        for (Block enemyPos : map.getEnemies().keySet()) {
            if (enemyPos.getLine() == heroPosition.getLine() && enemyPos.getColumn() == heroPosition.getColumn()) {
                if (!canTakeDamage) {
                    return; // 🔴 Empêche de prendre des dégâts si le délai n'est pas écoulé
                }

                System.out.println("💀 COLLISION AVEC UN ENNEMI ! Dégâts infligés !");
                hero.takeDamage(10); // ✅ Inflige 10 points de dégâts
                canTakeDamage = false; // 🔴 Désactive temporairement les dégâts

                // ✅ Réactiver la prise de dégâts après 1 seconde
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // ⏳ Attendre 1 seconde
                        canTakeDamage = true; // ✅ Réautoriser les dégâts après le délai
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                // ✅ Vérifier si le héros est mort
                if (hero.getHealth() <= 0) {
                    isGameOver = true; // 🔴 Empêcher le message de s'afficher plusieurs fois
                    System.out.println("☠️ GAME OVER ! Le héros est mort.");
                    JOptionPane.showMessageDialog(this, "☠️ GAME OVER ! Le héros est mort.");
                    System.exit(0); // ✅ Ferme l'application proprement
                }
            }
        }
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

    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // ✅ Création de la fenêtre
            JFrame frame = new JFrame("Test Affichage ShopMap");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // ✅ Création de GameDisplay (avec shopMap et currentMap)
            GameDisplay gameDisplay = new GameDisplay();

            // ✅ Gestion des touches pour entrer et sortir du magasin
            gameDisplay.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_B && !gameDisplay.isInShop) {
                        gameDisplay.enterShop();
                        System.out.println("🏪 Entrée dans la boutique !");
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameDisplay.isInShop) {
                        gameDisplay.exitShop();
                        System.out.println("🚪 Sortie de la boutique !");
                    }
                }
            });

            gameDisplay.setFocusable(true);
            gameDisplay.requestFocusInWindow();

            frame.add(gameDisplay);
            frame.setSize(400, 400); // Ajuste la taille selon le rendu des blocs
            frame.setLocationRelativeTo(null); // Centre la fenêtre
            frame.setVisible(true);
        });
    }
    
    

    
    
}
