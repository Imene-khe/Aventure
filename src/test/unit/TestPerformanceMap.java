package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Map;
import data.map.HostileMap;
import data.map.CombatMap;

public class TestPerformanceMap {

    @Test
    public void testMainMapGenerationTime() {
        long startTime = System.currentTimeMillis();
        Map map = new Map(22, 40, 5, false); // 22 lignes, 40 colonnes
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("⏱️ Temps de création de Map : " + duration + " ms");

        assertTrue("La génération de la Map prend trop de temps !", duration < 1000);
    }

    @Test
    public void testHostileMapGenerationTime() {
        long startTime = System.currentTimeMillis();
        HostileMap hostileMap = new HostileMap(22, 40, 5);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("⏱️ Temps de création de HostileMap : " + duration + " ms");

        assertTrue("La génération de la HostileMap prend trop de temps !", duration < 1000);
    }

    @Test
    public void testCombatMapGenerationTime() {
        long startTime = System.currentTimeMillis();
        CombatMap combatMap = new CombatMap(22, 40);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("⏱️ Temps de création de CombatMap : " + duration + " ms");

        assertTrue("La génération de la CombatMap prend trop de temps !", duration < 1000);
    }
}
