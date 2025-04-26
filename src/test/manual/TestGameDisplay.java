package test.manual;

import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import gui.GameDisplay;

public class TestGameDisplay {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Affichage + Déplacement");

        GameDisplay gameDisplay = new GameDisplay();

        // ➡️ Ajout d'un KeyListener pour bouger le héros
        gameDisplay.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameDisplay.getController() != null) {
                    gameDisplay.getController().moveHero(e.getKeyCode(), null); // ✅ Appelle moveHero
                }
            }
        });

        frame.add(gameDisplay);
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gameDisplay.setFocusable(true);
        gameDisplay.requestFocusInWindow();
    }
}
