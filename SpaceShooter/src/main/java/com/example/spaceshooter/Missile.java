package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class Missile {
    private double x, y;
    private boolean exploded = false;
    private final int explosionRadius = 200;
    private Player owner;
    private int damage;
    private static final SoundFX missileExplode = new SoundFX("missile.wav");

    public Missile(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Missile(double x, double y, Player owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.damage = 200;
    }

    public boolean update(ArrayList<Enemy> enemies, ArrayList<Explosion> explosions, ArrayList<PowerUp> powerUps) {
        if (exploded) return false;

        y -= 5;
        if (y <= 120) {
            explode(enemies, explosions, powerUps);
            return false;
        }
        return true;
    }

    public void explode(ArrayList<Enemy> enemies, ArrayList<Explosion> explosions, ArrayList<PowerUp> powerUps) { // Sửa: cho phép gọi từ bên ngoài
        missileExplode.play();

        exploded = true;
        explosions.add(new Explosion(x - explosionRadius / 2.0, y - explosionRadius / 2.0, explosionRadius));

        ArrayList<Enemy> toRemove = new ArrayList<>();
        for (Enemy e : enemies) {
            double dx = x - (e.getX() + 20);
            double dy = y - (e.getY() + 20);
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= explosionRadius) {
                boolean dead;
                dead = e.takeDamage(200);
                if (dead) {
                    toRemove.add(e);
                    owner.addScore((e instanceof SuperBossEnemy) ? 1000 : (e instanceof BossEnemy ? 500 : 100)); // Sửa: cộng điểm cho chủ tên lửa
                    explosions.add(new Explosion(e.getX(), e.getY()));
                    GameScene.dropPowerUps(e);
                }
            }
        }

        enemies.removeAll(toRemove);
    }

    public void render(GraphicsContext gc) {
        if (!exploded) {
            gc.drawImage(Assets.missile, x, y, 12, 30);
        }
    }

    public Player getOwner() {
        return owner;
    }
    public int getDamage() {
        return damage;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
