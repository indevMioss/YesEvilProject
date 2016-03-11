package com.interdev.game.screens.game.attack.ultimate.aiming;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.OneFloatChangeListener;
import com.interdev.game.tools.ScalableEffect;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Forcemeter extends Group implements Disposable {

    private static final float PREPARE_TIME = 1f;
    private static final float AFTERWARD_TIME = 3;
    private static final float DEFAULT_SPAWN_INTERVAL = 0.3f;
    private static final float SPAWN_INTERVAL_DISPERSION = 0.8f;
    private final Collar collar;

    private final ScalableEffect attractorEffect;

    private final float effectDefaultLowEmission;
    private final float effectEmissionDelta;

    private OneFloatChangeListener onCompleteListener;

    private int particlesCollected;
    private int particlesSpawned;

    private Pool<Particle> particlePool;
    public boolean active;

    private Random random = new Random();

    private Image catchImage;
    private Image goImage;

    public Forcemeter(final Array<TextureAtlas.AtlasRegion> particleAnimFrames) {
        setBounds(0, 0, GameScreen.hudWidth, GameScreen.hudHeight);
        particlesCollected = 0;
        particlesSpawned = 0;

        Texture goTexture = new Texture("attractor/go.png");
        Texture catchTexture = new Texture("attractor/catch.png");

        goImage = new Image(goTexture);
        catchImage = new Image(catchTexture);

        goImage.setPosition(getWidth()/2 - goImage.getWidth()/2, getHeight()*0.75f - goImage.getHeight()/2);
        catchImage.setPosition(getWidth()/2 - catchImage.getWidth()/2, getHeight()*0.75f - catchImage.getHeight()/2);

        goImage.setVisible(false);
        catchImage.setVisible(false);



        Texture collarTexture = new Texture("attractor/brass.png");
        Texture bgTexture = new Texture("attractor/dark_bg.png");
        Utils.applyLinearFilter(collarTexture, bgTexture);

        Image bg = new Image(bgTexture);
        bg.setPosition(getWidth() / 2 - bg.getWidth() / 2, getHeight() / 2 - bg.getHeight() / 2);
        bg.setOrigin(bg.getWidth() / 2, bg.getHeight() / 2);
        bg.setScale(8f);
        //  addActor(bg);

        final Texture particleTexture = new Texture(Gdx.files.internal("attractor/particle.png"));
        Utils.applyLinearFilter(particleTexture);

        final ArrayList<Particle> existingParticlesList = new ArrayList<Particle>();
        particlePool = new Pool<Particle>() {
            @Override
            protected Particle newObject() {
                Particle particle = new Particle(particleAnimFrames, particlePool);
                particle.reset();
                addActor(particle);
                existingParticlesList.add(particle);
                return particle;
            }
        };

        attractorEffect = new ScalableEffect();
        attractorEffect.load(Gdx.files.internal("effects/ultimate/attractor.p"), Gdx.files.internal("effects"));
        attractorEffect.setPosition(getWidth() / 2, getHeight() / 2);

        effectDefaultLowEmission = attractorEffect.getEmitters().get(0).getEmission().getLowMin();
        effectEmissionDelta = attractorEffect.getEmitters().get(0).getEmission().getHighMin() -
                attractorEffect.getEmitters().get(0).getEmission().getLowMin();
        attractorEffect.start();

        collar = new Collar(collarTexture, existingParticlesList);
        collar.setPosition(getWidth() / 2 - collar.getWidth() / 2, getHeight() / 2 - collar.getHeight() / 2);
        addActor(collar);
    }

    public void start(OneFloatChangeListener onCompleteListener) {
        active = true;

        this.onCompleteListener = onCompleteListener;

        particlesSpawned = 0;
        particlesCollected = 0;
        resetEffect();

        collar.setVisible(true);
        spawnParamsCounter = 0;

        scheduleSpawn(PREPARE_TIME);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        attractorEffect.update(delta);
    }

    private float lastPowerValue = 0;

    private void setEffectPower(float value) { // 0..1f can not decrease, needs to be reset each time before growth
        value = Math.max(0f, value);
        value = Math.min(1f, value);

        attractorEffect.setScale(1 + value / 2);

        if (value <= 0.5f) {
            attractorEffect.getEmitters().get(0).getEmission().setLowMin(
                    attractorEffect.getEmitters().get(0).getEmission().getLowMin() +
                            (value * 2) * effectEmissionDelta
            );
        } else {
            attractorEffect.getEmitters().get(0).getLife().setHighMin(
                    (1 - (value - 0.5f) * 2) * attractorEffect.getEmitters().get(0).getLife().getHighMax()
            );
        }
    }

    private void resetEffect() {
        attractorEffect.reset();

        attractorEffect.getEmitters().get(0).getEmission().setLowMin(effectDefaultLowEmission);
        attractorEffect.getEmitters().get(0).getLife().setHighMin(attractorEffect.getEmitters().get(0).getLife().getHighMax());

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        attractorEffect.draw(batch);
    }

    private void spawn(InterpolationBundle interpolation) {
        particlesSpawned++;
        particlePool.obtain().go(interpolation);
    }


    private final float[] spawnDelays = {
            1.2f,
            1.0f,
            0.8f,
            0.6f,
            0.4f
    };

    private final float[] spawnsNum = {
            1,
            1,
            1,
            2,
            4
    };

    private final int PARTICLES_IN_WAVE = 3;
    private float totalParticlesToSpawn = 0;

    {
        for (float spawnNum : spawnsNum) {
            totalParticlesToSpawn += spawnNum * PARTICLES_IN_WAVE;
        }
    }

    int spawnParamsCounter = 0;
    int localSpawnCounter = 0;

    private void scheduleSpawn(float delay) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                InterpolationBundle interpolationBundle = getRandInterpolationBundle();

                for (int i = 0; i < PARTICLES_IN_WAVE; i++) {
                    spawn(interpolationBundle);
                }

                if (localSpawnCounter < spawnsNum[spawnParamsCounter]) {
                    localSpawnCounter++;
                } else {
                    localSpawnCounter = 0;
                    spawnParamsCounter++;
                    if (spawnParamsCounter >= spawnDelays.length) {
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                end();
                            }
                        }, AFTERWARD_TIME);
                        return;
                    }
                }

                scheduleSpawn(spawnDelays[spawnParamsCounter]);
            }
        }, delay);
    }


    private void end() {
        active = false;
        float successFactor = (float) particlesCollected / totalParticlesToSpawn;
        System.out.println("success FACTOR " + successFactor);
        collar.setVisible(false);

        onCompleteListener.onValueChange(successFactor);
    }

    private float getRandSpawnInterval() {
        return Utils.randomizeVal(DEFAULT_SPAWN_INTERVAL, SPAWN_INTERVAL_DISPERSION);
    }


    private List<InterpolationBundle> interpolationsMap = new ArrayList<InterpolationBundle>();

    {
        //    interpolationsMap.add(new InterpolationBundle(Interpolation.linear, 0.4f));
        //     interpolationsMap.add(new InterpolationBundle(Interpolation.linear, 0.5f));
        interpolationsMap.add(new InterpolationBundle(Interpolation.linear, 0.35f));
        //    interpolationsMap.add(new InterpolationBundle(Interpolation.circle, 1.0f));
        // interpolationsMap.add(new InterpolationBundle(Interpolation.exp5, 1.2f));
        //    interpolationsMap.add(new InterpolationBundle(Interpolation.pow2, 0.5f));
        //  interpolationsMap.add(new InterpolationBundle(Interpolation.pow3, 0.75f));
        //   interpolationsMap.add(new InterpolationBundle(Interpolation.pow4, 0.98f));
        //   interpolationsMap.add(new InterpolationBundle(Interpolation.pow5, 1.1f));
    }

    private InterpolationBundle getRandInterpolationBundle() {
        int randInt = random.nextInt(interpolationsMap.size());
        return interpolationsMap.get(randInt);
    }

    public class InterpolationBundle {
        public Interpolation interpolation;
        public float travelTimeFactor;

        public InterpolationBundle(Interpolation interpolation, float travelTimeFactor) {
            this.interpolation = interpolation;
            this.travelTimeFactor = travelTimeFactor;
        }
    }


    public void onTouchDown() {
        particlesCollected += collar.collapse();
        float factor = (float) particlesCollected / totalParticlesToSpawn;
        setEffectPower(factor);
    }

    @Override
    public void dispose() {
        particlePool.clear();
        clearChildren();
    }

}
