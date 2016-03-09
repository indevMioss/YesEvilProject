package com.interdev.game.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.screens.game.attack.bullets.*;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.demons.Monsters;
import com.interdev.game.screens.game.levels.LevelsSystem;
import com.interdev.game.screens.game.trophy.Trophy;
import com.interdev.game.tools.ScalableEffect;
import com.interdev.game.tools.ScalableEffectPool;

public class EffectsSystem implements Disposable {

    public static EffectsSystem inst;
    private final ScalableEffectPool greenFlyAmmoEffectPool;
    private final ScalableEffectPool greenSharpAmmoPool;
    private final ScalableEffectPool blueRicochetAmmoPool;
    private final ScalableEffectPool miniFireAmmoPool;
    private final ScalableEffectPool scatterYellowAmmoPool;

    public enum Type {
        RED_DEMON,
        BLUE_STAR,
        RESURRECTION,
        GG
    }

    private final Player player;

    public final ScalableEffect sharpShield;
    public final ScalableEffect playerDeadBlow;
    public final ScalableEffect resurrectEffect;

    private ScalableEffectPool evilBlowPool;
    private ScalableEffectPool starBlowPool;

    private ScalableEffectPool spiritBulletBlowPool;
    private ScalableEffectPool greenFlyBulletBlowPool;
    private ScalableEffectPool greenSharpBulletBlowPool;
    private ScalableEffectPool blueRicochetBulletBlowPool;
    private ScalableEffectPool miniFireBulletBlowPool;
    private ScalableEffectPool scatterYellowBulletBlowPool;

    private Array<ScalableEffect> effects = new Array<ScalableEffect>();

    public EffectsSystem(Player player) {
        inst = this;
        this.player = player;

        evilBlowPool = newPool("effects/demon_blow.p", "effects");
        starBlowPool = newPool("effects/star_blow.p", "effects", 0.5f);

        spiritBulletBlowPool = newPool("effects/bullets/spirit_bullet_blow2.p", "effects");

        greenFlyBulletBlowPool = newPool("effects/bullets/green_fly_blow.p", "effects");
        greenSharpBulletBlowPool = newPool("effects/bullets/green_sharp_blow.p", "effects");
        blueRicochetBulletBlowPool = newPool("effects/bullets/blue_fly_blow.p", "effects");
        miniFireBulletBlowPool = newPool("effects/bullets/mini_fire_blow.p", "effects");
        scatterYellowBulletBlowPool = newPool("effects/bullets/yellow_blow.p", "effects");

        greenFlyAmmoEffectPool = newPool(BulletParamsEnum.effectsPathMap.get(BulletParamsEnum.GREEN_FLY), "effects");
        greenSharpAmmoPool = newPool(BulletParamsEnum.effectsPathMap.get(BulletParamsEnum.GREEN_SHARP), "effects");
        blueRicochetAmmoPool = newPool(BulletParamsEnum.effectsPathMap.get(BulletParamsEnum.BLUE_RICOCHET_BULLET), "effects");
        miniFireAmmoPool = newPool(BulletParamsEnum.effectsPathMap.get(BulletParamsEnum.MINI_FIRE), "effects");
        scatterYellowAmmoPool = newPool(BulletParamsEnum.effectsPathMap.get(BulletParamsEnum.SCATTER_YELLOW), "effects");

        sharpShield = new ScalableEffect();
        sharpShield.load(Gdx.files.internal("effects/sharp_shield.p"), Gdx.files.internal("effects"));
        sharpShield.scaleEffect(1 / GameMain.PPM);
        sharpShield.start();

        playerDeadBlow = new ScalableEffect();
        playerDeadBlow.load(Gdx.files.internal("effects/player_dead.p"), Gdx.files.internal("effects"));
        playerDeadBlow.scaleEffect(1 / GameMain.PPM);

        resurrectEffect = new ScalableEffect();
        resurrectEffect.load(Gdx.files.internal("effects/resurrection.p"), Gdx.files.internal("effects"));
        resurrectEffect.scaleEffect(1 / GameMain.PPM);
    }

    public ScalableEffectPool newPool(String effectPath, String assetsPath) {
        return newPool(effectPath, assetsPath, 1f);
    }

    public ScalableEffectPool newPool(String effectPath, String assetsPath, float scale) {
        ScalableEffect effectPrototype = new ScalableEffect();
        effectPrototype.load(Gdx.files.internal(effectPath), Gdx.files.internal(assetsPath));
        effectPrototype.scaleEffect(scale / GameMain.PPM);
        return new ScalableEffectPool(effectPrototype, 0, 50);
    }

