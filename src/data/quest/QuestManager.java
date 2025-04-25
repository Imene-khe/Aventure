package data.quest;

import java.util.ArrayList;

public class QuestManager {
    private ArrayList<Quest> activeQuests;
    private int totalCoins;  
    public QuestManager() {
        this.activeQuests = new ArrayList<>();
        this.totalCoins = 0;
    }

    public void addQuest(Quest quest) {
        activeQuests.add(quest);
        System.out.println("📜 Nouvelle quête ajoutée : " + quest.getName());
    }

    public void updateQuest(String questName, int amount) {
        for (Quest quest : activeQuests) {
            System.out.println("🔎 Tentative mise à jour de : " + quest.getName());
            if (quest.getName().trim().equalsIgnoreCase(questName.trim()) && !quest.isCompleted()) {
                quest.updateProgress(amount);
                System.out.println("✅ Progression : " + quest.getCurrentAmount() + "/" + quest.getRequiredAmount());
            }
        }
    }


    public void claimQuestReward(String questName) {
        for (Quest quest : activeQuests) {
            if (quest.getName().equals(questName)) {
                int reward = quest.claimReward();
                if (reward > 0) {
                    totalCoins += reward;
                    System.out.println("💰 Total des pièces après récompense : " + totalCoins);
                }
                return;
            }
        }
        System.out.println("❌ Quête introuvable : " + questName);
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public ArrayList<Quest> getActiveQuests() {
        return activeQuests;
    }

    public void displayQuests() {
        System.out.println("📌 Quêtes Actuelles :");
        for (Quest quest : activeQuests) {
            System.out.println(quest);
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
        System.out.println("🧹 Toutes les quêtes ont été supprimées.");
    }

    public void notifyQuestProgress(String type, int amount) {
        for (Quest quest : activeQuests) {
            if (!quest.isCompleted() && quest.getType().equals(type)) {
                quest.updateProgress(amount);
                if (quest.isCompleted()) {
                    System.out.println("🏁 ✅ Quête complétée : " + quest.getName());
                }
            }
        }
    }
    
    public void loadCombatMapQuests() {
    	addQuest(new Quest("Survivre aux vagues d'ennemis", "Résiste à la première vague de monstres", Quest.TYPE_WAVE, 3, 0));
        addQuest(new Quest("Tuer le boss", "Terrasse le boss final", Quest.TYPE_KILL, 1, 0));
        System.out.println("🆕 Quêtes de la CombatMap chargées.");
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
