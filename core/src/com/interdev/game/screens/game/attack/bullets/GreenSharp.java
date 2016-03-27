package com.interdev.game.screens.game.attack.bullets;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.tools.B2dForcePoint;

public class GreenSharp extends Bullet {
    public static int projectiles = 12;

    public GreenSharp(World world, Pool<? extends Bullet> pool) {
        super(BulletParamsEnum.GREEN_SHARP, world, "effects/bullets/green_sharp_bullet.p", "effects", pool);
    }

    @Override
    public void onHit() {
        B2dForcePoint.blast(DemonsSystem.inst.demonsList, getX(), getY(), 100, 20);
        if (!isChildProjectile) {
            float angleStep = 360 / projectiles;
            float ang = angle;
            for (int i = 0; i < projectiles; i++, ang += angleStep) {
                final float finalAng = ang;
                GameScreen.addAfterWorldStepRunnable(new Timer.Task() {
                    @Override
                    public void run() {
                        Bullet child = myPool.obtain();
                        child.passedTemporalPoint = true;
                        child.parentId = id;
                        child.isChildProjectile = true;
                        float scale = 0.75f;
                        child.setScale(scale);
                        child.damageMultiplier = scale;
                        child.launchAfterWorldStep(getX(), getY(), finalAng);
                    }
                });
            }
        }
        super.onHit();
    }
}
