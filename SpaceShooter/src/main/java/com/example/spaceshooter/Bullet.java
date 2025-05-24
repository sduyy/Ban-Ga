package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bullet {
    private double x, y;
    private boolean alive = true;
    private int damage;
    private Player owner;
    private Image image;

    // Constructor dùng cho đạn có owner & sprite riêng
    public Bullet(double x, double y, Image image, Player owner) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.owner = owner;
        this.damage = owner.getBulletDamage(); // Gán damage từ player
    }

    public boolean update() {
        y -= 10;
        return alive && y > 0;
    }

    public void kill() {
        alive = false;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, 5, 10);  // Dùng image đúng thay vì Assets.bullet cố định
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDamage() {
        return damage;
    }

    public Player getOwner() {
        return owner;
    }
}
