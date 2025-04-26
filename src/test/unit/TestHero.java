package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Block;
import data.player.Hero;

public class TestHero {

    @Test
    public void testTakeDamageDecreasesHealth() {
        Block startBlock = new Block(0, 0);
        Hero hero = new Hero(startBlock, 100); 

        hero.takeDamage(30); 

        assertEquals(70, hero.getHealth());
    }

    @Test
    public void testTakeDamageNeverNegative() {
        Block startBlock = new Block(0, 0);
        Hero hero = new Hero(startBlock, 50); 

        hero.takeDamage(100); 

        assertEquals(0, hero.getHealth()); 
    }

    @Test
    public void testMultipleTakeDamage() {
        Block startBlock = new Block(1, 1);
        Hero hero = new Hero(startBlock, 100);

        hero.takeDamage(20);
        hero.takeDamage(30);

        assertEquals(50, hero.getHealth());
    }
}
