package com.interdev.game.screens.game.attack.bullets;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.tools.B2dForcePoint;
import com.interdev.game.tools.Utils;


public class RicochetBullet extends Bullet {

    public RicochetBullet(World world, Pool<? extends Bullet> pool) {
        super(BulletParamsEnum.BLUE_RICOCHET_BULLET, world, "effects/bullets/blue_fly_bullet.p", "effects", pool);
    }

    @Override
    public void onHit() {
        float sqDist = 9999999;

        B2dForcePoint.blast(DemonsSystem.inst.demonsList, getX(), getY(), 100, 20);

        if (Utils.roll(0.9f)) {
            Actor targetForChild = null;
            for (Demon demon : DemonsSystem.inst.demonsList) {
                if (demon == target || !demon.isVisible()) continue;
                float currSqDist = Utils.sqDist(this, demon);
                if (sqDist > currSqDist) {
                    sqDist = currSqDist;
                    targetForChild = demon;
                }
            }
            if (targetForChild != null) {
                final Actor finalTargetForChild = targetForChild;
                GameScreen.addAfterWorldStepRunnable(new Timer.Task() {
                    @Override
                    public void run() {
                        Bullet child = myPool.obtain();
                        child.passedTemporalPoint = true;
                        child.parentId = id;
                        child.launchAfterWorldStep(getX(), getY(), finalTargetForChild);
                    }
                });

            }
        }
        amountOfSpearedEnemies++;
        hitListener.actionPerformed();
        if (amountOfSpearedEnemies >= amountOfEnemiesCanSpear) {
            freeSelf();
        }
    }
}
