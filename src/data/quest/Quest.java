package data.quest;

import org.apache.log4j.Logger;
import log.LoggerUtility;


public class Quest {
    public static final String TYPE_COLLECT = "collect";
    public static final String TYPE_KILL = "kill";
    public static final String TYPE_FIND = "find";
    public static final String TYPE_WAVE = "wave";
    public static final String STATUS_EN_COURS = "en_cours";
    public static final String STATUS_TERMINEE = "terminee";

    private static final Logger logger = LoggerUtility.getLogger(Quest.class, "text");

    private String name;
    private String description;
    private String type;
    private int requiredAmount;
    private int currentAmount;
    private String status;
    private int reward; 
    private boolean rewardClaimed; 

    public Quest(String name, String description, String type, int requiredAmount, int reward) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.requiredAmount = requiredAmount;
        this.currentAmount = 0;
        this.status = STATUS_EN_COURS;
        this.reward = reward;
        this.rewardClaimed = false;
    }

    public void updateProgress(int amount) {
        logger.info("updateProgress() appelé pour " + name + " | amount = " + amount + ", current = " + currentAmount);
        if (status.equals(STATUS_EN_COURS)) {
            currentAmount += amount;
            logger.info("Nouveau currentAmount pour " + name + " : " + currentAmount);
            if (currentAmount >= requiredAmount) {
                currentAmount = requiredAmount;
                status = STATUS_TERMINEE;
                logger.info("Quête terminée : " + name);            }
        } else {
        	logger.warn("Tentative de mise à jour d'une quête déjà terminée : " + name);
        	}
    }

    public void setRequiredAmount(int amount) {
        this.requiredAmount = amount;
    }


    public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}

	public boolean isCompleted() {
        return status.equals(STATUS_TERMINEE);
    }

    public int claimReward() {
        if (isCompleted() && !rewardClaimed) {
            rewardClaimed = true;
            logger.info("Récompense obtenue pour la quête : " + name + ", gain : " + reward + " pièces.");
            return reward;
        } else if (rewardClaimed) {
        	logger.warn("Récompense déjà obtenue pour la quête : " + name);
        	} else {
        		logger.warn("La quête " + name + " n'est pas encore terminée.");
        		}
        return 0;
    }

    public String getStatusText() {
        return status.equals(STATUS_EN_COURS) ? "En cours" : "Terminée";
    }

    @Override
    public String toString() {
        return name + " (" + getStatusText() + "): " + description + " [" + currentAmount + "/" + requiredAmount + "] - Récompense : " + reward + " pièces";
    }

    public String getName() {
    	return name;
    }
    public String getDescription() {
    	return description; 
    }
    public String getType() {
    	return type; 
    }
    public int getRequiredAmount() {
    	return requiredAmount;
    }
    public int getCurrentAmount() {
    	return currentAmount; 
    }
    public String getStatus() {
    	return status; 
    }
    public int getReward() {
    	return reward; 
    }
    public boolean isRewardClaimed() {
    	return rewardClaimed; 
    }

   
}
