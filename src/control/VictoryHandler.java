package control;

import javax.swing.JOptionPane;

import gui.MainGUI;

public class VictoryHandler {

    public static void handleVictory(MainGUI gui) {
        JOptionPane.showMessageDialog(gui, "👸 Tu as libéré la princesse !\nL’aventure touche à sa fin...");

        int choice = JOptionPane.showOptionDialog(
            gui,
            "🎊 Félicitations !\nTu as sauvé ta femme et vaincu le boss.\n\nSouhaites-tu rejouer ?",
            "Fin du jeu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"🔁 Rejouer", "❌ Quitter"},
            "🔁 Rejouer"
        );

        if (choice == JOptionPane.YES_OPTION) {
            restartGame(gui);
        } else {
            System.exit(0);
        }
    }

    private static void restartGame(MainGUI gui) {
        gui.dispose();
        javax.swing.SwingUtilities.invokeLater(MainGUI::new);
    }
} 
