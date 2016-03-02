package com.interdev.game.screens.game.trophy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.spine.*;
import com.interdev.game.GameMain;

public class SpineTrophy extends Trophy {

    private AnimationState animationState;
    private Skeleton skeleton;
    private SkeletonRenderer skeletonRenderer;

    public SpineTrophy(TrophySystem trophySystem, World world,
                       FileHandle jsonFile, TextureAtlas atlasWithSkeleton,
                       SkeletonRenderer skeletonRenderer) {

        super(trophySystem, null, world);
        this.skeletonRenderer = skeletonRenderer;
        SkeletonJson json = new SkeletonJson(atlasWithSkeleton);
        json.setScale(1 / GameMain.PPM);
        SkeletonData skeletonData = json.readSkeletonData(jsonFile);
        skeleton = new Skeleton(skeletonData);

        AnimationStateData animationStateData = new AnimationStateData(skeletonData);
        animationState = new AnimationState(animationStateData);

        animationState.addAnimation(0, "idle", true, 0);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        skeleton.setPosition(getX(), getY());
        animationState.update(delta * 1f);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        skeletonRenderer.draw(batch, skeleton);
    }


}
