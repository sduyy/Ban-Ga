package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

/**
 * Địch thường bay thẳng xuống, không dao động.
 */
public class FallingEnemy extends Enemy {
    private double x, y;
    private double speed = 1.0;

    public FallingEnemy(double x, double y, int wave) {
        super(x, y, wave);
        this.x = x;
        this.y = y;
    }

    @Override
    public void update() {
        y += speed;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.enemy, x, y, 40, 40);
    }

    public boolean isOutOfScreen(double screenHeight) {
        return y > screenHeight;
    }
}
