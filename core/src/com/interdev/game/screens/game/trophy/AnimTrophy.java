package com.interdev.game.screens.game.trophy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.interdev.game.GameMain;
import com.interdev.game.tools.OffsetAnimation;
import com.interdev.game.tools.Utils;

public class AnimTrophy extends Trophy {

    private TextureRegion textureRegion;
    private OffsetAnimation animation;
    private float timePassed;
    private float animScale = 1;
    private float yOffset = 0;
    private float animAlpha = 1f;
    boolean drawAnimOnTop;

    public AnimTrophy(TrophySystem trophySystem, Pool<AnimTrophy> myPool, World world) {
        super(trophySystem, myPool, world);

    }

    @Override
    public Trophy setType(Type type) {
        super.setType(type);
        timePassed = 0;
        animScale = 1;
        yOffset = 0;
        animAlpha = 1f;
        drawAnimOnTop = false;

        animation = trophySystem.trophyAnimMap.get(type);
        textureRegion = trophySystem.trophyTRMap.get(type);

        System.out.println("---- " + (textureRegion == null));
        setSize(textureRegion.getRegionWidth() / GameMain.PPM,
                textureRegion.getRegionHeight() / GameMain.PPM);
        System.out.println(getWidth());
        System.out.println(getHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        // setScale(Utils.getRand(0.5f, 0.75f));

        if (type == Type.RED_CRY || type == Type.BLUE_CRY) {
            if (Utils.roll(0.5f)) {
                setRotation(Utils.getRand(0, 30));
            } else {
                setRotation(Utils.getRand(330, 359));
            }
        } else {
            setRotation(0);
        }
        if (type == Type.SHIELD_BONUS) {
            animScale = 0.7f;
            animAlpha = 0.8f;
            drawAnimOnTop = false;
        } else if (type == Type.SHARP_SHIELD_BONUS) {
            animScale = 0.7f;
            animAlpha = 0.8f;
            drawAnimOnTop = false;
        } else if (type == Type.LIVE) {
            animScale = 1.8f;
            yOffset = 0.02f;
            animAlpha = 0.6f;
        }


        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timePassed += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (drawAnimOnTop) {
            drawTR(batch);
            drawAnim(batch);
        } else {
            drawAnim(batch);
            drawTR(batch);
        }

    }

    private void drawTR(Batch batch) {
        batch.draw(textureRegion,
                getX() - getWidth() / 2,
                getY() - getHeight() / 2,
                getOriginX(),
                getOriginY(),
                getWidth(),
                getHeight(),
                getScaleX(),
                getScaleY(),
                getRotation());
    }

    private void drawAnim(Batch batch) {
        if (animation != null) {
            TextureAtlas.AtlasSprite spriteFrame = getFrame(timePassed);
            spriteFrame.setAlpha(animAlpha);
            spriteFrame.setBounds(
                    getX() - getWidth() * animScale * 0.5f,
                    getY() - getHeight() * animScale * (0.5f - yOffset),
                    getWidth() * animScale, getHeight() * animScale);
            spriteFrame.draw(batch);
        }
    }


    public TextureAtlas.AtlasSprite getFrame(float stateTime) {
        return animation.getKeyFrame(stateTime);
    }


}
