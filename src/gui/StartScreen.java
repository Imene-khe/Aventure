package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    public StartScreen() {
        super("Bienvenue dans l'Aventure !");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ✅ Chargement du fond d'écran
        JLabel background = new JLabel(new ImageIcon("src/images/outdoors/screen.jpg"));
        background.setLayout(new BorderLayout());
        add(background);

        // ✅ Titre stylisé
        JLabel titleLabel = new JLabel("🌟 AVENTURE - LE DESTIN DE SERRE-GY 🌟", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

        // ✅ Bouton stylisé
        JButton startButton = new JButton("▶ COMMENCER L'AVENTURE");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBackground(new Color(50, 150, 250));
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createRaisedBevelBorder());
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(250, 50));

        // ✅ Effet de survol
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(30, 130, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(50, 150, 250));
            }
        });

        // ✅ Action du bouton avec animation fluide
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fadeOutAndStartGame();
            }
        });

        // ✅ Panel pour centrer les éléments
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(startButton, BorderLayout.CENTER);

        background.add(panel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ✅ Effet de fondu progressif AVEC TIMER (sans `setOpacity()` pour éviter les erreurs)
    private void fadeOutAndStartGame() {
        Timer timer = new Timer(50, new ActionListener() {
            float opacity = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0) {
                    ((Timer) e.getSource()).stop();
                    dispose();
                    new MainGUI();
                } else {
                    setOpacity(opacity);
                }
            }
        });

        // ✅ Vérifier que la fenêtre est bien non décorée avant de modifier l'opacité
        if (isUndecorated()) {
            timer.start();
        } else {
            dispose();
            new MainGUI();
        }
    }

    // ✅ Main pour tester l'affichage de l'écran de démarrage
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartScreen::new);
    }
}
