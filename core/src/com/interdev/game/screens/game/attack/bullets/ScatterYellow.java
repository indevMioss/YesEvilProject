package com.interdev.game.screens.game.attack.bullets;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.tools.Utils;

public class ScatterYellow extends Bullet {

    public ScatterYellow(World world, Pool<? extends Bullet> myPool) {
        super(BulletParamsEnum.SCATTER_YELLOW, world, "effects/bullets/yellow_bullet.p", "effects", myPool);
        projectilesPerShot = 5;
        strictAngleSpray = 35f;
        amountOfEnemiesCanSpear = 3;
    }

    @Override
    public float getApproxAngleSpray() {
        return super.getApproxAngleSpray() * Utils.getRand(0.75f, 1.25f);
    }
}
