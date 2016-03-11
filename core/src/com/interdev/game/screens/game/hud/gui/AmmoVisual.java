package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.tools.ScalableEffect;
import com.interdev.game.tools.Utils;


public class AmmoVisual extends Group {
    public static AmmoVisual inst;

    public static float xOffVal = 0.4f;
    public static float yOffVal = 0.05f;

    private WeaponEffectVis currentWeaponEffectVis;
    private ObjectMap<BulletParamsEnum, WeaponEffectVis> weaponVisMap = new ObjectMap<BulletParamsEnum, WeaponEffectVis>();

    public ObjectMap<BulletParamsEnum, float[]> scaleFactors = new OrderedMap<BulletParamsEnum, float[]>();

    {
        /* TYPE , MIN EFFECT SCALE , MAX EFFECT SCALE */
        scaleFactors.put(BulletParamsEnum.SPIRIT, new float[]{0.25f, 1.5f});
        scaleFactors.put(BulletParamsEnum.GREEN_FLY, new float[]{0.25f, 1f});
        scaleFactors.put(BulletParamsEnum.GREEN_SHARP, new float[]{0.25f, 1.5f});
        scaleFactors.put(BulletParamsEnum.BLUE_RICOCHET_BULLET, new float[]{0.25f, 1.5f});
        scaleFactors.put(BulletParamsEnum.MINI_FIRE, new float[]{0.25f, 2f});
        scaleFactors.put(BulletParamsEnum.SCATTER_YELLOW, new float[]{1f, 3f});
    }

    public AmmoVisual() {
        inst = this;
        for (ObjectMap.Entry<BulletParamsEnum, String> entry : BulletParamsEnum.effectsPathMap.entries()) {
            WeaponEffectVis weaponEffectVis = new WeaponEffectVis(entry.value, entry.key);
            weaponVisMap.put(entry.key, weaponEffectVis);
            addActor(weaponEffectVis);
        }

        setCurrent(BulletParamsEnum.SPIRIT);
    }

    public void updateVisualFullnessOf(BulletParamsEnum type) {
        weaponVisMap.get(type).setVisFullness(type.getAmmoRest() / type.ammoMax);
    }

    public void setCurrent(BulletParamsEnum type) {
        for (final ObjectMap.Entry<BulletParamsEnum, WeaponEffectVis> entry : weaponVisMap) {
            entry.value.setVisible(entry.key.equals(type));
        }
        currentWeaponEffectVis = weaponVisMap.get(type);
        updateVisualFullnessOf(type);
    }

    public WeaponEffectVis getCurrent(BulletParamsEnum type) {
        return currentWeaponEffectVis;
    }


    public WeaponEffectVis getWeaponEffectVis(BulletParamsEnum type) {
        return weaponVisMap.get(type);
    }


    public static class WeaponEffectVis extends Actor {
        private ScalableEffect effect;
        private BulletParamsEnum bulletType;
        private float visFullness = 1;

        public WeaponEffectVis(final String effectPath, BulletParamsEnum bulletType) {
            this.bulletType = bulletType;
            effect = new ScalableEffect();
            effect.load(Gdx.files.internal(effectPath), Gdx.files.internal("effects"));
            effect.scaleEffect(1 / GameMain.PPM);
            for (ParticleEmitter emitter : effect.getEmitters()) {
                emitter.setAttached(true);
            }
            effect.start();
        }

        public void setVisFullness(float fullness) {
            fullness = Utils.trimValue(0, 1, fullness);
            float minScale = AmmoVisual.inst.scaleFactors.get(bulletType)[0];
            float maxScale = AmmoVisual.inst.scaleFactors.get(bulletType)[1];
            visFullness = minScale + (maxScale - minScale) * fullness;
            effect.setScale(visFullness / GameMain.PPM);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            effect.setPosition((Player.inst.getX() + (Player.inst.facingRightSide ?
                            Player.inst.getWidth() * xOffVal : -Player.inst.getWidth() * xOffVal)),
                    Player.inst.getY() + Player.inst.getHeight() * yOffVal);
            effect.update(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            effect.draw(batch);
        }
    }
}
