package data.player;

import org.apache.log4j.Logger;
import log.LoggerUtility;


import data.map.Block;

public class Hero extends Person {
	private static final Logger logger = LoggerUtility.getLogger(Hero.class, "text");

    public Hero(Block startPosition, int health) {
        super(startPosition, health);
    }

    public Hero(Block position) {
        super(position);
       
    }

    public void takeDamage(int amount) {
        setHealth(Math.max(0, getHealth() - amount));
        logger.info("💥 Héros touché ! Vie restante : " + getHealth() + "%");
    }
}
