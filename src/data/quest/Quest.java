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

    public Quest(String name, String description, String type, int requiredAmount) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.requiredAmount = requiredAmount;
        this.currentAmount = 0;
        this.status = STATUS_EN_COURS;
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

    public String getStatusText() {
        return status.equals(STATUS_EN_COURS) ? "En cours" : "Terminée";
    }

    @Override
    public String toString() {
        return name + " (" + getStatusText() + "): " + description + " [" + currentAmount + "/" + requiredAmount + "]";
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getRequiredAmount() { return requiredAmount; }
    public int getCurrentAmount() { return currentAmount; }
    public String getStatus() { return status; }

    // 🔥 Main interne pour tester Quest
    public static void main(String[] args) {
        System.out.println("🔹 Test de la classe Quest 🔹");

        Quest quest = new Quest("Collecte de bois", "Ramasse 5 morceaux de bois", TYPE_COLLECT, 5);

        System.out.println(quest); // Affichage initial
        quest.updateProgress(2);
        System.out.println(quest); // Après collecte de 2 bois
        quest.updateProgress(3);
        System.out.println(quest); // Après collecte de 3 bois -> Terminé
        quest.updateProgress(1); // Tentative d'ajout après complétion (ne doit pas changer)
        System.out.println(quest);
    }
}
