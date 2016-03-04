package com.interdev.game.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.bullets.*;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.demons.Monsters;
import com.interdev.game.screens.game.levels.LevelsSystem;
import com.interdev.game.tools.ScalableParticleEffect;

public class EffectsSystem implements Disposable {

    public static EffectsSystem inst;

    public enum Type {
        RED_DEMON,
        BLUE_STAR,
        RESURRECTION,
        GG
    }

    private final Player player;

    public final ParticleEffect sharpShield;
    public final ParticleEffect playerDeadBlow;
    public final ParticleEffect resurrectEffect;

    private ParticleEffectPool evilBlowPool;
    private ParticleEffectPool starBlowPool;

    private ParticleEffectPool spiritBulletBlowPool;
    private ParticleEffectPool greenFlyBulletBlowPool;
    private ParticleEffectPool greenSharpBulletBlowPool;
    private ParticleEffectPool blueRicochetBulletBlowPool;
    private ParticleEffectPool miniFireBulletBlowPool;
    private ParticleEffectPool scatterYellowBulletBlowPool;

    private Array<ParticleEffect> effects = new Array<ParticleEffect>();

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

        sharpShield = new ParticleEffect();
        sharpShield.load(Gdx.files.internal("effects/sharp_shield.p"), Gdx.files.internal("effects"));
        sharpShield.scaleEffect(1 / GameMain.PPM);
        sharpShield.start();

        playerDeadBlow = new ParticleEffect();
        playerDeadBlow.load(Gdx.files.internal("effects/player_dead.p"), Gdx.files.internal("effects"));
        playerDeadBlow.scaleEffect(1 / GameMain.PPM);

        resurrectEffect = new ParticleEffect();
        resurrectEffect.load(Gdx.files.internal("effects/resurrection.p"), Gdx.files.internal("effects"));
        resurrectEffect.scaleEffect(1 / GameMain.PPM);
    }

    public ParticleEffectPool newPool(String effectPath, String assetsPath) {
        return newPool(effectPath, assetsPath, 1f);
    }

    public ParticleEffectPool newPool(String effectPath, String assetsPath, float scale) {
        ParticleEffect effectPrototype = new ParticleEffect();
        effectPrototype.load(Gdx.files.internal(effectPath), Gdx.files.internal(assetsPath));
        effectPrototype.scaleEffect(scale / GameMain.PPM);
        return new ParticleEffectPool(effectPrototype, 0, 50);
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
        for (ParticleEffect effect : effects) {
            effect.draw(batch, delta);
            if (effect.isComplete()) {
                effects.removeValue(effect, true);
                if (effect instanceof ParticleEffectPool.PooledEffect)
                    ((ParticleEffectPool.PooledEffect) effect).free();
            }
        }
    }

    public void doBulletBlow(Bullet bullet) {
        doBulletBlow(bullet, 0, 0);
    }

    public void doBulletBlow(Bullet bullet, float angle, float angleRange) {
        if (!GameScreen.inFrustum(bullet.getX(), bullet.getY())) return;
        ParticleEffectPool tempPool = getCorrespondingPool(bullet);

        if (tempPool == null) return;

        ParticleEffectPool.PooledEffect effect = tempPool.obtain();
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
        ParticleEffectPool tempPool = getCorrespondingPool(demon);
        ParticleEffectPool.PooledEffect effect = tempPool.obtain();
        effect.reset();
        effect.setPosition(demon.getX(), demon.getY());
        effects.add(effect);
    }


    public void doEffect(float x, float y, Type type) {
        if (!GameScreen.inFrustum(x, y)) return;
        ParticleEffectPool tempPool = null;
        ParticleEffect tempEffect = null;
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


    private ParticleEffectPool getCorrespondingPool(Bullet bullet) {
        if (bullet instanceof GreenFly) return greenFlyBulletBlowPool;
        if (bullet instanceof GreenSharp) return greenSharpBulletBlowPool;
        if (bullet instanceof MiniFire) return miniFireBulletBlowPool;
        if (bullet instanceof RicochetBullet) return blueRicochetBulletBlowPool;
        if (bullet instanceof ScatterYellow) return scatterYellowBulletBlowPool;
        if (bullet instanceof Spirit) return spiritBulletBlowPool;
        return null;
    }

    private ParticleEffectPool getCorrespondingPool(Demon demon) {
        if (demon instanceof Monsters.AnglerGray) return evilBlowPool;

        return evilBlowPool;
    }

    @Override
    public void dispose() {
        for (ParticleEffect effect : effects) {
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
