package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.sound.MusicSystem;
import com.interdev.game.sound.TrackChangeListener;
import com.interdev.game.tools.Utils;

public class MusicText {
    private static final float START_DELAY = 3f;
    private static final float APPEARING_TIME = 1f;
    private static final float SHOWING_TIME = 2f;
    private static final float DISAPPEARING_TIME = 1f;

    private static final float MAX_ALPHA = 0.7f;

    private TextButton musicTextButton1;
    private TextButton musicTextButton2;
    private TextButton musicTextButton3;
    private TextButton musicTextButton4;

    private float alpha = 0;
    private float targetAlpha = alpha;

    public MusicText(Stage stage, MusicSystem musicSystem) {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;

        parameter.size = 64;
        BitmapFont biggerFont = generator.generateFont(parameter);
        Utils.applyLinearFilter(biggerFont.getRegion().getTexture());

        parameter.size = 46;
        BitmapFont averageFont = generator.generateFont(parameter);
        Utils.applyLinearFilter(averageFont.getRegion().getTexture());

        parameter.size = 32;
        BitmapFont smallerFont = generator.generateFont(parameter);
        Utils.applyLinearFilter(smallerFont.getRegion().getTexture());


        TextButton.TextButtonStyle biggerTextButtonStyle = new TextButton.TextButtonStyle();
        biggerTextButtonStyle.font = biggerFont;

        TextButton.TextButtonStyle averageTextButtonStyle = new TextButton.TextButtonStyle();
        averageTextButtonStyle.font = averageFont;

        TextButton.TextButtonStyle smallerTextButtonStyle = new TextButton.TextButtonStyle();
        smallerTextButtonStyle.font = smallerFont;

        musicTextButton1 = new TextButton("", averageTextButtonStyle);
        musicTextButton2 = new TextButton("", biggerTextButtonStyle);
        musicTextButton3 = new TextButton("", smallerTextButtonStyle);
        musicTextButton4 = new TextButton("", smallerTextButtonStyle);

        setAlpha(alpha);

        stage.addActor(musicTextButton1);
        stage.addActor(musicTextButton2);
        stage.addActor(musicTextButton3);
        stage.addActor(musicTextButton4);


        musicSystem.setTrackChangeListener(new TrackChangeListener() {
            @Override
            public void trackChanged(String licenseText) {
                musicTextButton1.setText(licenseText.split("\n")[0]);
                musicTextButton2.setText(licenseText.split("\n")[1]);
                musicTextButton3.setText(licenseText.split("\n")[2]);

                musicTextButton1.setPosition(GameScreen.hudWidth / 2, musicTextButton1.getHeight() +
                        musicTextButton2.getHeight() +
                        musicTextButton3.getHeight() +
                        musicTextButton4.getHeight() - musicTextButton1.getHeight() / 2);
                musicTextButton2.setPosition(GameScreen.hudWidth / 2, musicTextButton1.getY() - musicTextButton2.getHeight());
                musicTextButton3.setPosition(GameScreen.hudWidth / 2, musicTextButton2.getY() - musicTextButton3.getHeight());
                musicTextButton4.setPosition(GameScreen.hudWidth / 2, musicTextButton3.getY() - musicTextButton4.getHeight());

                targetAlpha = MAX_ALPHA;
                calculateAlphaChangeSpeed(APPEARING_TIME);
            }
        });
    }

    private float startDelayTimePassed = 0;
    private float timePassed = SHOWING_TIME;
    private float alphaChangeSpeed;

    public void update(float delta) {
        if (startDelayTimePassed < START_DELAY) {
            startDelayTimePassed += delta;
            return;
        }

        if (alpha != targetAlpha) {
            if (timePassed < SHOWING_TIME) {
                timePassed += delta;
            } else {
                if ((alphaChangeSpeed > 0 && alpha < targetAlpha) || (alphaChangeSpeed < 0 && alpha > targetAlpha)) {
                    alpha += alphaChangeSpeed * delta;
                    if ((alpha >= targetAlpha && alphaChangeSpeed > 0) || (alpha <= targetAlpha && alphaChangeSpeed < 0)) {
                        targetAlpha = 0f;
                        calculateAlphaChangeSpeed(DISAPPEARING_TIME);
                        timePassed = 0;
                    }
                }
                alpha = Math.min(alpha, 1f);
                alpha = Math.max(alpha, 0f);
                setAlpha(alpha);
            }
        }
    }

    private void setAlpha(float alpha) {
        musicTextButton1.setColor(1f, 1f, 1f, alpha);
        musicTextButton2.setColor(1f, 1f, 1f, alpha);
        musicTextButton3.setColor(1f, 1f, 1f, alpha);
        musicTextButton4.setColor(1f, 1f, 1f, alpha);
    }

    private void calculateAlphaChangeSpeed(float time) {
        alphaChangeSpeed = (targetAlpha - alpha) / time;
    }

}
