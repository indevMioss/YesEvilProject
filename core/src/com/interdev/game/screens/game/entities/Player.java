package com.interdev.game.screens.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.attack.Aim;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.sound.SoundSystem;
import com.interdev.game.tools.ActionListener;
import com.interdev.game.tools.BooleanArgChangeListener;
import com.interdev.game.tools.OffsetAnimation;

public class Player extends AnimatedB2dActor {
    public static Player inst;

    private static final float MAX_SPEED_FACTOR = 1f;
    private static final float MIN_SPEED_FACTOR = 0.6f;

    private static float SPEED_FACTOR = MAX_SPEED_FACTOR;
    public static final float JUMP_DELAY = 0.8f;
    private static final float MAX_SPEED_X = 25f;
    private static final float JUMP_IMPULSE = 13f;
    private static final float MOVE_IMPULSE = 15f;

    private static final float SPEED_BONUS = 1.5f;

    public static float PUNCH_IMPULSE = 80f;


    private static final float FEET_FRICTION_RATE = 0.95f;
    private static final float BODY_SHAPE_RADIUS = 30f;
    private static final float BODY_SHIELD_RADIUS = 80f;

    private static final float SHARP_SHIELD_TIME = 4;
    private static final float SHIELD_TIME = 6;


    private static final float DEFAULT_SCALE = 1.5f;

    private Vector2 startPosition = new Vector2(800 / GameMain.PPM, 700 / GameMain.PPM);
    private boolean canJump = true;

    public boolean facingRightSide = true;

    private Body body, shieldBody;
    private World world;
    private boolean wannaChangePosition;
    private Aim aim;
    private SoundSystem soundSystem;

    private float maxUpFloatSpeed = 3f;
    public boolean floatingMode = true;

    private BooleanArgChangeListener flipListener;

    public static boolean hasResurrection = false;
    public static boolean hasMoveSpeedBonus = false;
    public static boolean hasShield = false;
    public static boolean hasSharpShield = false;

    public Player(Array<TextureAtlas.AtlasRegion> regions, World world, Aim aim, SoundSystem soundSystem) {
        super(new OffsetAnimation(0.04f, regions, OffsetAnimation.PlayMode.LOOP), DEFAULT_SCALE);
        this.world = world;
        this.aim = aim;
        this.soundSystem = soundSystem;
        defineBody();
        inst = this;
    }

    private void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(startPosition.x, startPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 2f;
        body = world.createBody(bodyDef);

        bodyDef.position.set(startPosition.x, startPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0f;

        shieldBody = world.createBody(bodyDef);

        float bRadius = BODY_SHAPE_RADIUS / GameMain.PPM;
        float offset = BODY_SHAPE_RADIUS * 0.5f / GameMain.PPM;

        Vector2[] shapeVect = new Vector2[3];
        PolygonShape topTriangle = new PolygonShape();
        shapeVect[0] = new Vector2(-bRadius, bRadius + offset);
        shapeVect[1] = new Vector2(bRadius, bRadius + offset);
        shapeVect[2] = new Vector2(0, offset);
        topTriangle.set(shapeVect);

        PolygonShape leftTriangle = new PolygonShape();
        shapeVect[0] = new Vector2(-bRadius - offset, bRadius);
        shapeVect[1] = new Vector2(-bRadius - offset, -bRadius);
        shapeVect[2] = new Vector2(-offset, 0);
        leftTriangle.set(shapeVect);

        PolygonShape rightTriangle = new PolygonShape();
        shapeVect[0] = new Vector2(bRadius + offset, bRadius);
        shapeVect[1] = new Vector2(bRadius + offset, -bRadius);
        shapeVect[2] = new Vector2(offset, 0);
        rightTriangle.set(shapeVect);

        PolygonShape bottomTriangle = new PolygonShape();
        shapeVect[0] = new Vector2(-bRadius, -bRadius - offset);
        shapeVect[1] = new Vector2(bRadius, -bRadius - offset);
        shapeVect[2] = new Vector2(0, -offset);
        bottomTriangle.set(shapeVect);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_PLAYER;

        fixtureDef.shape = topTriangle;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.PLAYER, this));

