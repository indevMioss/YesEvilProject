package com.interdev.game.screens.game.attack.bullets;


import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletSystem;

public class Spirit extends Bullet {

    public Spirit(World world, Pool<? extends Bullet> pool) {
        super(BulletSystem.Type.SPIRIT, world, "effects/bullets/spirit_bullet.p", "effects", pool);
    }


}

