package com.interdev.game.screens.game.entities.demons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.spine.SkeletonRenderer;

public abstract class FlyDemon extends SpineDemon {

    public FlyDemon(Monsters.Values values,
                    String jsonPath,
                    TextureAtlas atlasWithSkeleton,
                    SkeletonRenderer skeletonRenderer,
                    Pool<? extends Demon> myPool) {

        super(values, jsonPath, atlasWithSkeleton, skeletonRenderer, myPool);
    }
}
