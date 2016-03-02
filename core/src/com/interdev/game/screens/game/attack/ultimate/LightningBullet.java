package com.interdev.game.screens.game.attack.ultimate;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletSystem;

public class LightningBullet extends Bullet {
    {
        requiresStamina = 1f;
        bodyShapeRadius = 30f;
        bodyImpulse = 20f;
        defaultDamage = 200;
        amountOfEnemiesCanSpear = 3;
    }

    public LightningBullet(World world, Pool<? extends Bullet> pool) {
        super(BulletSystem.Type.SPIRIT, world, "effects/ultimate/bullet1.p", "effects", pool);
        disappear();
    }


}


























