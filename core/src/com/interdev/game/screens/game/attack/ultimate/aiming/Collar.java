package com.interdev.game.screens.game.attack.ultimate.aiming;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.List;

public class Collar extends Image {

    private static final float RECOVER_TIME = 0.25f;
    private static final float MAX_SCALE = 1.5f;

    private static final float COLLAPSE_TIME = 0.05f; // sec

    private boolean collapsing;
    private boolean recovering;

    private List<Particle> existingParticlesList;
    private float catchDistance;

    public Collar(Texture texture, List<Particle> existingParticlesList) {
        super(texture);
        this.existingParticlesList = existingParticlesList;
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(MAX_SCALE);
        catchDistance = (getWidth() * MAX_SCALE) * 1f / 2;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (collapsing) {
            float collapseSpeed = 1 / COLLAPSE_TIME;
            float newScale = getScaleX() - collapseSpeed * delta;
            newScale = Math.max(0, newScale);
            setScale(newScale);
            collapsing = !(newScale == 0);
            recovering = !collapsing;
        }

        if (recovering) {
            float recoverSpeed = MAX_SCALE / RECOVER_TIME;
            float newScale = getScaleX() + recoverSpeed * delta;
            newScale = Math.min(MAX_SCALE, newScale);
            setScale(newScale);
            recovering = !(newScale == MAX_SCALE);
        }
    }

    public int collapse() {
        int captured = 0;
        if (recovering || collapsing) return captured;
        collapsing = true;
        for (Particle particle : existingParticlesList) {
            if (!particle.isVisible()) continue;
            if (Math.hypot(getX() + getWidth() / 2 - particle.getX(),
                    getY() + getHeight() / 2 - particle.getY()) <= catchDistance) {
                particle.capture();
                captured++;
            }
        }
        return captured;
    }

}
