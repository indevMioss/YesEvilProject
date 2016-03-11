package com.interdev.game.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.utils.Array;
import com.interdev.game.screens.game.attack.ultimate.aiming.Particle;

public class ScalableEffect extends ParticleEffect {

    private Array<ParticleEmitter> snapshot = new Array<ParticleEmitter>();

    public ScalableEffect() {
    }

    public ScalableEffect(ScalableEffect effect) {
        super(effect);
        makeSnapshot();
    }

    public ScalableEffect(ParticleEffect effect) {
        super(effect);
        makeSnapshot();
    }


    @Override
    public void load(FileHandle effectFile, TextureAtlas atlas, String atlasPrefix) {
        super.load(effectFile, atlas, atlasPrefix);
        makeSnapshot();
    }

    @Override
    public void load(FileHandle effectFile, TextureAtlas atlas) {
        super.load(effectFile, atlas);
        makeSnapshot();
    }

    @Override
    public void load(FileHandle effectFile, FileHandle imagesDir) {
        super.load(effectFile, imagesDir);
        makeSnapshot();
    }

    public void makeSnapshot() {
        snapshot.clear();
        for (ParticleEmitter emitter : getEmitters()) {
            snapshot.add(new ParticleEmitter(emitter));
        }
    }

    @Override
    public void scaleEffect(float scaleFactor) {
        System.out.println("scaleFactor " + scaleFactor);
        setScale(scaleFactor);
    }


    public void setScale(float scaleFactor) {
        for (int i = 0; i < getEmitters().size; i++) {

            ParticleEmitter particleEmitter = getEmitters().get(i);
            ParticleEmitter snapshotEmitter = snapshot.get(i);

            particleEmitter.getScale().setHigh(snapshotEmitter.getScale().getHighMin() * scaleFactor, snapshotEmitter.getScale().getHighMax() * scaleFactor);
            particleEmitter.getScale().setLow(snapshotEmitter.getScale().getLowMin() * scaleFactor, snapshotEmitter.getScale().getLowMax() * scaleFactor);

            particleEmitter.getVelocity().setHigh(snapshotEmitter.getVelocity().getHighMin() * scaleFactor, snapshotEmitter.getVelocity().getHighMax() * scaleFactor);
            particleEmitter.getVelocity().setLow(snapshotEmitter.getVelocity().getLowMin() * scaleFactor, snapshotEmitter.getVelocity().getLowMax() * scaleFactor);

            particleEmitter.getGravity().setHigh(snapshotEmitter.getGravity().getHighMin() * scaleFactor, snapshotEmitter.getGravity().getHighMax() * scaleFactor);
            particleEmitter.getGravity().setLow(snapshotEmitter.getGravity().getLowMin() * scaleFactor, snapshotEmitter.getGravity().getLowMax() * scaleFactor);

            particleEmitter.getWind().setHigh(snapshotEmitter.getWind().getHighMin() * scaleFactor, snapshotEmitter.getWind().getHighMax() * scaleFactor);
            particleEmitter.getWind().setLow(snapshotEmitter.getWind().getLowMin() * scaleFactor, snapshotEmitter.getWind().getLowMax() * scaleFactor);

            particleEmitter.getSpawnWidth().setHigh(snapshotEmitter.getSpawnWidth().getHighMin() * scaleFactor, snapshotEmitter.getSpawnWidth().getHighMax() * scaleFactor);
            particleEmitter.getSpawnWidth().setLow(snapshotEmitter.getSpawnWidth().getLowMin() * scaleFactor, snapshotEmitter.getSpawnWidth().getLowMax() * scaleFactor);

            particleEmitter.getSpawnHeight().setHigh(snapshotEmitter.getSpawnHeight().getHighMin() * scaleFactor, snapshotEmitter.getSpawnHeight().getHighMax() * scaleFactor);
            particleEmitter.getSpawnHeight().setLow(snapshotEmitter.getSpawnHeight().getLowMin() * scaleFactor, snapshotEmitter.getSpawnHeight().getLowMax() * scaleFactor);

            particleEmitter.getXOffsetValue().setLow(snapshotEmitter.getXOffsetValue().getLowMin() * scaleFactor, snapshotEmitter.getXOffsetValue().getLowMax() * scaleFactor);

            particleEmitter.getYOffsetValue().setLow(snapshotEmitter.getYOffsetValue().getLowMin() * scaleFactor, snapshotEmitter.getYOffsetValue().getLowMax() * scaleFactor);
        }
    }

}





















