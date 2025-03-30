package data.map;

import data.player.EnemyImageManager;

import javax.swing.*;

public class CombatMapGui {

    public CombatMapGui() {
        // Crée la fenêtre
        JFrame frame = new JFrame("🌿 Combat contre les slimes !");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Initialisation du gestionnaire d'images
        EnemyImageManager imageManager = new EnemyImageManager();

        // Ajoute la carte de combat avec le gestionnaire d'images
        CombatMap combatMap = new CombatMap(imageManager);
        frame.add(combatMap);

        // Centrer la fenêtre
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CombatMapGui());
    }
}
