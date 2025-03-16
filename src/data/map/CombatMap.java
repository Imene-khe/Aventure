package data.map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import data.player.Hero;
import data.player.Skeleton;

public class CombatMap extends JPanel {
    private Hero hero;
    private ArrayList<Skeleton> skeletons;
    private int skeletonsKilled = 0;

    public CombatMap() {
        this.hero = new Hero(null); // 100 HP, 10 ATK
        this.skeletons = new ArrayList<>();
        generateSkeletons(10); // Générer 10 squelettes aléatoires

        setFocusable(true);
        requestFocusInWindow();

        // ✅ Ajouter le listener pour X, Y, Z
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'x') {
                    attack();
                } else if (e.getKeyChar() == 'y') {
                    defend();
                } else if (e.getKeyChar() == 'z') {
                    dodge();
                }
            }
        });

        // ✅ Démarrer le mouvement automatique des squelettes
        Timer skeletonTimer = new Timer(1000, e -> moveSkeletons());
        skeletonTimer.start();
    }

    private void generateSkeletons(int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            skeletons.add(new Skeleton(4)); // Chaque squelette a 4 HP
        }
    }

    private void moveSkeletons() {
        for (Iterator<Skeleton> iterator = skeletons.iterator(); iterator.hasNext();) {
            Skeleton skeleton = iterator.next();
            skeleton.move(); // Déplacer le squelette

            if (skeleton.getPosition() <= 0) {
                hero.takeDamage(10); // Si un squelette atteint le héros, il prend 10 dégâts
                iterator.remove();
            }
        }

        repaint();

        if (hero.getHealth() <= 0) {
            JOptionPane.showMessageDialog(this, "💀 Tu as perdu ! Les squelettes t'ont vaincu.");
            resetCombat();
        }
    }

    private void attack() {
        if (!skeletons.isEmpty()) {
            Skeleton target = skeletons.get(0);
            target.takeDamage(1);

            if (target.getHealth() <= 0) {
                skeletons.remove(target);
                skeletonsKilled++;
            }
        }

        if (skeletonsKilled >= 10) {
            JOptionPane.showMessageDialog(this, "🏆 Victoire ! Tu as vaincu tous les squelettes !");
            resetCombat();
        }

        repaint();
    }

    private void defend() {
        //hero.heal(5);
        repaint();
    }

    private void dodge() {
        JOptionPane.showMessageDialog(this, "Tu as esquivé l’attaque !");
    }

    private void resetCombat() {
        hero = new Hero(null);
        skeletons.clear();
        skeletonsKilled = 0;
        generateSkeletons(10);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ✅ Dessiner le héros
        g.setColor(Color.BLUE);
        g.fillRect(50, getHeight() - 100, 50, 50);

        // ✅ Dessiner les squelettes
        g.setColor(Color.RED);
        for (int i = 0; i < skeletons.size(); i++) {
            g.fillRect(200 + i * 60, 100, 50, 50);
        }

        // ✅ Afficher la vie du héros
        g.setColor(Color.BLACK);
        g.drawString("Vie du héros : " + hero.getHealth(), 50, 50);
    }
}