    public void update() {
        if (Player.hasSharpShield) {
            sharpShield.setPosition(player.getX(), player.getY());
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        if (Player.hasSharpShield) {
            sharpShield.draw(batch, delta);
        }
        playerDeadBlow.draw(batch, delta);

        for (ScalableEffect effect : effects) {
            effect.draw(batch, delta);
            if (effect.isComplete()) {
                effects.removeValue(effect, true);
                if (effect instanceof ScalableEffectPool.PooledEffect)
                    ((ScalableEffectPool.PooledEffect) effect).free();
            }
        }
    }

    public void doBulletBlow(Bullet bullet) {
        doBulletBlow(bullet, 0, 0);
    }

    public void doBulletBlow(Bullet bullet, float angle, float angleRange) {
        if (!GameScreen.inFrustum(bullet.getX(), bullet.getY())) return;
        ScalableEffectPool tempPool = getCorrespondingPool(bullet);

        if (tempPool == null) return;

        ScalableEffectPool.PooledEffect effect = tempPool.obtain();
        effect.reset();
        if (angleRange != 0) {
            for (ParticleEmitter emitter : effect.getEmitters()) {
                emitter.getAngle().setLow(angle - angleRange / 2, angle + angleRange / 2);
                emitter.getAngle().setHigh(angle - angleRange / 2, angle + angleRange / 2);
            }
        }

        effect.setPosition(bullet.getX(), bullet.getY());
        effects.add(effect);
    }

    public void doDemonBlow(Demon demon) {
        ScalableEffectPool tempPool = getCorrespondingPool(demon);
        ScalableEffectPool.PooledEffect effect = tempPool.obtain();
        effect.reset();
        effect.setPosition(demon.getX(), demon.getY());
        effects.add(effect);
    }

    public ScalableEffect getAmmoEffect(Trophy.Type type) {
        return getCorrespondingPool(type).obtain();
    }

    public void doEffect(float x, float y, Type type) {
        if (!GameScreen.inFrustum(x, y)) return;
        ScalableEffectPool tempPool = null;
        ScalableEffect tempEffect = null;
        switch (type) {
            case RED_DEMON:
                tempPool = evilBlowPool;
                break;
            case BLUE_STAR:
                tempPool = starBlowPool;
                break;
            case RESURRECTION:
                tempEffect = resurrectEffect;
                break;
            case GG:
                tempEffect = playerDeadBlow;
                tempEffect.getEmitters().get(0).setMaxParticleCount((LevelsSystem.levelsPassed + 1) * 40);
                break;
        }
        if (tempPool == null) {
            if (tempEffect == null) {
                return;
            }
        } else {
            tempEffect = tempPool.obtain();
        }

        tempEffect.reset();
        tempEffect.setPosition(x, y);
        effects.add(tempEffect);
    }

    private ScalableEffectPool getCorrespondingPool(Trophy.Type type) {
       switch (type) {
           case AMMO_GREEN_FLY:
               return greenFlyAmmoEffectPool;
           case AMMO_GREEN_SHARP:
               return greenSharpAmmoPool;
           case AMMO_RICOCHET_BLUE:
               return blueRicochetAmmoPool;
           case AMMO_MINI_FIRE:
               return miniFireAmmoPool;
           case AMMO_SCATTER_YELLOW:
               return scatterYellowAmmoPool;
       }
        return greenFlyAmmoEffectPool;
    }

    private ScalableEffectPool getCorrespondingPool(Bullet bullet) {
        if (bullet instanceof GreenFly) return greenFlyBulletBlowPool;
        if (bullet instanceof GreenSharp) return greenSharpBulletBlowPool;
        if (bullet instanceof MiniFire) return miniFireBulletBlowPool;
        if (bullet instanceof RicochetBullet) return blueRicochetBulletBlowPool;
        if (bullet instanceof ScatterYellow) return scatterYellowBulletBlowPool;
        if (bullet instanceof Spirit) return spiritBulletBlowPool;
        return null;
    }

    private ScalableEffectPool getCorrespondingPool(Demon demon) {
        if (demon instanceof Monsters.AnglerGray) return evilBlowPool;

        return evilBlowPool;
    }

    @Override
    public void dispose() {
        for (ScalableEffect effect : effects) {
            effect.dispose();
        }
        effects.clear();
        evilBlowPool.clear();
        starBlowPool.clear();
        sharpShield.dispose();
        spiritBulletBlowPool.clear();
        greenFlyBulletBlowPool.clear();
        greenSharpBulletBlowPool.clear();
        blueRicochetBulletBlowPool.clear();
        miniFireBulletBlowPool.clear();
    }
}
