package data.map;

import java.util.Random;

public class CombatMap extends Map {

    public CombatMap(int lineCount, int columnCount) {
        super(lineCount, columnCount, 0, true);
        setStatic(false);

        staticTerrain.clear();
        staticObjects.clear();
        enemies.clear();
        terrainBlocked.clear();

        generateTerrain();
    }

    
    @Override
    public void generateTerrain() {
        int centerStartLine = (getLineCount() - 15) / 2;
        int centerStartCol  = (getColumnCount() - 15) / 2;
        int bridgeCol = centerStartCol + 7;

        // Étape 1 : tout initialiser à black
        for (int line = 0; line < getLineCount(); line++) {
            for (int col = 0; col < getColumnCount(); col++) {
                staticTerrain.put(getBlock(line, col), "black");
            }
        }

        // Étape 2 : remplir la zone centrale avec du floorCave
        for (int line = centerStartLine; line < centerStartLine + 15; line++) {
            for (int col = centerStartCol; col < centerStartCol + 15; col++) {
                staticTerrain.put(getBlock(line, col), "floorCave");
            }
        }

     // Appliquer les bordures à l'intérieur de la zone centrale
        for (int i = 0; i < 15; i++) {
            // Haut (ligne 0 de la zone)
            Block top = getBlock(centerStartLine, centerStartCol + i);
            staticTerrain.put(top, "horizontalBorder");

            // Bas (ligne 14 de la zone) sauf l’ouverture du pont
            if (i != 7) { // on laisse un passage au centre
                Block bottom = getBlock(centerStartLine + 14, centerStartCol + i);
                staticTerrain.put(bottom, "horizontalBorder");
            }

            // Gauche (colonne 0 de la zone)
            Block left = getBlock(centerStartLine + i, centerStartCol);
            staticTerrain.put(left, "verticalBorder");

            // Droite (colonne 14 de la zone)
            Block right = getBlock(centerStartLine + i, centerStartCol + 14);
            staticTerrain.put(right, "verticalBorder");
        }


        // Étape 4 : pont vertical descendant
        for (int line = centerStartLine + 15; line < getLineCount(); line++) {
            staticTerrain.put(getBlock(line, bridgeCol), "bridge");
        }

        generatePlatforms();
    }







    public void generatePlatforms() {
        Random rand = new Random();
        int islands = 10 + rand.nextInt(3);

        int attempts = 0;

        // ↪️ Recalculer les coordonnées centrales localement
        int centerStartLine = (getLineCount() - 15) / 2;
        int centerStartCol  = (getColumnCount() - 15) / 2;
        int bridgeCol = centerStartCol + 7;

        while (islands > 0 && attempts < 200) {
            int i = rand.nextInt(getLineCount());
            int j = rand.nextInt(getColumnCount());
            Block block = getBlock(i, j);

            if (!"black".equals(staticTerrain.get(block))) {
                attempts++;
                continue;
            }

            // Évite les bords de l’arène
            if (i >= centerStartLine - 1 && i <= centerStartLine + 15 &&
                j >= centerStartCol - 1 && j <= centerStartCol + 15) {
                attempts++;
                continue;
            }

            // Évite le pont et ses bords
            if (Math.abs(j - bridgeCol) <= 1 && i >= centerStartLine + 14) {
                attempts++;
                continue;
            }

            staticTerrain.put(block, "platformCave");
            setTerrainBlocked(block, false);
            islands--;
            attempts++;
        }
    }

    @Override
    public void generateObjects() {
        // non utilisé pour l’instant
    }

    @Override
    public void generateEnemies() {
        // à faire plus tard
    }
}
