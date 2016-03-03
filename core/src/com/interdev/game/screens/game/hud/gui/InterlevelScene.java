package com.interdev.game.screens.game.hud.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.levels.LevelsSystem;
import com.interdev.game.tools.ActionListener;
import com.interdev.game.tools.Utils;

public class InterlevelScene extends Group {
    private static final float SKIP_RATE = 4f;

    private static final float APPEARING_BG_TIME = 2f / SKIP_RATE;
    private static final float APPEARING_TEXT_TIME = 0.5f / SKIP_RATE;
    private static final float SHOW_TIME = 1f / SKIP_RATE;
    private static final float DISAPPEARING_TEXT_TIME = 1f / SKIP_RATE;
    private static final float DISAPPEARING_BG_TIME = 1.5f / SKIP_RATE;

    public static InterlevelScene inst;
    private TextButton levelTextButton;
    private Image blackBg;
    private ActionListener sceneEndListener;


    public InterlevelScene() {
        inst = this;

        Texture blackBgTexture = new Texture("black_rect.png");
        Utils.applyLinearFilter(blackBgTexture);
        blackBg = new Image(blackBgTexture);
        blackBg.setScale(10);
        addActor(blackBg);

        FreeTypeFontGenerator generatorLB = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lbd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterLB = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameterLB.size = 256;
        BitmapFont levelFont = generatorLB.generateFont(parameterLB);
        Utils.applyLinearFilter(levelFont.getRegion().getTexture());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = levelFont;

        levelTextButton = new TextButton("", textButtonStyle);

        setLevelText();

        addActor(levelTextButton);

        setVisible(false);
    }

    public void setLevelText() {
        levelTextButton.setText("Level " + LevelsSystem.levelsPassed);
        levelTextButton.setPosition(GameScreen.hudWidth / 2 - levelTextButton.getWidth() / 2, GameScreen.hudHeight / 2 - levelTextButton.getHeight() / 2);
    }


    public void show() {
        blackBg.setColor(1f, 1f, 1f, 0);
        levelTextButton.setColor(1f, 1f, 1f, 0);
        setVisible(true);

        Utils.graduallyChangeAlpha(blackBg, APPEARING_BG_TIME, 1f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Utils.graduallyChangeAlpha(levelTextButton, APPEARING_TEXT_TIME, 1f);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        hide();
                    }
                }, SHOW_TIME);
            }
        }, APPEARING_BG_TIME);
    }


    private void hide() {
        Utils.graduallyChangeAlpha(levelTextButton, DISAPPEARING_TEXT_TIME, 0f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Utils.graduallyChangeAlpha(blackBg, DISAPPEARING_BG_TIME, 0);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        setVisible(false);
                        sceneEndListener.actionPerformed();
                    }
                }, 2f);
            }
        }, DISAPPEARING_TEXT_TIME / 2);
    }

    public void setSceneEndListener(ActionListener sceneEndListener) {
        this.sceneEndListener = sceneEndListener;
    }
}
