package data.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestManager {
    private ArrayList<Quest> activeQuests;
    private int totalCoins; // ✅ Ajout d'un compteur de pièces pour suivre les récompenses
    private final Map<String, Integer> dynamicCounters = new HashMap<>();

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
        updateQuest("Éteindre les flammes", 1); // ou le nom exact que tu as donné
    }
    
    public void setRequiredAmount(String questName, int requiredAmount) {
        for (Quest quest : activeQuests) {
            if (quest.getName().equals(questName)) {
                quest.setRequiredAmount(requiredAmount);
                return;
            }
        }
    }
    
    public void clearAllQuests() {
        activeQuests.clear();
        System.out.println("🧹 Toutes les quêtes ont été supprimées.");
    }

    public void notifyQuestProgress(String type, int amount) {
        System.out.println("🔔 Appel à notifyQuestProgress(type = " + type + ", amount = " + amount + ")");

        for (Quest quest : activeQuests) {
            System.out.println("🧪 ➤ Quête : " + quest.getName() + " | Type = " + quest.getType() + " | Avancement = " + quest.getCurrentAmount() + "/" + quest.getRequiredAmount());

            if (!quest.isCompleted() && quest.getType().equals(type)) {
                System.out.println("✅ Mise à jour : " + quest.getName());
                quest.updateProgress(amount);
                System.out.println("📊 ➤ Nouveau total : " + quest.getCurrentAmount() + "/" + quest.getRequiredAmount());

                if (quest.isCompleted()) {
                    System.out.println("🏁 ✅ Quête complétée : " + quest.getName());
                }
            }
        }
    }



 





    // 🔥 Main interne pour tester QuestManager
    public static void main(String[] args) {
        System.out.println("🔹 Test de la classe QuestManager 🔹");

        QuestManager questManager = new QuestManager();

        // Ajouter des quêtes
        questManager.addQuest(new Quest("Chasseur de Squelettes", "Tue 3 squelettes", Quest.TYPE_KILL, 3, 100));
        questManager.addQuest(new Quest("Chasseur de Slimes", "Tue 5 slimes", Quest.TYPE_KILL, 5, 150));
        questManager.addQuest(new Quest("Collecteur de pièces", "Ramasse 10 pièces", Quest.TYPE_COLLECT, 10, 200));

        questManager.displayQuests();

        // Mise à jour de progression
        System.out.println("\n🔄 Mise à jour des quêtes...");
        questManager.updateQuest("Chasseur de Squelettes", 3);
        questManager.updateQuest("Chasseur de Slimes", 5);
        questManager.updateQuest("Collecteur de pièces", 10);

        questManager.displayQuests();

        // Réclamation des récompenses
        System.out.println("\n🎁 Réclamation des récompenses...");
        questManager.claimQuestReward("Chasseur de Squelettes");
        questManager.claimQuestReward("Chasseur de Slimes");
        questManager.claimQuestReward("Collecteur de pièces");

        // Vérification des pièces totales
        System.out.println("💰 Total des pièces du joueur : " + questManager.getTotalCoins());

        // Vérifier qu'on ne peut pas récupérer la récompense deux fois
        questManager.claimQuestReward("Chasseur de Squelettes");
    }
}
