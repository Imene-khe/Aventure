package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.item.InventoryManager;
import data.map.Block;

/**
 * Classe représentant l'interface graphique principale du jeu. 
 * Elle gère l'affichage de la carte, le déplacement du héros et l'inventaire du joueur.
 */
public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Affichage et gestion de la carte du jeu
    private GameDisplay dashboard;
    
    // Gestionnaire de l'inventaire
    private InventoryManager inventory;
  
    // Ajout du bouton d'interaction (dialogue, ouverture...)
    private JButton interactionButton;

    /**
     * Constructeur de la classe. Il initialise la fenêtre, les composants graphiques
     * et les actions du clavier.
     */
    public MainGUI() { 
        super("Aventure - Déplacement du Héros");

        // Configuration de la fenêtre principale
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        
        // Initialisation de la carte, du tableau et de l'inventaire
        //this.setMap(new Map(30, 30));
        this.dashboard = new GameDisplay();
        this.inventory = new InventoryManager();

        // === Création du panneau principal ===
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dashboard, BorderLayout.CENTER); // Assure que GameDisplay est bien affiché au centre

        // === Création d'un panel contenant l'inventaire et le bouton ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inventory, BorderLayout.CENTER); // L'inventaire reste centré

        // === Ajout du bouton d'interaction ===
        interactionButton = new JButton("Interagir");
     // Ajout d'un ActionListener pour le bouton d'interaction
        interactionButton.addActionListener(e -> {
            // Appel de la méthode dans GameDisplay pour obtenir la position du coffre à proximité
            Block chestPos = dashboard.getNearbyChestPosition();

            if (chestPos != null) {
                // Le coffre est trouvé à proximité, on peut l'ouvrir
                dashboard.openNearbyChest();  // Ouvre le coffre (la méthode déjà définie)
            } else {
                System.out.println("Aucun coffre à proximité pour interagir !");
            }
        });

        // Panel pour aligner le bouton à droite
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(interactionButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST); // Ajoute le bouton à droite

        // Ajout du bottomPanel en bas du mainPanel
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Ajout du mainPanel à la fenêtre principale
        add(mainPanel);

        // Ajout d'un écouteur d'événements pour détecter les pressions de touches
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                moveHero(e.getKeyCode());  // Déplace le héros en fonction de la touche pressée
            }
        });

        // Permet à la fenêtre de recevoir des événements clavier
        setFocusable(true);
        setVisible(true);
    }

    /**
     * Met à jour l'état du bouton d'interaction selon la position du héros.
     */
    /*public void updateChestButtonState() {
        boolean isNearChest = dashboard.isHeroNearChest();
        interactionButton.setEnabled(isNearChest);
    }*/

    /**
     * Déplace le héros en fonction de la touche pressée.
     * @param keyCode Le code de la touche pressée
     */
    private void moveHero(int keyCode) {
        Block currentPos = dashboard.getHero().getPosition();  // Récupère la position actuelle du héros
        Block newPos = currentPos;  // Par défaut, la nouvelle position est l'ancienne

        // Déterminer la nouvelle position en fonction de la touche pressée
        if (keyCode == KeyEvent.VK_LEFT && currentPos.getColumn() > 0) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() - 1);
            dashboard.getHero().moveLeft();
        } else if (keyCode == KeyEvent.VK_RIGHT && currentPos.getColumn() < dashboard.getMap().getColumnCount() - 1) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine(), currentPos.getColumn() + 1);
            dashboard.getHero().moveRight();
        } else if (keyCode == KeyEvent.VK_UP && currentPos.getLine() > 0) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() - 1, currentPos.getColumn());
            dashboard.getHero().moveUp();
        } else if (keyCode == KeyEvent.VK_DOWN && currentPos.getLine() < dashboard.getMap().getLineCount() - 1) {
            newPos = dashboard.getMap().getBlock(currentPos.getLine() + 1, currentPos.getColumn());
            dashboard.getHero().moveDown();
        }

        // Vérifier si la nouvelle position est valide (non bloquée)
        if (!dashboard.getMap().isBlocked(newPos)) {
            // Effacer l'ancienne position du héros avant de le déplacer
            dashboard.repaint(currentPos.getColumn() * 32, currentPos.getLine() * 32, 32, 32); 
            dashboard.getHero().setPosition(newPos);
            System.out.println("Héros déplacé à : " + newPos.getLine() + ", " + newPos.getColumn());
            // Redessiner la nouvelle position du héros
            dashboard.repaint(newPos.getColumn() * 32, newPos.getLine() * 32, 32, 32); 
        } else {
            System.out.println("Déplacement bloqué !");  // Si la position est bloquée, afficher un message
        }
    }


    /**
     * Méthode principale pour démarrer l'application et afficher l'interface graphique.
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
