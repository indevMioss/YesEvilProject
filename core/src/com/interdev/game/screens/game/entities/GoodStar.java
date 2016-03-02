package com.interdev.game.screens.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.EffectsSystem;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.WorldContactListener;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.tools.OffsetAnimation;

import java.util.Random;

public class GoodStar extends AnimatedB2dActor {

    public float containsStamina = 2.5f;

    public enum Type {
        DEFAULT,
        SHIELD,
        SHARP_SHIELD
    }

    public Type type = Type.DEFAULT;

    private static final float WORLD_BORDERS_OFFSET = 0.2f;
    private static final float BODY_SHAPE_RADIUS = 60f;
    private static final float DEFAULT_SCALE = 1.0f;

    public boolean willBeRemoved; //To avoid body repositioning during collision (causes fatal error)
    private Body body;
    private EffectsSystem effectsSystem;
    private Random random = new Random();

    public GoodStar(Array<TextureAtlas.AtlasRegion> regions, World world, EffectsSystem effectsSystem) {
        super(new OffsetAnimation(0.04f, regions, OffsetAnimation.PlayMode.LOOP), DEFAULT_SCALE);
        this.effectsSystem = effectsSystem;
        defineBody(world);
    }

    private void defineBody(World world) {
        Vector2 randPosition = getRandomPosition();
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(randPosition.x, randPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0.0001f;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(BODY_SHAPE_RADIUS / GameMain.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_GOODS;
        fixtureDef.filter.maskBits = WorldContactListener.BIT_CATEGORY_PLAYER | WorldContactListener.BIT_CATEGORY_PLATFORM;
        body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.GOODSTAR, this));
    }


    public void setType(Type type) {
        this.type = type;
        switch (type) {
            case DEFAULT:
                setColor(1f, 1f, 1f, 0f);
                break;
            case SHIELD:
                setColor(1f, 1f, 0f, 0f);
                break;
            case SHARP_SHIELD:
                setColor(1f, 90 / 255f, 1f, 0f);
                break;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (willBeRemoved) {
            willBeRemoved = false;
            body.setTransform(getRandomPosition(), body.getAngle());
            body.setLinearVelocity(0, 0);
            float rnd = random.nextFloat();
            if (rnd <= 0.25f) {
                setType(Type.SHIELD);
            } else if (rnd <= 0.35f) {
                setType(Type.SHARP_SHIELD);
            } else {
                setType(Type.DEFAULT);
            }
        } else if (getColor().a != 1f) {
            setColor(getColor().r, getColor().g, getColor().b, 1f);
        }
    }

    public void resetPosition() {
        willBeRemoved = true;
        effectsSystem.doEffect(getX(), getY(), EffectsSystem.Type.BLUE_STAR);
    }

    @Override
    public Body getBody() {
        return body;
    }

    private Vector2 getRandomPosition() {
        Random rand = new Random();
        int randomPosX = rand.nextInt((int) GameScreen.worldWidthPx);
        int randomPosY = rand.nextInt((int) GameScreen.worldHeightPx);
        // preventing unreachable spawn position
        randomPosX = (int) Math.max(randomPosX, GameScreen.worldWidthPx * WORLD_BORDERS_OFFSET);
        randomPosX = (int) Math.min(randomPosX, GameScreen.worldWidthPx * (1 - WORLD_BORDERS_OFFSET));
        randomPosY = (int) Math.max(randomPosY, GameScreen.worldHeightPx * WORLD_BORDERS_OFFSET);
        randomPosY = (int) Math.min(randomPosY, GameScreen.worldHeightPx * (1 - WORLD_BORDERS_OFFSET));
        return new Vector2(randomPosX / GameMain.PPM, randomPosY / GameMain.PPM);
    }
}
