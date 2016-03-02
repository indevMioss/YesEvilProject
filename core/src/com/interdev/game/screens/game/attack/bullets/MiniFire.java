package com.interdev.game.screens.game.attack.bullets;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletSystem;

public class MiniFire extends Bullet {

    public MiniFire(World world, Pool<? extends Bullet> pool) {
        super(BulletSystem.Type.MINI_FIRE, world, "effects/bullets/mini_fire_bullet.p", "effects", pool);
    }
}
