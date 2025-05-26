package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Lớp đại diện cho địch Mini-Boss (cỡ trung bình, xuất hiện khi SuperBoss triệu hồi).
 */
class MiniBossEnemy extends BossEnemy {

    private final int maxHP;

    /**
     * Tạo một Mini-Boss tại vị trí (x, y), với lượng máu bằng khoảng 20% máu tối đa của Boss Siêu Cấp.
     *
     * @param x tọa độ X
     * @param y tọa độ Y
     * @param superBossMaxHp máu tối đa của SuperBoss (dùng làm cơ sở tính máu MiniBoss)
     */
    public MiniBossEnemy(double x, double y, int superBossMaxHp) {
        super(x, y, 0); // wave = 0 để không ảnh hưởng bởi wave logic của BossEnemy
        this.maxHP = (int) (superBossMaxHp * 0.20);
        this.hp = this.maxHP;
        this.startX = x;
        this.baseY = y;
    }

    /**
     * Vẽ Mini-Boss lên màn hình, kèm thanh máu nhỏ phía trên.
     *
     * @param gc GraphicsContext để vẽ
     */
    @Override
    public void render(GraphicsContext gc) {
        double x = getX();
        double y = getY();
        gc.drawImage(Assets.miniBoss, x, y, 60, 60);

        // Thanh máu
        gc.setFill(Color.RED);
        gc.fillRect(x, y - 8, 60, 6);
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(x, y - 8, (hp / (double) maxHP) * 60, 6);
    }

    /**
     * Trả về máu tối đa của Mini-Boss.
     * @return giá trị máu tối đa
     */
    public int getMaxHp() {
        return maxHP;
    }
}
