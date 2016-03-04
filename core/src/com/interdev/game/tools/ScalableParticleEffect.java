package com.interdev.game.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ScalableParticleEffect extends ParticleEffect {

    private ScalableValues[] snapshot;

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
    public void  load(FileHandle effectFile, FileHandle imagesDir) {
        super.load(effectFile, imagesDir);
        makeSnapshot();
    }

    public void makeSnapshot() {
        snapshot = new ScalableValues[getEmitters().size];
        for (int i = 0, snapshotLength = snapshot.length; i < snapshotLength; i++) {
            snapshot[i] = new ScalableValues();
        }

        int i = 0;

        for (ParticleEmitter particleEmitter : getEmitters()) {
            snapshot[i].scale.highMin = particleEmitter.getScale().getHighMin();
            snapshot[i].scale.highMax = particleEmitter.getScale().getHighMax();
            snapshot[i].scale.lowMin = particleEmitter.getScale().getLowMin();
            snapshot[i].scale.lowMax = particleEmitter.getScale().getLowMax();

            snapshot[i].velocity.highMin = particleEmitter.getVelocity().getHighMin();
            snapshot[i].velocity.highMax = particleEmitter.getVelocity().getHighMax();
            snapshot[i].velocity.lowMin = particleEmitter.getVelocity().getLowMin();
            snapshot[i].velocity.lowMax = particleEmitter.getVelocity().getLowMax();

            snapshot[i].gravity.highMin = particleEmitter.getGravity().getHighMin();
            snapshot[i].gravity.highMax = particleEmitter.getGravity().getHighMax();
            snapshot[i].gravity.lowMin = particleEmitter.getGravity().getLowMin();
            snapshot[i].gravity.lowMax = particleEmitter.getGravity().getLowMax();

            snapshot[i].wind.highMin = particleEmitter.getWind().getHighMin();
            snapshot[i].wind.highMax = particleEmitter.getWind().getHighMax();
            snapshot[i].wind.lowMin = particleEmitter.getWind().getLowMin();
            snapshot[i].wind.lowMax = particleEmitter.getWind().getLowMax();

            snapshot[i].spawnWidth.highMin = particleEmitter.getSpawnWidth().getHighMin();
            snapshot[i].spawnWidth.highMax = particleEmitter.getSpawnWidth().getHighMax();
            snapshot[i].spawnWidth.lowMin = particleEmitter.getSpawnWidth().getLowMin();
            snapshot[i].spawnWidth.lowMax = particleEmitter.getSpawnWidth().getLowMax();

            snapshot[i].spawnHeight.highMin = particleEmitter.getSpawnHeight().getHighMin();
            snapshot[i].spawnHeight.highMax = particleEmitter.getSpawnHeight().getHighMax();
            snapshot[i].spawnHeight.lowMin = particleEmitter.getSpawnHeight().getLowMin();
            snapshot[i].spawnHeight.lowMax = particleEmitter.getSpawnHeight().getLowMax();

            snapshot[i].xOffset.lowMin = particleEmitter.getXOffsetValue().getLowMin(); //no high values
            snapshot[i].xOffset.lowMax = particleEmitter.getXOffsetValue().getLowMax();

            snapshot[i].yOffset.lowMin = particleEmitter.getYOffsetValue().getLowMin();
            snapshot[i].yOffset.lowMax = particleEmitter.getYOffsetValue().getLowMax();

            i++;
        }
    }

    @Override
    public void scaleEffect(float scaleFactor) {
        setScale(scaleFactor);
    }

    public void setScale(float scaleFactor) {
        int i = 0;
        for (ParticleEmitter particleEmitter : getEmitters()) {
            particleEmitter.getScale().setHigh(snapshot[i].scale.highMin * scaleFactor, snapshot[i].scale.highMax * scaleFactor);
            particleEmitter.getScale().setLow(snapshot[i].scale.lowMin * scaleFactor, snapshot[i].scale.lowMax * scaleFactor);

            particleEmitter.getVelocity().setHigh(snapshot[i].velocity.highMin * scaleFactor, snapshot[i].scale.highMax * scaleFactor);
            particleEmitter.getVelocity().setLow(snapshot[i].velocity.lowMin * scaleFactor, snapshot[i].scale.lowMax * scaleFactor);

            particleEmitter.getGravity().setHigh(snapshot[i].gravity.highMin * scaleFactor, snapshot[i].gravity.highMax * scaleFactor);
            particleEmitter.getGravity().setLow(snapshot[i].gravity.lowMin * scaleFactor, snapshot[i].gravity.lowMax * scaleFactor);

            particleEmitter.getWind().setHigh(snapshot[i].wind.highMin * scaleFactor, snapshot[i].wind.highMax * scaleFactor);
            particleEmitter.getWind().setLow(snapshot[i].wind.lowMin * scaleFactor, snapshot[i].wind.lowMax * scaleFactor);

            particleEmitter.getSpawnWidth().setHigh(snapshot[i].spawnWidth.highMin * scaleFactor, snapshot[i].spawnWidth.highMax * scaleFactor);
            particleEmitter.getSpawnWidth().setLow(snapshot[i].spawnWidth.lowMin * scaleFactor, snapshot[i].spawnWidth.lowMax * scaleFactor);

            particleEmitter.getSpawnHeight().setHigh(snapshot[i].spawnHeight.highMin * scaleFactor, snapshot[i].spawnHeight.highMax * scaleFactor);
            particleEmitter.getSpawnHeight().setLow(snapshot[i].spawnHeight.lowMin * scaleFactor, snapshot[i].spawnHeight.lowMax * scaleFactor);

            particleEmitter.getXOffsetValue().setLow(snapshot[i].xOffset.lowMin * scaleFactor, snapshot[i].xOffset.lowMax * scaleFactor);

            particleEmitter.getYOffsetValue().setLow(snapshot[i].yOffset.lowMin * scaleFactor, snapshot[i].yOffset.lowMax * scaleFactor);

            i++;
        }
    }

    class ScalableValues {
        public FourFloats scale = new FourFloats();
        public FourFloats velocity = new FourFloats();
        public FourFloats gravity = new FourFloats();
        public FourFloats wind = new FourFloats();
        public FourFloats spawnWidth = new FourFloats();
        public FourFloats spawnHeight = new FourFloats();
        public FourFloats xOffset = new FourFloats();
        public FourFloats yOffset = new FourFloats();

        class FourFloats {
            public float highMin;
            public float highMax;
            public float lowMin;
            public float lowMax;

        }
    }


}





















