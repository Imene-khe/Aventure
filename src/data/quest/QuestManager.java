package data.quest;

import java.util.ArrayList;

public class QuestManager {
    private ArrayList<Quest> activeQuests;

    public QuestManager() {
        this.activeQuests = new ArrayList<>();
    }

    public void addQuest(Quest quest) {
        activeQuests.add(quest);
        System.out.println("📜 Nouvelle quête ajoutée : " + quest.getName());
    }

    public void updateQuest(String questName, int amount) {
        for (Quest quest : activeQuests) {
            if (quest.getName().equals(questName) && !quest.isCompleted()) {
                quest.updateProgress(amount);
            }
        }
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

    // 🔥 Main interne pour tester QuestManager
    public static void main(String[] args) {
        System.out.println("🔹 Test de la classe QuestManager 🔹");

        QuestManager questManager = new QuestManager();

        // Ajouter des quêtes
        questManager.addQuest(new Quest("Chasseur de Squelettes", "Tue 3 squelettes", Quest.TYPE_KILL, 3));
        questManager.addQuest(new Quest("Chasseur de Slimes", "Tue 5 slimes", Quest.TYPE_KILL, 5));
        questManager.addQuest(new Quest("Collecteur de pièces", "Ramasse 10 pièces", Quest.TYPE_COLLECT, 10));

        // Afficher les quêtes après ajout
        questManager.displayQuests();

        // Mise à jour de progression
        System.out.println("\n🔄 Mise à jour des quêtes...");
        questManager.updateQuest("Chasseur de Squelettes", 1);
        questManager.updateQuest("Chasseur de Slimes", 3);
        questManager.updateQuest("Collecteur de pièces", 5);

        // Afficher après mise à jour
        questManager.displayQuests();

        // Complétion des quêtes
        System.out.println("\n🔄 Finalisation des quêtes...");
        questManager.updateQuest("Chasseur de Squelettes", 2);
        questManager.updateQuest("Chasseur de Slimes", 2);
        questManager.updateQuest("Collecteur de pièces", 5);

        // Afficher après complétion
        questManager.displayQuests();
    }
}
