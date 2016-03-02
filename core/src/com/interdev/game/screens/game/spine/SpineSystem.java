package com.interdev.game.screens.game.spine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.entities.demons.bosses.SpineBossVIS;

import java.util.ArrayList;
import java.util.List;

public class SpineSystem {
    private List<SpineBossVIS> spineBossVISList = new ArrayList<SpineBossVIS>();
    private final SkeletonRenderer skeletonRenderer = new SkeletonRenderer();

    public SpineSystem(World world, Player player) {
        //spineBossVISList.add(new SpineBossVIS("skeleton", world, player, skeletonRenderer));
        //   spineObjList.add(new SpineObj("skeleton", batch, skeletonRenderer));
    }

    public void update(float delta) {
        for (SpineBossVIS obj : spineBossVISList) {
            obj.update(delta);
        }
    }

    public void draw(SpriteBatch batch) {
        for (SpineBossVIS obj : spineBossVISList) obj.draw(batch);
    }

}
