package com.interdev.game.screens.game.attack;


import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.EffectsSystem;
import com.interdev.game.screens.game.attack.bullets.*;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.hud.gui.AmmoVisual;
import com.interdev.game.sound.SoundSystem;
import com.interdev.game.tools.ActionListener;
import com.interdev.game.tools.TwoFloatsChangeListener;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulletSystem extends Group {

    private static final float ammoMax = 20;
    public static ObjectMap<Type, String> effectsPathMap = new OrderedMap<Type, String>();
    public static BulletSystem inst;

    static {
        effectsPathMap.put(BulletSystem.Type.SPIRIT, "effects/bullets/spirit_bullet.p");
        effectsPathMap.put(BulletSystem.Type.GREEN_FLY, "effects/bullets/green_fly_bullet.p");
        effectsPathMap.put(BulletSystem.Type.GREEN_SHARP, "effects/bullets/green_sharp_bullet.p");
        effectsPathMap.put(BulletSystem.Type.BLUE_RICOCHET_BULLET, "effects/bullets/blue_fly_bullet.p");
        effectsPathMap.put(BulletSystem.Type.MINI_FIRE, "effects/bullets/mini_fire_bullet.p");
        effectsPathMap.put(BulletSystem.Type.SCATTER_YELLOW, "effects/bullets/yellow_bullet.p");
    }

    public static float timeFactor = 1f;

    private static Type currentType = Type.SPIRIT;

    public enum Type {

        SPIRIT(6, 0.12f, 0, 10, 10, 2),
        GREEN_FLY(10, 0.4f, 0.4f, 10, 10, 5),
        GREEN_SHARP(10, 0.3f, 0.3f, 10, 10, 5),
        BLUE_RICOCHET_BULLET(10, 0.25f, 0.25f, 10, 10, 2),
        MINI_FIRE(10, 0.2f, 0.2f, 10, 10, 2),
        SCATTER_YELLOW(3, 0.5f, 0.5f, 10, 20, 3);

        public float ammoRest = 20;

        public final float shootInterval;
        public final float damage;
        public final float ammoCost;
        public final float bodyShapeRadius;
        public final float bodyImpulse;
        public final float punchForce;

        Type(float damage, float shootInterval, float ammoCost, float bodyShapeRadius, float bodyImpulse, float punchForce) {
            this.damage = damage;
            this.shootInterval = shootInterval;
            this.ammoCost = ammoCost;
            this.bodyShapeRadius = bodyShapeRadius;
            this.bodyImpulse = bodyImpulse;
            this.punchForce = punchForce;
        }

        public boolean hasAmmoToShoot() {
            return ammoRest >= ammoCost;
        }
    }

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
                setBulletHitListener(bullet, 60f, true);
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
            if (currentType.ammoRest < currentType.ammoCost) {
                setNextTypeWithAmmo();
                return;
            }
            fireDelayCounter += delta;
            float interval = currentType.shootInterval;
            interval *= (player.isWalking()) ? walkingSlowShootFactor : 1;
            if (fireDelayCounter >= interval * timeFactor) {
                currentType.ammoRest -= currentType.ammoCost;
                AmmoVisual.inst.getWeaponEffectVis(currentType).setVisFullness(currentType.ammoRest / ammoMax);
                launch();
                fireDelayCounter = 0;
            }
        }
    }

    private List<Type> typesPowerOrderList = new ArrayList<Type>();

    {
        typesPowerOrderList.add(Type.GREEN_FLY);
        typesPowerOrderList.add(Type.GREEN_SHARP);
        typesPowerOrderList.add(Type.BLUE_RICOCHET_BULLET);
        typesPowerOrderList.add(Type.SCATTER_YELLOW);
        typesPowerOrderList.add(Type.MINI_FIRE);
    }

    private void setNextTypeWithAmmo() {
        for (Type type : typesPowerOrderList) {
            if (type.hasAmmoToShoot()) {
                setType(type);
                return;
            }
        }
        setType(Type.SPIRIT);
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

    public static void setType(Type type) {
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
            System.out.println(launchAngle);
            System.out.println(projectilesToLaunch);
        }
        if (bullet.projectilesPerShot > 1) {
            int sign = (Utils.roll(0.5f)) ? -1 : 1;
            launchAngle += sign * Utils.getRand(0f, thisShotAngleSpray / 2);
        }

        bullet.launchAfterWorldStep(startX, startY, launchAngle);
        projectilesToLaunch--;
        if (projectilesToLaunch > 0) launchNewBullet(pool, false);
    }


}








