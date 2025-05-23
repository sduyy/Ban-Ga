package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Explosion {
    private double x, y;
    private int duration = 15;
    private int radius;

    public Explosion(double x, double y) {
        this(x, y, 40);
    }

    public Explosion(double x, double y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.ORANGERED);
        gc.fillOval(x, y, radius, radius);

        gc.setFill(Color.YELLOW);
        gc.fillOval(x + radius * 0.2, y + radius * 0.2, radius * 0.6, radius * 0.6);

        gc.setFill(Color.WHITE);
        gc.fillOval(x + radius * 0.35, y + radius * 0.35, radius * 0.3, radius * 0.3);

        duration--;
    }

    public boolean isFinished() {
        return duration <= 0;
    }
}
