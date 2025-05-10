
package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class Player {
    private double x, y;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double dx) {
        x += dx;
        x = Math.max(0, Math.min(760, x));
    }

    public Bullet shoot() {
        return new Bullet(x + 20, y);
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.player, x, y, 40, 40);
    }
}
