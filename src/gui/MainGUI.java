package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.item.Chest;
import data.item.InventoryManager;
import data.map.Block;


public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // Affichage et gestion de la carte du jeu
    private GameDisplay dashboard;

    // Gestionnaire de l'inventaire
    private InventoryManager inventory;

    // Ajout du bouton d'interaction (dialogue, ouverture...)
    private JButton interactionButton;
    private boolean isInteracting;

    /**
     * Constructeur de la classe. Il initialise la fenêtre, les composants graphiques
     * et les actions du clavier.
     */
    public MainGUI() {
        super("Aventure - Déplacement du Héros");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        this.dashboard = new GameDisplay();
        this.inventory = new InventoryManager();
        isInteracting = false;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dashboard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inventory, BorderLayout.CENTER);

        interactionButton = new JButton("Interagir");
        interactionButton.addActionListener(e -> {
            if (isInteracting) {
                return;  // Empêche de débuter une nouvelle interaction si déjà en cours
            }

            isInteracting = true;  // Début d'une interaction

         // Appel de la méthode pour ouvrir le coffre si à proximité
            Chest chest = dashboard.openNearbyChest(); // Récupère le coffre ouvert
            

            if (chest != null) {
                System.out.println("Coffre ouvert à cette position !");

                ChestUIManager chestUIManager = new ChestUIManager();
                chestUIManager.displayChestContents(chest); // Passe le coffre en paramètre

            } else {
                System.out.println("Aucun coffre à proximité pour interagir !");
            }


            // Réinitialiser isInteracting à false après l'interaction
            isInteracting = false;
            
            // Redonne le focus à la fenêtre pour permettre les déplacements après l'interaction
            requestFocusInWindow();
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(interactionButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                moveHero(e.getKeyCode());
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    /**
     * Déplace le héros en fonction de la touche pressée.
     * @param keyCode Le code de la touche pressée
     */
    public void moveHero(int keyCode) {
        // Si le héros est en interaction, on ne bloque pas son mouvement
        if (isInteracting) {
            System.out.println("Interagir en cours, mais mouvement possible...");
            return;  // Le mouvement est possible, mais l'interaction doit être gérée avant
        }

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
            System.out.println("Déplacement bloqué !");
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
