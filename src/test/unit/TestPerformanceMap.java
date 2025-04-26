package test.unit;

import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

import data.map.Map;
import data.map.HostileMap;
import data.map.CombatMap;
import log.LoggerUtility;

public class TestPerformanceMap {

    private static final Logger logger = LoggerUtility.getLogger(TestPerformanceMap.class, "text");

    @Test
    public void testMainMapGenerationTime() {
        long startTime = System.currentTimeMillis();
        Map map = new Map(22, 40, 5, false); 
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        logger.info("Temps de création de Map : " + duration + " ms");

        assertTrue("La génération de la Map prend trop de temps !", duration < 1000);
    }

    @Test
    public void testHostileMapGenerationTime() {
        long startTime = System.currentTimeMillis();
        HostileMap hostileMap = new HostileMap(22, 40, 5);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        logger.info("Temps de création de HostileMap : " + duration + " ms");

        assertTrue("La génération de la HostileMap prend trop de temps !", duration < 1000);
    }

    @Test
    public void testCombatMapGenerationTime() {
        long startTime = System.currentTimeMillis();
        CombatMap combatMap = new CombatMap(22, 40);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        logger.info("Temps de création de CombatMap : " + duration + " ms");

        assertTrue("La génération de la CombatMap prend trop de temps !", duration < 1000);
    }
}
