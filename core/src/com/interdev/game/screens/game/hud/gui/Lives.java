package com.interdev.game.screens.game.hud.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.interdev.game.tools.OneFloatChangeListener;
import com.interdev.game.tools.ScalableEffect;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Lives extends Group implements Disposable {

    private float maxLives = 6;
    private float livesNow = maxLives;

    private List<Heart> heartsList = new ArrayList<Heart>();

    public Lives() {
        Texture heartTexture = new Texture("lives_heart.png");
        Utils.applyLinearFilter(heartTexture);

        float xHeartOffset = heartTexture.getWidth() * 0.55f;

        setSize(xHeartOffset * maxLives - 1, heartTexture.getHeight() * 0.55f);

        for (int i = 1; i <= maxLives; i++) {
            Heart heart = new Heart(heartTexture);
            heart.setPosition(xHeartOffset * i, 0);
            heartsList.add(heart);
            addActor(heart);
        }


      //  removeLives(2.8f);

    }

    public OneFloatChangeListener getListenerForTrophyLives() {
        return new OneFloatChangeListener() {
            @Override
            public void onValueChange(float val) {
                addLives(val);
            }
        };
    }


    private Heart getFirstUnfilledHeart() {
        for (Heart heart : heartsList) if (heart.fullness < 1) return heart;
        return null;
    }

    private Heart getLastNotEmptyHeart() {
        ListIterator<Heart> iterator = heartsList.listIterator(heartsList.size());
        while (iterator.hasPrevious()) {
            Heart heart = iterator.previous();
            if (heart.fullness > 0) {
                return heart;
            }
        }
        return null;
    }

    private void changeVisibleHeartsAmount(int delta) {
        if (livesNow + delta < 0) return;
        if (livesNow + delta >= maxLives) return;
        livesNow += delta;

        for (Heart heart : heartsList) {
            heart.setVisible(false);
        }

        for (int i = 0; i < livesNow; i++) {
            heartsList.get(i).setVisible(true);
        }
    }

    public void addLives(float lives) {
        Heart heart = getFirstUnfilledHeart();
        if (heart == null) return;
        if (!heart.isVisible()) changeVisibleHeartsAmount(+1); //for the first one
        float rest = heart.fill(lives);
        if (rest != 0) {
            changeVisibleHeartsAmount(+1);
            addLives(rest);
        }
    }

    public void removeLives(float lives) {
        if (lives > 0) lives *= -1;
        Heart heart = getLastNotEmptyHeart();
        if (heart == null) return;

        float rest = heart.fill(lives);
        if (rest != 0) {
            changeVisibleHeartsAmount(-1);
            removeLives(rest);
        }
    }

    public float getTotalHealth() {
        float totalAmount = 0;
        for (Heart heart : heartsList) {
            totalAmount += heart.fullness;
        }
        return totalAmount;
    }


    @Override
    public void dispose() {
        for (Heart heart : heartsList) {
            heart.dispose();
        }
    }

    class Heart extends Actor implements Disposable {
        public float fullness = 1f;
        private Image image;
        private ScalableEffect particleEffect;

        public Heart(Texture texture) {
            particleEffect = new ScalableEffect();
            particleEffect.load(Gdx.files.internal("effects/lives_heart.p"), Gdx.files.internal("effects"));
            particleEffect.setScale(1.25f);

            image = new Image(texture);
            image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
            setPosition(0, 0);
            addActor(image);

        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y);
            image.setPosition(x - image.getWidth() / 2, y - image.getHeight() / 2);
            particleEffect.setPosition(x, y);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            particleEffect.draw(batch);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            particleEffect.update(delta * 1.5f);
        }

        private void updateSizeFromFullness() {
            float factor = 0.6f + fullness * 0.4f;
            image.setScale(factor); //[0.5 - 1]
            particleEffect.setScale(factor);
            image.setColor(1f, 1f, 1f, factor);
        }

        public float fill(float percent) {
            System.out.println("percent in: " + percent);
            fullness += percent;
            float rest = 0;
            if (fullness > 1) {
                rest = fullness - 1f;
            } else if (fullness < 0) {
                rest = fullness;
            }
            fullness = Utils.trimValue(0, 1, fullness);
            updateSizeFromFullness();
            return rest;
        }

        @Override
        public void dispose() {
            particleEffect.dispose();
        }
    }

}



