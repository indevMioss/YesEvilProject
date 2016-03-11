package com.interdev.game.screens.game.entities.demons.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.spine.*;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.tools.ScalableEffect;
import com.interdev.game.tools.Utils;

public class Vis extends SpineBoss {
    private static final float DEFAULT_SCALE = 0.75f;

    private final AnimationState animationState;
    public final Skeleton skeleton;

    private final SkeletonRenderer skeletonRenderer;
    public SkeletonRendererDebug debugRenderer = new SkeletonRendererDebug();

    private Body body;
    private static final float BODY_SHAPE_RADIUS = 100;

    private static final int HIDE_ANIM_TRACK = 0;
    private static final int SHAKE_ANIM_TRACK = 1;
    private static final int SHOW_ANIM_TRACK = 2;

    private ScalableEffect rightEyeEffect, leftEyeEffect, bgFireEffect;

    public Vis(SkeletonRenderer skeletonRenderer) {
        this.skeletonRenderer = skeletonRenderer;

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("spine/vit2.atlas"));
        SkeletonJson json = new SkeletonJson(atlas);
        json.setScale(DEFAULT_SCALE / GameMain.PPM);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("spine/vit.json"));

        defineBody();

        skeleton = new Skeleton(skeletonData);
        AnimationStateData animationStateData = new AnimationStateData(skeletonData);
        animationState = new AnimationState(animationStateData);
        //animationState.setTimeScale(0.5f);
        //    skeleton.setFlipX(true);
        animationState.addAnimation(HIDE_ANIM_TRACK, "hide", false, 0);


        skeleton.updateWorldTransform();
        Vector2 offset = new Vector2();
        Vector2 size = new Vector2();

        skeleton.getBounds(offset, size);
        setSize(size.x, size.y);

        // body.setTransform(new Vector2(18, 5), 0);
        // body.applyLinearImpulse(new Vector2(-0.005f, 0), new Vector2(BODY_SHAPE_RADIUS, BODY_SHAPE_RADIUS), true);

        bgFireEffect = new ScalableEffect();
        bgFireEffect.load(Gdx.files.internal("effects/bosses/red_smoke.p"), Gdx.files.internal("effects"));
        bgFireEffect.setScale(1.5f * DEFAULT_SCALE / GameMain.PPM);
        bgFireEffect.setPosition(0, 0);

        leftEyeEffect = new ScalableEffect();
        leftEyeEffect.load(Gdx.files.internal("effects/bosses/vit_eye.p"), Gdx.files.internal("effects"));
        leftEyeEffect.setScale(0.9f * DEFAULT_SCALE / GameMain.PPM);
        leftEyeEffect.setPosition(0, 0);

        rightEyeEffect = new ScalableEffect();
        rightEyeEffect.load(Gdx.files.internal("effects/bosses/vit_eye.p"), Gdx.files.internal("effects"));
        rightEyeEffect.setScale(1.3f * DEFAULT_SCALE / GameMain.PPM);
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
                    }, Utils.getRand(1, 6));
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

    @Override
    public void act(float delta) {
        updateFloat(delta);
        calcLocalToPlayerDest(delta);


      //  body.setTransform(Player.inst.getX() + locToPlayerXdest, Player.inst.getY() + locToPlayerYdest, 0);


        float deltaX = (Player.inst.getX() + locToPlayerXdest) - body.getPosition().x;
        float deltaY = (Player.inst.getY() + locToPlayerYdest) - body.getPosition().y;

        Vector2 vec = new Vector2(deltaX, deltaY);
        vec.nor();
        vec.scl(10f);

        body.applyLinearImpulse(vec.x, vec.y, getWidth() / 2, getHeight() / 2, false);
        body.setLinearVelocity(body.getLinearVelocity().limit(10f));

        skeleton.setPosition(body.getTransform().getPosition().x,
                body.getTransform().getPosition().y + yInterpolatedOffset * FLOAT_AMPLITUDE - FLOAT_AMPLITUDE);

        leftEyeEffect.update(delta);
        leftEyeEffect.setPosition(skeleton.getX() - getWidth() * 0.235f, skeleton.getY() + getHeight() * 0.25f);

        rightEyeEffect.update(delta);
        rightEyeEffect.setPosition(skeleton.getX() + getWidth() * 0.04f, skeleton.getY() + getHeight() * 0.265f);

        bgFireEffect.update(delta);
        bgFireEffect.setPosition(skeleton.getX() + getWidth() * 0.25f, skeleton.getY() + 0.25f);

        animationState.update(delta * 1f);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
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

        body = GameScreen.world.createBody(bodyDef);
        MassData massData = new MassData();
        massData.mass = 10f;
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




















