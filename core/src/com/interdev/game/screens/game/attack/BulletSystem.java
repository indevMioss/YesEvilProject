package com.interdev.game.screens.game.attack;


import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.EffectsSystem;
import com.interdev.game.screens.game.attack.bullets.*;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.hud.gui.AmmoVisual;
import com.interdev.game.screens.game.trophy.Trophy;
import com.interdev.game.sound.SoundSystem;
import com.interdev.game.tools.ActionListener;
import com.interdev.game.tools.TwoFloatsChangeListener;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class BulletSystem extends Group {
    public static BulletSystem inst;

    public static float timeFactor = 1f;

    private static BulletParamsEnum currentType = BulletParamsEnum.SPIRIT;

    private static final float walkingSlowShootFactor = 1.25f;
    private Player player;

    private Pool<Spirit> spiritBulletPool;
    private Pool<GreenFly> greenFlyBulletPool;
    private Pool<GreenSharp> greenSharpBulletPool;
    private Pool<RicochetBullet> blueFlyBulletPool;
    private Pool<MiniFire> miniFireBulletPool;
    private Pool<ScatterYellow> scatterYellowBulletPool;

    private boolean shooting = false;
    private float fireDelayCounter;
    private float currentFireAngle;


    private Array<TwoFloatsChangeListener> launchPosChangeListeners = new Array<TwoFloatsChangeListener>();

    public void addLaunchPosChangeListener(TwoFloatsChangeListener launchPosChangeListener) {
        launchPosChangeListeners.add(launchPosChangeListener);
    }

    public BulletSystem(final World world, Player player) {
        inst = this;
        this.player = player;

        spiritBulletPool = new Pool<Spirit>() {
            @Override
            protected Spirit newObject() {
                final Spirit bullet = new Spirit(world, spiritBulletPool);
                setBulletHitListener(bullet);
                addActor(bullet);
                return bullet;
            }
        };

        greenFlyBulletPool = new Pool<GreenFly>() {
            @Override
            protected GreenFly newObject() {
                final GreenFly bullet = new GreenFly(world, greenFlyBulletPool);
                setBulletHitListener(bullet);
                addActor(bullet);
                return bullet;
            }
        };

        greenSharpBulletPool = new Pool<GreenSharp>() {
            @Override
            protected GreenSharp newObject() {
                final GreenSharp bullet = new GreenSharp(world, greenSharpBulletPool);
                setBulletHitListener(bullet);
                addActor(bullet);
                return bullet;
            }
        };

        blueFlyBulletPool = new Pool<RicochetBullet>() {
            @Override
            protected RicochetBullet newObject() {
                final RicochetBullet bullet = new RicochetBullet(world, blueFlyBulletPool);
                setBulletHitListener(bullet);
                addActor(bullet);
                return bullet;
            }
        };

        miniFireBulletPool = new Pool<MiniFire>() {
            @Override
            protected MiniFire newObject() {
                final MiniFire bullet = new MiniFire(world, miniFireBulletPool);
                setBulletHitListener(bullet);
                addActor(bullet);
                return bullet;
            }
        };


        scatterYellowBulletPool = new Pool<ScatterYellow>() {
            @Override
            protected ScatterYellow newObject() {
                final ScatterYellow bullet = new ScatterYellow(world, scatterYellowBulletPool);
                setBulletHitListener(bullet, 60f, false);
                addActor(bullet);
                return bullet;
            }
        };


    }

    private void setBulletHitListener(final Bullet bullet) {
        setBulletHitListener(bullet, 0, false);
    }

    private void setBulletHitListener(final Bullet bullet, final float angleSpray, final boolean reversed) {
        bullet.setHitListener(new ActionListener() {
            @Override
            public void actionPerformed() {
                SoundSystem.inst.playBulletBlow(bullet);
                if (angleSpray == 0) {
                    EffectsSystem.inst.doBulletBlow(bullet);
                } else {
                    EffectsSystem.inst.doBulletBlow(bullet, bullet.getAngle() + ((reversed) ? 180 : 0), angleSpray);
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (shooting) {
            if (!currentType.hasAmmoToShoot()) {
                setNextTypeWithAmmo();
                return;
            }
            fireDelayCounter += delta;
            float interval = currentType.shootInterval;
            interval *= (player.isWalking()) ? walkingSlowShootFactor : 1;
            if (fireDelayCounter >= interval * timeFactor) {
                currentType.shotAmmo();
                AmmoVisual.inst.updateVisualFullnessOf(currentType);
                launch();
                fireDelayCounter = 0;
            }
        }
    }

    private List<BulletParamsEnum> typesPowerOrderList = new ArrayList<BulletParamsEnum>();

    {
        typesPowerOrderList.add(BulletParamsEnum.GREEN_FLY);
        typesPowerOrderList.add(BulletParamsEnum.GREEN_SHARP);
        typesPowerOrderList.add(BulletParamsEnum.BLUE_RICOCHET_BULLET);
        typesPowerOrderList.add(BulletParamsEnum.SCATTER_YELLOW);
        typesPowerOrderList.add(BulletParamsEnum.MINI_FIRE);
    }

    private void setNextTypeWithAmmo() {
        for (BulletParamsEnum type : typesPowerOrderList) {
            if (type.hasAmmoToShoot()) {
                setType(type);
                return;
            }
        }
        setType(BulletParamsEnum.SPIRIT);
    }

    public void setAngle(float angle) {
        currentFireAngle = angle;
    }

    public void startFire() {
        shooting = true;
    }

    public void endFire() {
        shooting = false;
    }

    public static void setType(BulletParamsEnum type) {
        currentType = type;
        AmmoVisual.inst.setCurrent(type);
    }

    private float lastOffsetX;
    private float lastOffsetY;

    private void launch() {
        final Pool<? extends Bullet> pool;

        switch (currentType) {
            case GREEN_FLY:
                pool = greenFlyBulletPool;
                break;
            case GREEN_SHARP:
                pool = greenSharpBulletPool;
                break;
            case BLUE_RICOCHET_BULLET:
                pool = blueFlyBulletPool;
                break;
            case MINI_FIRE:
                pool = miniFireBulletPool;
                break;
            case SCATTER_YELLOW:
                pool = scatterYellowBulletPool;
                break;
            default:
                pool = spiritBulletPool;
                break;
        }
        launchNewBullet(pool, true);

    }

    private int projectilesToLaunch = 0;
    private float thisShotAngleSpray = 0;

    private void launchNewBullet(Pool<? extends Bullet> pool, boolean itsFirstProjectile) {
        final Bullet bullet = pool.obtain();

        if (itsFirstProjectile) {
            SoundSystem.inst.playBulletShoot(bullet, Utils.getRand(0.8f, 1f));
            projectilesToLaunch = bullet.projectilesPerShot;
            thisShotAngleSpray = bullet.getApproxAngleSpray();
        }
        float startX = player.getX();
        float startY = player.getY();

        float offsetX = player.facingRightSide ? player.getWidth() * AmmoVisual.xOffVal : -player.getWidth() * AmmoVisual.xOffVal;
        float offsetY = player.getHeight() * AmmoVisual.yOffVal;

        startX += offsetX;
        startY += offsetY;

        if (offsetX != lastOffsetX || offsetY != lastOffsetY) {
            for (TwoFloatsChangeListener listener : launchPosChangeListeners) {
                listener.onValuesChange(offsetX, offsetY);
            }
            lastOffsetX = offsetX;
            lastOffsetY = offsetY;
        }

        float launchAngle = currentFireAngle;

        if (false) {
            launchAngle -= bullet.projectilesPerShot * bullet.strictAngleSpray / 2;
            int currentProjectileNum = bullet.projectilesPerShot - projectilesToLaunch;
            launchAngle += currentProjectileNum * bullet.strictAngleSpray;
        }
        if (bullet.projectilesPerShot > 1) {
            int sign = (Utils.roll(0.5f)) ? -1 : 1;
            launchAngle += sign * Utils.getRand(0f, thisShotAngleSpray / 2);
        }

        bullet.launchAfterWorldStep(startX, startY, launchAngle);
        projectilesToLaunch--;
        if (projectilesToLaunch > 0) launchNewBullet(pool, false);
    }

    public void addAmmo(Trophy.Type type) {
        float valueAdd = 10;
        BulletParamsEnum bulletType = getCorrespondingBulletType(type);
        bulletType.addAmmo(valueAdd);
        AmmoVisual.inst.updateVisualFullnessOf(bulletType);

    }

    private BulletParamsEnum getCorrespondingBulletType(Trophy.Type type) {
        switch (type) {
            case AMMO_GREEN_FLY:
                return BulletParamsEnum.GREEN_FLY;
            case AMMO_GREEN_SHARP:
                return BulletParamsEnum.GREEN_SHARP;
            case AMMO_MINI_FIRE:
                return BulletParamsEnum.MINI_FIRE;
            case AMMO_RICOCHET_BLUE:
                return BulletParamsEnum.BLUE_RICOCHET_BULLET;
            case AMMO_SCATTER_YELLOW:
                return BulletParamsEnum.SCATTER_YELLOW;
        }
        return BulletParamsEnum.GREEN_FLY;
    }

}








