package com.interdev.game.screens.game.hud;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.entities.Player;

public class ShieldField extends Actor {
    private final Player player;
    private Animation animation;
    public float elapsedTime;

    public static final Vector2 SCREEN_POS = new Vector2(0.5f, 0.51f);

    private float width;
    private float height;

    public ShieldField(Array<TextureAtlas.AtlasRegion> regions, Player player) {
        this.player = player;
        animation = new Animation(0.04f, regions, Animation.PlayMode.LOOP);

        width = animation.getKeyFrame(0).getRegionWidth();
        height = animation.getKeyFrame(0).getRegionHeight();
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        width = animation.getKeyFrame(0).getRegionWidth() * scaleXY;
        height = animation.getKeyFrame(0).getRegionHeight() * scaleXY;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (player.hasShield || player.hasSharpShield) {
            elapsedTime += Gdx.graphics.getDeltaTime();
            batch.draw(animation.getKeyFrame(elapsedTime, (animation.getPlayMode() == Animation.PlayMode.LOOP)), getX() - width / 2, getY() - height / 2, width, height);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void resize() {
        setScale(1 + (1 - GameScreen.zoom) + 0.15f);
        setPosition(GameScreen.hudWidth * SCREEN_POS.x, GameScreen.hudHeight * SCREEN_POS.y);

    }
}
