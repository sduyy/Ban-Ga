package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

class MiniBossEnemy extends BossEnemy {

    public MiniBossEnemy(double x, double y, int superBossMaxHp) {
        super(x, y, 0);
        this.hp = (int)(superBossMaxHp * 0.20);
        this.startX = x;
        this.baseY = y;
    }

    @Override
    public void render(GraphicsContext gc) {
        double x = getX();
        double y = getY();
        gc.drawImage(Assets.miniBoss, x, y, 60, 60);
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(x, y - 8, 60, 6);
        gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
        gc.fillRect(x, y - 8, (hp / (double) getMaxHp()) * 60, 6);
    }

    private int getMaxHp() {
        return (int)(GameScene.enemies.stream()
                .filter(e -> e instanceof SuperBossEnemy)
                .mapToInt(e -> ((SuperBossEnemy) e).getMaxHp())
                .max().orElse(1000) * 0.20);
    }
}
