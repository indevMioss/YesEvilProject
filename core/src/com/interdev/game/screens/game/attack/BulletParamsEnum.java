package com.interdev.game.screens.game.attack;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;

public enum BulletParamsEnum {

    SPIRIT                  (6 , 0.12f, 20f,   0.0f,  10, 10, 2),
    MINI_FIRE               (10, 0.2f,  20f,   0.2f,  10, 10, 2),
    SCATTER_YELLOW          (3 , 0.5f,  20f,   0.5f,  10, 20, 3),
    BLUE_RICOCHET_BULLET    (10, 0.25f, 20f,   0.25f, 10, 10, 2),
    GREEN_FLY               (10, 0.4f,  20f,   0.4f,  10, 30, 50),
    GREEN_SHARP             (10, 0.3f,  20f,   0.3f,  10, 10, 5);

    private static final float AMMO_MAX = 20;
    public static ObjectMap<BulletParamsEnum, String> effectsPathMap = new OrderedMap<BulletParamsEnum, String>();
    static {
        effectsPathMap.put(BulletParamsEnum.SPIRIT, "effects/bullets/spirit_bullet.p");
        effectsPathMap.put(BulletParamsEnum.GREEN_FLY, "effects/bullets/green_fly_bullet.p");
        effectsPathMap.put(BulletParamsEnum.GREEN_SHARP, "effects/bullets/green_sharp_bullet.p");
        effectsPathMap.put(BulletParamsEnum.BLUE_RICOCHET_BULLET, "effects/bullets/blue_fly_bullet.p");
        effectsPathMap.put(BulletParamsEnum.MINI_FIRE, "effects/bullets/mini_fire_bullet.p");
        effectsPathMap.put(BulletParamsEnum.SCATTER_YELLOW, "effects/bullets/yellow_bullet.p");
    }

    public float ammoMax = AMMO_MAX;
    public final float shootInterval;
    public final float damage;
    private float ammoRest;
    public final float ammoCost;
    public final float bodyShapeRadius;
    public final float bodyImpulse;
    public final float punchForce;

    BulletParamsEnum(float damage,
                     float shootInterval,
                     float ammoRest,
                     float ammoCost,
                     float bodyShapeRadius,
                     float bodyImpulse,
                     float punchForce) {

        this.damage = damage;
        this.shootInterval = shootInterval;
        this.ammoRest = ammoRest;
        this.ammoCost = ammoCost;
        this.bodyShapeRadius = bodyShapeRadius;
        this.bodyImpulse = bodyImpulse;
        this.punchForce = punchForce;
    }

    public float getAmmoRest() {
        return ammoRest;
    }

    public boolean hasAmmoToShoot() {
        return ammoRest >= ammoCost;
    }

    public void shotAmmo() {
        ammoRest -= ammoCost;
    }

    public void addAmmo(float valueAdd) {
        ammoRest += valueAdd;
        ammoRest = Math.min(ammoRest, ammoMax);
    }
}
