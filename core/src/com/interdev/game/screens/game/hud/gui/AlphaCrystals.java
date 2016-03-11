package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.OneFloatChangeListener;
import com.interdev.game.tools.Utils;

public class AlphaCrystals extends Group {

    private final CrystTable redCrystTable;
    private final CrystTable blueCrystTable;

    public AlphaCrystals() {
        setSize(GameScreen.hudWidth, GameScreen.hudHeight);

        Texture redCryTexture = new Texture("red_cryst_alpha_ui.png");
        Texture blueCryTexture = new Texture("blue_cryst_alpha_ui.png");
        Texture blackRect = new Texture("black_rect_alpha_ui.png");

        Utils.applyLinearFilter(redCryTexture, blueCryTexture, blackRect);

        Color pinkColor = new Color(255 / 255f, 169 / 255f, 199 / 255f, 1f);
        Color blueColor = new Color(100 / 255f, 196 / 255f, 238 / 255f, 1f);


        redCrystTable = new CrystTable(GameMain.redCrystals, redCryTexture, blackRect, pinkColor,
                GameScreen.hudWidth * 0.12f, GameScreen.hudWidth * 0.12f);
        addActor(redCrystTable);

        blueCrystTable = new CrystTable(GameMain.blueCrystals, blueCryTexture, blackRect, blueColor,
                GameScreen.hudWidth * 0.12f, GameScreen.hudWidth * 0.19f);
        addActor(blueCrystTable);

        //     redCrystTable.setColor(1f, 1f, 1f, 0f);
        //   redCrystTable.show();
    }


    public OneFloatChangeListener getBlueCryChangeListener() {
        return new OneFloatChangeListener() {
            @Override
            public void onValueChange(float val) {
                blueCrystTable.upd(GameMain.blueCrystals);
            }
        };
    }

    public OneFloatChangeListener getRedCryChangeListener() {
        return new OneFloatChangeListener() {
            @Override
            public void onValueChange(float val) {
                redCrystTable.upd(GameMain.redCrystals);
            }
        };
    }

    private class CrystTable extends Group {

        private static final float HIDE_AFTER = 3F;

        private final TextButton cryPlusTextButton;
        private final TextButton cryTotalAmountTextButton;
        private Timer disappearTimer = new Timer();

        private int plusAmount = 0;
        private int crystAmountOnTB;

        public CrystTable(int crystAmount, Texture crystTexture, Texture blackRectTexture, Color color, float offsetFromRight, float offsetFromTop) {
            crystAmountOnTB = crystAmount;

            setSize(GameScreen.hudWidth, GameScreen.hudHeight);

            Image blackRect = new Image(blackRectTexture);
            blackRect.setColor(1f, 1f, 1f, 0.5f);
            addActor(blackRect);

            Image cryImage = new Image(crystTexture);

            addActor(cryImage);

            FreeTypeFontGenerator generatorLB = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lb.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameterLB = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameterLB.color = color;

            parameterLB.size = 100;
            BitmapFont plusFont = generatorLB.generateFont(parameterLB);
            Utils.applyLinearFilter(plusFont.getRegion().getTexture());

            FreeTypeFontGenerator generatorLBD = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lbd.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameterLBD = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameterLBD.color = color;

            parameterLBD.size = 50;
            BitmapFont amountFont = generatorLBD.generateFont(parameterLBD);
            Utils.applyLinearFilter(amountFont.getRegion().getTexture());

            parameterLBD.size = 40;
            BitmapFont inTotalFont = generatorLBD.generateFont(parameterLBD);
            Utils.applyLinearFilter(inTotalFont.getRegion().getTexture());


            TextButton.TextButtonStyle plusTextButtonStyle = new TextButton.TextButtonStyle();
            plusTextButtonStyle.font = plusFont;

            TextButton.TextButtonStyle amountTextButtonStyle = new TextButton.TextButtonStyle();
            amountTextButtonStyle.font = amountFont;

            TextButton.TextButtonStyle inTotalTextButtonStyle = new TextButton.TextButtonStyle();
            inTotalTextButtonStyle.font = inTotalFont;

            cryPlusTextButton = new TextButton("+1", plusTextButtonStyle);
            cryTotalAmountTextButton = new TextButton(String.valueOf(crystAmountOnTB), amountTextButtonStyle);
            TextButton cryInTotalTextTB = new TextButton("in total", inTotalTextButtonStyle);

            addActor(cryPlusTextButton);
            addActor(cryTotalAmountTextButton);
            addActor(cryInTotalTextTB);

            cryImage.setPosition(getWidth() - offsetFromRight - cryImage.getWidth() / 2, getHeight() - offsetFromTop - cryImage.getHeight() / 2);

            blackRect.setPosition(cryImage.getX() + cryImage.getWidth() * 0.7f - blackRect.getWidth() / 2,
                    cryImage.getY() + cryImage.getHeight() / 2 - blackRect.getHeight() / 2);

            cryPlusTextButton.setPosition(cryImage.getX() + cryImage.getWidth() * 0.15f - cryPlusTextButton.getWidth() * 0.95f,
                    cryImage.getY() + cryPlusTextButton.getHeight() * 0.25f);

            cryTotalAmountTextButton.setPosition(cryImage.getX() + cryImage.getWidth() * 0.85f,
                    cryImage.getY() + cryImage.getHeight() * 0.45f);

            cryInTotalTextTB.setPosition(cryImage.getX() + cryImage.getWidth() * 0.85f,
                    cryImage.getY() + cryImage.getHeight() * 0.22f);

            setColor(1f, 1f, 1f, 0);
        }


        private Timer totalAmountUpdateCounter = new Timer();

        public void upd(final float newTotalAmount) {
            disappearTimer.clear();
            totalAmountUpdateCounter.clear();

            plusAmount++;
            cryPlusTextButton.setText("+" + plusAmount);

            show();

            totalAmountUpdateCounter.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (crystAmountOnTB < newTotalAmount) {
                        crystAmountOnTB++;
                    } else cancel();
                    cryTotalAmountTextButton.setText(String.valueOf(crystAmountOnTB));
                }
            }, 0.25f, 0.02f);

            disappearTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    hide();
                }
            }, HIDE_AFTER);
        }


        private Timer showTimer = new Timer();

        private void show() {
            hideTimer.clear();

            showTimer.scheduleTask(new Timer.Task() {
                float alpha = getColor().a;

                @Override
                public void run() {
                    if (alpha < 1) {
                        alpha += 0.01f;
                    } else {
                        alpha = 1f;
                        cancel();
                    }
                    CrystTable.this.setColor(1f, 1f, 1f, alpha);
                }
            }, 0.015f, 0.01f);
        }


        private Timer hideTimer = new Timer();

        public void hide() {
            showTimer.clear();
            hideTimer.scheduleTask(new Timer.Task() {
                float alpha = getColor().a;
                @Override
                public void run() {
                    if (alpha > 0) {
                        alpha -= 0.01f;
                    } else {
                        alpha = 0;
                        plusAmount = 0;
                        cancel();
                    }
                    CrystTable.this.setColor(1f, 1f, 1f, alpha);
                }
            }, 0.015f, 0.010f);
        }

    }


}




















