package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Explosion {
    private double x, y;
    private int duration = 15;

    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /*
    public void render(GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillOval(x, y, 40, 40);
        gc.setFill(Color.YELLOW);
        gc.fillOval(x + 10, y + 10, 20, 20);
        duration--;
    }*/

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.explosion, x, y, 40, 40);
        duration--;
    }

    public boolean isFinished() {
        return duration <= 0;
    }
}
