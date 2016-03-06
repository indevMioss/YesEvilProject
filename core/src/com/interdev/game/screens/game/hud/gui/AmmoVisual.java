package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ObjectMap;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.attack.BulletSystem;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.tools.BooleanArgChangeListener;
import com.interdev.game.tools.ScalableParticleEffect;
import com.interdev.game.tools.TwoFloatsChangeListener;

import java.util.Map;


public class AmmoVisual extends Group {
    public static AmmoVisual inst;

    public static float xOffVal = 0.4f;
    public static float yOffVal = 0.05f;


    private WeaponEffectVis currentWeaponEffectVis;
    private ObjectMap<BulletSystem.Type, WeaponEffectVis> weaponVisMap = new ObjectMap<BulletSystem.Type, WeaponEffectVis>();

    public AmmoVisual() {
        inst = this;
        for (ObjectMap.Entry<BulletSystem.Type, String> entry : BulletSystem.effectsPathMap.entries()) {
            WeaponEffectVis weaponEffectVis = new WeaponEffectVis(entry.value);
            weaponVisMap.put(entry.key, weaponEffectVis);
            addActor(weaponEffectVis);
        }

        setCurrent(BulletSystem.Type.SPIRIT);
    }

    public void setCurrent(BulletSystem.Type type) {
        for (final ObjectMap.Entry<BulletSystem.Type, WeaponEffectVis> entry : weaponVisMap) {
            entry.value.setVisible(entry.key.equals(type));
        }
        currentWeaponEffectVis = weaponVisMap.get(type);
    }

    public WeaponEffectVis getCurrent(BulletSystem.Type type) {
        return currentWeaponEffectVis;
    }


    public WeaponEffectVis getWeaponEffectVis(BulletSystem.Type type) {
        return weaponVisMap.get(type);
    }


    public static class WeaponEffectVis extends Actor {
        private float visFullness = 1;
        private ScalableParticleEffect effect;

        public WeaponEffectVis(final String effectPath) {
            effect = new ScalableParticleEffect();
            effect.load(Gdx.files.internal(effectPath), Gdx.files.internal("effects"));
            effect.scaleEffect(1 / GameMain.PPM);
            for (ParticleEmitter emmiter : effect.getEmitters()) {
                emmiter.setAttached(true);
            }
            effect.start();
        }

        public void setVisFullness(float fullness) { //[0;1]
            visFullness = fullness;
            effect.setScale(fullness / GameMain.PPM);
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
