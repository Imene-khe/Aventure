package gui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Classe utilitaire pour dessiner la barre de vie d’un ennemi à l’écran.
 */
public class EnemyHealthBar {

    private static final int BAR_WIDTH = 40;
    private static final int BAR_HEIGHT = 6;

    public static void draw(Graphics g, int currentHealth, int maxHealth, int x, int y) {
        int healthBarWidth = (int) ((currentHealth / (float) maxHealth) * BAR_WIDTH);

        // Fond rouge
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, BAR_WIDTH, BAR_HEIGHT);

        // Barre verte proportionnelle
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 10, healthBarWidth, BAR_HEIGHT);

        // Bordure noire
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 10, BAR_WIDTH, BAR_HEIGHT);
    }

}
