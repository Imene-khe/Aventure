package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Block;
import data.player.Hero;

public class TestHero {

    @Test
    public void testTakeDamageDecreasesHealth() {
        Block startBlock = new Block(0, 0); // Création d'un bloc de départ
        Hero hero = new Hero(startBlock, 100); // Héros avec 100 PV

        hero.takeDamage(30); // Le héros prend 30 dégâts

        assertEquals(70, hero.getHealth()); // 100 - 30 = 70
    }

    @Test
    public void testTakeDamageNeverNegative() {
        Block startBlock = new Block(0, 0);
        Hero hero = new Hero(startBlock, 50); // Héros avec 50 PV

        hero.takeDamage(100); // Inflige plus de dégâts que les PV

        assertEquals(0, hero.getHealth()); // La vie ne doit jamais passer en dessous de 0
    }

    @Test
    public void testMultipleTakeDamage() {
        Block startBlock = new Block(1, 1);
        Hero hero = new Hero(startBlock, 100);

        hero.takeDamage(20);
        hero.takeDamage(30);

        assertEquals(50, hero.getHealth()); // 100 - 20 - 30 = 50
    }
}
