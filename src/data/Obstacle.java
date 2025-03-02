package data;

public class Obstacle {
    private String type;
    private boolean isDestructible; /*est ce qu'il peut casser l'objet ou pas */


    public Obstacle(String type, boolean isDestructible) {
        this.type = type;
        this.isDestructible = isDestructible;
    }

    public String getType() {
        return type;
    }

    public boolean isDestructible() {
        return isDestructible;
    }

    @Override
    public String toString() {
        return "Obstacle{type='" + type + "', isDestructible=" + isDestructible + "}";
    }
}
