package com.interdev.game.screens.game.hud.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.interdev.game.screens.game.levels.LevelsSystem;
import com.interdev.game.tools.Utils;

public class InterlevelScene extends Group {
    public static InterlevelScene inst;

    private TextButton levelText;
    private Image blackBg;//hkjjhk

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
        BitmapFont plusFont = generatorLB.generateFont(parameterLB);
        Utils.applyLinearFilter(plusFont.getRegion().getTexture());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = plusFont;

        levelText = new TextButton("LEVEL 0", textButtonStyle);
        addActor(levelText);

        setVisible(false);
    }

    private void setLevelText() {
        levelText.setText("Level " + LevelsSystem.levelsPassed);
        levelText.setPosition(-levelText.getWidth() / 2, -levelText.getHeight() / 2);

        setVisible(true);
    }

    public void show() {

    }


}
