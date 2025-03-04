package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartScreen extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StartScreen() {
        super("Bienvenue dans le jeu !");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Ajouter un titre
        JLabel titleLabel = new JLabel("Bienvenue dans l'Aventure !", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);

        // Ajouter un bouton pour commencer
        JButton startButton = new JButton("Jouer");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Ferme l'écran de démarrage
                new MainGUI(); // Lance le jeu
            }
        });

        // Ajouter le bouton en bas
        add(startButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Centre la fenêtre
        setVisible(true);
    }

    // Main interne pour tester uniquement l'affichage de StartScreen
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartScreen::new);
    }
}