        fixtureDef.shape = leftTriangle;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.PLAYER, this));

        fixtureDef.shape = rightTriangle;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.PLAYER, this));

        fixtureDef.shape = bottomTriangle;
        fixtureDef.friction = FEET_FRICTION_RATE;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.PLAYER_FEET, this));

        CircleShape shape = new CircleShape();
        shape.setRadius(BODY_SHIELD_RADIUS / GameMain.PPM);
        fixtureDef.shape = shape;

        fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_PLAYER_SHIELDS;
        // fixtureDef.isSensor = true;
        fixtureDef.filter.maskBits = WorldContactListener.BIT_CATEGORY_DEMONS;
        shieldBody.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.SHIELD, this));
    }

    public boolean goingBackwards = false;
    public boolean walkingLeft = false;
    public boolean walkingRight = false;

    @Override
    public TextureAtlas.AtlasSprite getFrame(float stateTime) {
        TextureAtlas.AtlasSprite region = animation.getKeyFrame(stateTime);
        if (aim.destAlpha != 0) {
            if (((aim.getRotation() >= 90 && aim.getRotation() < 270) || !facingRightSide) && !region.isFlipX()) {
                region.flip(true, false);
                facingRightSide = false;
                // goingBackwards = body.getLinearVelocity().x > 1.5f;
            } else if (((aim.getRotation() < 90 || aim.getRotation() >= 270) || facingRightSide) && region.isFlipX()) {
                region.flip(true, false);
                facingRightSide = true;
                // goingBackwards = body.getLinearVelocity().x < -1.5f;
            }
        } else {
            if ((body.getLinearVelocity().x < -1.5f || !facingRightSide) && !region.isFlipX()) {
                region.flip(true, false);
                facingRightSide = false;

            } else if ((body.getLinearVelocity().x > 1.5f || facingRightSide) && region.isFlipX()) {
                region.flip(true, false);
                facingRightSide = true;
            }
        }

        walkingLeft = body.getLinearVelocity().x < -1.5f;
        walkingRight = body.getLinearVelocity().x > 1.5f;
        goingBackwards = facingRightSide && walkingLeft || !facingRightSide && walkingRight;

        if (facedRightSideFrameAgo != facingRightSide) {
            facedRightSideFrameAgo = facingRightSide;
            //if (flipListener != null)flipListener.onValueChange(facingRightSide);
        }

        return region;
    }

    private boolean facedRightSideFrameAgo = facingRightSide;

    public void setFlipListener(BooleanArgChangeListener listener) {
        flipListener = listener;
    }

    private float jumpTimeCounter = 0;


    private float xSecAgo = 0;
    private float ySecAgo = 0;

    public float getXSecAgo() {
        return xSecAgo;
    }

    public float getYSecAgo() {
        return ySecAgo;
    }

    private float secAgoCounter = 0;

    @Override
    public void act(float delta) {
        super.act(delta);
        secAgoCounter += delta;

        if (secAgoCounter >= 1f) {
            secAgoCounter = 0;
            xSecAgo = getX();
            ySecAgo = getY();
        }

        if (!canJump) {
            jumpTimeCounter += delta;
            if (jumpTimeCounter >= JUMP_DELAY) {
                jumpTimeCounter = 0;
                canJump = true;
            }
        }

        shieldBody.setTransform(getX(), getY(), 0);
        if (wannaChangePosition) {
            body.setTransform(startPosition, body.getAngle());
            wannaChangePosition = false;
        }
    }

    public void resetPosition() {
        wannaChangePosition = true;
    }


    public void restoreJumps() {
        canJump = true;
    }

    public void floatUp(float accelFactor) {
        body.setLinearVelocity(body.getLinearVelocity().x, maxUpFloatSpeed * accelFactor);
    }

    public void jump() {
        if (floatingMode) {
            floatUp(1f);
            return;
        }
        if (canJump) {
            canJump = false;
            body.setLinearVelocity(body.getLinearVelocity().x, JUMP_IMPULSE);
            soundSystem.playSound(SoundSystem.Sounds.JUMP);
        }

    }

    public void move(float accelFactor) {
        float signum = Math.signum(accelFactor);
        float value = Math.min(Math.abs(accelFactor / 2f), 1f);

        SPEED_FACTOR = goingBackwards ? MIN_SPEED_FACTOR : MAX_SPEED_FACTOR;

        if (hasMoveSpeedBonus) SPEED_FACTOR *= SPEED_BONUS;

        if (signum > 0 && body.getLinearVelocity().x <= MAX_SPEED_X * SPEED_FACTOR) {
            body.setLinearVelocity(new Vector2(MOVE_IMPULSE * SPEED_FACTOR * value, body.getLinearVelocity().y));
        } else if (signum < 0 && body.getLinearVelocity().x > -MAX_SPEED_X * SPEED_FACTOR) {
            body.setLinearVelocity(new Vector2(-MOVE_IMPULSE * SPEED_FACTOR * value, body.getLinearVelocity().y));
        }
    }

    public void moveRight() {
        if (body.getLinearVelocity().x <= MAX_SPEED_X) {
            body.applyLinearImpulse(new Vector2(1, 0f), body.getWorldCenter(), true);
        }
    }

    public void moveLeft() {
        if (body.getLinearVelocity().x >= -MAX_SPEED_X) {
            body.applyLinearImpulse(new Vector2(-1, 0f), body.getWorldCenter(), true);
        }
    }

    @Override
    public Body getBody() {
        return body;
    }

    public boolean isWalking() {
        return walkingRight || walkingLeft;
    }

    private Timer sharpShieldResetTimer = new Timer();
    public void activateSharpShield() {
        sharpShieldResetTimer.clear();
        hasSharpShield = true;
        sharpShieldResetTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                hasSharpShield = false;
                activateShield();
            }
        }, SHARP_SHIELD_TIME);
    }


    private Timer shieldResetTimer = new Timer();
    public void activateShield() {
        shieldResetTimer.clear();
        hasShield = true;
        shieldResetTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                hasShield = false;
            }
        }, SHIELD_TIME);
    }
}
