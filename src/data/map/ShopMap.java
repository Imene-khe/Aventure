package data.map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import data.player.Hero;
import data.player.Marchant;

public class ShopMap extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image shopBackground;
    private Marchant marchant;
    private Hero hero;
    private boolean showMerchant = true;

    public ShopMap(Hero hero) {
        this.hero = hero;
        loadImages();

        // ✅ Créer une mini map pour la boutique
        Map shopMap = new Map(5, 5, 0,true);
        this.marchant = new Marchant(shopMap, 2, 2);

        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    returnToMainMap();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    marchant.move(-1, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    marchant.move(1, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    marchant.move(0, -1);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    marchant.move(0, 1);
                }
                repaint();
            }
        });

        // ✅ Effet de disparition du marchand
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    showMerchant = !showMerchant;
                    repaint();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void loadImages() {
        shopBackground = new ImageIcon("src/images/outdoor/shop_background.png").getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(marchant.getCurrentSprite(), getX(), getY(), 100, 100, this);


        // ✅ Afficher le marchand s’il est visible
        if (showMerchant) {
            int x = marchant.getPosition().getColumn() * 100;
            int y = marchant.getPosition().getLine() * 100;
            g.drawImage(marchant.getCurrentSprite(), x, y, 100, 100, this);
        }
    }

    private void returnToMainMap() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
       // parent.setContentPane(new GameDisplay());
        parent.revalidate();
        parent.repaint();
    }
    
 
}

