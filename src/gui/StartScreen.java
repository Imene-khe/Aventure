
package gui;

import javax.swing.*;

import org.apache.log4j.Logger;
import log.LoggerUtility;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import data.map.CombatMap;

public class StartScreen extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerUtility.getLogger(StartScreen.class, "text");
    private JButton startButton;
    private JButton combatMapButton;
    private JButton shopButton;

    public StartScreen() {
        super("Bienvenue dans l'Aventure !");
        logger.info("🖥️ Écran de démarrage (StartScreen) initialisé.");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel background = new JLabel(new ImageIcon("src/images/outdoors/screen.jpg"));
        background.setLayout(new BorderLayout());
        add(background);

        JLabel titleLabel = new JLabel("🌟 AVENTURE - LE DESTIN DE SERRE-GY 🌟", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

        startButton = createStyledButton("🚀 Commencer l'Aventure");
        combatMapButton = createStyledButton("⚔ Mode Combat");
        shopButton = createStyledButton("🛒 Boutique");

        startButton.addActionListener(e -> startGame("adventure"));
        combatMapButton.addActionListener(e -> startGame("combat"));
        shopButton.addActionListener(e -> startGame("shop"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10)); 
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(combatMapButton);
        buttonPanel.add(shopButton);

        background.add(titleLabel, BorderLayout.NORTH);
        background.add(buttonPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 63, 65));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 130, 230), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 130, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 63, 65));
            }
        });

        return button;
    }

    public void startGame(String mapType) {
        dispose();
        logger.info("🚀 Bouton pressé : démarrage du mode " + mapType + ".");

        switch (mapType) {
            case "adventure":
                new MainGUI();
                break;
            case "combat":
                JFrame combatFrame = new JFrame("Mode Combat");
                combatFrame.setSize(800, 600);
                combatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                GameDisplay combatDisplay = new GameDisplay();
                CombatMap combatMap = new CombatMap(23, 40); 
                combatDisplay.setMap(combatMap); 
                combatDisplay.setHero(new data.player.Hero(combatMap.getBlock(12, 20), 100)); 
                combatDisplay.repaint(); 

                combatFrame.add(combatDisplay);
                combatFrame.setVisible(true);
                break;

            case "shop":
                JFrame shopFrame = new JFrame("Boutique");
                shopFrame.setSize(800, 600);
                shopFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                GameDisplay gameDisplay = new GameDisplay();
                gameDisplay.enterShop(); 
                shopFrame.add(gameDisplay);
                shopFrame.setVisible(true);
                break;
                
            default:
                new MainGUI();
                break;
        }
    }

    public void fadeOutAndStartGame() {
        Timer timer = new Timer(50, new ActionListener() {
            float opacity = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0) {
                    ((Timer) e.getSource()).stop();
                    dispose();
                    new MainGUI();
                }
            }
        });

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartScreen::new);
    }
}
