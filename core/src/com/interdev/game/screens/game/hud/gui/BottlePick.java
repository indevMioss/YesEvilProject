package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.hud.stamina.StaminaOrbits;
import com.interdev.game.tools.AdvancedButton;
import com.interdev.game.tools.Utils;

public class BottlePick extends Group {

    private final AdvancedButton drinkHealthButton;
    private final AdvancedButton drinkPowerButton;
    private TextButton healthBottlesAmountTB;
    private TextButton powerBottlesAmountTB;
    private TextButton resurrectionBottlesAmountTB;
    private TextButton timeBottlesAmountTB;

    public BottlePick(final Lives lives, final StaminaOrbits stamina) {
        Actor bgDisablingRect = new Actor();
        bgDisablingRect.setSize(GameScreen.hudWidth, GameScreen.hudHeight);

        bgDisablingRect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("dssdfdsfdsfs clicked bg dis rect");
                hide();
            }
        });
        addActor(bgDisablingRect);

        Texture bgTexture = new Texture("bottles_ui_bg.png");
        Utils.applyLinearFilter(bgTexture);
        addActor(new Image(bgTexture));
        setSize(bgTexture.getWidth(), bgTexture.getHeight());
        bgDisablingRect.setPosition(getWidth() / 2 - bgDisablingRect.getWidth() / 2, getHeight() / 2 - bgDisablingRect.getHeight() / 2);

        Texture bottlesUiButton1Texture = new Texture("bottles_ui_button_1.png");
        Texture bottlesUiButton2Texture = new Texture("bottles_ui_button_2.png");
        Texture bottlesUiButton3Texture = new Texture("bottles_ui_button_3.png");
        Texture bottlesUiButtonShineTexture = new Texture("bottles_ui_button_shine.png");

        drinkHealthButton = new AdvancedButton(bottlesUiButton1Texture, bottlesUiButton2Texture,
                bottlesUiButton3Texture, bottlesUiButtonShineTexture);
        drinkHealthButton.setPosition(getWidth() * 0.837f - drinkHealthButton.getWidth() / 2,
                getHeight() * 0.702f - drinkHealthButton.getHeight() / 2);
        drinkHealthButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameMain.healthBottles > 0) {
                    GameMain.healthBottles--;
                    lives.addLives(999f);
                    updateTBAndButtons();
                }
            }
        });
        addActor(drinkHealthButton);

        drinkPowerButton = new AdvancedButton(bottlesUiButton1Texture, bottlesUiButton2Texture,
                bottlesUiButton3Texture, bottlesUiButtonShineTexture);
        drinkPowerButton.setPosition(getWidth() * 0.837f - drinkPowerButton.getWidth() / 2,
                getHeight() * 0.507f - drinkPowerButton.getHeight() / 2);
        drinkPowerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameMain.powerBottles > 0) {
                    GameMain.powerBottles--;
                    stamina.addStamina(999);
                    updateTBAndButtons();
                }
            }
        });
        addActor(drinkPowerButton);

        FreeTypeFontGenerator generatorLBD = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lbd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterLBD = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterLBD.color = Color.WHITE;

        parameterLBD.size = 64;
        BitmapFont font = generatorLBD.generateFont(parameterLBD);
        Utils.applyLinearFilter(font.getRegion().getTexture());

        TextButton.TextButtonStyle textButtonsStyle = new TextButton.TextButtonStyle();
        textButtonsStyle.font = font;

        healthBottlesAmountTB = new TextButton("x", textButtonsStyle);
        powerBottlesAmountTB = new TextButton("x", textButtonsStyle);
        resurrectionBottlesAmountTB = new TextButton("x", textButtonsStyle);
        timeBottlesAmountTB = new TextButton("x", textButtonsStyle);

        addActor(healthBottlesAmountTB);
        addActor(powerBottlesAmountTB);
        addActor(resurrectionBottlesAmountTB);
        addActor(timeBottlesAmountTB);

        updateTBAndButtons();

        hide();
    }

    private void updateTBAndButtons() {
        healthBottlesAmountTB.setText(String.valueOf(GameMain.healthBottles));
        powerBottlesAmountTB.setText(String.valueOf(GameMain.powerBottles));
        resurrectionBottlesAmountTB.setText(String.valueOf(GameMain.resurrectionBottles));
        timeBottlesAmountTB.setText(String.valueOf(GameMain.timeBottles));

        float xPercOffset = 0.476f;
        healthBottlesAmountTB.setPosition(getWidth() * xPercOffset - healthBottlesAmountTB.getWidth() / 2,
                getHeight() * 0.70f - healthBottlesAmountTB.getHeight() / 2);
        powerBottlesAmountTB.setPosition(getWidth() * xPercOffset - powerBottlesAmountTB.getWidth() / 2,
                getHeight() * 0.507f - powerBottlesAmountTB.getHeight() / 2);
        resurrectionBottlesAmountTB.setPosition(getWidth() * xPercOffset - resurrectionBottlesAmountTB.getWidth() / 2,
                getHeight() * 0.311f - resurrectionBottlesAmountTB.getHeight() / 2);
        timeBottlesAmountTB.setPosition(getWidth() * xPercOffset - timeBottlesAmountTB.getWidth() / 2,
                getHeight() * 0.115f - timeBottlesAmountTB.getHeight() / 2);

        drinkHealthButton.setDisabled(GameMain.healthBottles < 1);
        drinkPowerButton.setDisabled(GameMain.powerBottles < 1);
    }

    public void show() {
        updateTBAndButtons();
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }


}






























