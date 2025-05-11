package com.example.spaceshooter;

import javafx.scene.image.Image;

public class Assets {
    public static Image player;
    public static Image enemy;
    public static Image bullet;
    public static Image enemyBullet;
    public static Image explosion;
    public static Image powerUp;  // ✅ THÊM DÒNG NÀY
    public static Image missile;

    public static void load() {
        player = new Image(Assets.class.getResource("/assets/images/player_ship.png").toExternalForm());
        enemy = new Image(Assets.class.getResource("/assets/images/enemy1.png").toExternalForm());
        bullet = new Image(Assets.class.getResource("/assets/images/bullet.png").toExternalForm());
        enemyBullet = new Image(Assets.class.getResource("/assets/images/enemy_bullet.png").toExternalForm());
        explosion = new Image(Assets.class.getResource("/assets/images/explosion.png").toExternalForm());
        powerUp = new Image(Assets.class.getResource("/assets/images/power_up.png").toExternalForm());
        missile = new Image(Assets.class.getResource("/assets/images/missile.png").toExternalForm());

    }
}
