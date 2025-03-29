package data.map;

import data.player.Antagonist;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsable de la gestion des vagues d'ennemis dans un niveau.
 */
public class WaveManager {

    private int currentWave;
    private final List<List<Antagonist>> waves;
    private boolean levelFinished;

    /**
     * Constructeur : initialise les vagues et l'état du niveau.
     */
    public WaveManager() {
        this.currentWave = 0;
        this.levelFinished = false;
        this.waves = new ArrayList<>();

        // Création des vagues d'ennemis
        initWaves();
    }

    private void initWaves() {
        List<Antagonist> wave1 = new ArrayList<>();
        wave1.add(new Antagonist(null, "small", null));
        wave1.add(new Antagonist(null, "small", null));
        wave1.add(new Antagonist(null, "small", null));
        waves.add(wave1);

        List<Antagonist> wave2 = new ArrayList<>();
        wave2.add(new Antagonist(null, "small", null));
        wave2.add(new Antagonist(null, "small", null));
        wave2.add(new Antagonist(null, "small", null));
        wave2.add(new Antagonist(null, "medium", null));
        wave2.add(new Antagonist(null, "medium", null));
        waves.add(wave2);

        List<Antagonist> wave3 = new ArrayList<>();
        wave3.add(new Antagonist(null, "small", null));
        wave3.add(new Antagonist(null, "small", null));
        wave3.add(new Antagonist(null, "small", null));
        wave3.add(new Antagonist(null, "small", null));
        wave3.add(new Antagonist(null, "medium", null));
        wave3.add(new Antagonist(null, "medium", null));
        wave3.add(new Antagonist(null, "medium", null));
        wave3.add(new Antagonist(null, "large", null));
        wave3.add(new Antagonist(null, "large", null));
        wave3.add(new Antagonist(null, "large", null));
        waves.add(wave3);
    }

    /**
     * Retourne la liste des ennemis de la vague actuelle.
     */
    public List<Antagonist> getCurrentWaveEnemies() {
        if (currentWave < waves.size()) {
            return waves.get(currentWave);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Met à jour la vague automatiquement quand tous les ennemis sont morts.
     */
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

    /**
     * Déclenche manuellement une vague précise (0 = première vague).
     * Utile pour le storytelling.
     */
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
