package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.attack.Aim;
import com.interdev.game.screens.game.attack.ultimate.UltimateSystem;
import com.interdev.game.screens.game.hud.stamina.StaminaOrbits;
import com.interdev.game.tools.AdvancedButton;
import com.interdev.game.tools.Utils;

public class GUI extends Group implements Disposable {

    public final MovePad movePad;
    private final Lives lives;
    public final AlphaCrystals crystAlphaUI;

    public GUI(final UltimateSystem ultimateSystem, Aim aim, Lives lives, StaminaOrbits staminaOrbits) {
        this.lives = lives;

        setBounds(0, 0, GameScreen.hudWidth, GameScreen.hudHeight);

       // lives.setPosition(getWidth() / 2 - lives.getWidth() / 2, getHeight() - lives.getHeight() * 1.2f);
       // addActor(lives);

        crystAlphaUI = new AlphaCrystals();
        addActor(crystAlphaUI);

        Texture texture1 = new Texture("attack_button1.png");
        Texture texture2 = new Texture("attack_button2.png");

        Utils.applyLinearFilter(texture1, texture2);

        Button attackButton = new Button(new Image(texture1).getDrawable(), new Image(texture2).getDrawable());

        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ultimateSystem.start();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        attackButton.setPosition(GameScreen.hudWidth - attackButton.getWidth() * 1.5f,
                GameScreen.hudHeight / 2 - attackButton.getHeight() / 2);
        //   addActor(attackButton);

        float padRadius = GameScreen.hudWidth * 0.07f;

        movePad = new MovePad(padRadius);
        movePad.setPosition(movePad.getWidth() * 0.5f, movePad.getHeight() * 0.5f);
        addActor(movePad);
/*        final Actor movePadPosAdaption = new Actor();
        movePadPosAdaption.setSize(GameScreen.hudWidth * 0.33f, GameScreen.hudWidth * 0.33f);
        movePadPosAdaption.setPosition(0, 0);
        movePadPosAdaption.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                movePad.setPosition(movePadPosAdaption.getX() + x - movePad.getWidth() / 2,
                        movePadPosAdaption.getY() + y - movePad.getHeight() / 2);
                return movePad.inputListener.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                movePad.inputListener.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                movePad.inputListener.touchUp(event, x, y, pointer, button);
            }
        });
        addActor(movePadPosAdaption);
*/

        final WeaponPick weaponPick = new WeaponPick(getWidth(), getHeight());
        addActor(weaponPick);

        final AimPad aimPad = new AimPad(padRadius, aim);
        aimPad.setPosition(GameScreen.hudWidth - aimPad.getWidth() * 1.5f, aimPad.getHeight() * 0.5f);
        addActor(aimPad);

        final Actor multiClickDetector = new Actor();
        multiClickDetector.setSize(aimPad.getWidth(), aimPad.getHeight());
        multiClickDetector.setPosition(aimPad.getX(), aimPad.getY());

        multiClickDetector.addListener(new ClickListener() {
            private int clicksAlready;
            private Timer clicksResetTimer = new Timer();

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                clicksAlready++;
                clicksResetTimer.clear();
                if (clicksAlready >= 3) {
                    weaponPick.show();
                    clicksAlready = 0;
                } else {
                    clicksResetTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            clicksAlready = 0;
                        }
                    }, 1f);
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                return aimPad.inputListener.touchDown(event, x, y, pointer, button);

            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                aimPad.inputListener.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                aimPad.inputListener.touchUp(event, x, y, pointer, button);
            }
        });
        addActor(multiClickDetector);

 /*
        final Actor aimPadPosAdaption = new Actor();
        aimPadPosAdaption.setSize(GameScreen.hudWidth * 0.33f, GameScreen.hudWidth * 0.33f);
        aimPadPosAdaption.setPosition(GameScreen.hudWidth - aimPadPosAdaption.getWidth(), 0);
        aimPadPosAdaption.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                aimPad.setPosition(aimPadPosAdaption.getX() + x - aimPad.getWidth() / 2,
                        aimPadPosAdaption.getY() + y - aimPad.getHeight() / 2);
                return aimPad.inputListener.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                aimPad.inputListener.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                aimPad.inputListener.touchUp(event, x, y, pointer, button);
            }
        });
        addActor(aimPadPosAdaption);
*/
        Texture pauseButtonUpTexture = new Texture("pause_1.png");
        Texture pauseButtonDownTexture = new Texture("pause_2.png");

        Utils.applyLinearFilter(pauseButtonDownTexture, pauseButtonUpTexture);

        final ImageButton pauseButton = new ImageButton(
                new Image(pauseButtonUpTexture).getDrawable(),
                new Image(pauseButtonDownTexture).getDrawable());

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("CLICK PAUSE");
            }
        });

        pauseButton.setColor(1f, 1f, 1f, 0.5f);
        pauseButton.setPosition(getWidth() - pauseButton.getWidth() * 1.05f, getHeight() - pauseButton.getHeight());
        addActor(pauseButton);

        final BottlePick bottles = new BottlePick(lives, staminaOrbits);
        bottles.setPosition(getWidth() / 2 - bottles.getWidth() / 2, getHeight() * 0.5f - bottles.getHeight() / 2);

        AdvancedButton bottlesButton = new AdvancedButton(new Texture("bottles_button_1.png"), new Texture("bottles_button_2.png"));
        bottlesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                bottles.show();
            }
        });
        bottlesButton.setPosition(0, getHeight() * 1.0f - bottlesButton.getHeight());
        bottlesButton.setColor(1f, 1f, 1f, 0.5f);
        addActor(bottlesButton);
        addActor(bottles);

    }

    @Override
    public void dispose() {
        lives.dispose();
    }
}















