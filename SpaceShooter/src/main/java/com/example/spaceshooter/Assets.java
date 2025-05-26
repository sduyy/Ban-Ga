package com.example.spaceshooter;

import javafx.scene.image.Image;

/**
 * Quản lý và tải các tài nguyên hình ảnh của trò chơi (các hình ảnh cho tàu, đạn, hiệu ứng, v.v.).
 */
public class Assets {
    /** Hình ảnh tàu vũ trụ của người chơi 1. */
    public static Image player;
    /** Hình ảnh tàu vũ trụ của người chơi 2 (trong chế độ 2 người). */
    public static Image player2;
    /** Hình ảnh tàu địch thường. */
    public static Image enemy;
    /** Hình ảnh đạn do người chơi bắn. */
    public static Image bullet;
    /** Hình ảnh đạn do địch thường bắn. */
    public static Image enemyBullet;
    /** Hình ảnh hiệu ứng nổ (sử dụng khi tạo vụ nổ). */
    public static Image explosion;
    /** Hình ảnh đạn tên lửa của người chơi. */
    public static Image missile;
    /** Hình ảnh biểu tượng vật phẩm tăng máu (HEALTH). */
    public static Image powerUpHealth;
    /** Hình ảnh biểu tượng vật phẩm tăng tốc độ bắn (ENERGY). */
    public static Image powerUpEnergy;
    /** Hình ảnh biểu tượng vật phẩm tăng lượng đạn bắn (AMMO). */
    public static Image powerUpAmmo;
    /** Hình ảnh biểu tượng vật phẩm tăng sát thương đạn (DAMAGE). */
    public static Image powerUpDamage;
    /** Hình ảnh biểu tượng vật phẩm tăng số tên lửa (ROCKET). */
    public static Image powerUpRockets;
    /** Hình ảnh địch Boss thường. */
    public static Image bossEnemy;
    /** Hình ảnh đạn do Boss thường bắn. */
    public static Image bossBullet;
    /** Hình ảnh địch Boss Siêu Cấp (trùm cuối). */
    public static Image superBossEnemy;
    /** Hình ảnh đạn do Boss Siêu Cấp bắn. */
    public static Image superBossBullet;
    /** Hình ảnh địch Mini-Boss. */
    public static Image miniBoss;
    /** Hình ảnh đạn do Mini-Boss bắn. */
    public static Image miniBossBullet;

    /**
     * Tải tất cả các hình ảnh từ tệp tài nguyên và gán vào các biến tương ứng.
     * Phương thức này cần được gọi một lần khi khởi động game để chuẩn bị sẵn các hình ảnh.
     */
    public static void load() {
        player = new Image(Assets.class.getResource("/assets/images/player_ship.png").toExternalForm());
        enemy = new Image(Assets.class.getResource("/assets/images/enemy1.png").toExternalForm());
        bullet = new Image(Assets.class.getResource("/assets/images/bullet.png").toExternalForm());
        enemyBullet = new Image(Assets.class.getResource("/assets/images/enemy_bullet.png").toExternalForm());
        explosion = new Image(Assets.class.getResource("/assets/images/explosion.png").toExternalForm());
        missile = new Image(Assets.class.getResource("/assets/images/missile.png").toExternalForm());
        powerUpHealth = new Image(Assets.class.getResource("/assets/images/Powerup_Health.png").toExternalForm());
        powerUpEnergy = new Image(Assets.class.getResource("/assets/images/Powerup_Energy.png").toExternalForm());
        powerUpAmmo = new Image(Assets.class.getResource("/assets/images/Powerup_Ammo.png").toExternalForm());
        powerUpDamage = new Image(Assets.class.getResource("/assets/images/Powerup_Damage.png").toExternalForm());
        powerUpRockets = new Image(Assets.class.getResource("/assets/images/Powerup_Rockets.png").toExternalForm());
        bossEnemy = new Image(Assets.class.getResource("/assets/images/boss_enermy.png").toExternalForm());
        bossBullet = new Image(Assets.class.getResource("/assets/images/Boss_enermy_bullet.png").toExternalForm());
        superBossEnemy = new Image(Assets.class.getResource("/assets/images/super_boss_enermy.png").toExternalForm());
        superBossBullet = new Image(Assets.class.getResource("/assets/images/Super_boss_enermy_bullet.png").toExternalForm());
        miniBoss = new Image(Assets.class.getResource("/assets/images/miniboss.png").toExternalForm());
        player2 = new Image(Assets.class.getResource("/assets/images/player_ship_2.png").toExternalForm());
    }
}
