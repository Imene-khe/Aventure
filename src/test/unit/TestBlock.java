package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Block;

public class TestBlock {

    @Test
    public void testBlockCreation() {
        Block block = new Block(3, 5);

        assertEquals(3, block.getLine());
        assertEquals(5, block.getColumn());
    }

    @Test
    public void testBlockEquality() {
        Block block1 = new Block(2, 4);
        Block block2 = new Block(2, 4);

        assertTrue(block1.equals(block2));
    }
}
