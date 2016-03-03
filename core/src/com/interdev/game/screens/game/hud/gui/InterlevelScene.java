package com.interdev.game.screens.game.hud.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.levels.LevelsSystem;
import com.interdev.game.tools.Utils;

public class InterlevelScene extends Group {
    public static InterlevelScene inst;

    private TextButton levelTextButton;

    private Image blackBg;

    public InterlevelScene() {
        inst = this;

        Texture blackBgTexture = new Texture("black_rect.png");
        Utils.applyLinearFilter(blackBgTexture);
        blackBg = new Image(blackBgTexture);
        blackBg.setScale(10);
        addActor(blackBg);

        FreeTypeFontGenerator generatorLB = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterLB = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameterLB.size = 256;
        BitmapFont levelFont = generatorLB.generateFont(parameterLB);
        Utils.applyLinearFilter(levelFont.getRegion().getTexture());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = levelFont;

        setLevelText();

        addActor(levelTextButton);

        setVisible(false);
    }

    private void setLevelText() {
        levelTextButton.setText("Level " + LevelsSystem.levelsPassed);
        levelTextButton.setPosition(-levelTextButton.getWidth() / 2, -levelTextButton.getHeight() / 2);
    }

    public void show() {
        blackBg.setColor(1f, 1f, 1f, 0);
        levelTextButton.setColor(1f, 1f, 1f, 0);
        setVisible(true);

        Utils.graduallyChangeAlpha(blackBg, 3f, 1f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Utils.graduallyChangeAlpha(levelTextButton, 1f, 1f);
            }
        }, 3.5f);
    }


    public void hide() {
        Utils.graduallyChangeAlpha(levelTextButton, 2f, 0f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Utils.graduallyChangeAlpha(blackBg, 2f, 0);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        blackBg.setVisible(false);
                        levelTextButton.setVisible(false);
                    }
                }, 2f);
            }
        }, 2.5f);
    }
}
