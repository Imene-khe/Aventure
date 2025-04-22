package data.map;

import data.player.Antagonist;
import data.player.EnemyImageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsable de la gestion des vagues d'ennemis dans un niveau.
 */
public class WaveManager {

	private CombatMap combatMap;
	private int arenaLine;
	private int arenaCol;
    private int currentWave;
    private List<List<Antagonist>> waves;
    private boolean levelFinished;
    private EnemyImageManager imageManager;

    // ✅ Constructeur avec EnemyImageManager
    public WaveManager(EnemyImageManager imageManager, int arenaLine, int arenaCol) {
    	this.currentWave = 0;
        this.levelFinished = false;
        this.waves = new ArrayList<>();
        this.imageManager = imageManager;
        this.arenaLine = arenaLine;
        this.arenaCol = arenaCol;

        initWaves();
    }

    private void initWaves() {
        List<Antagonist> wave1 = new ArrayList<>();
        wave1.add(new Antagonist(arenaBlock(1, 4), "slime", imageManager));
        wave1.add(new Antagonist(arenaBlock(1, 6), "slime", imageManager));
        wave1.add(new Antagonist(arenaBlock(1, 8), "slime", imageManager));
        waves.add(wave1);

        List<Antagonist> wave2 = new ArrayList<>();
        wave2.add(new Antagonist(arenaBlock(2, 3), "slime", imageManager));
        wave2.add(new Antagonist(arenaBlock(2, 5), "slime", imageManager));
        wave2.add(new Antagonist(arenaBlock(2, 7), "slime", imageManager));
        wave2.add(new Antagonist(arenaBlock(2, 9), "slime", imageManager));
        wave2.add(new Antagonist(arenaBlock(2, 11), "slime", imageManager));
        waves.add(wave2);

        List<Antagonist> wave3 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            wave3.add(new Antagonist(arenaBlock(3 + (i / 5), 2 + (i % 5) * 2), "slime", imageManager));
        }
        waves.add(wave3);
    }



    public List<Antagonist> getCurrentWaveEnemies() {
        if (currentWave < waves.size()) {
            return waves.get(currentWave);
        } else {
            return new ArrayList<>();
        }
    }

    public void updateWave() {
        boolean allDead = true;
        for (Antagonist enemy : getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                allDead = false;
                break;
            }
        }
        if (allDead) {
            currentWave++;
            if (currentWave >= waves.size()) {
                levelFinished = true;
            }
        }
    }

    public void triggerWave(int waveIndex) {
        if (waveIndex >= 0 && waveIndex < waves.size()) {
            this.currentWave = waveIndex;
            this.levelFinished = false;
        }
    }
    private Block arenaBlock(int lineOffset, int colOffset) {
        return new Block(arenaLine + lineOffset, arenaCol + colOffset);
    }


    public boolean isLevelFinished() {
        return levelFinished;
    }

    public int getCurrentWaveNumber() {
        return currentWave + 1;
    }
}

