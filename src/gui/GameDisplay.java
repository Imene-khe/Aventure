package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.item.Chest;
import data.item.Coin;
import data.item.Equipment;
import data.item.Inventory;
import data.map.Block;
import data.map.Map;
import data.player.Antagonist;
import data.player.EnemyImageManager;
import data.player.Hero;

/**
 * Classe représentant l'affichage du jeu. Elle gère le rendu graphique de la CARTE, des ennemis, du héros
 * et de la barre de vie. Elle permet également de déplacer le héros et de générer des ennemis aléatoirement sur la carte.
 */
public class GameDisplay extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int GRID_SIZE = 30;		 // Taille de la grille (30x30 blocs)
    private static final int BLOCK_SIZE = 32;		    // Taille d'un bloc en pixels (32x32)
    private Map map;		    // Instance de la carte du jeu
    private Hero hero;		    // Instance du héros
    private ArrayList<Antagonist> enemies;		    // Liste des ennemis présents sur la carte
    private EnemyImageManager enemyImageManager;		    // Gestionnaire des images des ennemis
    private HashMap<String, Image> tileset;		    // Dictionnaire des images de terrain et objets

    /**
     * Constructeur de la classe. Initialise la carte, le héros, les ennemis et les images.
     */
    public GameDisplay() {
        try {
            // Initialisation de la carte et du héros
            int numberOfChests = 5; //A ajuster selon besoins
            this.map = new Map(GRID_SIZE, GRID_SIZE, numberOfChests);
            this.hero = new Hero(map.getBlock(GRID_SIZE / 2, GRID_SIZE / 2), 100);
            this.enemies = new ArrayList<>();
            this.enemyImageManager = new EnemyImageManager();
            this.tileset = new HashMap<>();
            
         // Thread pour rafraîchir l'affichage et voir l'animation des pièces
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(100); // Rafraîchir toutes les 100 ms
                        repaint(); // Force le redessin de la fenêtre
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        

            // Chargement des images
            loadImages();

            // Génération des ennemis
            spawnEnemies(30);  // Génère 30 ennemis en évitant les obstacles
            
            
            System.out.println("✅ GameDisplay créé avec succès avec des ennemis aléatoires !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible d'initialiser GameDisplay !");
            e.printStackTrace();
        }
    }

    /**
     * Charge toutes les images nécessaires pour le rendu du jeu (terrains, objets, ennemis).
     */
    private void loadImages() {
        try {
            System.out.println(" Chargement des images...");
            
            // Chargement des terrains
            tileset.put("grass", loadImage("src/images/outdoors/Grass_Middle.png"));
            tileset.put("water", loadImage("src/images/outdoors/Water_Middle.png"));
            tileset.put("path", loadImage("src/images/outdoors/Path_Middle.png"));

            // Chargement des obstacles
            tileset.put("house", loadImage("src/images/outdoors/House.png"));
            tileset.put("tree", loadImage("src/images/outdoors/Oak_Tree.png"));
            
            // Chargement des ennemis (erreur a traiter car les images des heros sont charger indépendament du tileset dans EnemyImageManager)
            //tileset.put("skeleton", loadImage("src/images/Enemies/Skeleton.png"));
            //tileset.put("slime", loadImage("src/images/Enemies/Slime.png"));
            //tileset.put("slime_green", loadImage("src/images/Enemies/slime_green2.png"));
            
         // Chargement des objets (coffre, etc.)
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
     * @throws IOException Si une erreur survient lors du chargement de l'image
     */
    private Image loadImage(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("❌ L'image n'a pas été trouvée : " + path);
            return null;  // Retourne null si l'image n'est pas trouvée
        }
        return ImageIO.read(file);
    }
    
    /**
     * Génère des ennemis aléatoirement sur des blocs libres de la carte.
     * @param numberOfEnemies Nombre d'ennemis à générer
     */
    private void spawnEnemies(int numberOfEnemies) {
        ArrayList<Block> freeBlocks = map.getFreeBlocks();  // Récupère la liste des blocs disponibles
        Random random = new Random();

        // Types d'ennemis possibles
        String[] enemyTypes = {"skeleton", "slime", "slime_green"};  

        int spawnedEnemies = 0;
        while (spawnedEnemies < numberOfEnemies && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());  // Choisit un bloc libre aléatoire
            Block spawnBlock = freeBlocks.get(index);  // Récupère le bloc sélectionné

            // Vérifier que le bloc n'est PAS de l'eau avant de placer un ennemi
            if (!map.getStaticTerrain().getOrDefault(spawnBlock, "").equals("water")) {
                // Sélection aléatoire d'un type d'ennemi
                String enemyType = enemyTypes[random.nextInt(enemyTypes.length)];

                // Crée un ennemi et l'ajoute à la carte
                Antagonist enemy = new Antagonist(spawnBlock, enemyType, enemyImageManager);
                enemies.add(enemy);
                map.getEnemies().put(spawnBlock, enemyType);  // Ajoute l'ennemi dans la carte
                
                freeBlocks.remove(index);  // Retirer ce bloc de la liste des blocs libres
                spawnedEnemies++;  // Incrémenter le compteur
            }
        }
    }




    /**
     * Définit une nouvelle carte et redessine l'affichage.
     * @param map La nouvelle carte à afficher
     */
    public void setMap(Map map) {
        this.map = map;
        repaint();
    }

    /**
     * Retourne l'instance actuelle de la carte.
     * @return La carte actuelle
     */
    public Map getMap() {
        return map;
    }

    /**
     * Retourne l'instance actuelle du héros.
     * @return Le héros actuel
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Déplace le héros vers une nouvelle position.
     * Si la position contient un ennemi, le héros perd de la vie.
     * @param newPosition La nouvelle position du héros
     */
    public void moveHero(Block newPosition, MainGUI mainGUI) {
        if (map.getEnemies().containsKey(newPosition)) {
            hero.takeDamage(10);  // Le héros perd de la vie en touchant un ennemi
        }

        hero.setPosition(newPosition);
        checkHeroCoinCollision(mainGUI); // ✅ Vérifier si une pièce est ramassée
        repaint();
    }



    public void checkHeroCoinCollision(MainGUI mainGUI) {
        ArrayList<Coin> collectedCoins = new ArrayList<>();

        for (Coin coin : map.getCoins()) {
            if (!coin.isCollected() && coin.getBlock().equals(hero.getPosition())) {
                coin.collect();
                collectedCoins.add(coin);

                // ✅ Augmenter le compteur de pièces dans MainGUI
                mainGUI.incrementCoinCount();
                
                System.out.println("💰 Pièce ramassée ! Total : " + mainGUI.getCoinCount());
            }
        }

        // Supprimer les pièces collectées de la carte
        map.getCoins().removeAll(collectedCoins);
    }



	/**
     * Méthode de rendu graphique. Elle dessine la carte, les ennemis, le héros et la barre de vie.
     * @param g L'objet Graphics utilisé pour dessiner
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (map == null || tileset == null || tileset.isEmpty()) {
            System.out.println("Erreur: la map ou le tileset est null ou vide");
            return;
        }

        Block[][] blocks = map.getBlocks();

        // Dessiner la carte
        for (int lineIndex = 0; lineIndex < map.getLineCount(); lineIndex++) {
            for (int columnIndex = 0; columnIndex < map.getColumnCount(); columnIndex++) {
                Block block = blocks[lineIndex][columnIndex];
                String terrainType = map.getStaticTerrain().getOrDefault(block, "grass");
                Image terrainImage = tileset.get(terrainType);

                if (terrainImage != null) {
                    g.drawImage(terrainImage, block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }

                // Dessiner les objets (maison, arbre, etc.)
                String objectType = map.getStaticObjects().get(block);
                if (objectType != null && tileset.containsKey(objectType)) {
                    g.drawImage(tileset.get(objectType), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }
            }
        }

        // 🔥 Dessiner les pièces en rotation
        for (Coin coin : map.getCoins()) {
            coin.draw(g, BLOCK_SIZE);
        }

        // Dessiner les ennemis
        for (Antagonist enemy : enemies) {
            enemy.draw(g, BLOCK_SIZE);
        }

        // Dessiner le héros
        if (hero != null) {
            hero.draw(g, BLOCK_SIZE);
        }
        drawHealthBar(g);
    }


    /**
     * Dessine la barre de vie du héros.
     * @param g L'objet Graphics utilisé pour dessiner la barre de vie
     */
    private void drawHealthBar(Graphics g) {
        int maxHealth = 100;
        int currentHealth = hero.getHealth();  // Utilise la vraie vie du héros

        g.setColor(java.awt.Color.RED);
        g.fillRect(10, 10, 200, 20); // 🔴 Fond rouge
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(10, 10, (currentHealth * 200) / maxHealth, 20); //  Barre verte dynamique
        g.setColor(java.awt.Color.BLACK);
        g.drawRect(10, 10, 200, 20); // Contour
        g.drawString("Vie : " + currentHealth + "%", 90, 25);
    }
    
    public Block getNearbyChestPosition() {
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
                    
                    // Accéder à staticObjects via la classe Map
                    if (map.getStaticObjects().containsKey(adjacentBlock) && map.getStaticObjects().get(adjacentBlock).equals("chest")) {
                        return adjacentBlock;
                    }
                }
            }
        }

        // Aucun coffre à proximité
        return null;
    }
    
 // Méthode pour ouvrir un coffre à proximité
    public Chest openNearbyChest() {
        Block chestPos = getNearbyChestPosition(); // Vérifie s'il y a un coffre proche
        if (chestPos != null) {
            Chest chest = map.getChestManager().getChests().get(chestPos); // Récupère le coffre
            map.getChestManager().openChest(chestPos);
            return chest; // ✅ Retourne juste le coffre, SANS ajouter les objets à l'inventaire
        }
        return null;
    }






    


    
    




    /**
     * Méthode main pour tester l'affichage du jeu.
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        GameDisplay gameDisplay = new GameDisplay();

        // Créer et configurer le JFrame
        JFrame frame = new JFrame("Test Game Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gameDisplay);
        frame.setSize(800, 800);
        frame.setVisible(true);

        // Appeler repaint pour assurer l'affichage
        gameDisplay.repaint();

        // Tester les coffres et vérifier leur contenu
        System.out.println("Vérification du contenu des coffres :");
        for (Block chestBlock : gameDisplay.getMap().getChestManager().getChests().keySet()) {
            Chest chest = gameDisplay.getMap().getChestManager().getChests().get(chestBlock);
            if (chest != null && !chest.getInventory().isFull()) {
                System.out.println("Coffre à " + chestBlock + " contient : " + chest.getInventory());
            } else {
                System.out.println("Coffre à " + chestBlock + " est vide.");
            }
        }

        // Déplacer le héros et tester l'affichage
        Block newPosition = gameDisplay.getMap().getBlock(5, 5);
        //gameDisplay.moveHero(newPosition);
    }

}
