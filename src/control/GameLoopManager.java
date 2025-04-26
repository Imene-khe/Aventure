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
    private final int hostileEnemyMoveDelay = 1000; 
    private int bossAttackTimer = 0;
    private int bossAttackDelay = 10000; 


    private GameLoopManager() {
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
                onGameTick();
                Thread.sleep(40); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onGameTick() {
        if (MainGUI.getInstance() == null || !MainGUI.getInstance().isDialogueActive()) {
            // ✅ Si MainGUI est null (test manuel) ou dialogue inactif (jeu normal) ➔ On continue
            
            hostileEnemyMoveTimer += 40;
            if (hostileEnemyMoveTimer >= hostileEnemyMoveDelay) {
                hostileEnemyMoveTimer = 0;
                if (controller != null) {
                    controller.moveEnemiesTowardsHero(); 
                }
            }
        }

        bossAttackTimer += 40;
        if (bossAttackTimer >= bossAttackDelay) {
            bossAttackTimer = 0;
            if (controller != null) {
                controller.bossSpecialAttack(); 
            }
        }

        if (controller != null && display != null) {
            if (heroDamageCooldown > 0) {
                heroDamageCooldown -= 40;
                if (heroDamageCooldown <= 0) {
                    controller.setCanTakeDamage(true);
                }
            }

            controller.onRepaintTick(); 
            controller.checkEnemyCollision(); 

            if (display.getCoinAnimator() != null) {
                display.getCoinAnimator().update(40);
            }
            if (display.getFlameAnimator() != null) {
                display.getFlameAnimator().update(40);
            }
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
