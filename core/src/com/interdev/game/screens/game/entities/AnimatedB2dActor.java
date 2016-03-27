package com.interdev.game.screens.game.entities;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.interdev.game.GameMain;
import com.interdev.game.tools.OffsetAnimation;

public abstract class AnimatedB2dActor extends Actor {
    protected OffsetAnimation animation;
    protected float timePassed;
    public boolean willBeRemoved = false; //To avoid body repositioning during collision (causes fatal error)

    public AnimatedB2dActor(OffsetAnimation animation, float scale) {
        this.animation = animation;
        setScale(scale);
        //  setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        TextureAtlas.AtlasSprite spriteFrame = getFrame(timePassed);
        spriteFrame.setBounds(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
        if (drawWithRotation()) {
            batch.draw(spriteFrame.getAtlasRegion(), spriteFrame.getX(), spriteFrame.getY(),
                    spriteFrame.getWidth() / 2,
                    spriteFrame.getHeight() / 2,
                    spriteFrame.getWidth(), spriteFrame.getHeight(),
                    spriteFrame.getScaleX(), spriteFrame.getScaleY(), getRotation());
        } else {
            spriteFrame.draw(batch);
        }

    }

    public TextureAtlas.AtlasSprite getFrame(float stateTime) {
        return animation.getKeyFrame(stateTime);
    }

    public void passiveAct(float delta) {
        timePassed += delta;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timePassed += delta;

        // setPosition(getBody().getPosition().x, getBody().getPosition().y);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        setSize(animation.getKeyFrame(0).getRegionWidth() * scaleXY / GameMain.PPM,
                animation.getKeyFrame(0).getRegionHeight() * scaleXY / GameMain.PPM);
    }

    @Override
    public float getX() {
        return getBody().getPosition().x;
    }

    @Override
    public float getY() {
        return getBody().getPosition().y;
    }

    public abstract Body getBody();

    protected boolean drawWithRotation() {
        return false;
    }
}
