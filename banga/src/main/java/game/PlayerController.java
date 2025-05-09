package game;

import javafx.animation.AnimationTimer;

public class PlayerController {
    private Player player;
    private double targetX, targetY;

    public PlayerController(Player player) {
        this.player = player;
        this.targetX = player.getX();
        this.targetY = player.getY();
        start();
    }

    public void setTarget(double x, double y) {
        this.targetX = x;
        this.targetY = y;
    }

    private void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                double currentX = player.getX();
                double currentY = player.getY();

                double dx = targetX - currentX - player.getWidth() / 2;
                double dy = targetY - currentY - player.getHeight() / 2;
                double distance = Math.sqrt(dx * dx + dy * dy);
                double speed = 1.5;

                if (distance > speed) {
                    double moveX = dx / distance * speed;
                    double moveY = dy / distance * speed;
                    player.setPosition(currentX + moveX, currentY + moveY);
                } else {
                    player.setPosition(targetX - player.getWidth() / 2, targetY - player.getHeight() / 2);
                }
            }
        }.start();
    }
}

