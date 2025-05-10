package com.example.spaceshooter;

import javafx.scene.image.Image;

public class Assets {
    public static Image player, bullet, enemy, bg;

    public static void load() {
        player = new Image(Assets.class.getResource("/assets/images/player_ship.png").toExternalForm());
        bullet = new Image(Assets.class.getResource("/assets/images/bullet.png").toExternalForm());
        enemy = new Image(Assets.class.getResource("/assets/images/enemy1.png").toExternalForm());
    }
}
