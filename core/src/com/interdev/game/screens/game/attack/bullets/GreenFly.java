package com.interdev.game.screens.game.attack.bullets;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.tools.B2dForcePoint;
import com.interdev.game.tools.Utils;

public class GreenFly extends Bullet {

    private float startX, startY;
    private float detonateDist = 3.5f;

    public GreenFly(World world, Pool<? extends Bullet> pool) {
        super(BulletParamsEnum.GREEN_FLY, world, "effects/bullets/green_fly_bullet.p", "effects", pool);
    }

    @Override
    protected void preLaunch() {
        super.preLaunch();
        startX = getX();
        startY = getY();
    }

    private int blastInActTimesSkip = 0;
    private int blastInActTimesSkipCounter = 0;

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isVisible()) return;

        if (blastInActTimesSkipCounter < blastInActTimesSkip) {
            blastInActTimesSkipCounter++;
        } else {
            blastInActTimesSkipCounter = 0;
            B2dForcePoint.blast(DemonsSystem.inst.demonsList, getX(), getY(), 3, -40);
        }

        if (Utils.sqDist(startX, startY, getX(), getY()) >= detonateDist * detonateDist) {
            blow();
        }
    }

    @Override
    public void onHit() {

    }

    private void blow() {
        B2dForcePoint.blast(DemonsSystem.inst.demonsList, getX(), getY(), 4, -100);
        DemonsSystem.inst.splashDamage(getX(), getY(), 4, 10);
        super.onHit();
    }

}
