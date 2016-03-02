package com.interdev.game.screens.game.hud.directionsigns;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.Utils;


public class DirectionSign extends Group {
    private static float maxDistance = (GameScreen.worldWidthPx * 0.4f) / GameMain.PPM;

    private float offsetFromCenter = GameScreen.hudHeight * 0.28f;
    private float addOffsetFromCenter = GameScreen.hudHeight * 0.25f;

    private Image signImage;

    private Actor player;
    public Actor target;
    public float targetDistPercent;

    public DirectionSign(Texture texture, Actor player, Actor target) {
        this.target = target;
        this.player = player;
        Utils.applyLinearFilter(texture);
        signImage = new Image(texture);


        signImage.setOrigin(signImage.getWidth() / 2, signImage.getHeight() / 2);
        signImage.setPosition(-signImage.getWidth() / 2, -offsetFromCenter - signImage.getHeight() / 2);

        setPosition(GameScreen.hudWidth / 2, GameScreen.hudHeight * 0.52f);

        signImage.setColor(1f, 1f, 1f, 1f);
        addActor(signImage);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void act(float delta) {
        setRotation(calculateRotation());
        float percent = getPercentDistance();
        signImage.setColor(1f, 1f, 1f, percent);
        signImage.setPosition(-signImage.getWidth() / 2,
                -(offsetFromCenter + addOffsetFromCenter * Interpolation.pow3.apply(1 - percent)) - signImage.getHeight() / 2);
    }

    private float calculateRotation() {
        return (float) Math.toDegrees(-Math.atan2(
                player.getX() - target.getX(),
                player.getY() - target.getY()
        ));
    }

    // returned value range:  from 0.0 to 1.0
    private float getPercentDistance() {
        float sqDistance =
                (player.getX() - target.getX()) * (player.getX() - target.getX())
                        +
                        (player.getY() - target.getY()) * (player.getY() - target.getY());

        targetDistPercent = (float) (Math.min((Math.pow(sqDistance, 0.5f) / maxDistance) * 0.8f, 1f));
        return (1f - targetDistPercent);
    }


    public void onResize() {
        setPosition(GameScreen.hudWidth / 2, GameScreen.hudHeight / 2);
    }
}



















