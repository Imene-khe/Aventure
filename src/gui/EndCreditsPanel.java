package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class EndCreditsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private int yPosition = 800;
    private Timer timer;

    public EndCreditsPanel() {
        setBackground(Color.BLACK);
        timer = new Timer(30, new ScrollCredits());
        timer.start();
    }

    private class ScrollCredits implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            yPosition -= 1;
            if (yPosition < -300) {
                timer.stop(); // Fin du générique
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        int centerX = getWidth() / 2;

        g.drawString("Jeu réalisé par :", centerX - 100, yPosition);
        g.drawString("Mathis Albrun - Développeur Gameplay", centerX - 180, yPosition + 40);
        g.drawString("Imene Khelil - Intégration IHM et Design", centerX - 180, yPosition + 80);

        try {
            Image hero = ImageIO.read(new File("src/images/player/Player.png"));
            Image princess = ImageIO.read(new File("src/images/player/princess.png"));
            Image boss = ImageIO.read(new File("src/images/enemies/boss/boss.png")); 

            if (hero != null)
                g.drawImage(hero, centerX - 100, yPosition + 120, 32, 32, null);
            if (princess != null)
                g.drawImage(princess, centerX, yPosition + 120, 32, 32, null);
            if (boss != null)
                g.drawImage(boss, centerX + 100, yPosition + 120, 32, 32, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Générique de fin");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JPanel panel = new JPanel() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				int yPosition = 100;
                Image heroImg, princessImg, bossImg;

                {
                    try {
                        heroImg = ImageIO.read(new File("src/images/player/Player.png"));
                        princessImg = ImageIO.read(new File("src/images/player/princess.png"));
                        bossImg = ImageIO.read(new File("src/images/enemies/boss/boss.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Timer timer = new Timer(50, e -> {
                        yPosition--;
                        repaint();
                    });
                    timer.start();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int centerX = getWidth() / 2;

                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());

                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 24));
                    g.drawString("Jeu réalisé par :", centerX - 100, yPosition);
                    g.drawString("Mathis Albrun - Développeur Gameplay", centerX - 180, yPosition + 40);
                    g.drawString("Imene Khelil - Intégration IHM et Design", centerX - 180, yPosition + 80);

                    if (heroImg != null)
                        g.drawImage(heroImg, centerX - 100, yPosition + 120, 32, 32, null);
                    if (princessImg != null)
                        g.drawImage(princessImg, centerX, yPosition + 120, 32, 32, null);
                    if (bossImg != null)
                        g.drawImage(bossImg, centerX + 100, yPosition + 120, 32, 32, null);
                }
            };

            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }



    
 
}
