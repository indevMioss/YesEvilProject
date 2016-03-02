package com.interdev.game.screens.game.entities.demons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.EffectsSystem;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.trophy.TrophySystem;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.sound.SoundSystem;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Demon extends Actor implements Pool.Poolable {

    private static final float MIN_INTER_BETWEEN_TAKING_DMG = 3f; // from 1 bullet
    protected float CALCULATE_DIRECTION_INTERVAL = 0.20f;
    protected float CHASING_SPEED_INCREASE = 0.5f;
    protected float CRAWL_RADIUS = 450 / GameMain.PPM;
    protected float CRAWL_MIN_INTERVAL = 5;

    protected boolean FLIPPABLE = true;
    protected boolean facingLeft = false;
    //protected float ADV_CHASE_MIN_RADIUS = 10;

    ////////////
    private float defaultLives;
    private float defaultDamage;
    private float defaultChasingImpulse;
    private float defaultMaxSpeed = 2f;
    private float defaultBodyShapeRadius;
    private float defaultMass;
    private float defaultScale;
    ////////////

    private float damage;
    protected float chasingImpulse;
    private float bodyShapeRadius;

    private float hitInterval = 2f;

    public float velocityTimeSlowFactor = 1f;
    private Pool<Demon> myPool;
    private float lives;
    private Body body;

    private List<UUID> sourcesOfDamage = new ArrayList<UUID>();

    private float XYSpeedRatio;

    public Demon(Monsters.Values values, Pool<? extends Demon> myPool) {

        defaultLives = values.DEFAULT_LIVES;
        defaultDamage = values.DEFAULT_DAMAGE;
        defaultChasingImpulse = values.DEFAULT_CHASING_IMPULSE;
        defaultMaxSpeed = values.DEFAULT_MAX_SPEED;
        defaultBodyShapeRadius = values.DEFAULT_BODY_SHAPE_RADIUS / GameMain.PPM;
        bodyShapeRadius = defaultBodyShapeRadius;
        defaultMass = values.DEFAULT_MASS;
        defaultScale = values.DEFAULT_SCALE;

        this.myPool = (Pool<Demon>) myPool;
        defineBody(values.DEFAULT_BODY_SHAPE_RADIUS);
        resetSpeed();
    }

    public void setAdditionalStr(float addHardness) {
        lives = defaultLives * (1 + addHardness);
        damage = defaultDamage * (1 + addHardness);
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        lives = defaultLives * scale;
        damage = defaultDamage * scale;
        scaleBody(scale);
        setMass(defaultMass * scale);
        resetSpeed();
    }

    private void scaleBody(float scale) {
        bodyShapeRadius = defaultBodyShapeRadius * scale;
        body.getFixtureList().get(0).getShape().setRadius(bodyShapeRadius);
    }

    private void setMass(float mass) {
        MassData massData = new MassData();
        massData.mass = mass;
        massData.center.set(body.getFixtureList().get(0).getShape().getRadius(), body.getFixtureList().get(0).getShape().getRadius());
        body.setMassData(massData);
    }

    private void defineBody(float bodyShapeRadius) {
        Vector2 randPosition = Utils.getNewWorldEdgePos();
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(randPosition.x, randPosition.y);
        bodyDef.gravityScale = 0;
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = GameScreen.world.createBody(bodyDef);

        MassData massData = new MassData();
        massData.mass = defaultMass;
        massData.center.set(bodyShapeRadius, bodyShapeRadius);
        body.setMassData(massData);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(bodyShapeRadius);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_DEMONS; //setting self category
        fixtureDef.filter.maskBits = // Contacts with:
                WorldContactListener.BIT_CATEGORY_PLAYER |
                        WorldContactListener.BIT_CATEGORY_PLAYER_SHIELDS |
                        WorldContactListener.BIT_CATEGORY_BULLETS;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.DEMON, this));

    }

    private void startRandAdvChaseActivation() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (Utils.roll(0.5f)) {
                    advancedChase = true;
                }
            }
        }, 0, 2f);
    }

    public void takeDamageFrom(final Bullet bullet) {
        if (sourcesOfDamage.contains(bullet.id) || sourcesOfDamage.contains(bullet.parentId)) return;

        bullet.target = this;
        sourcesOfDamage.add(bullet.id);

        if (bullet.parentId != null) sourcesOfDamage.add(bullet.parentId);

        if (bullet.type.punchForce > 0) {
            Vector2 delta = new Vector2(bullet.body.getLinearVelocity().x, bullet.body.getLinearVelocity().y);
            //float angle = (float) Math.toDegrees(Math.atan2(deltaDist.y, deltaDist.x));
            System.out.println("x " + delta.x);
            System.out.println("y " + delta.y);
            delta.nor();
            delta.scl(bullet.type.punchForce);
            body.applyLinearImpulse(delta, new Vector2(bodyShapeRadius, bodyShapeRadius), true);
        }


        bullet.onHit();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                sourcesOfDamage.remove(bullet.id);
                if (bullet.parentId != null) sourcesOfDamage.remove(bullet.parentId);
            }
        }, MIN_INTER_BETWEEN_TAKING_DMG);

        lives -= bullet.getDamage();


        if (lives <= 0) die();
    }

    public boolean readyToHitAgain = true;

    public void punchFromPlayer(float impulse) {
        Vector2 delta = new Vector2(getX() - Player.inst.getX(), getY() - Player.inst.getY());
        delta.nor();
        delta.scl(impulse);
        body.applyLinearImpulse(delta, new Vector2(bodyShapeRadius, bodyShapeRadius), true);

        readyToHitAgain = false;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                readyToHitAgain = true;
            }
        }, hitInterval);
    }

    private float checkDirectionTime = 0;
    private float timeFromLastCrawl = 0;

    private boolean advancedChase = true;
    private boolean initedAdvChase = false;

    private float playerPosFarPredictionX;
    private float playerPosFarPredictionY;

    private float switchAdvChaseBackCounter = 0;
    private float initAdvChaseCounter = 0;


    @Override
    public void act(float delta) {
        if (!isVisible()) return;
        super.act(delta);
        timeFromLastCrawl += delta;
        checkDirectionTime += delta;

        if (checkDirectionTime >= CALCULATE_DIRECTION_INTERVAL) {
            checkDirectionTime = 0;

            float destDeltaX = 0;
            float destDeltaY = 0;

            if (advancedChase) {
                float playerSecDeltaX = Player.inst.getX() - Player.inst.getXSecAgo();
                float playerSecDeltaY = Player.inst.getY() - Player.inst.getYSecAgo();

                if (!initedAdvChase) {
                    playerPosFarPredictionX = Player.inst.getX() + playerSecDeltaX * Utils.getRand(7.5f, 12.5f);
                    playerPosFarPredictionY = Player.inst.getY() + playerSecDeltaY * Utils.getRand(7.5f, 12.5f);
                    initedAdvChase = true;
                }
                float deltaXNow = Player.inst.getX() - getX();
                float deltaYNow = Player.inst.getY() - getY();
                float distSqNow = deltaXNow * deltaXNow + deltaYNow * deltaYNow;

                float playerPosSoonPredictionX = Player.inst.getX() + playerSecDeltaX;
                float playerPosSoonPredictionY = Player.inst.getY() + playerSecDeltaY;
                float distSqSoonSecPredict = (playerPosSoonPredictionX - getX()) * (playerPosSoonPredictionX - getX())
                        +
                        (playerPosSoonPredictionY - getY()) * (playerPosSoonPredictionY - getY());

                if (distSqNow >= distSqSoonSecPredict) advancedChase = false;

                if (initedAdvChase) {
                    initAdvChaseCounter += CALCULATE_DIRECTION_INTERVAL;
                    if (initAdvChaseCounter >= 0.1f) {
                        initAdvChaseCounter = 0;
                        initedAdvChase = false;
                    }
                    destDeltaX = playerPosFarPredictionX - getX();
                    destDeltaY = playerPosFarPredictionY - getY();
                }
            } else {
                switchAdvChaseBackCounter += CALCULATE_DIRECTION_INTERVAL;
                if (switchAdvChaseBackCounter >= 3f) {
                    switchAdvChaseBackCounter = 0;
                    advancedChase = true;
                }
                destDeltaX = Player.inst.getX() - getX();
                destDeltaY = Player.inst.getY() - getY();
            }
            Vector2 vec = new Vector2(destDeltaX * XYSpeedRatio, destDeltaY * (1 - XYSpeedRatio));
            vec.nor();

            float velocityX = chasingImpulse * vec.x * velocityTimeSlowFactor;
            float velocityY = chasingImpulse * vec.y * velocityTimeSlowFactor;

            body.applyLinearImpulse(velocityX, velocityY, defaultBodyShapeRadius / 2, defaultBodyShapeRadius / 2, true);
            body.setLinearVelocity(body.getLinearVelocity().limit(defaultMaxSpeed));

            if (FLIPPABLE) facingLeft = (destDeltaX < 0);
        }
    }

    private void makeSoundIfPlayerNear(float deltaX, float deltaY) {
        if (timeFromLastCrawl >= CRAWL_MIN_INTERVAL && Math.abs(deltaX) + Math.abs(deltaY) <= CRAWL_RADIUS) {
            timeFromLastCrawl = 0;
            SoundSystem.inst.playSound(SoundSystem.Sounds.EVIL_CRAWL);
        }
    }

    @Override
    public float getX() {
        return getBody().getPosition().x;
    }

    @Override
    public float getY() {
        return getBody().getPosition().y;
    }

    public float getDamageVal() {
        return damage;
    }

    public Body getBody() {
        return body;
    }

    public void speedUp() {
        chasingImpulse += CHASING_SPEED_INCREASE;
    }

    public void resetSpeed() {
        float revScale = 1 - getScaleX();
        chasingImpulse = defaultChasingImpulse * (1 + revScale * 1.5f) * Utils.getRand(0.8f, 1.2f);
        XYSpeedRatio = Utils.getRand(0.25f, 0.75f);
    }

    public void die() {
        EffectsSystem.inst.doDemonBlow(this);
        TrophySystem.inst.spawnAfterStep(getX(), getY(), this);
        GameScreen.addAfterWorldStepRunnable(new Runnable() {
            @Override
            public void run() {
                getBody().setActive(false);
            }
        });
        setVisible(false);
        DemonsSystem.inst.removeDemon(this);
        myPool.free(this);
    }

    @Override
    public void reset() {
        resetSpeed();
        lives = defaultLives;
        GameScreen.addAfterWorldStepRunnable(new Runnable() {
            @Override
            public void run() {
                getBody().setTransform(Utils.getNewWorldEdgePos(), getBody().getAngle());
            }
        });
    }

    public void go() {
        getBody().setActive(true);
        setVisible(true);
    }

    public void setDefaultDamage(float defaultDamage) {
        this.defaultDamage = defaultDamage;
        damage = defaultDamage;
    }


}
