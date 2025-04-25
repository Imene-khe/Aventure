package gui.animation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class EndCreditsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private int yPosition = 600;
    private Timer timer;
    private final int scrollSpeed = 3; // ✅ Accéléré (pixels par tick)

    private Image heroImg, princessImg, bossImg;

    public EndCreditsPanel() {
        setBackground(Color.BLACK);
        loadImages();
        timer = new Timer(30, this::scrollCredits); // 🔁 Toutes les 30 ms
        timer.start();
    }

    private void loadImages() {
        try {
            heroImg = ImageIO.read(new File("src/images/player/Player.png"));
            princessImg = ImageIO.read(new File("src/images/player/princess.png"));
            bossImg = ImageIO.read(new File("src/images/enemies/boss/boss.png"));
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement d'image dans le générique.");
            e.printStackTrace();
        }
    }

    private void scrollCredits(ActionEvent e) {
        yPosition -= scrollSpeed;
        if (yPosition < -300) {
            timer.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int centerX = getWidth() / 2;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("🎮 Jeu réalisé par :", centerX - 120, yPosition);
        g.drawString("Mathis Albrun - Développeur Gameplay", centerX - 200, yPosition + 40);
        g.drawString("Imene Khelil - IHM et Design", centerX - 200, yPosition + 80);

        if (heroImg != null) g.drawImage(heroImg, centerX - 100, yPosition + 130, 32, 32, null);
        if (princessImg != null) g.drawImage(princessImg, centerX, yPosition + 130, 32, 32, null);
        if (bossImg != null) g.drawImage(bossImg, centerX + 100, yPosition + 130, 32, 32, null);
    }

    // ✅ Méthode statique pour lancer le générique dans une fenêtre propre
    public static void showInWindow() {
        JFrame frame = new JFrame("🎬 Générique de fin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setContentPane(new EndCreditsPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ✅ Méthode main uniquement pour test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EndCreditsPanel::showInWindow);
    }
}
