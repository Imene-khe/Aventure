package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Map;
import data.map.Block;

public class TestMap {

    @Test
    public void testMapDimensions() {
        Map map = new Map(20, 30); 

        assertEquals(20, map.getRows()); 
        assertEquals(30, map.getCols()); 
    }

    @Test
    public void testBlocksInitialization() {
        Map map = new Map(10, 15); 

        Block[][] blocks = map.getBlocks();

        assertNotNull(blocks); 
        assertEquals(10, blocks.length); 
        assertEquals(15, blocks[0].length); 

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                assertNotNull(blocks[i][j]); 
            }
        }
    }

    @Test
    public void testStaticTerrainGenerated() {
        Map map = new Map(20, 20, 5, true); 

        assertFalse(map.getStaticTerrain().isEmpty()); 
    }

    @Test
    public void testShopPlacement() {
        Map map = new Map(20, 20, 5, true); 

        Block shopPosition = map.getShopPosition();

        assertNotNull(shopPosition); 
        assertEquals("shop", map.getStaticObjects().get(shopPosition)); 
    }

}
