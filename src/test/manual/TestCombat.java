package test.manual;

import javax.swing.JFrame;

import gui.GameDisplay;

public class TestCombat {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Affichage - CombatMap");

        GameDisplay gameDisplay = new GameDisplay();
        gameDisplay.enterCombatMap(); 

        frame.add(gameDisplay);
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gameDisplay.requestFocusInWindow();
    }
}
