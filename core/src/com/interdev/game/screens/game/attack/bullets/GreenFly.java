package com.interdev.game.screens.game.attack.bullets;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletParamsEnum;

public class GreenFly extends Bullet {

    public GreenFly(World world, Pool<? extends Bullet> pool) {
        super(BulletParamsEnum.GREEN_FLY, world, "effects/bullets/green_fly_bullet.p", "effects", pool);
    }
}
