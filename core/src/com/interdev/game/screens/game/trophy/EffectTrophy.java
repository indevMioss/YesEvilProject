package com.interdev.game.screens.game.trophy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.EffectsSystem;
import com.interdev.game.tools.ScalableEffect;

public class EffectTrophy extends Trophy {
    private ParticleEffect effect;

    public EffectTrophy(TrophySystem trophySystem, Pool<? extends Trophy> myPool, World world) {
        super(trophySystem, myPool, world);
    }


    @Override
    public Trophy setType(Type type) {
        super.setType(type);
        effect = EffectsSystem.inst.getAmmoEffect(type);
        effect.reset();

        float scale = 1f;
        switch (type) {
            case AMMO_GREEN_FLY:

                break;
            case AMMO_GREEN_SHARP:

                break;
            case AMMO_MINI_FIRE:

                break;
            case AMMO_RICOCHET_BLUE:

                break;
            case AMMO_SCATTER_YELLOW:
                scale = 2f;
                break;
        }
        effect.scaleEffect(scale);
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX(), getY());
        effect.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        effect.draw(batch);
    }
}
