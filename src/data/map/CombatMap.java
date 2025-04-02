package data.map;

import data.player.Antagonist;
import data.player.EnemyImageManager;
import data.player.Hero;
import gui.EnemyHealthBar;
import gui.animation.HeroAnimator;
import gui.animation.HeroRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import gui.*;

public class CombatMap extends JPanel {

    private Hero hero;
    private HeroAnimator heroAnimator;
    private HeroRenderer heroRenderer;
    private WaveManager waveManager;
    private EnemyImageManager imageManager;
    private Image decorSpriteSheet;

    public CombatMap(EnemyImageManager imageManager) {
        this.hero = new Hero(new Block(4, 1));
        this.hero.setHealth(100);
        this.imageManager = imageManager;
        this.waveManager = new WaveManager(imageManager);

        try {
        	
            this.heroAnimator = new HeroAnimator("src/images/player/Player.png");
            this.heroRenderer = new HeroRenderer(heroAnimator, hero.getPosition().getColumn() * 50, hero.getPosition().getLine() * 50);

         
        } catch (IOException e) {
            System.out.println("❌ Erreur chargement HeroAnimator");
            e.printStackTrace();
        }

        this.decorSpriteSheet = new ImageIcon(getClass().getResource("/images/outdoor/FT_x16Decorations.png")).getImage();

        setFocusable(true);
        requestFocusInWindow();

        // ✅ Gestion des touches
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> heroRenderer.goLeft();
                    case KeyEvent.VK_RIGHT -> heroRenderer.goRight();
                    case KeyEvent.VK_UP -> heroRenderer.goUp();
                    case KeyEvent.VK_DOWN -> heroRenderer.goDown();
                    case KeyEvent.VK_X -> {
                        heroRenderer.attack(); // animation attaque
                        attack();              // logique d’attaque
                    }
                }
                repaint();
            }
        });

        // Timer logique jeu
        new Timer(1000, e -> updateEnemies()).start();

        // Timer animation
        new Timer(100, e -> repaint()).start();
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

        for (int x = 0; x < getWidth(); x += 50) {
            drawTile(g, 0, 2, x, getHeight() - 50);
            drawTile(g, 0, 4, x, getHeight() - 100);
        }

        drawTile(g, 5, 3, 50, getHeight() - 150);
        drawTile(g, 6, 3, 500, getHeight() - 150);

        // ✅ Ghaya animé
        if (heroRenderer != null) {
            heroRenderer.draw(g, 50); // taille d’un bloc
        }

        // ❤️ PV Héros
        g.setColor(Color.BLACK);
        g.drawString("PV Héros : " + hero.getHealth(), 50, 50);

        // ✅ Ennemis avec animation
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                g.drawImage(enemy.getCurrentImage(), enemy.getX(), enemy.getY(), 50, 50, null);
                EnemyHealthBar.draw(g, enemy, enemy.getX(), enemy.getY() - 10);
            }
        }
    }
}

