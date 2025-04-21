package data.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;

import data.player.Antagonist;
import data.player.Hero;
import gui.GameDisplay;

public class HostileMap extends Map {

    private final ArrayList<Antagonist> antagonistList = new ArrayList<>();
    private final HashMap<Antagonist, String> antagonistTypes = new HashMap<>();

   

    public HostileMap(int lineCount, int columnCount, int maxChest) {
        super(lineCount, columnCount, maxChest, true);
        setStatic(false);

        staticObjects.clear();
        staticTerrain.clear();
        enemies.clear();
        terrainBlocked.clear();

        generateTerrain();
        generateObjects();
        generateEnemies();
        generateCave();
    }

    @Override
    public void generateTerrain() {
        for (int line = 0; line < getLineCount(); line++) {
            for (int col = 0; col < getColumnCount(); col++) {
                Block block = getBlock(line, col);
                staticTerrain.put(block, "floor1");
                setTerrainBlocked(block, false);
            }
        }

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

        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            int line = rand.nextInt(getLineCount());
            int col = rand.nextInt(getColumnCount());

            if (line <= 2 || line >= getLineCount() - 3 || col <= 2 || col >= getColumnCount() - 3) {
                Block block = getBlock(line, col);
                staticTerrain.put(block, "lava");
                setTerrainBlocked(block, true);
            }
        }
    }

    @Override
    public void generateEnemies() {
        ArrayList<Block> freeBlocks = getFreeBlocks();
        Random random = new Random();
        int maxEnemies = 10;

        antagonistList.clear();
        antagonistTypes.clear(); // important : reset

        for (int i = 0; i < maxEnemies && !freeBlocks.isEmpty(); i++) {
            int index = random.nextInt(freeBlocks.size());
            Block block = freeBlocks.remove(index);

            // Choix aléatoire du type
            String type = Math.random() < 0.5 ? "skeleton" : "slime";

            Antagonist enemy = new Antagonist(block, type, null);
            antagonistList.add(enemy);
            antagonistTypes.put(enemy, type); // 🔗 association
        }
    }


    @Override
    public void generateObjects() {
        Random rng = new Random();
        int numBosquets = 10;
        for (int b = 0; b < numBosquets; b++) {
            int centerLine = rng.nextInt(getLineCount() - 6) + 3;
            int centerCol = rng.nextInt(getColumnCount() - 6) + 3;
            int radius = rng.nextInt(2) + 2;

            for (int i = centerLine - radius; i <= centerLine + radius; i++) {
                for (int j = centerCol - radius; j <= centerCol + radius; j++) {
                    if (i < 0 || j < 0 || i >= getLineCount() || j >= getColumnCount()) continue;

                    Block block = getBlock(i, j);
                    String terrainType = getStaticTerrain().get(block);

                    double dx = j - centerCol;
                    double dy = i - centerLine;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    double prob = 1.0 - (distance / radius);

                    if (terrainType != null && terrainType.startsWith("floor") &&
                        !getStaticObjects().containsKey(block) && rng.nextDouble() < prob * 0.8) {

                        int type = rng.nextInt(3) + 1;
                        getStaticObjects().put(block, "deadTree" + type);
                        setTerrainBlocked(block, true);
                    }
                }
            }
        }

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

    public void generateCave() {
        int baseLine = 14;
        int baseCol = 17;

        Block top = getBlock(baseLine, baseCol + 1);
        Block shadow = getBlock(baseLine + 1, baseCol + 1);

        Block leftTop = getBlock(baseLine, baseCol);
        Block leftBottom = getBlock(baseLine + 1, baseCol);

        Block rightTop = getBlock(baseLine, baseCol + 2);
        Block rightBottom = getBlock(baseLine + 1, baseCol + 2);

        staticObjects.put(leftTop, "cave_left");
        staticObjects.put(top, "cave_top");
        staticObjects.put(rightTop, "cave_right");

        staticObjects.put(leftBottom, "cave_bottom");
        staticObjects.put(shadow, "cave_shadow");
        staticObjects.put(rightBottom, "cave_bottom");

        setTerrainBlocked(leftTop, true);
        setTerrainBlocked(top, true);
        setTerrainBlocked(rightTop, true);
        setTerrainBlocked(leftBottom, true);
        setTerrainBlocked(shadow, true);
        setTerrainBlocked(rightBottom, true);
    }
    
    public ArrayList<Antagonist> getAntagonistList() {
        return antagonistList;
    }
    public HashMap<Antagonist, String> getAntagonistTypes() {
        return antagonistTypes;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            HostileMap hostileMap = new HostileMap(23, 40, 0);
            GameDisplay gameDisplay = new GameDisplay();
            gameDisplay.setMap(hostileMap);
            gameDisplay.loadImages();
            gameDisplay.setHero(new Hero(hostileMap.getBlock(10, 10), 100));

            JFrame frame = new JFrame("🧪 Test de la HostileMap avec Grotte");
            frame.add(gameDisplay);
            frame.setSize(800, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            gameDisplay.setFocusable(true);
            gameDisplay.requestFocusInWindow();
        });
    }
}
