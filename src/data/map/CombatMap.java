package data.map;

import data.player.Antagonist;
import data.player.EnemyImageManager;
import data.player.Hero;
import gui.EnemyHealthBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;

public class CombatMap extends JPanel {

    private Hero hero;
    private WaveManager waveManager;
    private EnemyImageManager imageManager;
    private Image decorSpriteSheet;


    public CombatMap() {
        this.hero = new Hero(null);
        this.imageManager = new EnemyImageManager(); // tu dois l’avoir déjà
        this.waveManager = new WaveManager(); // vague 0 par défaut
        this.decorSpriteSheet = new ImageIcon("data.map/FT_x16Decorations.png").getImage(); 


        setFocusable(true);
        requestFocusInWindow();

        //  Contrôles X Y Z les touches pour se battre
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

        //  Timer de déplacement/MAJ des ennemis
        Timer gameLoop = new Timer(1000, e -> updateEnemies());
        gameLoop.start();
    }

    private void attack() {
        // Attaque le premier slime vivant
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
        // Ici tu peux ajouter un système de déplacement ou attaque auto si tu veux
        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                hero.takeDamage(5); // attaque passive toutes les secondes
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
        int tileSize = 16; // taille dans la spritesheet
        g.drawImage(decorSpriteSheet, destX, destY, destX + 50, destY + 50,
                tileX * tileSize, tileY * tileSize,
                (tileX + 1) * tileSize, (tileY + 1) * tileSize, null);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fond vert forêt
        g.setColor(new Color(50, 120, 50));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // modification integrale du decor 

        //  Affichage du héros
        g.setColor(Color.BLUE);
        g.fillRect(50, getHeight() - 100, 50, 50);
        g.setColor(Color.BLACK);
        g.drawString("PV Héros : " + hero.getHealth(), 50, 50);

        //  Affichage des ennemis de la vague en cours
        int startX = 200;
        int startY = getHeight() - 100;

        for (Antagonist enemy : waveManager.getCurrentWaveEnemies()) {
            if (!enemy.isDead()) {
                enemy.draw(g, 50); // utilise les frames animées du slime
                EnemyHealthBar.draw(g, enemy, startX, startY);
                startX += 60;
            }
        }
    }
}

