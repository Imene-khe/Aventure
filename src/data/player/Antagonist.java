package data.player;


import data.map.Block;
import data.map.HostileMap;
import data.map.Map;

public class Antagonist extends Person {

    private int health;
    private int maxHealth; // ✅ nouveau champ
    private String enemyType;
    
    private int x, y; // Coordonnées pixels pour CombatMap

    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;

        if ("boss".equals(enemyType)) {
            this.maxHealth = 300;
        } else {
            this.maxHealth = 20;
        }

        this.health = this.maxHealth;

        if (startPosition != null) {
            this.x = startPosition.getColumn() * 50;
            this.y = startPosition.getLine() * 50;
        }
    }


    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }


  

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("🩸 " + enemyType + " prend " + damage + " dégâts. PV restants : " + health);
        if (health < 0) health = 0;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getX() { return x; }

    public int getY() { return y; }
    
    public void moveTowards(Block target, Map map) {
        Block current = getPosition();
        int dLine = target.getLine() - current.getLine();
        int dCol = target.getColumn() - current.getColumn();
        int nextLine = current.getLine();
        int nextCol = current.getColumn();

        if (Math.abs(dLine) > Math.abs(dCol)) {
            nextLine += Integer.compare(dLine, 0); // +1 ou -1
        } else if (dCol != 0) {
            nextCol += Integer.compare(dCol, 0);
        }

        Block nextBlock = map.getBlock(nextLine, nextCol);

        // ⛔ Interdiction d'entrer dans le shelter
        if (map instanceof HostileMap hostileMap && hostileMap.getShelterBlocks().contains(nextBlock)) {
            return;
        }

        if (!map.isBlocked(nextBlock)) {
            setPosition(nextBlock);
        }
    }
    
    public String getType() {
        return enemyType;
    }



}
