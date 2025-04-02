
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import data.map.CombatMap;

public class StartScreen extends JFrame {

    private static final long serialVersionUID = 1L;
    private JButton startButton;
    private JButton combatMapButton;
    private JButton shopButton;

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
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

        // ✅ Création des boutons stylisés
        startButton = createStyledButton("🚀 Commencer l'Aventure");
        combatMapButton = createStyledButton("⚔ Mode Combat");
        shopButton = createStyledButton("🛒 Boutique");

        // ✅ Ajout des actions aux boutons
        startButton.addActionListener(e -> startGame("adventure"));
        combatMapButton.addActionListener(e -> startGame("combat"));
        shopButton.addActionListener(e -> startGame("shop"));

        // ✅ Panel pour organiser les boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10)); // Trois boutons empilés
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(combatMapButton);
        buttonPanel.add(shopButton);

        // ✅ Ajout des composants à l'écran principal
        background.add(titleLabel, BorderLayout.NORTH);
        background.add(buttonPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ✅ Méthode pour créer des boutons stylisés
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

        // ✅ Effet de survol
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

    // ✅ Gérer le choix entre l'Aventure, la Boutique et le Mode Combat
    public void startGame(String mapType) {
        dispose(); // Fermer l’écran de démarrage

        switch (mapType) {
            case "adventure":
                new MainGUI();
                break;
            case "combat":
                JFrame combatFrame = new JFrame("Mode Combat");
                combatFrame.setSize(800, 600);
                combatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                combatFrame.add(new CombatMap(null));
                combatFrame.setVisible(true);
                break;
            case "shop":
            	// ✅ Création de la fenêtre pour la boutique
                JFrame shopFrame = new JFrame("Boutique");
                shopFrame.setSize(800, 600);
                shopFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // ✅ Création de GameDisplay et activation de shopMap
                GameDisplay gameDisplay = new GameDisplay();
                gameDisplay.enterShop(); // ✅ Afficher directement la boutique

                shopFrame.add(gameDisplay);
                shopFrame.setVisible(true);
                break;
                
            default:
                new MainGUI();
                break;
        }
    }

    // ✅ Effet de fondu progressif AVEC TIMER
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

    // ✅ Main pour tester l'affichage de l'écran de démarrage
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartScreen::new);
    }
}
