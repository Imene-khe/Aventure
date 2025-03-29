package gui;

import java.awt.Color;
import java.awt.Graphics;
import data.player.Antagonist;

/**
 * Classe utilitaire pour dessiner la barre de vie d’un ennemi à l’écran.
 */
public class EnemyHealthBar {

    private static final int BAR_WIDTH = 40;
    private static final int BAR_HEIGHT = 6;

    /**
     * Dessine la barre de vie au-dessus de l'ennemi.
     *
     * @param g Le contexte graphique
     * @param enemy L’ennemi à dessiner
     * @param x Position X à l’écran
     * @param y Position Y à l’écran
     */
    public static void draw(Graphics g, Antagonist enemy, int x, int y) {
        int maxHealth = enemy.getHealth();
        int currentHealth = enemy.getMaxHealth();

        // Calcul de la largeur de la barre verte
        int healthBarWidth = (int) ((currentHealth / (float) maxHealth) * BAR_WIDTH);

        // Fond rouge
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, BAR_WIDTH, BAR_HEIGHT);

        // Barre verte selon la vie actuelle
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 10, healthBarWidth, BAR_HEIGHT);

        // Bordure noire
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 10, BAR_WIDTH, BAR_HEIGHT);
    }
}
