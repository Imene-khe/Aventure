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
        // ❌ ne fais rien ici pour waveManager (CombatMap pas encore dispo)
    }




    public void handleClick(Point mousePoint) {
        Block heroBlock = hero.getPosition();
        Map activeMap = display.getActiveMap();

        ArrayList<Antagonist> enemies = new ArrayList<>();

        if (activeMap instanceof HostileMap hMap) {
            enemies = hMap.getAntagonistList();
        } else if (activeMap instanceof CombatMap cMap) {
            enemies = cMap.getAntagonists();
        } else {
            return;
        }

        Iterator<Antagonist> it = enemies.iterator();
        boolean enemyKilled = false;

        while (it.hasNext()) {
            Antagonist enemy = it.next();
            Block enemyBlock = enemy.getPosition();

            if (gameController.isAdjacent(heroBlock, enemyBlock)) {
                System.out.println("🗡️ Ennemi adjacent trouvé sur " + enemyBlock + ", attaque !");
                enemy.takeDamage(50);

                if (enemy.isDead()) {
                    it.remove();
                    MainGUI.getInstance().getQuestManager().displayQuests(); 
                    MainGUI.getInstance().getQuestManager().updateQuest("Chasseur de têtes", 1);
                    System.out.println("☠️ Ennemi tué sur " + enemyBlock);
                }

                enemyKilled = true;
                break;
            }
        }

        if (!enemyKilled) {
            System.out.println("🔍 Aucun ennemi adjacent trouvé.");
        }

        display.repaint();
    }

    public void attack(Block targetBlock) {
        System.out.println("🔍 Ennemis dans hostileMap : " + hostileMap.getAntagonistList().size());

        for (Antagonist enemy : hostileMap.getAntagonistList()) {
            System.out.println("➡️ Ennemi sur : " + enemy.getPosition());

            if (enemy.getPosition().equals(targetBlock)) {
                System.out.println("🎯 ENNEMI TOUCHÉ !");
                enemy.takeDamage(50);

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
            // ⚠️ Initialisation du WaveManager si nécessaire
            if (waveManager == null) {
                int arenaLine = combatMap.getCenterStartLine();
                int arenaCol = combatMap.getCenterStartCol();
                waveManager = new WaveManager(display.getEnemyImageManager(), arenaLine, arenaCol);
            }

            // Récupère la vague actuelle sous forme d'ArrayList
            ArrayList<Antagonist> firstWave = new ArrayList<>(waveManager.getCurrentWaveEnemies());

            // Affecte les antagonistes à la map (data)
            combatMap.setAntagonists(firstWave);

            // (optionnel) log de debug
            System.out.println("🌀 Première vague d'ennemis chargée dans CombatMap : " + firstWave.size() + " ennemis.");
        }
    }


    public void loadNextWave() {
        waveManager.updateWave(); // met à jour l'état de la vague actuelle

        if (!waveManager.isLevelFinished()) {
            ArrayList<Antagonist> nextWave = new ArrayList<>(waveManager.getCurrentWaveEnemies());

            Map activeMap = gameController.getDisplay().getActiveMap();
            if (activeMap instanceof CombatMap combatMap) {
                combatMap.setAntagonists(nextWave);
                System.out.println("🌀 Nouvelle vague chargée : " + nextWave.size() + " ennemis.");
            }
        } else {
            System.out.println("✅ Toutes les vagues sont terminées !");
            // tu peux ici déclencher un dialogue, ouvrir une porte, etc.
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
