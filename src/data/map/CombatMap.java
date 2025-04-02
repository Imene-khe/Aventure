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
    private int spriteRow = 0;
    private boolean flipped = false;


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

        // 🕒 Timer pour la logique de jeu (ennemis)
        Timer gameLoop = new Timer(1000, e -> updateEnemies());
        gameLoop.start();

        // 🔄 Timer pour rafraîchir l'affichage des animations (héros notamment)
        Timer repaintTimer = new Timer(100, e -> repaint());
        repaintTimer.start();
    }

    private void attack() {
        spriteRow = 4; // ligne de dispute/attaque
        Timer resetAnimation = new Timer(400, e -> {
            spriteRow = 0; // retour à idle
            repaint();
        });
        resetAnimation.setRepeats(false);
        resetAnimation.start();

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

        // ✅ Héros animé via HeroAnimator
        if (heroAnimator != null) {
            int x = 50;
            int y = getHeight() - 100;
            int spriteRow = 0;
            boolean flipped = false;
            heroAnimator.draw(g, 50, getHeight() - 100, spriteRow, flipped, 50);

        }

        // PV du héros
        g.setColor(Color.BLACK);
        g.drawString("PV Héros : " + hero.getHealth(), 50, 50);

        // ✅ Ennemis (avec debug pour visibilité)
        int startX = 200;
        int startY = getHeight() - 100;
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                Image img = enemy.getCurrentImage();
                if (img != null) {
                    g.drawImage(img, enemy.getX(), enemy.getY(), 50, 50, null);
                } else {
                    System.out.println("❌ Ennemi " + enemy + " n'a pas d'image !");
                }
                EnemyHealthBar.draw(g, enemy, startX, startY);
                startX += 60;
            }
        }
    }
}
