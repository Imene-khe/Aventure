package gui.animation;

import java.awt.Graphics;
import java.awt.Image;


public class HeroRenderer {

    private final HeroAnimator animator;
    private int x, y;
    private int spriteRow;
    private boolean flipped;

    public HeroRenderer(HeroAnimator animator, int startX, int startY) {
        this.animator = animator;
        this.x = startX;
        this.y = startY;
        this.spriteRow = 0;
        this.flipped = false;
    }

    public void draw(Graphics g) {
        animator.draw(g, x, y, spriteRow, flipped, 50);
    }

    // --- Mouvements simples ---
    public void goUp() {
        y -= 10;
        spriteRow = 2;
    }

    public void goDown() {
        y += 10;
        spriteRow = 0;
    }

    public void goLeft() {
        x -= 10;
        spriteRow = 3;
        flipped = true;
    }

    public void goRight() {
        x += 10;
        spriteRow = 3;
        flipped = false;
    }

    public void attack() {
        spriteRow = 4;
        // Retour à idle après 400 ms
        new javax.swing.Timer(400, e -> {
            spriteRow = 0;
        }).start();
    }

    // Getters si besoin
    public int getX() { return x; }
    public int getY() { return y; }
}
