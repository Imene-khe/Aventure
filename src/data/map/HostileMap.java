package data.map;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import data.player.Hero;
import gui.GameDisplay;

public class HostileMap extends Map {

	public HostileMap(int lineCount, int columnCount, int maxChest) {
	    // On passe true → empêche la génération automatique dans Map
	    super(lineCount, columnCount, maxChest, true);

	    this.isStatic = false; // Important : on réactive manuellement le mode non statique

	    // ✅ Appel explicite à la bonne version (celle de HostileMap)
	    generateTerrain();       // => appel à ta version hostile
	    generateObjects();       // => tu peux override si besoin
	    generateEnemies();       // => override dans HostileMap
	}

	@Override
	public void generateTerrain() {
	    // Étape 1 : remplir toute la map avec un sol de base
	    for (int lineIndex = 0; lineIndex < getLineCount(); lineIndex++) {
	        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
	            Block block = getBlock(lineIndex, columnIndex);
	            double rand = Math.random();

	            if (rand < 0.33) {
	                staticTerrain.put(block, "floor1");
	            } else if (rand < 0.66) {
	                staticTerrain.put(block, "floor2");
	            } else {
	                staticTerrain.put(block, "floor3");
	            }
	        }
	    }

	    // Étape 2 : dessiner une rivière de lave sinueuse
	    int x = 0;
	    int y = getLineCount() / 2;
	    Random rand = new Random();

	    for (int i = 0; i < getColumnCount(); i++) {
	        if (x >= 0 && x < getColumnCount() && y >= 0 && y < getLineCount()) {
	            Block lavaBlock = getBlock(y, x);
	            staticTerrain.put(lavaBlock, "lava");

	            // Variante un peu plus large
	            if (y + 1 < getLineCount()) staticTerrain.put(getBlock(y + 1, x), "lava");
	            if (y - 1 >= 0) staticTerrain.put(getBlock(y - 1, x), "lava");
	        }

	        x++;
	        int direction = rand.nextInt(3) - 1; // -1, 0 ou 1
	        y += direction;
	        if (y < 2) y = 2;
	        if (y >= getLineCount() - 2) y = getLineCount() - 3;
	    }

	    // Étape 3 : tracer un pont horizontal (ligne praticable de gauche à droite)
	    int safeRow = getLineCount() / 2;
	    for (int col = 0; col < getColumnCount(); col++) {
	        Block block = getBlock(safeRow, col);
	        if ("lava".equals(staticTerrain.get(block))) {
	            staticTerrain.put(block, "floor1"); // pont au-dessus de la lave
	        }
	    }

	    logger.info("🔥 Terrain hostile généré avec rivière de lave + passage sécurisé !");
	}





    @Override
    public void generateEnemies() {
        ArrayList<Block> freeBlocks = getFreeBlocks();
        Random random = new Random();
        int maxEnemies = 20; // 💀 HostileMap → plus d’ennemis
        int generatedEnemies = 0;

        logger.info("💀 Génération ennemis hostiles...");

        while (generatedEnemies < maxEnemies && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index);

            double rand = Math.random();
            String enemyType = (rand < 0.5) ? "skeleton" : "slime"; // ✅ Plus de demon
            getEnemies().put(block, enemyType);
            if (!enemyType.equals("skeleton") && !enemyType.equals("slime")) {
                logger.warn("⚠ Ennemi inconnu ignoré : " + enemyType);
                continue; // ne l'ajoute pas à la map
            }

            generatedEnemies++;
        }

        logger.info("☠️ " + generatedEnemies + " ennemis hostiles placés.");
    }

    @Override
    public void generateObjects() {
        logger.info("🌑 Génération des objets hostiles (arbres morts, rochers)");

        for (int i = 0; i < getLineCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                Block block = getBlock(i, j);
                String terrainType = getStaticTerrain().get(block);

                if (terrainType != null && terrainType.startsWith("floor")) {
                    double rand = Math.random();

                    // 🌳 Arbres morts
                    if (rand < 0.05) {
                        int type = new Random().nextInt(3) + 1;
                        getStaticObjects().put(block, "deadTree" + type);
                        setTerrainBlocked(block, true);
                    }
                    // 🪨 Rochers (avec terrain de base forcé)
                    else if (rand < 0.10) {
                        getStaticObjects().put(block, "rock");
                        setTerrainBlocked(block, true);

                        // Si jamais le terrain est nul ou erroné, on force un sol par défaut
                        if (terrainType == null || !terrainType.startsWith("floor")) {
                            String fallback = "floor" + (new Random().nextInt(3) + 1);
                            getStaticTerrain().put(block, fallback);
                        }
                    }
                }
            }
        }

        logger.info("✅ Objets hostiles placés (avec terrain sous les rochers)");
    }


    


 // ✅ Méthode main pour tester visuellement la map hostile
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Création d'une map hostile de test
            HostileMap hostileMap = new HostileMap(20, 20, 5);
            hostileMap.generateObjects();
            hostileMap.generateEnemies();

            // Création du GameDisplay
            GameDisplay gameDisplay = new GameDisplay();
            gameDisplay.setMap(hostileMap);

            // Chargement des images (tileset normal + hostile)
            gameDisplay.loadImages(); // ⚠️ seulement si ce n'est pas déjà appelé dans le constructeur

            // Placement du héros au centre de la carte hostile
            gameDisplay.setHero(new Hero(hostileMap.getBlock(10, 10), 100));

            // Préparation de la fenêtre
            JFrame frame = new JFrame("🧪 Test de la HostileMap");
            frame.add(gameDisplay);
            frame.setSize(800, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Focus clavier
            gameDisplay.setFocusable(true);
            gameDisplay.requestFocusInWindow();
        });
    }

}
