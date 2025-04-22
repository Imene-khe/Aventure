package control;

import data.map.Block;
import data.map.HostileMap;
import data.player.Antagonist;
import data.player.Hero;
import data.quest.Quest;
import data.quest.QuestManager;
import gui.GameDisplay;
import gui.MainGUI;

import java.awt.Point;
import java.util.Iterator;

public class CombatController {

    private GameDisplay display;
    private HostileMap hostileMap;
    private Hero hero;
    private GameController gameController;

    public CombatController(GameDisplay display, GameController gameController) {
        this.display = display;
        this.hostileMap = (HostileMap) display.getHostileMap();
        this.hero = display.getHero();
        this.gameController = gameController;
    }

    public void handleClick(Point mousePoint) {
        if (!display.isInHostileMap()) return;

        Block heroBlock = hero.getPosition();

        Iterator<Antagonist> it = hostileMap.getAntagonistList().iterator();
        boolean enemyKilled = false;

        while (it.hasNext()) {
            Antagonist enemy = it.next();
            Block enemyBlock = enemy.getPosition();

            if (gameController.isAdjacent(heroBlock, enemyBlock)) {
                System.out.println("🗡️ Ennemi adjacent trouvé sur " + enemyBlock + ", attaque !");
                enemy.takeDamage(50);

                if (enemy.isDead()) {
                    it.remove();
                    MainGUI.getInstance().getQuestManager().displayQuests(); // ✅ Affiche les quêtes et progression en console
                    MainGUI.getInstance().getQuestManager().updateQuest("Chasseur de têtes", 1);
                    System.out.println("☠️ Ennemi tué sur " + enemyBlock);
                }

                enemyKilled = true;
                break; // Tu veux tuer qu’un seul ennemi à la fois
            }
        }

        if (!enemyKilled) {
            System.out.println("🔍 Aucun ennemi adjacent trouvé.");
        }

        display.repaint();
    }





    public void attack(Block targetBlock) {
        System.out.println("🔍 Ennemis dans hostileMap : " + hostileMap.getAntagonistList().size());

        for (Antagonist enemy : hostileMap.getAntagonistList()) {
            System.out.println("➡️ Ennemi sur : " + enemy.getPosition());

            if (enemy.getPosition().equals(targetBlock)) {
                System.out.println("🎯 ENNEMI TOUCHÉ !");
                enemy.takeDamage(50);

                if (enemy.isDead()) {
                    System.out.println("💀 Ennemi MORT !");
                    
                    // ✅ Supprime l'ennemi
                    hostileMap.getAntagonistTypes().remove(enemy); // même si non utilisé, garde-le pour cohérence
                    hostileMap.getAntagonistList().remove(enemy);

                    // ✅ Mise à jour dynamique de la quête "Chasseur de têtes"
                    MainGUI.getInstance().getQuestManager().notifyQuestProgress(Quest.TYPE_KILL, 1);
                    QuestManager qm = MainGUI.getInstance().getQuestManager();
                    qm.displayQuests(); // ✅ Affiche toutes les quêtes dans la console avec progression

                    display.repaint();
                }
                return;
            }
        }

        System.out.println("❌ Aucun ennemi trouvé sur ce bloc !");
    }




    



}
