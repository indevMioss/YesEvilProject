package com.interdev.game.screens.game.entities.demons;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.GameMain;
import com.interdev.game.tools.OffsetAnimation;

public abstract class AnimatedDemon extends Demon {
    protected OffsetAnimation animation;
    protected float timePassed;

    public AnimatedDemon(Monsters.Values values, Array<TextureAtlas.AtlasRegion> atlasRegions, Pool<? extends Demon> myPool) {
        super(values, myPool);
        this.animation = new OffsetAnimation(0.04f, atlasRegions, OffsetAnimation.PlayMode.LOOP);
        //setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        TextureAtlas.AtlasSprite spriteFrame = getFrame(timePassed);
        spriteFrame.setBounds(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
        spriteFrame.draw(batch);
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
    public void setScale(float scale) {
        super.setScale(scale * defaultScale);
        setSize(animation.getKeyFrame(0).getRegionWidth() * scale * defaultScale / GameMain.PPM,
                animation.getKeyFrame(0).getRegionHeight() * scale * defaultScale / GameMain.PPM);
    }

}
