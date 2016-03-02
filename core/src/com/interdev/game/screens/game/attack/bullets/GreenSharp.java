package com.interdev.game.screens.game.attack.bullets;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletSystem;

public class GreenSharp extends Bullet {

    public GreenSharp(World world, Pool<? extends Bullet> pool) {
        super(BulletSystem.Type.GREEN_SHARP, world, "effects/bullets/green_sharp_bullet.p", "effects", pool);
    }
}
