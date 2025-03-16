package data.quest;

public class Quest {
    public static final String TYPE_COLLECT = "collect";
    public static final String TYPE_KILL = "kill";
    public static final String TYPE_FIND = "find";

    public static final String STATUS_EN_COURS = "en_cours";
    public static final String STATUS_TERMINEE = "terminee";

    private String name;
    private String description;
    private String type;
    private int requiredAmount;
    private int currentAmount;
    private String status;
    private int reward; // ✅ Ajout d'une récompense
    private boolean rewardClaimed; // ✅ Empêcher de réclamer plusieurs fois la récompense

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
        if (status.equals(STATUS_EN_COURS)) {
            currentAmount += amount;
            if (currentAmount >= requiredAmount) {
                currentAmount = requiredAmount;
                status = STATUS_TERMINEE;
                System.out.println("✔ Quête terminée : " + name);
            }
        }
    }

    public boolean isCompleted() {
        return status.equals(STATUS_TERMINEE);
    }

    public int claimReward() {
        if (isCompleted() && !rewardClaimed) {
            rewardClaimed = true;
            System.out.println("🎁 Récompense obtenue pour la quête : " + name + " => +" + reward + " pièces !");
            return reward;
        } else if (rewardClaimed) {
            System.out.println("❌ Récompense déjà obtenue pour la quête : " + name);
        } else {
            System.out.println("⚠️ La quête " + name + " n'est pas encore terminée !");
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

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getRequiredAmount() { return requiredAmount; }
    public int getCurrentAmount() { return currentAmount; }
    public String getStatus() { return status; }
    public int getReward() { return reward; }
    public boolean isRewardClaimed() { return rewardClaimed; }

    // 🔥 Main interne pour tester Quest
    public static void main(String[] args) {
        System.out.println("🔹 Test de la classe Quest 🔹");

        Quest quest = new Quest("Collecte de bois", "Ramasse 5 morceaux de bois", TYPE_COLLECT, 5, 50);

        System.out.println(quest);
        quest.updateProgress(2);
        System.out.println(quest);
        quest.updateProgress(3);
        System.out.println(quest);

        // Tentative de récupérer la récompense
        quest.claimReward();
        quest.claimReward(); // ✅ Ne doit pas donner la récompense une seconde fois
    }
}
