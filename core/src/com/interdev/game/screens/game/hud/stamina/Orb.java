package com.interdev.game.screens.game.hud.stamina;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.interdev.game.tools.Utils;

import java.util.Random;

public class Orb extends Actor {
    private static final float MIN_SIZE = 0.25f;
    private static final float MAX_SIZE = 0.75f;

    public float fullnessPercent = 0;

    private Animation animation;
    private float timePassed;

    public Orb(float x, float y, Array<? extends TextureRegion> orbRegions) {
        timePassed = (new Random().nextInt(400)) / 100f; //random initial frame
        setVisible(false);
        animation = new Animation(1 / 40f, orbRegions, Animation.PlayMode.LOOP);
        setPosition(x, y);
        setSize(animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
        updateSizeFromFullness();
    }

    private void updateSizeFromFullness() {
        setScale(MIN_SIZE + (MAX_SIZE - MIN_SIZE) * fullnessPercent);
    }

    public float fill(float percent) {
        fullnessPercent += percent;
        float rest = 0;
        if (fullnessPercent > 1) {
            rest = fullnessPercent - 1f;
        } else if (fullnessPercent < 0) {
            rest = fullnessPercent;
        }
        fullnessPercent = Utils.trimValue(0, 1, fullnessPercent);
        updateSizeFromFullness();
        return rest;
    }

    @Override
    public void act(float delta) {
        if (isVisible()) {
            timePassed += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isVisible()) {
            batch.draw(animation.getKeyFrame(timePassed), getX() - getWidth() / 2, getY() - getHeight() / 2,
                    getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }


}
