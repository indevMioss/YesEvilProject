package com.interdev.game.screens.game.entities.demons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.spine.*;
import com.interdev.game.GameMain;
import com.interdev.game.tools.Log;

public class SpineDemon extends Demon {

    private final AnimationState animationState;
    private final Skeleton skeleton;
    private final SkeletonRenderer skeletonRenderer;
    protected boolean byDefaultFacingRight = true;

    public SpineDemon(Monsters.Values values,
                      String jsonPath, TextureAtlas atlasWithSkeleton,
                      SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {

        super(values, myPool);
        this.skeletonRenderer = skeletonRenderer;

        SkeletonJson json = new SkeletonJson(atlasWithSkeleton);
        json.setScale(1 / GameMain.PPM);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(jsonPath));
        skeleton = new Skeleton(skeletonData);

        AnimationStateData animationStateData = new AnimationStateData(skeletonData);
        animationState = new AnimationState(animationStateData);

        animationState.addAnimation(0, "idle", true, 0);

        setScale(values.DEFAULT_SCALE);
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale*defaultScale);
        skeleton.getBones().get(0).setScale(scale*defaultScale);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        skeleton.setPosition(getBody().getTransform().getPosition().x, getBody().getTransform().getPosition().y);
        animationState.update(delta * 1f);

        animationState.apply(skeleton);

        if (byDefaultFacingRight)
            skeleton.setFlipX(facingLeft);
        else
            skeleton.setFlipX(!facingLeft);

        skeleton.updateWorldTransform();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        skeletonRenderer.draw(batch, skeleton);

    }


}
