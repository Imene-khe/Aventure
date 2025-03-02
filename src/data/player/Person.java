package data.player;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.map.Block;

import java.awt.BorderLayout;
import java.awt.Image;

public class Person {
    private String name;
    private int id;
    private int health;
    private Block position;
    private ImageIcon image;

    

	// Constructeur pour les personnages mobiles
    public Person(String name, int id, int health, String imagePath, Block position) {
        this.name = name;
        this.id = id;
        this.health = health;
        // Utiliser un chemin relatif pour l'image
        this.image = new ImageIcon(getClass().getResource(imagePath)); // imagePath doit être relatif
        this.position=position;
    }

    public Person(String name, int id, int health) {
		// TODO Auto-generated constructor stub
    	 this.name = name;
         this.id = id;
         this.health = health;
	}

	public Person(String string, int i, int j, String imagePath) {
		// TODO Auto-generated constructor stub
		this.name = name;
        this.id = i;
        this.health = j;
        this.image = new ImageIcon(getClass().getResource(imagePath)); // imagePath doit être relatif
	}
	
	public Person(Block position, int health) {
		this.position=position;
		this.health=health;
	}
	
	public Person(Block position) {
		this.position=position;
	}
	

	// Méthode pour afficher l'info et l'image
    public void showPersonInfo() {
        // Créer une fenêtre JFrame pour afficher le personnage
        JFrame frame = new JFrame(name + " (" + id + ")");
        frame.setLayout(new BorderLayout());

        // Créer un JLabel pour afficher l'image
        JLabel imageLabel = new JLabel(image);  // Afficher l'image sur le JLabel

        // Créer un JPanel pour afficher l'information (nom et ID)
        JPanel infoPanel = new JPanel();
        infoPanel.add(new JLabel("Nom : " + name));
        infoPanel.add(new JLabel("ID : " + id));

        // Ajouter les éléments à la fenêtre
        frame.add(imageLabel, BorderLayout.CENTER);  // Afficher l'image au centre
        frame.add(infoPanel, BorderLayout.SOUTH);    // Afficher l'info en bas

        // Configurer la fenêtre
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public Block getPosition() {
		return position;
	}

	public void setPosition(Block position) {
		this.position = position;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

}
