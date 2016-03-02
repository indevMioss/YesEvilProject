package com.interdev.game.screens.game.attack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.tools.ActionListener;
import com.interdev.game.tools.ScalableParticleEffect;

import java.util.UUID;

public abstract class Bullet extends Actor implements Pool.Poolable {

    public Actor target;
    public BulletSystem.Type type;

    public int projectilesPerShot = 1;
    public float strictAngleSpray;

    public float getApproxAngleSpray() {
        return strictAngleSpray;
    }

    public float requiresStamina = 1f;
    public float bodyShapeRadius = 10f;
    public float bodyImpulse = 20f;
    public float selfFreeAfter = 3f; //sec

    protected float defaultDamage = 200;
    private float damageMultiplier = 1f;

    protected int amountOfEnemiesCanSpear = 1;
    protected int amountOfSpearedEnemies;

    public Body body;
    private ScalableParticleEffect particleEffect;
    protected Pool<Bullet> myPool;

    protected ActionListener hitListener;

    public UUID id;
    public UUID parentId;

    private float angle;

    private boolean goingToTemporalPoint = true;
    public boolean passedTemporalPoint = false;

    private boolean waitingForLaunch = false;
    private boolean waitingForDeactivation = false;
    private float bodyTransformDestX, bodyTransformDestY;
    private float removalCounter;


    public Bullet(BulletSystem.Type type, World world, String effectFile, String imagesDir, Pool<? extends Bullet> myPool) {
        this.type = type;

        requiresStamina = type.ammoCost;
        bodyShapeRadius = type.bodyShapeRadius / GameMain.PPM;
        bodyImpulse = type.bodyImpulse;
        defaultDamage = type.damage;

        id = UUID.randomUUID();
        parentId = null;
        this.myPool = (Pool<Bullet>) myPool;
        body = defineBody(world);

        particleEffect = new ScalableParticleEffect();
        particleEffect.load(Gdx.files.internal(effectFile), Gdx.files.internal(imagesDir));
        particleEffect.setScale(1 / GameMain.PPM);
        particleEffect.start();
    }

    public void setHitListener(ActionListener hitActionListener) {
        hitListener = hitActionListener;
    }


    private boolean initedDestTemporalPoint = false;
    private Vector2 destTemporalPoint;

    private Vector2 totalDeltaDistance;
    private Vector2 start;


    @Override
    public void act(float delta) {
        super.act(delta);

        if (waitingForDeactivation) {
            body.setActive(false);
            waitingForDeactivation = false;
            return;
        } else if (waitingForLaunch) {
            if (!passedTemporalPoint) {
                preLaunch();
                goingToTemporalPoint = true;
                initedDestTemporalPoint = false;
            } else {
                preLaunch();
                launch();
            }
            waitingForLaunch = false;
            return;
        }
        if (!isVisible()) return;

        if (goingToTemporalPoint) {
            if (!initedDestTemporalPoint) {
                destTemporalPoint = getTemporalMovePoint(angle);
                start = new Vector2(getX(), getY());
                totalDeltaDistance = new Vector2(destTemporalPoint.x - getX(), destTemporalPoint.y - getY());
                initedDestTemporalPoint = true;
            }

            boolean interpolatingX = true;


            // float newX = Interpolation.circle.apply(start.x, destTemporalPoint.x, 0.25f);
            // float newY = Interpolation.circle.apply(start.y, destTemporalPoint.y, 0.25f);

            float velocity = 10;
            float deltaX = destTemporalPoint.x - getX();
            float deltaY = destTemporalPoint.y - getY();

            Vector2 vec = new Vector2(deltaX, deltaY);
            vec.nor();
            vec.scl(velocity * delta);

            float newX, newY;
            if ((angle >= 315 || angle < 45) || (angle >= 135 && angle < 225)) {
                newY = getY() + vec.y;
                newX = Interpolation.pow2In.apply(
                        start.x, destTemporalPoint.x,
                        (newY - start.y) / totalDeltaDistance.y
                );

            } else {
                newY = getY() + vec.y;
                newX = Interpolation.pow4.apply(
                        start.x, destTemporalPoint.x,
                        (newY - start.y) / totalDeltaDistance.y
                );
            }
            body.setTransform(newX, newY, 0);

            if (Math.abs(deltaX) <= 0.1f && Math.abs(deltaY) <= 0.1f) {
                goingToTemporalPoint = false;
                passedTemporalPoint = true;
                launch();
            }
        }

        particleEffect.update(delta);
        particleEffect.setPosition(getX(), getY());

        removalCounter += delta;
        if (removalCounter >= selfFreeAfter) {
            freeSelf();
        }
    }

    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        particleEffect.draw(batch);
    }

    protected Body defineBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0f;
        Body body = world.createBody(bodyDef);

        // MassData massData = new MassData();
        // massData.mass = 10;
        // body.setMassData(massData);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(bodyShapeRadius);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_BULLETS;
        fixtureDef.filter.maskBits = WorldContactListener.BIT_CATEGORY_DEMONS;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.BULLET, this));
        return body;
    }

    public void setEffectScale(float scale) {
        //System.out.println("power " + scale);
        particleEffect.setScale((1 + scale / 2) / GameMain.PPM);
    }

    private void preLaunch() {
        body.setActive(true);
        body.setTransform(bodyTransformDestX, bodyTransformDestY, 0);
        particleEffect.reset();
        particleEffect.setPosition(getX(), getY());
        setVisible(true);
    }


    private void launch() {
        Vector2 norVector = new Vector2();
        norVector.x = (float) Math.cos(Math.toRadians(angle));
        norVector.y = (float) Math.sin(Math.toRadians(angle));
        norVector.scl(bodyImpulse);
        body.applyLinearImpulse(norVector.x, norVector.y, bodyShapeRadius / 2, bodyShapeRadius / 2, true);

    }


    private Vector2 getTemporalMovePoint(float aimAngle) {
        Vector2 vec = new Vector2();
        float radius = Player.inst.getWidth() * 0.6f;
        vec.x = (float) (Player.inst.getX() + radius * Math.cos(Math.toRadians(aimAngle)));
        vec.y = (float) (Player.inst.getY() + radius * Math.sin(Math.toRadians(aimAngle)));
        return vec;
    }


    public void launchAfterWorldStep(float x, float y, Actor target) {
        this.target = target;
        float angle = (float) Math.toDegrees(Math.atan2(target.getY() - getY(), target.getX() - getX()));
        if (angle < 0) angle += 360;

        launchAfterWorldStep(x, y, angle);
    }


    public void launchAfterWorldStep(float x, float y, float angle) {
        bodyTransformDestX = x;
        bodyTransformDestY = y;
        this.angle = angle;
        waitingForLaunch = true;
    }

    protected void disappear() {
        waitingForDeactivation = true;
    }

    public float getDamage() {
        return defaultDamage * damageMultiplier;
    }


    public void freeSelf() {
        //     Log.log(j + " was made free, removalCounter " + removalCounter, Log.tag.A);
        myPool.free(this);
    }

    public void onHit() {
        amountOfSpearedEnemies++;
        hitListener.actionPerformed();
        if (amountOfSpearedEnemies >= amountOfEnemiesCanSpear) {
            freeSelf();
        }
    }

    public float getAngle() {
        return angle;
    }

    @Override
    public void reset() {
        setVisible(false);
        passedTemporalPoint = false;
        goingToTemporalPoint = false;
        target = null;
        disappear();
        id = UUID.randomUUID();
        parentId = null;
        removalCounter = 0;
        amountOfSpearedEnemies = 0;
        body.setLinearVelocity(0, 0);
    }

}

















