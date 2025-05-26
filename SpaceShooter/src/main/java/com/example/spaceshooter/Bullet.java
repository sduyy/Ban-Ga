package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Lớp đại diện cho viên đạn do người chơi bắn ra trong trò chơi.
 */
public class Bullet {
    /** Tọa độ X của viên đạn (trục ngang). */
    private double x;
    /** Tọa độ Y của viên đạn (trục dọc). */
    private double y;
    /** Trạng thái hoạt động của viên đạn (true nếu còn tồn tại, false nếu đã bị hủy). */
    private boolean alive = true;
    /** Sát thương gây ra bởi viên đạn. */
    private int damage;
    /** Người chơi đã bắn ra viên đạn này. */
    private Player owner;
    /** Hình ảnh đại diện cho viên đạn. */
    private Image image;

    /**
     * Tạo viên đạn mới tại vị trí (x, y) với hình ảnh và người chơi sở hữu cụ thể.
     * @param x tọa độ X ban đầu của viên đạn
     * @param y tọa độ Y ban đầu của viên đạn
     * @param image hình ảnh của viên đạn
     * @param owner đối tượng người chơi đã bắn viên đạn này
     */
    public Bullet(double x, double y, Image image, Player owner) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.owner = owner;
        this.damage = owner.getBulletDamage();  // Lấy sát thương từ người chơi
    }

    /**
     * Cập nhật vị trí của viên đạn. Đạn bay thẳng lên trên.
     * @return true nếu viên đạn vẫn đang hoạt động trong màn hình, ngược lại false
     */
    public boolean update() {
        y -= 10;
        return alive && y > 0;
    }

    /**
     * Đánh dấu viên đạn là đã bị hủy (không còn tồn tại trong game).
     */
    public void kill() {
        alive = false;
    }

    /**
     * Vẽ viên đạn lên canvas trò chơi.
     * @param gc đối tượng GraphicsContext để vẽ hình
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, 5, 10);  // Kích thước mặc định 5x10
    }

    /** @return tọa độ X hiện tại của viên đạn */
    public double getX() {
        return x;
    }

    /** @return tọa độ Y hiện tại của viên đạn */
    public double getY() {
        return y;
    }

    /** @return sát thương mà viên đạn gây ra */
    public int getDamage() {
        return damage;
    }

    /** @return người chơi đã bắn ra viên đạn này */
    public Player getOwner() {
        return owner;
    }
}
