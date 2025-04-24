package control;

import data.map.Block;
import data.map.CombatMap;
import data.map.HostileMap;
import data.map.Map;
import data.map.WaveManager;
import data.player.Antagonist;
import data.player.Hero;
import data.quest.Quest;
import data.quest.QuestManager;
import gui.GameDisplay;
import gui.MainGUI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class CombatController {

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
        ArrayList<Antagonist> enemies;

        boolean isCombatMap = false;

        if (activeMap instanceof CombatMap cMap) {
            enemies = cMap.getAntagonists();
            isCombatMap = true;
        } else if (activeMap instanceof HostileMap hMap) {
            enemies = hMap.getAntagonistList();
        } else {
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
                    it.remove();
                    enemyKilled = true;
                }

                break; // ❗ Un seul ennemi attaqué par clic
            }
        }

        if (enemyKilled && isCombatMap) {
            CombatMap cMap = (CombatMap) activeMap;

            if (cMap.areAllEnemiesDead()) {
                System.out.println("🌊 Tous les ennemis de la vague sont morts !");
                loadNextWave(); 
            }
        }

        display.repaint(); // 🔄 Mise à jour visuelle
    }




    public void attack(Block targetBlock) {
        System.out.println("🔍 Ennemis dans hostileMap : " + hostileMap.getAntagonistList().size());

        for (Antagonist enemy : hostileMap.getAntagonistList()) {
            System.out.println("➡️ Ennemi sur : " + enemy.getPosition());

            if (enemy.getPosition().equals(targetBlock)) {
                System.out.println("🎯 ENNEMI TOUCHÉ !");
                enemy.takeDamage(25);

                if (enemy.isDead()) {
                    System.out.println("💀 Ennemi MORT !");
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

        System.out.println("❌ Aucun ennemi trouvé sur ce bloc !");
    }
    public void loadFirstWaveIfNeeded() {
        Map activeMap = gameController.getDisplay().getActiveMap();
        if (activeMap instanceof CombatMap combatMap) {
            if (waveManager == null) {
                int arenaLine = combatMap.getCenterStartLine();
                int arenaCol = combatMap.getCenterStartCol();
                waveManager = new WaveManager(display.getEnemyImageManager(), arenaLine, arenaCol);
                waveManager.setCombatMap(combatMap);
                waveManager.setGameController(gameController); // ✅ ICI
            }

            combatMap.clearAntagonists();
            combatMap.setAntagonists(new ArrayList<>(waveManager.getCurrentWaveEnemies()));
            System.out.println("🌀 Première vague d'ennemis chargée : " + combatMap.getAntagonists().size());
        }
    }






    public void loadNextWave() {
        waveManager.updateWave(); // ➕ vérifie la mort de tous les ennemis

        if (!waveManager.isLevelFinished()) {
            Map activeMap = gameController.getDisplay().getActiveMap();
            if (activeMap instanceof CombatMap combatMap) {
                combatMap.clearAntagonists();
                System.out.println("📊 currentWave = " + waveManager.getCurrentWaveNumber());
                System.out.println("📦 Ennemis de la vague actuelle : " + waveManager.getCurrentWaveEnemies().size());
                combatMap.setAntagonists(new ArrayList<>(waveManager.getCurrentWaveEnemies()));
                System.out.println("🌀 Nouvelle vague chargée : " + combatMap.getAntagonists().size());
            }
        } else {
            System.out.println("✅ Toutes les vagues sont terminées !");
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
