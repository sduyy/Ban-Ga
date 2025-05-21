package com.example.spaceshooter;

import javafx.scene.image.Image;

public class Assets {
    public static Image player;
    public static Image enemy;
    public static Image bullet;
    public static Image enemyBullet;
    public static Image explosion;
    public static Image missile;
    public static Image powerUpHealth;
    public static Image powerUpEnergy;
    public static Image powerUpAmmo;
    public static Image powerUpDamage;
    public static Image powerUpRockets;
    public static Image bossEnemy;
    public static Image bossBullet;
    public static Image superBossEnemy;
    public static Image superBossBullet;
    public static Image miniBoss;
    public static Image miniBossBullet;

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
    }
}
