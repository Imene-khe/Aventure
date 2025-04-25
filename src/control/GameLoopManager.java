package control;

import gui.GameDisplay;
import gui.MainGUI;

public class GameLoopManager implements Runnable {

    private static GameLoopManager instance;
    private Thread gameLoopThread;
    private boolean running = false;
    private GameDisplay display;
    private GameController controller;
    private int heroDamageCooldown = 0;
    private int hostileEnemyMoveTimer = 0;
    private final int hostileEnemyMoveDelay = 1000; // 1 seconde
    private int bossAttackTimer = 0;
    private int bossAttackDelay = 10000; 


    private GameLoopManager() {
        // Constructeur privé : Singleton
    }

    public static GameLoopManager getInstance() {
        if (instance == null) {
            instance = new GameLoopManager();
        }
        return instance;
    }

    public void start() {
        if (running) return;

        running = true;
        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // 🌀 Rafraîchir toutes les animations et updates ici
                onGameTick();

                // 💤 Pause entre chaque frame
                Thread.sleep(40); // ~25 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onGameTick() {
        if (!MainGUI.getInstance().isDialogueActive()) {
            hostileEnemyMoveTimer += 40;
            if (hostileEnemyMoveTimer >= hostileEnemyMoveDelay) {
                hostileEnemyMoveTimer = 0;
                controller.moveEnemiesTowardsHero(); // ✅ les ennemis bougent uniquement hors dialogue
            }
        }

        bossAttackTimer += 40;
        if (bossAttackTimer >= bossAttackDelay) {
            bossAttackTimer = 0;
            controller.bossSpecialAttack(); // ✅ attaque spéciale à intervalle régulé
        }

        if (controller != null && display != null) {
            if (heroDamageCooldown > 0) {
                heroDamageCooldown -= 40;
                if (heroDamageCooldown <= 0) {
                    controller.setCanTakeDamage(true);
                }
            }

            controller.onRepaintTick(); // 🔁 projectiles, boss, etc.
            controller.checkEnemyCollision(); 
            if (display.getCoinAnimator() != null) display.getCoinAnimator().update(40);
            if (display.getFlameAnimator() != null) display.getFlameAnimator().update(40);
            display.repaint();
        }
    }


    public void setGameDisplay(GameDisplay display) {
        this.display = display;
    }

    public void setGameController(GameController controller) {
        this.controller = controller;
    }
    

    public void startHeroDamageCooldown() {
    	this.heroDamageCooldown = 1000; 
    }
}
