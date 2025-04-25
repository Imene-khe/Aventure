package data.player;

import data.map.Block;

public abstract class Person {
    private String name;
    private int id;
    private int health;
    private Block position;

    public Person(String name, int id, int health, String imagePath, Block position) {
        this.name = name;
        this.id = id;
        this.health = health;
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
    }

    public Person(Block position, int health) {
        this.position = position;
        this.health = health;
    }

    public Person(Block position) {
        this.position = position;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public Block getPosition() { return position; }
    public void setPosition(Block position) { this.position = position; }

}
