package com.interdev.game.screens.game.trophy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.tools.Utils;

public abstract class Trophy extends Actor implements Pool.Poolable {

    private static final float SELF_FREE_TIME = 20f;

    protected TrophySystem trophySystem;
    private final Pool<Trophy> myPool;

    public enum Type {
        RED_CRY,
        BLUE_CRY,
        LIVE,
        LIVES_BOTTLE,
        POWER_BOTTLE,
        RESURRECT_BOTTLE,
        TIME_BOTTLE,

        SHIELD_BONUS,
        SHARP_SHIELD_BONUS,

        MOVE_SPEED_BONUS,
        BOMB_BONUS,
        ATTACK_SPEED_BONUS,
        SLOW_ALL_BONUS,
        RESURRECT_BONUS
    }

    private Body body;
    private float BODY_SHAPE_RADIUS = 50f;

    private boolean justSpawned = false;
    protected Type type;

    public Trophy(TrophySystem trophySystem, Pool<? extends Trophy> myPool, World world) {
        this.trophySystem = trophySystem;
        this.myPool = (Pool<Trophy>) myPool;
        setVisible(false);
        defineBody(world);
    }

    private void defineBody(World world) {
        BodyDef bodyDef = new BodyDef();
        //bodyDef.position.set(randPosition.x, randPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0.01f;
        bodyDef.linearDamping = 0.75f;
        body = world.createBody(bodyDef);

        MassData massData = new MassData();
        massData.mass = 1f;
        body.setMassData(massData);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(BODY_SHAPE_RADIUS / GameMain.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_GOODS;
        fixtureDef.filter.maskBits = WorldContactListener.BIT_CATEGORY_PLAYER | WorldContactListener.BIT_CATEGORY_PLATFORM;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.TROPHY, this));
    }

    public Trophy setType(Type type) {
        this.type = type;
        return this;
    }

    private float selfFreeCounter = 0;

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isVisible()) return;
        setPosition(body.getPosition().x, body.getPosition().y);
        //System.out.println("333");
        selfFreeCounter += delta;
        if (selfFreeCounter >= SELF_FREE_TIME) {
            selfFreeCounter = 0;
            if (myPool != null) {
                myPool.free(this);
            } else {
                reset();
            }
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible()) return;
    }

    private void applyRandomImpulse() {
        float impulseX = Utils.getRand(-1, 1) * 4f;
        float impulseY = Utils.getRand(0, 1) * 3f;
        body.applyLinearImpulse(impulseX, impulseY, BODY_SHAPE_RADIUS, BODY_SHAPE_RADIUS, false);
    }


    private boolean changedPosition = false;

    public void go(float x, float y) {
        changedPosition = true;

        justSpawned = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                justSpawned = false;
            }
        }, 0.5f);

        body.setTransform(x, y, 0);
        setVisible(true);
        body.setActive(true);
        applyRandomImpulse();
    }


    @Override
    public void reset() {
        setVisible(false);
        body.setActive(false);
        body.setLinearVelocity(0, 0);
    }

    public void pickUp() {
        changedPosition = false;

        if (!isVisible() || justSpawned) return;
        setVisible(false);
        trophySystem.onPickedUp(type);

        GameScreen.addAfterWorldStepRunnable(new Timer.Task() {
            @Override
            public void run() {
                if (myPool != null) {
                    myPool.free(Trophy.this);
                } else {
                    if (!changedPosition) reset();
                }
            }
        });
    }
}
