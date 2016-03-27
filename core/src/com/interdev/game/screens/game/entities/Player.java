package com.interdev.game.screens.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.attack.Aim;
import com.interdev.game.screens.game.hud.gui.AmmoVisual;
import com.interdev.game.screens.game.hud.stamina.StaminaOrbits;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.sound.SoundSystem;
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
    private boolean wannaChangePosition;

    private float maxUpFloatSpeed = 3f;
    public boolean floatingMode = true;

    private BooleanArgChangeListener flipListener;

    public static boolean hasResurrection = false;
    public static boolean hasMoveSpeedBonus = false;
    public static boolean hasShield = false;
    public static boolean hasSharpShield = false;

    public boolean shumpoJumping = false;
    private float maxShumpoJumpTime = 0.25f;
    private float shumpoJumpSpeed = 100;

    private float shumpoJumpTime;
    private float shumpoJumpTimeCounter;
    public boolean hitInWall = false;
    private Vector2 shumpoDirVector = new Vector2();

    private OffsetAnimation shumpoAnim;

    public Player(Array<TextureAtlas.AtlasRegion> regions, Array<TextureAtlas.AtlasRegion> shumpoRegions) {
        super(new OffsetAnimation(0.04f, regions, OffsetAnimation.PlayMode.LOOP), DEFAULT_SCALE);
        defineBody();

        shumpoAnim = new OffsetAnimation(0.02f, shumpoRegions, OffsetAnimation.PlayMode.LOOP);

        inst = this;
    }

    private void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(startPosition.x, startPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 2f;
        body = GameScreen.world.createBody(bodyDef);

        MassData massData = new MassData();
        massData.mass = 50f;
        body.setMassData(massData);

        bodyDef.position.set(startPosition.x, startPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0f;

        shieldBody = GameScreen.world.createBody(bodyDef);

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

        body.setBullet(true);

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
        TextureAtlas.AtlasSprite region;
        if (!shumpoJumping) {
            region = animation.getKeyFrame(stateTime);
            if (Aim.inst.destAlpha != 0) {
                if (((Aim.inst.getRotation() >= 90 && Aim.inst.getRotation() < 270) || !facingRightSide) && !region.isFlipX()) {
                    region.flip(true, false);
                    facingRightSide = false;
                    // goingBackwards = body.getLinearVelocity().x > 1.5f;
                } else if (((Aim.inst.getRotation() < 90 || Aim.inst.getRotation() >= 270) || facingRightSide) && region.isFlipX()) {
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
        } else {
            region = shumpoAnim.getKeyFrame(stateTime);
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
    protected boolean drawWithRotation() {
        return shumpoJumping;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        secAgoCounter += delta;

        if (secAgoCounter >= 1f) {
            secAgoCounter = 0;
            xSecAgo = getX();
            ySecAgo = getY();
        }

        if (!floatingMode || !canJump) {
            jumpTimeCounter += delta;
            if (jumpTimeCounter >= JUMP_DELAY) {
                jumpTimeCounter = 0;
                canJump = true;
            }
        }

        if (hitInWall) {
            hitInWall = false;
            stopShumpo();
        }

        if (shumpoJumping) {
            shumpoJumpTimeCounter += delta;

            float complete = shumpoJumpTimeCounter / shumpoJumpTime;

            float zoomDiff = 0.5f;
            GameScreen.inst.changeZoom(GameScreen.DEFAULT_ZOOM - (0.5f - Math.abs(complete - 0.5f)) * 2 * zoomDiff);

            Vector2 vec = new Vector2(shumpoDirVector.x, shumpoDirVector.y);
            vec.nor();
            vec.scl(shumpoJumpSpeed * Interpolation.linear.apply(complete));
            body.setLinearVelocity(vec);

            if (shumpoJumpTimeCounter >= shumpoJumpTime) {
                stopShumpo();
            }
        }

        shieldBody.setTransform(getX(), getY(), 0);
        if (wannaChangePosition) {
            body.setTransform(startPosition, body.getAngle());
            wannaChangePosition = false;

        }
    }

    private void stopShumpo() {
        setShumpoJumping(false);
        shumpoJumpTimeCounter = 0;
        body.setLinearVelocity(0, 0);
        GameScreen.inst.changeZoom(GameScreen.DEFAULT_ZOOM);

    }

    public void setShumpoJumping(boolean shumpo) {
        shumpoJumping = shumpo;
        if (shumpo) {
            StaminaOrbits.inst.compact();
            AmmoVisual.inst.setVisible(false);
        } else {
            setRotation(0);
            StaminaOrbits.inst.uncompact();
            AmmoVisual.inst.setVisible(true);
        }
    }


    public void shumpoJump(float angle, float distanceRate) {
        System.out.println(angle);
        System.out.println(distanceRate);
        shumpoJumpTime = maxShumpoJumpTime * distanceRate;
        shumpoDirVector.x = (float) Math.cos(angle);
        shumpoDirVector.y = (float) Math.sin(angle);
        facingRightSide = (shumpoDirVector.x > 0);
        angle = (float) Math.toDegrees(angle);
        angle += 180;
        if (angle >= 360) angle -= 360;
        setRotation(angle);
        setShumpoJumping(true);
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
            SoundSystem.inst.playSound(SoundSystem.Sounds.JUMP);
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
