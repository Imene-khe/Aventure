package test.unit;

import org.junit.Test;
import static org.junit.Assert.*;

import data.quest.Quest;
import data.quest.QuestManager;

public class TestQuestManager {

    @Test
    public void testAddQuest() {
        QuestManager questManager = new QuestManager();
        Quest quest = new Quest("Trouver l'orbe", "Récupère l'orbe sacré", "find", 1, 0);

        questManager.addQuest(quest);

        assertEquals(1, questManager.getActiveQuests().size());
        assertEquals("Trouver l'orbe", questManager.getActiveQuests().get(0).getName());
    }

    @Test
    public void testUpdateQuestProgress() {
        QuestManager questManager = new QuestManager();
        Quest quest = new Quest("Éteindre les flammes", "Éteins toutes les flammes", "extinguish", 3, 0);
        questManager.addQuest(quest);

        questManager.updateQuest("Éteindre les flammes", 1);

        assertEquals(1, questManager.getActiveQuests().get(0).getCurrentAmount());
        assertFalse(questManager.getActiveQuests().get(0).isCompleted());

        questManager.updateQuest("Éteindre les flammes", 2);

        assertEquals(3, questManager.getActiveQuests().get(0).getCurrentAmount());
        assertTrue(questManager.getActiveQuests().get(0).isCompleted());
    }

    @Test
    public void testClaimQuestReward() {
        QuestManager questManager = new QuestManager();
        Quest quest = new Quest("Tuer 3 monstres", "Tue 3 ennemis", "kill", 3, 100); // ✅ Reward de 100 pièces
        questManager.addQuest(quest);

        questManager.updateQuest("Tuer 3 monstres", 3); // ✅ On complète la quête

        int coinsBefore = questManager.getTotalCoins();
        questManager.claimQuestReward("Tuer 3 monstres");
        int coinsAfter = questManager.getTotalCoins();

        assertTrue(coinsAfter > coinsBefore); // ✅ Cette fois on a gagné des pièces !
    }




    @Test
    public void testClearQuests() {
        QuestManager questManager = new QuestManager();
        questManager.addQuest(new Quest("Quête 1", "Description 1", "type1", 1, 0));
        questManager.addQuest(new Quest("Quête 2", "Description 2", "type2", 2, 0));

        questManager.clearQuests();

        assertEquals(0, questManager.getActiveQuests().size());
    }
}
