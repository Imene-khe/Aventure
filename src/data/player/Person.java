package data.player;

import data.map.Block;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;

/**
 * Classe abstraite de base pour tous les personnages du jeu.
 */
public abstract class Person {
    private String name;
    private int id;
    private int health;
    private Block position;
    private ImageIcon image;

    // Constructeurs
    public Person(String name, int id, int health, String imagePath, Block position) {
        this.name = name;
        this.id = id;
        this.health = health;
        this.image = new ImageIcon(getClass().getResource(imagePath)); // imagePath doit être relatif
        this.position = position;
    }

    public Person(String name, int id, int health) {
        this.name = name;
        this.id = id;
        this.health = health;
    }

    public Person(String name, int id, int health, String imagePath) {
        this.name = name;
        this.id = id;
        this.health = health;
        this.image = new ImageIcon(getClass().getResource(imagePath)); // imagePath doit être relatif
    }

    public Person(Block position, int health) {
        this.position = position;
        this.health = health;
    }

    public Person(Block position) {
        this.position = position;
    }


    // Autres méthodes (gestion des personnages)
    public void showPersonInfo() {
        JFrame frame = new JFrame(name + " (" + id + ")");
        frame.setLayout(new BorderLayout());

        JLabel imageLabel = new JLabel(image);  // Afficher l'image sur le JLabel

        JPanel infoPanel = new JPanel();
        infoPanel.add(new JLabel("Nom : " + name));
        infoPanel.add(new JLabel("ID : " + id));

        frame.add(imageLabel, BorderLayout.CENTER);
        frame.add(infoPanel, BorderLayout.SOUTH);

        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public Block getPosition() { return position; }
    public void setPosition(Block position) { this.position = position; }

    public ImageIcon getImage() { return image; }
    public void setImage(ImageIcon image) { this.image = image; }
}
