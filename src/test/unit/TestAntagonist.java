package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Block;
import data.player.Antagonist;
import data.player.EnemyImageManager;

public class TestAntagonist {

    @Test
    public void testAntagonistCreation() {
        Block startBlock = new Block(5, 5);
        EnemyImageManager imageManager = new EnemyImageManager();
        Antagonist enemy = new Antagonist(startBlock, "slime", imageManager);

        assertNotNull(enemy);
        assertEquals(startBlock, enemy.getPosition());
    }

    @Test
    public void testAntagonistDeath() {
        Block startBlock = new Block(5, 5);
        EnemyImageManager imageManager = new EnemyImageManager();
        Antagonist enemy = new Antagonist(startBlock, "slime", imageManager);

        assertFalse(enemy.isDead());

        enemy.takeDamage(1000); 
        assertTrue(enemy.isDead());
    }
}
