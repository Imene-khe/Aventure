package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.item.Chest;
import data.item.Coin;
import data.map.Block;
import data.map.Map;
import data.player.EnemyImageManager;
import data.player.Hero;

/**
 * Classe représentant l'affichage du jeu. Elle gère le rendu graphique de la CARTE, des ennemis, du héros
 * et de la barre de vie. Elle permet également de déplacer le héros et de gérer les interactions avec les objets.
 */
public class GameDisplay extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int GRID_SIZE = 20;  // Réduire la taille à 20x20
    private static final int BLOCK_SIZE = 32; // Taille inchangée
    private Map map; // Instance de la carte du jeu
    private Hero hero; // Instance du héros
    private EnemyImageManager enemyImageManager; // Gestionnaire des images des ennemis
    private HashMap<String, Image> tileset; // Dictionnaire des images de terrain et objets
    private boolean canTakeDamage = true; // ✅ Contrôle si le héros peut prendre des dégâts
    private boolean isGameOver = false; // ✅ Empêche l'affichage multiple du message de Game Over


    /**
     * Constructeur de la classe. Initialise la carte, le héros et les images.
     */
    public GameDisplay() {
        try {
            int numberOfChests = 5; // Ajustable selon besoins
            this.enemyImageManager = new EnemyImageManager();
            this.map = new Map(GRID_SIZE, GRID_SIZE, numberOfChests);
            this.hero = new Hero(map.getBlock(GRID_SIZE / 2, GRID_SIZE / 2), 100);
            this.tileset = new HashMap<>();
            
            
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

            // Chargement des images
            loadImages();

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

            // Chargement des obstacles
            tileset.put("house", loadImage("src/images/outdoors/House.png"));
            tileset.put("tree", loadImage("src/images/outdoors/Oak_Tree.png"));

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

        // ✅ Vérifier si le bloc contient un ennemi AVANT de déplacer le héros
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

        // ✅ Déplacer le héros si aucun obstacle n'est présent
        hero.setPosition(newPosition);
        System.out.println("✅ Héros déplacé à : " + hero.getPosition());

        // ✅ Vérifier si une pièce est ramassée
        checkHeroCoinCollision(mainGUI);

        // ✅ Vérifier si un coffre est proche et l’ouvrir
        Chest chest = openNearbyChest();
        if (chest != null) {
            ChestUIManager chestUI = new ChestUIManager(mainGUI);
            chestUI.displayChestContents(chest);
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

        if (map == null || tileset == null || tileset.isEmpty()) {
            System.out.println("❌ Erreur: la map ou le tileset est null ou vide");
            return;
        }

        Block[][] blocks = map.getBlocks();

        // 🔹 Dessiner la carte
        for (int lineIndex = 0; lineIndex < map.getLineCount(); lineIndex++) {
            for (int columnIndex = 0; columnIndex < map.getColumnCount(); columnIndex++) {
                Block block = blocks[lineIndex][columnIndex];
                String terrainType = map.getStaticTerrain().getOrDefault(block, "grass");
                Image terrainImage = tileset.get(terrainType);

                if (terrainImage != null) {
                    g.drawImage(terrainImage, block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }

                // 🔹 Dessiner les objets statiques (arbres, maisons, coffres)
                String objectType = map.getStaticObjects().get(block);
                if (objectType != null && tileset.containsKey(objectType)) {
                    g.drawImage(tileset.get(objectType), block.getColumn() * BLOCK_SIZE, block.getLine() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
                }
            }
        }

        // 🔹 Dessiner les pièces (coins)
        for (Coin coin : map.getCoins()) {
            coin.draw(g, BLOCK_SIZE);
        }

        // 🔥 Dessiner les ennemis basés sur la position logique définie dans `Map`
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

        // 🔹 Dessiner le héros
        if (hero != null) {
            hero.draw(g, BLOCK_SIZE);
        }

        // 🔹 Dessiner la barre de vie
        drawHealthBar(g);
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
}
