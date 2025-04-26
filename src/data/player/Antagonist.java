package data.player;

import org.apache.log4j.Logger;
import log.LoggerUtility;
import data.map.Block;
import data.map.HostileMap;
import data.map.Map;

public class Antagonist extends Person {
	
	private static final Logger logger = LoggerUtility.getLogger(Antagonist.class, "text");
    private int health;
    private int maxHealth; 
    private String enemyType;
    
    public Antagonist(Block startPosition, String enemyType, EnemyImageManager imageManager) {
        super(startPosition);
        this.enemyType = enemyType;

        if ("boss".equals(enemyType)) {
            this.maxHealth = 300;
        } else {
            this.maxHealth = 20;
        }
        this.health = this.maxHealth;
    }


    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }


    public void takeDamage(int damage) {
        health -= damage;
        logger.info(enemyType + " prend " + damage + " dégâts. Points de vie restants : " + health);
        if (health < 0) health = 0;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void moveTowards(Block target, Map map) {
        Block current = getPosition();
        int dLine = target.getLine() - current.getLine();
        int dCol = target.getColumn() - current.getColumn();
        int nextLine = current.getLine();
        int nextCol = current.getColumn();

        if (Math.abs(dLine) > Math.abs(dCol)) {
            nextLine += Integer.compare(dLine, 0);
        } else if (dCol != 0) {
            nextCol += Integer.compare(dCol, 0);
        }

        Block nextBlock = map.getBlock(nextLine, nextCol);

        if (map.isShelterBlock(nextBlock)) {
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
