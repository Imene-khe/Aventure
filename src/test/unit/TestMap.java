package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Map;
import data.map.Block;

public class TestMap {

    @Test
    public void testMapDimensions() {
        Map map = new Map(20, 30); // Crée une carte 20x30

        assertEquals(20, map.getRows()); // Vérifie que le nombre de lignes est correct
        assertEquals(30, map.getCols()); // Vérifie que le nombre de colonnes est correct
    }

    @Test
    public void testBlocksInitialization() {
        Map map = new Map(10, 15); // Crée une carte 10x15

        Block[][] blocks = map.getBlocks();

        assertNotNull(blocks); // Le tableau ne doit pas être nul
        assertEquals(10, blocks.length); // 10 lignes
        assertEquals(15, blocks[0].length); // 15 colonnes

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                assertNotNull(blocks[i][j]); // Chaque bloc doit être instancié
            }
        }
    }

    @Test
    public void testStaticTerrainGenerated() {
        Map map = new Map(20, 20, 5, true); // ⚡ Utilise le bon constructeur

        assertFalse(map.getStaticTerrain().isEmpty()); // ✅ Terrain doit exister
    }

    @Test
    public void testShopPlacement() {
        Map map = new Map(20, 20, 5, true); // ⚡ Utilise le bon constructeur

        Block shopPosition = map.getShopPosition();

        assertNotNull(shopPosition); // ✅ Un shop doit être placé
        assertEquals("shop", map.getStaticObjects().get(shopPosition)); // ✅ C’est bien un "shop"
    }

}
