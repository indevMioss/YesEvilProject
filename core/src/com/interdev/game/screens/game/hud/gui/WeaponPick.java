package com.interdev.game.screens.game.hud.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.screens.game.attack.BulletSystem;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;

public class WeaponPick extends Group {
    private ArrayList<EffectButton> effectButtons = new ArrayList<EffectButton>();

    public WeaponPick(float width, float height) {
        setSize(width, height);


        Texture blackBgTexture = new Texture("black_rect.png");
        Utils.applyLinearFilter(blackBgTexture);
        Image blackBg = new Image(blackBgTexture);
        blackBg.setScale(10);
        blackBg.setColor(1, 1, 1, 0.75f);
        addActor(blackBg);

        float buttonWidth = width / 10f;
        float buttonHeight = buttonWidth;

        int buttonsAmount = BulletParamsEnum.effectsPathMap.size;

        float overallButtonsWidth = buttonsAmount * buttonWidth;

        float startX = width / 2 - overallButtonsWidth / 2;
        float y = height / 2;

        int i = 0;

        for (final ObjectMap.Entry<BulletParamsEnum, String> entry : BulletParamsEnum.effectsPathMap) {
            ParticleEffect effect = new ParticleEffect();
            effect.load(Gdx.files.internal(entry.value), Gdx.files.internal("effects"));
            effect.start();
            EffectButton effectButton = new EffectButton(buttonWidth, buttonHeight, effect);
            effectButton.addListener(new InputListener() {
                private BulletParamsEnum tempType = entry.key;
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    BulletSystem.setType(tempType);
                    hide();
                    return true;
                }
            });

            effectButton.setPosition(startX + buttonWidth * i - buttonWidth / 2, y - buttonHeight / 2);

            addActor(effectButton);
            effectButtons.add(effectButton);
            i++;
        }

        setColor(1f, 1f, 1f, 0);
        setVisible(false);
        for (EffectButton button : effectButtons) {
            for (ParticleEmitter emitter : button.effect.getEmitters()) {
                emitter.setContinuous(false);
            }
        }
    }

    public void show() {
        GameScreen.setSlowmoFactor(0.1f);
        setVisible(true);
        for (EffectButton button : effectButtons) {
            for (ParticleEmitter emitter : button.effect.getEmitters()) {
                emitter.setContinuous(true);
                emitter.reset();
            }
        }
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float newAlpha = WeaponPick.this.getColor().a + 0.02f;
                newAlpha = Math.min(newAlpha, 1f);
                WeaponPick.this.setColor(1f, 1f, 1f, newAlpha);
                if (newAlpha == 1) {

                    cancel();
                }
            }
        }, 0, 0.01f);
    }

    public void hide() {
        GameScreen.setSlowmoFactor(1f);
        for (EffectButton button : effectButtons) {
            for (ParticleEmitter emitter : button.effect.getEmitters()) {
                emitter.setContinuous(false);
            }
        }
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float newAlpha = WeaponPick.this.getColor().a - 0.02f;
                newAlpha = Math.max(newAlpha, 0);
                WeaponPick.this.setColor(1f, 1f, 1f, newAlpha);
                if (newAlpha == 0) {
                    cancel();
                    setVisible(false);
                }
            }
        }, 0, 0.01f);
    }

    class EffectButton extends Actor {
        public ParticleEffect effect;

        public EffectButton(float width, float height, ParticleEffect effect) {
            this.effect = effect;
            setSize(width, height);
            effect.setPosition(width / 2, height / 2);
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y);
            effect.setPosition(x + getWidth() / 2, y + getHeight() / 2);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            effect.draw(batch);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            effect.update(delta / 8);
        }
    }
}
