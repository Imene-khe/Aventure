package control;

import org.apache.log4j.Logger;
import log.LoggerUtility;

import data.map.Block;
import data.map.CombatMap;
import data.map.HostileMap;
import data.map.Map;
import data.map.WaveManager;
import data.player.Antagonist;
import data.player.Hero;
import data.quest.Quest;
import data.quest.QuestManager;
import generator.CombatMapGenerator;
import gui.GameDisplay;
import gui.MainGUI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class CombatController {
	
	private static final Logger logger = LoggerUtility.getLogger(CombatController.class, "text");
	private GameDisplay display;
    private HostileMap hostileMap;
    private Hero hero;
    private GameController gameController;
    private WaveManager waveManager;

    public CombatController(GameDisplay display, GameController gameController) {
        this.display = display;
        this.hero = display.getHero();
        this.gameController = gameController;
    }

    public void handleClick(Point mousePoint) {
        Block heroBlock = hero.getPosition();
        Map activeMap = display.getActiveMap();
        ArrayList<Antagonist> enemies = activeMap.getMobileAntagonists(); // ✅ Utilisation méthode propre

        if (enemies.isEmpty()) {
            return;
        }

        Iterator<Antagonist> it = enemies.iterator();
        boolean enemyKilled = false;

        while (it.hasNext()) {
            Antagonist enemy = it.next();
            Block enemyBlock = enemy.getPosition();

            if (gameController.isAdjacent(heroBlock, enemyBlock)) {
                enemy.takeDamage(50);

                if (enemy.isDead()) {
                    MainGUI.getInstance().getQuestManager().updateQuest("Chasseur de têtes", 1);

                    if ("boss".equals(enemy.getType()) && activeMap.supportsFinalZoneReveal()) { // ✅ Map fournit l'info
                        activeMap.revealFinalZone();
                        it.remove();
                        display.repaint();
                        javax.swing.JOptionPane.showMessageDialog(display, "🏁 Un pont s'est ouvert... Va sauver ta femme !");
                    } else {
                        it.remove();
                    }
                    enemyKilled = true;
                }
                break;
            }
        }

        if (enemyKilled && activeMap.supportsWaves() && activeMap.areAllEnemiesDead()) {
            logger.info("Tous les ennemis de la vague sont morts !");
            loadNextWave();
        }

        display.repaint();
    }


    public void attack(Block targetBlock) {
        for (Antagonist enemy : hostileMap.getAntagonistList()) {
            if (enemy.getPosition().equals(targetBlock)) {
            	logger.info("🎯 ENNEMI TOUCHÉ !");
                enemy.takeDamage(25);
                if (enemy.isDead()) {
                	logger.info("💀 Ennemi MORT !");
                	hostileMap.getAntagonistTypes().remove(enemy); 
                    hostileMap.getAntagonistList().remove(enemy);
                    MainGUI.getInstance().getQuestManager().notifyQuestProgress(Quest.TYPE_KILL, 1);
                    QuestManager qm = MainGUI.getInstance().getQuestManager();
                    qm.displayQuests(); 
                    display.repaint();
                }
                return;
            }
        }
        logger.warn("❌ Aucun ennemi trouvé sur ce bloc !");
    }
    
    public void loadFirstWaveIfNeeded() {
        Map activeMap = gameController.getDisplay().getActiveMap();

        if (!activeMap.supportsWaves()) {
            return;
        }

        CombatMap combatMap = (CombatMap) activeMap;

        if (waveManager == null) {
            int arenaLine = combatMap.getCenterStartLine();
            int arenaCol = combatMap.getCenterStartCol();
            waveManager = new WaveManager(display.getEnemyImageManager(), arenaLine, arenaCol);
            waveManager.setCombatMap(combatMap);
            waveManager.setGameController(gameController);
            waveManager.setQuestManager(MainGUI.getInstance().getQuestManager());
        }

        combatMap.clearAntagonists();
        combatMap.setAntagonists(new ArrayList<>(waveManager.getCurrentWaveEnemies()));
        logger.info("Première vague d'ennemis chargée : " + combatMap.getAntagonists().size() + " ennemis");
    }



    public void loadNextWave() {
        waveManager.updateWave();

        if (!waveManager.isLevelFinished()) {
            Map activeMap = gameController.getDisplay().getActiveMap();

            if (!activeMap.supportsWaves()) {
                return;
            }

            CombatMap combatMap = (CombatMap) activeMap;
            combatMap.clearAntagonists();
            logger.debug("currentWave = " + waveManager.getCurrentWaveNumber());
            combatMap.setAntagonists(new ArrayList<>(waveManager.getCurrentWaveEnemies()));
            logger.info("Nouvelle vague chargée : " + combatMap.getAntagonists().size() + " ennemis");
        } else {
            logger.info("Toutes les vagues sont terminées !");
        }
    }

    public HostileMap getHostileMap() {
		return hostileMap;
	}

	public void setHostileMap(HostileMap hostileMap) {
		this.hostileMap = hostileMap;
	}

	public GameController getGameController() {
		return gameController;
	}

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	public WaveManager getWaveManager() {
		return waveManager;
	}

	public void setWaveManager(WaveManager waveManager) {
		this.waveManager = waveManager;
	}
}
