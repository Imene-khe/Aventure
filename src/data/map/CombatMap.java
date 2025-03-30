package data.map;

import data.player.Antagonist;
import data.player.EnemyImageManager;
import data.player.Hero;
import gui.EnemyHealthBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CombatMap extends JPanel {

    private Hero hero;
    private WaveManager waveManager;
    private EnemyImageManager imageManager;
    private Image decorSpriteSheet;

    // ✅ Constructeur modifié pour recevoir imageManager
    public CombatMap(EnemyImageManager imageManager) {
        this.hero = new Hero(null);
        this.hero.setHealth(100); 
        this.imageManager = imageManager;
        this.waveManager = new WaveManager(imageManager); // on passe le manager aux ennemis
        this.decorSpriteSheet = new ImageIcon(getClass().getResource("/images/outdoor/FT_x16Decorations.png")).getImage();

        setFocusable(true);
        requestFocusInWindow();

        // Contrôles X, Y, Z
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'x' -> attack();
                    case 'y' -> defend();
                    case 'z' -> dodge();
                }
            }
        });

        Timer gameLoop = new Timer(1000, e -> updateEnemies());
        gameLoop.start();
    }

    private void attack() {
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                enemy.takeDamage(10);
                break;
            }
        }
        repaint();
        waveManager.updateWave();

        if (waveManager.isLevelFinished()) {
            JOptionPane.showMessageDialog(this, "🏆 Tu as vaincu toutes les vagues !");
        }
    }

    private void defend() {
        JOptionPane.showMessageDialog(this, "🛡️ Tu te défends !");
        repaint();
    }

    private void dodge() {
        JOptionPane.showMessageDialog(this, "💨 Tu esquives !");
    }

    private void updateEnemies() {
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                hero.takeDamage(1);
                break;
            }
        }

        if (hero.getHealth() <= 0) {
            JOptionPane.showMessageDialog(this, "💀 Tu es mort !");
            System.exit(0);
        }

        repaint();
    }

    private void drawTile(Graphics g, int tileX, int tileY, int destX, int destY) {
        int tileSize = 16;
        g.drawImage(decorSpriteSheet, destX, destY, destX + 50, destY + 50,
                tileX * tileSize, tileY * tileSize,
                (tileX + 1) * tileSize, (tileY + 1) * tileSize, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Sol herbe
        for (int x = 0; x < getWidth(); x += 50) {
            drawTile(g, 0, 2, x, getHeight() - 50);
        }

        // Terre dessous
        for (int x = 0; x < getWidth(); x += 50) {
            drawTile(g, 0, 4, x, getHeight() - 100);
        }

        // Arbres
        drawTile(g, 5, 3, 50, getHeight() - 150);
        drawTile(g, 6, 3, 500, getHeight() - 150);

        // Héros
        g.setColor(Color.BLUE);
        g.fillRect(50, getHeight() - 100, 50, 50);
        g.setColor(Color.BLACK);
        g.drawString("PV Héros : " + hero.getHealth(), 50, 50);

        // Ennemis
        int startX = 200;
        int startY = getHeight() - 100;
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                enemy.draw(g, 50);
                EnemyHealthBar.draw(g, enemy, startX, startY);
                startX += 60;
            }
        }
    }
}
