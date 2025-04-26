package data.map;

public class Obstacle {
    private String type;
    public Obstacle(String type, boolean isDestructible) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Obstacle{type='" + type + "}";
    }
}
