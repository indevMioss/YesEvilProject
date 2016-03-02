package com.interdev.game.screens.game.entities.demons.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.spine.*;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.tools.ScalableParticleEffect;
import com.interdev.game.tools.Utils;

public class SpineBossVIS {
    private static final float DEFAULT_SCALE = 1.0f;

    private final AnimationState animationState;
    public final Skeleton skeleton;

    private Player player;
    private final SkeletonRenderer skeletonRenderer;
    private World world;
    public SkeletonRendererDebug debugRenderer = new SkeletonRendererDebug();

    private Body body;
    private static final float BODY_SHAPE_RADIUS = 100;

    private static final int HIDE_ANIM_TRACK = 0;
    private static final int SHAKE_ANIM_TRACK = 1;
    private static final int SHOW_ANIM_TRACK = 2;

    private ScalableParticleEffect rightEyeEffect, leftEyeEffect, bgFireEffect;

    public SpineBossVIS(String fileName, World world, Player player, SkeletonRenderer skeletonRenderer) {
        this.world = world;
        this.player = player;
        this.skeletonRenderer = skeletonRenderer;

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("spine/" + fileName + ".atlas"));
        SkeletonJson json = new SkeletonJson(atlas);
        json.setScale(DEFAULT_SCALE / GameMain.PPM);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("spine/" + fileName + ".json"));

        defineBody();

        skeleton = new Skeleton(skeletonData);
        AnimationStateData animationStateData = new AnimationStateData(skeletonData);
        animationState = new AnimationState(animationStateData);
        //animationState.setTimeScale(0.5f);
        //    skeleton.setFlipX(true);
        animationState.addAnimation(HIDE_ANIM_TRACK, "hide", false, 0);


        body.setTransform(new Vector2(18, 5), 0);

        body.applyLinearImpulse(new Vector2(-0.005f, 0), new Vector2(BODY_SHAPE_RADIUS, BODY_SHAPE_RADIUS), true);

        bgFireEffect = new ScalableParticleEffect();
        bgFireEffect.load(Gdx.files.internal("effects/bosses/red_smoke.p"), Gdx.files.internal("effects"));
        bgFireEffect.setScale(1 / GameMain.PPM);
        bgFireEffect.setPosition(0, 0);

        leftEyeEffect = new ScalableParticleEffect();
        leftEyeEffect.load(Gdx.files.internal("effects/bosses/vit_eye.p"), Gdx.files.internal("effects"));
        leftEyeEffect.setScale(0.8f / GameMain.PPM);
        leftEyeEffect.setPosition(0, 0);

        rightEyeEffect = new ScalableParticleEffect();
        rightEyeEffect.load(Gdx.files.internal("effects/bosses/vit_eye.p"), Gdx.files.internal("effects"));
        rightEyeEffect.setScale(1 / GameMain.PPM);
        rightEyeEffect.setPosition(0, 0);

        startAnimCycle();

    }


    private void startAnimCycle() {
        animationState.addAnimation(SHOW_ANIM_TRACK, "show", false, 0);
        animationState.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void event(int trackIndex, Event event) {

            }

            private int shakes = 0;

            @Override
            public void complete(int trackIndex, int loopCount) {
                if (trackIndex == SHAKE_ANIM_TRACK) shakes++;

                if (shakes < Utils.getRand(3, 16)) {
                    animationState.addAnimation(SHAKE_ANIM_TRACK, "shake", false, 0);
                } else {
                    animationState.addAnimation(HIDE_ANIM_TRACK, "hide", false, 0);
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            startAnimCycle();
                        }
                    }, Utils.getRand(1,6));
                    animationState.removeListener(this);
                }
            }

            @Override
            public void start(int trackIndex) {

            }

            @Override
            public void end(int trackIndex) {
            }
        });


    }

    private static final float FLOAT_AMPLITUDE = 100 / GameMain.PPM;

    private float yInterpolatedOffset;

    private float yRelOffset;
    private float relSpeed = 2f;
    private int dir = 1;

    private void updateFloat(float delta) {
        if (yRelOffset == 1 || yRelOffset == 0) dir *= -1;
        yRelOffset += relSpeed * delta * dir;

        yRelOffset = Math.max(0, yRelOffset);
        yRelOffset = Math.min(1, yRelOffset);

        yInterpolatedOffset = Interpolation.pow2.apply(yRelOffset);
    }

    private static final float BG_FIRE_X_OFFSET = 70 / GameMain.PPM;
    private static final float BG_FIRE_Y_OFFSET = 40 / GameMain.PPM;

    private static final float LEFT_EYE_X_OFFSET = -115 / GameMain.PPM;
    private static final float LEFT_EYE_Y_OFFSET = 120 / GameMain.PPM;

    private static final float RIGHT_EYE_X_OFFSET = -18 / GameMain.PPM;
    private static final float RIGHT_EYE_Y_OFFSET = 125 / GameMain.PPM;

    public void update(float delta) {
        updateFloat(delta);
        calcLocalToPlayerDest(delta);
        body.setTransform(player.getX() + locToPlayerXdest, player.getY() + locToPlayerYdest, 0);


        skeleton.setPosition(body.getTransform().getPosition().x,
                body.getTransform().getPosition().y + yInterpolatedOffset * FLOAT_AMPLITUDE - FLOAT_AMPLITUDE);
        leftEyeEffect.update(delta);
        leftEyeEffect.setPosition(skeleton.getX() + LEFT_EYE_X_OFFSET, skeleton.getY() + LEFT_EYE_Y_OFFSET);
        rightEyeEffect.update(delta);
        rightEyeEffect.setPosition(skeleton.getX() + RIGHT_EYE_X_OFFSET, skeleton.getY() + RIGHT_EYE_Y_OFFSET);
        bgFireEffect.update(delta);
        bgFireEffect.setPosition(skeleton.getX() + BG_FIRE_X_OFFSET, skeleton.getY() + BG_FIRE_Y_OFFSET);

        animationState.update(delta * 1f);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
    }


    public void draw(SpriteBatch batch) {
        bgFireEffect.draw(batch);
        skeletonRenderer.draw(batch, skeleton);
        leftEyeEffect.draw(batch);
        rightEyeEffect.draw(batch);
    }

    private void defineBody() {
        Vector2 randPosition = Utils.getNewWorldEdgePos();
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(randPosition.x, randPosition.y);
        bodyDef.gravityScale = 0;
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);
        MassData massData = new MassData();
        massData.mass = 0.01f;
        massData.center.set(BODY_SHAPE_RADIUS, BODY_SHAPE_RADIUS);
        body.setMassData(massData);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(BODY_SHAPE_RADIUS / GameMain.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_DEMONS; //setting self category

        fixtureDef.filter.maskBits = // Contacts with:
                WorldContactListener.BIT_CATEGORY_PLAYER |
                        WorldContactListener.BIT_CATEGORY_PLAYER_SHIELDS |
                        WorldContactListener.BIT_CATEGORY_BULLETS;

        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.DEMON, this));
    }


    private float locToPlayerXdest, locToPlayerYdest;

    private float relLtpYdest;
    private float locToPlayerDestRad = 600 / GameMain.PPM;
    private float ltpYSpeed = 40 / GameMain.PPM;
    private float ltpYDir = 1;

    private void updatePlayerLocalYDest(float delta) {
        if (relLtpYdest == 1 || relLtpYdest == 0) ltpYDir *= -1;
        relLtpYdest += ltpYSpeed * delta * ltpYDir;

        relLtpYdest = Math.max(relLtpYdest, 0);
        relLtpYdest = Math.min(relLtpYdest, 1);

        float interpolated = Interpolation.linear.apply(relLtpYdest);
        locToPlayerYdest = (interpolated - 0.5f) * 2 * locToPlayerDestRad;
        locToPlayerYdest = Math.min(locToPlayerYdest, locToPlayerDestRad / 2);
        locToPlayerYdest = Math.max(locToPlayerYdest, -locToPlayerDestRad / 2);
    }

    private void calcLocalToPlayerDest(float delta) {
        updatePlayerLocalYDest(delta);
        locToPlayerXdest = getCircleXPoint(locToPlayerYdest, locToPlayerDestRad);
    }


    private float getCircleXPoint(float y, float rad) {
        float x = (float) Math.sqrt(rad * rad - y * y);
        x = Math.min(x, rad);
        x = Math.max(x, rad / 2);
        return x;
    }


}




















