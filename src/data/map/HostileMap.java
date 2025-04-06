package data.map;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import data.player.Hero;
import gui.GameDisplay;
import log.LoggerUtility;

public class HostileMap extends Map {
	

	public HostileMap(int lineCount, int columnCount, int maxChest) {
	    // On passe true → empêche la génération automatique dans Map
	    super(lineCount, columnCount, maxChest, true);

	    setStatic(false);
	    // ✅ Appel explicite à la bonne version (celle de HostileMap)
	    generateTerrain();       // => appel à ta version hostile
	    generateObjects();       // => tu peux override si besoin
	    generateEnemies();       // => override dans HostileMap
	}

	@Override
	public void generateTerrain() {
	    // Étape 1 : sol de base
	    for (int line = 0; line < getLineCount(); line++) {
	        for (int col = 0; col < getColumnCount(); col++) {
	            Block block = getBlock(line, col);
	            staticTerrain.put(block, "floor1"); // sol de base simple
	            setTerrainBlocked(block, false);
	        }
	    }

	    // Étape 2 : contour de lave
	    for (int col = 0; col < getColumnCount(); col++) {
	        Block top = getBlock(0, col);
	        Block bottom = getBlock(getLineCount() - 1, col);
	        staticTerrain.put(top, "lava");
	        staticTerrain.put(bottom, "lava");
	        setTerrainBlocked(top, true);
	        setTerrainBlocked(bottom, true);
	    }

	    for (int line = 0; line < getLineCount(); line++) {
	        Block left = getBlock(line, 0);
	        Block right = getBlock(line, getColumnCount() - 1);
	        staticTerrain.put(left, "lava");
	        staticTerrain.put(right, "lava");
	        setTerrainBlocked(left, true);
	        setTerrainBlocked(right, true);
	    }

	    // Étape 3 : petits débordements internes
	    Random rand = new Random();
	    for (int i = 0; i < 30; i++) { // 💧 30 débordements max
	        int line = rand.nextInt(getLineCount());
	        int col = rand.nextInt(getColumnCount());

	        // 💡 On limite les débordements à une bande de 3 cases autour du bord
	        if (line <= 2 || line >= getLineCount() - 3 || col <= 2 || col >= getColumnCount() - 3) {
	            Block block = getBlock(line, col);
	            staticTerrain.put(block, "lava");
	            setTerrainBlocked(block, true);
	        }
	    }

	    // ✅ Le reste de la map est praticable et la lave encadre visuellement la zone jouable
	}


	
 
    @Override
    public void generateEnemies() {
        ArrayList<Block> freeBlocks = getFreeBlocks();
        Random random = new Random();
        int maxEnemies = 20; // 💀 HostileMap → plus d’ennemis
        int generatedEnemies = 0;


        while (generatedEnemies < maxEnemies && !freeBlocks.isEmpty()) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index);

            double rand = Math.random();
            String enemyType = (rand < 0.5) ? "skeleton" : "slime"; // ✅ Plus de demon
            getEnemies().put(block, enemyType);
            if (!enemyType.equals("skeleton") && !enemyType.equals("slime")) {
                continue; // ne l'ajoute pas à la map
            }

            generatedEnemies++;
        }

    }

    @Override
    public void generateObjects() {
        int centerLine = getLineCount() - 6;
        int centerCol = 6;
        int radius = 5;

        Random rng = new Random(42); // ✅ pour reproductibilité

        for (int i = centerLine - radius; i <= centerLine + radius; i++) {
            for (int j = centerCol - radius; j <= centerCol + radius; j++) {
                if (i < 0 || j < 0 || i >= getLineCount() || j >= getColumnCount()) continue;

                Block block = getBlock(i, j);
                String terrainType = getStaticTerrain().get(block);

                if (terrainType != null && terrainType.startsWith("floor")) {

                    // ✅ distance au centre (forme ronde + aléatoire)
                    double dx = j - centerCol;
                    double dy = i - centerLine;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    double noise = rng.nextDouble();

                    double prob = 1.0 - (distance / radius); // ↘ densité décroît vers les bords

                    // 💡 Ajout d’un peu d’aléatoire à la forme pour casser le côté “rond parfait”
                    if (distance <= radius && noise < prob * 0.8) {
                        int type = rng.nextInt(3) + 1;
                        getStaticObjects().put(block, "deadTree" + type);
                        setTerrainBlocked(block, true);
                    }
                }
            }
        }

        // 🪨 Rochers ailleurs (hors zone de forêt)
        for (int i = 0; i < getLineCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                Block block = getBlock(i, j);
                String terrainType = getStaticTerrain().get(block);

                if (terrainType != null && terrainType.startsWith("floor") &&
                    !getStaticObjects().containsKey(block) && Math.random() < 0.03) {

                    getStaticObjects().put(block, "rock");
                    setTerrainBlocked(block, true);
                }
            }
        }
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
