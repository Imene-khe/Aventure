package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.item.InventoryManager;
import data.map.Block;
import data.map.Map;

/**
 * Classe représentant l'interface graphique principale du jeu. 
 * Elle gère l'affichage de la carte, le déplacement du héros et l'inventaire du joueur.
 */
public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Affichage de la carte du jeu
    private GameDisplay dashboard;
    
    // Gestionnaire de l'inventaire
    private InventoryManager inventory;
    
    // Carte du jeu
    private Map map;
    
    // Ajout du bouton d'interraction (dialogue, ouverture...)
    private JButton interraction;

    /**
     * Constructeur de la classe. Il initialise la fenêtre, les composants graphiques
     * et les actions du clavier.
     */
    public MainGUI() {
        super("Aventure - Déplacement du Héros");

        // Configuration de la fenêtre principale
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        
        // Initialisation des composants
        this.setMap(new Map(30, 30));
        this.dashboard = new GameDisplay();
        this.inventory = new InventoryManager();

        // === Création du panneau principal ===
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dashboard, BorderLayout.CENTER); // Assure que GameDisplay est bien affiché au centre

        // === Création d'un panel contenant l'inventaire et le bouton ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inventory, BorderLayout.CENTER); // L'inventaire reste centré

        // === Ajout du bouton d'interaction ===
        JButton interactionButton = new JButton("Interagir");
        interactionButton.addActionListener(e -> dashboard.openNearbyChest());

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

    /*// Création du bouton et placement dans le layout
    interraction = new JButton("Interragir");
    interraction.setEnabled(false); // Désactivé par défaut
    interraction.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dashboard.openNearbyChest();
            updateChestButtonState(); // Vérifie si un autre coffre est proche
        }
    });
    JPanel controlPanel = new JPanel();
    controlPanel.add(interraction);
    add(controlPanel, BorderLayout.SOUTH);*/
 // Met à jour l'état du bouton selon la position du héros
    public void updateChestButtonState() {
        boolean isNearChest = dashboard.isHeroNearChest();
        interraction.setEnabled(isNearChest);
    }
    
    

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
            System.out.println(" Héros déplacé à : " + newPos.getLine() + ", " + newPos.getColumn());
            // Redessiner la nouvelle position du héros
            dashboard.repaint(newPos.getColumn() * 32, newPos.getLine() * 32, 32, 32); 
        } else {
            System.out.println(" Déplacement bloqué !");  // Si la position est bloquée, afficher un message
        }
    }
    
    
    
    /**
     * Retourne la carte actuelle du jeu.
     * @return La carte actuelle
     */
    public Map getMap() {
        return map;
    }

    /**
     * Définit une nouvelle carte pour le jeu.
     * @param map La nouvelle carte à utiliser
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Méthode principale pour démarrer l'application et afficher l'interface graphique.
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartScreen::new);
    }

    
}
