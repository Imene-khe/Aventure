package gui.animation;

import gui.DefaultPaintStrategy;
import viewstrategy.PaintStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EndCreditsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Timer timer;
    private final int scrollSpeed = 3;
    private int yPosition = 600;

    private final PaintStrategy paintStrategy;

    public EndCreditsPanel() {
        this.paintStrategy = new DefaultPaintStrategy();

        setBackground(Color.BLACK);
        timer = new Timer(30, this::scrollCredits);
        timer.start();
    }

    private void scrollCredits(ActionEvent e) {
        yPosition -= scrollSpeed;
        if (yPosition < -300) {
            timer.stop();
            SwingUtilities.getWindowAncestor(this).dispose(); 
        }
        repaint();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintStrategy.paintEndCredits(g, this);
    }

    public int getYPosition() {
        return yPosition;
    }

    public static void showInWindow() {
        JFrame frame = new JFrame("🎬 Générique de fin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setContentPane(new EndCreditsPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EndCreditsPanel::showInWindow);
    }
}
