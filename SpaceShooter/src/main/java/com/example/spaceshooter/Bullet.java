package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    private double x, y;
    private boolean alive = true;

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean update() {
        y -= 10;
        return alive && y > 0;
    }

    public void kill() {
        alive = false;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(x, y, 5, 10);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
