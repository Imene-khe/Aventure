package gui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.item.InventoryManager;
import data.map.Block;
import data.map.Map;
import data.player.Hero;

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
    
    //Gestion de l'avancement de l'histoire

    /**
     * Constructeur de la classe. Il initialise la fenêtre, les composants graphiques
     * et les actions du clavier.
     */
    public MainGUI() {
        super("Aventure - Déplacement du Héros");

        // Configuration de la fenêtre principale
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        
        // Initialisation de la carte et de l'affichage
        this.setMap(new Map(30, 30));
        this.dashboard = new GameDisplay();
        this.inventory = new InventoryManager();
        
        // Ajout des composants dans la fenêtre
        add(dashboard, BorderLayout.CENTER);
        add(inventory, BorderLayout.SOUTH);
        
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
     * Méthode principale pour démarrer l'application et afficher l'interface graphique.
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
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
}
