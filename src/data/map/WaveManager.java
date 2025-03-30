package data.map;

import data.player.Antagonist;
import data.player.EnemyImageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsable de la gestion des vagues d'ennemis dans un niveau.
 */
public class WaveManager {

    private int currentWave;
    private final List<List<Antagonist>> waves;
    private boolean levelFinished;
    private final EnemyImageManager imageManager;

    // ✅ Constructeur avec EnemyImageManager
    public WaveManager(EnemyImageManager imageManager) {
        this.currentWave = 0;
        this.levelFinished = false;
        this.waves = new ArrayList<>();
        this.imageManager = imageManager;

        initWaves();
    }

    private void initWaves() {
        List<Antagonist> wave1 = new ArrayList<>();
        wave1.add(new Antagonist(new Block(4, 2), "small", imageManager));
        wave1.add(new Antagonist(new Block(4, 4), "small", imageManager));
        wave1.add(new Antagonist(new Block(4, 6), "small", imageManager));
        waves.add(wave1);

        List<Antagonist> wave2 = new ArrayList<>();
        wave2.add(new Antagonist(new Block(4, 1), "small", imageManager));
        wave2.add(new Antagonist(new Block(4, 3), "small", imageManager));
        wave2.add(new Antagonist(new Block(4, 5), "small", imageManager));
        wave2.add(new Antagonist(new Block(4, 7), "medium", imageManager));
        wave2.add(new Antagonist(new Block(4, 9), "medium", imageManager));
        waves.add(wave2);

        List<Antagonist> wave3 = new ArrayList<>();
        wave3.add(new Antagonist(new Block(4, 0), "small", imageManager));
        wave3.add(new Antagonist(new Block(4, 2), "small", imageManager));
        wave3.add(new Antagonist(new Block(4, 4), "small", imageManager));
        wave3.add(new Antagonist(new Block(4, 6), "small", imageManager));
        wave3.add(new Antagonist(new Block(4, 8), "medium", imageManager));
        wave3.add(new Antagonist(new Block(4, 10), "medium", imageManager));
        wave3.add(new Antagonist(new Block(4, 12), "medium", imageManager));
        wave3.add(new Antagonist(new Block(4, 14), "large", imageManager));
        wave3.add(new Antagonist(new Block(4, 16), "large", imageManager));
        wave3.add(new Antagonist(new Block(4, 18), "large", imageManager));
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

    public boolean isLevelFinished() {
        return levelFinished;
    }

    public int getCurrentWaveNumber() {
        return currentWave + 1;
    }
}

