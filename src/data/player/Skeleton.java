package data.player;

public class Skeleton {
    private int health;
    private int position;

    public Skeleton(int health) {
        this.health = health;
        this.position = 10; // Position initiale
    }

    public void move() {
        position--; // Se rapproche du héros
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public int getHealth() {
        return health;
    }

    public int getPosition() {
        return position;
    }
}
