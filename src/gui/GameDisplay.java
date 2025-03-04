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

import data.item.ChestManager;
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
    private ChestManager chestManager;		//Gestionnaire de contenu des coffres

    /**
     * Constructeur de la classe. Initialise la carte, le héros, les ennemis et les images.
     */
    public GameDisplay() {
        try {
            // Initialisation de la carte et du héros
            this.map = new Map(GRID_SIZE, GRID_SIZE);
            this.hero = new Hero(map.getBlock(GRID_SIZE / 2, GRID_SIZE / 2), 100);
            this.enemies = new ArrayList<>();
            this.enemyImageManager = new EnemyImageManager();
            this.tileset = new HashMap<>();
            this.chestManager = new ChestManager();

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
            tileset.put("chest", loadImage("src/images/outdoors/Chest.png"));
            
            // Chargement des ennemis
            tileset.put("skeleton", loadImage("src/images/Enemies/Skeleton.png"));
            tileset.put("slime", loadImage("src/images/Enemies/Slime.png"));
            tileset.put("slime_green", loadImage("src/images/Enemies/Slime_Green.png"));
            
            //Chargement des ressources
            tileset.put("coin", loadImage("src/images/items/coin.png"));

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

        for (int i = 0; i < numberOfEnemies && !freeBlocks.isEmpty(); i++) {
            int index = random.nextInt(freeBlocks.size());  // Choisit un bloc libre aléatoire
            Block spawnBlock = freeBlocks.remove(index);  // Retire ce bloc pour éviter un double spawn

            // Sélection aléatoire d'un type d'ennemi
            String enemyType = enemyTypes[random.nextInt(enemyTypes.length)];

            // Crée un ennemi et l'ajoute à la carte
            Antagonist enemy = new Antagonist(spawnBlock, enemyType, enemyImageManager);
            enemies.add(enemy);
            map.getEnemies().put(spawnBlock, enemyType);  // Ajoute l'ennemi dans la carte
        }
    }
    
    
    public ArrayList<String> openChest(String chestKey) {
        return chestManager.openChest(chestKey);  // Appelle la méthode dans ChestManager pour ouvrir un coffre
    }

    /**
     * Affiche le contenu du coffre dans la console (exemple d'affichage).
     * Vous pouvez l'adapter pour afficher dans l'interface graphique.
     */
    public void displayChestContent(String chestKey) {
        ArrayList<String> items = openChest(chestKey);
        if (!items.isEmpty()) {
            System.out.println("Contenu du coffre (" + chestKey + "):");
            for (String item : items) {
                System.out.println("- " + item);
            }
        } else {
            System.out.println("Ce coffre est vide ou introuvable.");
        }
    }
 
    
    /**
     * Fait apparaître un certain nombre de pièces (de type "coin") sur la carte, dans des blocs libres.
     * 
     * Cette méthode sélectionne aléatoirement des blocs libres sur la carte pour y faire apparaître les pièces. 
     * Les pièces sont placées dans les blocs libres disponibles.
     * Une fois une pièce générée, elle est ajoutée à la liste des pièces du jeu et la carte enregistre la pièce 
     * dans un mappage entre chaque bloc et son type de pièce.
     * 
     * @param numberOfCoins Le nombre de pièces à générer sur la carte.
     *                      La méthode s'arrêtera une fois ce nombre atteint ou si les blocs libres sont épuisés.
     */
    /*private void spawnCoins(int numberOfCoins) {
    	ArrayList<Block> freeBlocks = map.getFreeBlocks();  // Récupère la liste des blocs disponibles
        Random random = new Random();

        for (int i = 0; i < numberOfCoins && !freeBlocks.isEmpty(); i++) {
            int index = random.nextInt(freeBlocks.size());  // Choisit un bloc libre aléatoire
            Block spawnBlock = freeBlocks.remove(index);  // Retire ce bloc pour éviter un double spawn

            // Ajoute la pièce à la carte en utilisant un objet générique (par exemple, String ou Integer)
            map.getCoins().put(spawnBlock, "coin");  // "coin" représente ici une pièce
        }
    }*/


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
    public void moveHero(Block newPosition) {
        if (map.getEnemies().containsKey(newPosition)) {
            hero.takeDamage(10);  // Le héros perd 10% de vie s'il touche un ennemi
        }

        hero.setPosition(newPosition);
        repaint();
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

                // Dessiner les objets (maison, arbre, etc.) sur le terrain
                String objectType = map.getStaticObjects().get(block);
                if (objectType != null && tileset.containsKey(objectType)) {
                    g.drawImage(tileset.get(objectType), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }
            }
        }

     // Dessiner les coffres
        HashMap<Block, String> chests = chestManager.getChests();
        for (Block chestBlock : chests.keySet()) {
            // Vérifiez que le bloc contient un coffre
            if (tileset.containsKey("chest")) {
                // Dessinez l'image du coffre à la position du bloc
                Image chestImage = tileset.get("chest");
                if (chestImage != null) {
                    g.drawImage(chestImage, chestBlock.getColumn() * BLOCK_SIZE, chestBlock.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                } else {
                    System.out.println("❌ L'image du coffre n'est pas chargée correctement.");
                }
            }
        }



        // Dessiner les ennemis
        for (Antagonist enemy : enemies) {
            enemy.draw(g, BLOCK_SIZE);  // Utilise la méthode draw de chaque ennemi pour afficher leur animation
        }

        // Dessiner le héros
        if (hero != null) {
            hero.draw(g, BLOCK_SIZE);
        }

        // Dessiner la barre de vie
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
    
    public boolean isHeroNearChest() {
        if (hero == null || map == null || chestManager == null) {
            System.out.println("Erreur : hero, map ou chestManager est null !");
            return false;
        }

        Block heroBlock = hero.getPosition(); 
        if (heroBlock == null) {
            System.out.println("Erreur : heroBlock est null !");
            return false;
        }

        HashMap<Block, String> chests = chestManager.getChests();
        if (chests == null) {
            System.out.println("Erreur : chests est null !");
            return false;
        }

        // Vérifie si un coffre est dans les 4 directions autour du héros
        return chests.containsKey(map.getBlock(heroBlock.getLine() - 1, heroBlock.getColumn())) ||
               chests.containsKey(map.getBlock(heroBlock.getLine() + 1, heroBlock.getColumn())) ||
               chests.containsKey(map.getBlock(heroBlock.getLine(), heroBlock.getColumn() - 1)) ||
               chests.containsKey(map.getBlock(heroBlock.getLine(), heroBlock.getColumn() + 1));
    }



    public void openNearbyChest() {
        Block heroPosition = hero.getPosition(); // Position actuelle du héros
        int heroLine = heroPosition.getLine();
        int heroColumn = heroPosition.getColumn();

        // Vérification des blocs voisins
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Block neighborBlock = map.getBlock(heroLine + i, heroColumn + j);
                System.out.println("Vérification du bloc voisin : " + neighborBlock); // Affiche chaque voisin

                // Vérifier si un coffre se trouve à la position voisine
                if (chestManager.getChests().containsKey(neighborBlock)) {
                    String chestKey = chestManager.getChests().get(neighborBlock);
                    System.out.println("Coffre détecté à : " + neighborBlock); // Affiche la détection d'un coffre
                    chestManager.openChest(chestKey); // Ouvrir le coffre avec la clé correspondante
                    return; // Sortir une fois qu'on a ouvert un coffre
                }
            }
        }

        System.out.println("❌ Aucun coffre à proximité !");
    }

    /**
     * Méthode main pour tester l'affichage du jeu.
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        // Crée une instance de GameDisplay
        GameDisplay gameDisplay = new GameDisplay();

        // Crée une fenêtre pour afficher le jeu
        JFrame frame = new JFrame("Jeu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GRID_SIZE * BLOCK_SIZE, GRID_SIZE * BLOCK_SIZE); // Taille de la fenêtre selon la carte
        frame.add(gameDisplay);  // Ajoute le GameDisplay à la fenêtre
        frame.setVisible(true);  // Affiche la fenêtre

        // Test : Déplace le héros de manière aléatoire
        Random random = new Random();
        Block currentBlock = gameDisplay.getHero().getPosition();
        int newX = (currentBlock.getColumn() + random.nextInt(3) - 1 + GRID_SIZE) % GRID_SIZE;
        int newY = (currentBlock.getLine() + random.nextInt(3) - 1 + GRID_SIZE) % GRID_SIZE;
        Block newBlock = gameDisplay.getMap().getBlock(newX, newY);
        gameDisplay.moveHero(newBlock);

        // Affiche le contenu d'un coffre (exemple)
        gameDisplay.displayChestContent("chest1");

        // Test : Affiche un message pour vérifier que tout est bien initialisé
        System.out.println("✅ Le jeu est prêt !");
    }

}
