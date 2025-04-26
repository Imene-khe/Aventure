package data.quest;
import org.apache.log4j.Logger;
import log.LoggerUtility;


import java.util.ArrayList;

public class QuestManager {
	private static final Logger logger = LoggerUtility.getLogger(QuestManager.class, "text");
    private ArrayList<Quest> activeQuests;
    private int totalCoins;  
    
    public QuestManager() {
        this.activeQuests = new ArrayList<>();
        this.totalCoins = 0;
    }

    public void addQuest(Quest quest) {
        activeQuests.add(quest);
        logger.info("Nouvelle quête ajoutée : " + quest.getName());
    }

    public void updateQuest(String questName, int amount) {
        for (Quest quest : activeQuests) {
        	logger.debug("Tentative de mise à jour de la quête : " + quest.getName());
        	if (quest.getName().trim().equalsIgnoreCase(questName.trim()) && !quest.isCompleted()) {
                quest.updateProgress(amount);
                logger.info("Progression de la quête " + quest.getName() + " : " + quest.getCurrentAmount() + "/" + quest.getRequiredAmount());
            }
        }
    }


    public void claimQuestReward(String questName) {
        for (Quest quest : activeQuests) {
            if (quest.getName().equals(questName)) {
                int reward = quest.claimReward();
                if (reward > 0) {
                    totalCoins += reward;
                    logger.info("Total des pièces après récompense : " + totalCoins);
                }
                return;
            }
        }
        logger.warn("Quête introuvable : " + questName);
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public ArrayList<Quest> getActiveQuests() {
        return activeQuests;
    }

    public void displayQuests() {
        for (Quest quest : activeQuests) {
            logger.debug("📌 Quêtes Actuelles :"+quest);
        }
    }
    
    public void notifyFlameExtinguished() {
        updateQuest("Éteindre les flammes", 1); 
    }
    
    public void setRequiredAmount(String questName, int requiredAmount) {
        for (Quest quest : activeQuests) {
            if (quest.getName().equals(questName)) {
                quest.setRequiredAmount(requiredAmount);
                return;
            }
        }
    }
    
    public void clearQuests() {
        activeQuests.clear();
        logger.info("Toutes les quêtes ont été supprimées.");
    }

    public void notifyQuestProgress(String type, int amount) {
        for (Quest quest : activeQuests) {
            if (!quest.isCompleted() && quest.getType().equals(type)) {
                quest.updateProgress(amount);
                if (quest.isCompleted()) {
                	logger.info("Quête complétée : " + quest.getName());
                }
            }
        }
    }
    
    public void loadCombatMapQuests() {
    	addQuest(new Quest("Survivre aux vagues d'ennemis", "Résiste à la première vague de monstres", Quest.TYPE_WAVE, 3, 0));
        addQuest(new Quest("Tuer le boss", "Terrasse le boss final", Quest.TYPE_KILL, 1, 0));
        logger.info("Quêtes de la CombatMap chargées.");
        }
    
    public boolean isQuestCompleted(String questName) {
        for (Quest quest : activeQuests) {
            if (quest.getName().equals(questName)) {
                return quest.isCompleted();
            }
        }
        return false;
    }

}
