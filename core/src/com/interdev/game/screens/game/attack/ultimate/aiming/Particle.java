package com.interdev.game.screens.game.attack.ultimate.aiming;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.OffsetAnimation;
import com.interdev.game.tools.Utils;

import java.util.Random;

public class Particle extends Actor implements Pool.Poolable {
    private static final float TRAVEL_TIME = 3f;
    private static final float TRAVEL_TIME_DISPERSE = 0.05f;

    private static final float DEFAULT_SCALE = 1f;
    private static final float SCALE_DISPERSE = 0.0f;

    private Random random = new Random();
    private Vector2 v2Destination = new Vector2();

    private OffsetAnimation animation;
    private float timePassed = 0;
    private Pool<Particle> particlePool;

    static int countParticle = 0;

    public Particle(Array<TextureAtlas.AtlasRegion> frames, Pool<Particle> particlePool) {
        this.particlePool = particlePool;
        animation = new OffsetAnimation(1 / 50f, frames, OffsetAnimation.PlayMode.LOOP);
        setSize(animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        timePassed = random.nextFloat() * 60;
    }

    public void go(Forcemeter.InterpolationBundle interpolationBundle) {

        setScale(Utils.randDecreaseVal(DEFAULT_SCALE, SCALE_DISPERSE));
        setSize(animation.getKeyFrame(0).getRegionWidth()*getScaleX(), animation.getKeyFrame(0).getRegionHeight()*getScaleY());

        setNewPosition();
        v2Destination.x = GameScreen.hudWidth - getX();
        v2Destination.y = GameScreen.hudHeight - getY();

        setRotation((float) (Math.toDegrees(Math.atan2(getY() - v2Destination.y, getX() - v2Destination.x) + Math.PI)));
        float travelTime = Utils.randomizeVal(TRAVEL_TIME, TRAVEL_TIME_DISPERSE);

        MoveToAction moveToAction = Actions.moveTo(v2Destination.x, v2Destination.y,
                travelTime * interpolationBundle.travelTimeFactor, interpolationBundle.interpolation);
        //   travelTime*1.1f, Interpolation.pow5);
        Action completeAction = new Action() {
            @Override
            public boolean act(float delta) {
                reset();
                particlePool.free(Particle.this);
                return true;
            }
        };

        SequenceAction sequence = new SequenceAction(moveToAction, completeAction);
        addAction(sequence);

        setVisible(true);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible()) return;
        TextureAtlas.AtlasSprite spriteFrame = animation.getKeyFrame(timePassed);
        spriteFrame.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
        spriteFrame.setRotation(getRotation());
        spriteFrame.draw(batch);
    }

    @Override
    public void act(float delta) {
        if (!isVisible()) return;
        super.act(delta);
        timePassed += delta;
    }

    private void setNewPosition() {
        float newPositionX, newPositionY;
        if (random.nextBoolean()) {
            newPositionX = random.nextBoolean() ? -getWidth() * 2 : GameScreen.hudWidth + getWidth() * 2;
            newPositionY = random.nextInt((int) GameScreen.hudHeight);
        } else {
            newPositionY = random.nextBoolean() ? -getHeight() * 2 : GameScreen.hudHeight + getHeight() * 2;
            newPositionX = random.nextInt((int) GameScreen.hudWidth);
        }
        setPosition(newPositionX, newPositionY);
    }

    @Override
    public void reset() {
        setVisible(false);
        clearActions();
    }

    public void capture() {
        reset();
        particlePool.free(this);
    }
}





































