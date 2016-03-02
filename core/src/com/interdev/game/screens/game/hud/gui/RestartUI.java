package com.interdev.game.screens.game.hud.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.Utils;

public class RestartUI {
    private static final float DELAY_BEFORE_APPEAR = 1f;
    private static final float APPEARING_TIME = 2.5f;
    private static final float DISAPPEARING_TIME = 0.5f;
    private float alpha = 0;
    private float targetAlpha = alpha;
    private Image bg;

    private TextButton score;

    public RestartUI(Stage stage, final GameScreen gameScreen) {
        Texture texture = new Texture(Gdx.files.internal("restart_bg.png"));
        Utils.applyLinearFilter(texture);
        bg = new Image(texture);
        bg.setVisible(true);
        bg.setPosition(GameScreen.hudWidth / 2 - bg.getWidth() / 2, GameScreen.hudHeight / 2 - bg.getHeight() / 2);
        bg.setColor(1f, 1f, 1f, alpha);
        bg.setZIndex(100);
        stage.addActor(bg);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.size = 84;
        BitmapFont font = generator.generateFont(parameter);
        Utils.applyLinearFilter(font.getRegion().getTexture());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        score = new TextButton("0", textButtonStyle);
        score.setColor(1f, 1f, 1f, alpha);
        stage.addActor(score);

        bg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (bg.getColor().a != 0) {
                    System.out.println("reset game");
                    gameScreen.resetGame();
                    return true;
                }
                return false;
            }
        });
    }

    private float timePassed = 0;
    private float alphaChangeSpeed;

    public void update(float delta) {
        if (alpha != targetAlpha) {
            if (timePassed < DELAY_BEFORE_APPEAR) {
                timePassed += delta;
            } else {
                if ((alphaChangeSpeed > 0 && alpha < targetAlpha) ||
                        (alphaChangeSpeed < 0 && alpha > targetAlpha)) {
                    alpha += alphaChangeSpeed * delta;
                } else {
                    alpha = targetAlpha;
                    timePassed = 0;
                }
                alpha = Math.min(alpha, 1f);
                alpha = Math.max(alpha, 0f);
                bg.setColor(1f, 1f, 1f, alpha);
                score.setColor(1f, 1f, 1f, alpha);
            }
        }
    }


    public void show() {
        // score.setText(String.valueOf(MotioGame.getScore()));
        score.setPosition(GameScreen.hudWidth / 2 - score.getWidth() / 2,
                bg.getY() + bg.getHeight() * 0.41f - score.getHeight() / 2);
        targetAlpha = 1f;
        calculateAlphaChangeSpeed(APPEARING_TIME);
    }

    public void hide() {
        targetAlpha = 0f;
        calculateAlphaChangeSpeed(DISAPPEARING_TIME);
    }

    private void calculateAlphaChangeSpeed(float time) {
        alphaChangeSpeed = (targetAlpha - alpha) / time;
    }

}
