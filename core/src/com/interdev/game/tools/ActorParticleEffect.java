package com.interdev.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorParticleEffect extends Actor {
    public ScalableEffect effect;

    public ActorParticleEffect(String effectPath, String assetsPath) {
        effect = new ScalableEffect();
        effect.load(Gdx.files.internal(effectPath), Gdx.files.internal(assetsPath));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        effect.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX(), getY());
        effect.update(delta);
    }
}
