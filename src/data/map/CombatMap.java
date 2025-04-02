package data.map;

import data.player.Antagonist;
import data.player.EnemyImageManager;
import data.player.Hero;
import gui.EnemyHealthBar;
import gui.animation.HeroAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class CombatMap extends JPanel {

    private Hero hero;
    private HeroAnimator heroAnimator;
    private WaveManager waveManager;
    private EnemyImageManager imageManager;
    private Image decorSpriteSheet;

    public CombatMap(EnemyImageManager imageManager) {
        this.hero = new Hero(new Block(4, 1));
        this.hero.setHealth(100);
        this.imageManager = imageManager;
        this.waveManager = new WaveManager(imageManager);
        this.decorSpriteSheet = new ImageIcon(getClass().getResource("/images/outdoor/FT_x16Decorations.png")).getImage();

        try {
            this.heroAnimator = new HeroAnimator("src/images/player/Player.png");
        } catch (IOException e) {
            System.out.println("❌ Erreur chargement HeroAnimator");
            e.printStackTrace();
        }

        setFocusable(true);
        requestFocusInWindow();

        // 🎮 Contrôles : flèches pour bouger, x/y/z pour actions
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Block pos = hero.getPosition();
                int line = pos.getLine();
                int col = pos.getColumn();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> hero.setPosition(new Block(line, col - 1));
                    case KeyEvent.VK_RIGHT -> hero.setPosition(new Block(line, col + 1));
                    case KeyEvent.VK_UP -> hero.setPosition(new Block(line - 1, col));
                    case KeyEvent.VK_DOWN -> hero.setPosition(new Block(line + 1, col));
                }

                switch (e.getKeyChar()) {
                    case 'x' -> attack();
                    case 'y' -> defend();
                    case 'z' -> dodge();
                }

                repaint();
            }
        });

        Timer gameLoop = new Timer(1000, e -> updateEnemies());
        gameLoop.start();

        Timer repaintTimer = new Timer(150, e -> repaint());
        repaintTimer.start();
    }

    private void attack() {
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                enemy.takeDamage(10);
                break;
            }
        }
        waveManager.updateWave();
        repaint();

        if (waveManager.isLevelFinished()) {
            JOptionPane.showMessageDialog(this, "🏆 Tu as vaincu toutes les vagues !");
        }
    }

    private void defend() {
        JOptionPane.showMessageDialog(this, "🛡️ Tu te défends !");
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

        for (int x = 0; x < getWidth(); x += 50) {
            drawTile(g, 0, 2, x, getHeight() - 50);
            drawTile(g, 0, 4, x, getHeight() - 100);
        }

        drawTile(g, 5, 3, 50, getHeight() - 150);
        drawTile(g, 6, 3, 500, getHeight() - 150);

        // 👤 Ghaya (Héros)
        if (heroAnimator != null) {
            int drawX = hero.getPosition().getColumn() * 50;
            int drawY = hero.getPosition().getLine() * 50;
            heroAnimator.draw(g, drawX, drawY, 0, false, 50);
        }

        // ❤️ Vie du héros
        g.setColor(Color.BLACK);
        g.drawString("PV Héros : " + hero.getHealth(), 50, 50);

        // 👾 Ennemis
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                g.drawImage(enemy.getCurrentImage(), enemy.getX(), enemy.getY(), 50, 50, null);
                EnemyHealthBar.draw(g, enemy, enemy.getX(), enemy.getY() - 10);
            }
        }
    }
}

