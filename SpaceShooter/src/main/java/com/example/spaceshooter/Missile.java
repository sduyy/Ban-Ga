package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

/**
 * Lớp đại diện cho tên lửa bắn ra bởi người chơi.
 * Tên lửa di chuyển lên và phát nổ khi chạm đến độ cao nhất định hoặc va chạm với địch,
 * gây sát thương diện rộng cho các kẻ địch trong bán kính vụ nổ.
 */
public class Missile {
    /** Tọa độ X hiện tại của tên lửa. */
    private double x;
    /** Tọa độ Y hiện tại của tên lửa. */
    private double y;
    /** Cờ cho biết tên lửa đã phát nổ hay chưa. */
    private boolean exploded = false;
    /** Bán kính vụ nổ của tên lửa. */
    private final int explosionRadius = 300;
    /** Người chơi sở hữu (bắn ra) tên lửa này. */
    private Player owner;
    /** Sát thương gây ra cho mỗi địch trong phạm vi nổ. */
    private int damage;
    /** Âm thanh phát ra khi tên lửa nổ. */
    private static final SoundFX missileExplode = new SoundFX("missile.wav");

    /**
     * Tạo một tên lửa tại vị trí (x, y) không có người sở hữu (chỉ dùng cho thử nghiệm).
     * @param x tọa độ X
     * @param y tọa độ Y
     */
    public Missile(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Tạo một tên lửa tại vị trí (x, y) với người chơi sở hữu.
     * @param x tọa độ X
     * @param y tọa độ Y
     * @param owner người chơi bắn tên lửa này
     */
    public Missile(double x, double y, Player owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.damage = 300;
    }

    /**
     * Cập nhật vị trí và trạng thái của tên lửa mỗi khung hình.
     * Nếu vượt quá độ cao cho phép thì tự động phát nổ.
     * @param enemies danh sách địch hiện tại
     * @param explosions danh sách hiệu ứng nổ để thêm hiệu ứng nếu cần
     * @param powerUps danh sách vật phẩm rơi ra từ địch
     * @return true nếu tên lửa vẫn còn hoạt động, false nếu đã nổ
     */
    public boolean update(ArrayList<Enemy> enemies, ArrayList<Explosion> explosions, ArrayList<PowerUp> powerUps) {
        if (exploded) return false;

        y -= 5;
        if (y <= 120) {
            explode(enemies, explosions, powerUps);
            return false;
        }
        return true;
    }

    /**
     * Kích hoạt vụ nổ của tên lửa, gây sát thương cho các địch trong vùng ảnh hưởng.
     * @param enemies danh sách địch để kiểm tra va chạm
     * @param explosions danh sách hiệu ứng nổ để thêm mới
     * @param powerUps danh sách vật phẩm tăng sức mạnh
     */
    public void explode(ArrayList<Enemy> enemies, ArrayList<Explosion> explosions, ArrayList<PowerUp> powerUps) {
        missileExplode.play();

        exploded = true;
        explosions.add(new Explosion(x - explosionRadius / 2.0, y - explosionRadius / 2.0, explosionRadius));

        ArrayList<Enemy> toRemove = new ArrayList<>();
        for (Enemy e : enemies) {
            double dx = x - (e.getX() + 20);
            double dy = y - (e.getY() + 20);
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= explosionRadius) {
                boolean dead = e.takeDamage(damage);
                if (dead) {
                    toRemove.add(e);
                    // Cộng điểm cho người chơi sở hữu tên lửa
                    if (owner != null) {
                        int score = (e instanceof SuperBossEnemy) ? 1000 : (e instanceof BossEnemy ? 500 : 100);
                        owner.addScore(score);
                    }
                    explosions.add(new Explosion(e.getX(), e.getY()));
                    GameScene.dropPowerUps(e);
                }
            }
        }

        enemies.removeAll(toRemove);
    }

    /**
     * Vẽ tên lửa lên màn hình nếu chưa phát nổ.
     * @param gc đối tượng GraphicsContext dùng để vẽ
     */
    public void render(GraphicsContext gc) {
        if (!exploded) {
            gc.drawImage(Assets.missile, x, y, 12, 30);
        }
    }

    /**
     * Trả về người sở hữu tên lửa.
     * @return người chơi sở hữu tên lửa này
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Trả về sát thương của tên lửa.
     * @return giá trị sát thương
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Trả về hoành độ (x) hiện tại của tên lửa.
     * @return giá trị x
     */
    public double getX() {
        return x;
    }

    /**
     * Trả về tung độ (y) hiện tại của tên lửa.
     * @return giá trị y
     */
    public double getY() {
        return y;
    }
}
