package com.interdev.game.screens.game.attack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.BooleanArgChangeListener;
import com.interdev.game.tools.TwoFloatsChangeListener;
import com.interdev.game.tools.Utils;

public class Aim extends Group {
    public static Aim inst;
    private static final float ANGLE_CHANGE_SPEED = 800; // per sec
    private static final float ALPHA_CHANGE_SPEED = 3f;
    private final Image aimImage;

    public TwoFloatsChangeListener getAdjustPosListener() {
        return new TwoFloatsChangeListener() {
            @Override
            public void onValuesChange(float offsetX, float offsetY) {
            //    Aim.this.adjustPosition(offsetX, offsetY);
            }
        };
    }

    public Aim() {
        inst = this;
        Texture texture = new Texture("aim.png");
        Utils.applyLinearFilter(texture);
        aimImage = new Image(texture);

        float offsetRadius = aimImage.getWidth() * 3.5f;
        aimImage.setOrigin(-offsetRadius, aimImage.getHeight() / 2);

        adjustPosition(0, 0);

        addActor(aimImage);
    }

    private void adjustPosition(float offsetX, float offsetY) {
        float offsetRadius = aimImage.getWidth() * 3.5f;
        aimImage.setPosition(offsetX * GameMain.PPM / GameScreen.zoom + offsetRadius,
                offsetY * GameMain.PPM / GameScreen.zoom - aimImage.getHeight() / 2);
    }


    private float destAngle;
    private float angle;
    private float angChaseSpeed;

    public void onValuesChange(float x, float y, float relLen) {
        destAngle = (float) Math.toDegrees(Math.atan2(y, x));
        if (destAngle < 0) destAngle += 360;
        else if (destAngle >= 360) destAngle -= 360;

        if (relLen >= 0.85f) {
            setRotation(destAngle);
        } else {
            angChaseSpeed = relLen * ANGLE_CHANGE_SPEED;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (aimImage.getColor().a != destAlpha) {
            int sign = 1;
            if (aimImage.getColor().a > destAlpha) {
                sign = -1;
            }
            float newAlpha = aimImage.getColor().a + ALPHA_CHANGE_SPEED * sign * delta;
            newAlpha = Utils.trimValue(0, 1, newAlpha);
            aimImage.setColor(1f, 1f, 1f, newAlpha);
        }


        if (angle == destAngle) return;

        if (destAngle > 340f || destAngle < 20f) setRotation(destAngle);

        boolean larger180 = (Math.abs(angle - destAngle) > 180);
        int sig = 1;
        if ((larger180 && angle < destAngle) || (!larger180 && angle > destAngle)) {
            sig = -1;
        }
        angle += sig * angChaseSpeed * delta;

        if (!larger180) {
            if ((sig == 1 && angle > destAngle) || (sig == -1 && angle < destAngle)) angle = destAngle;
        }

        if (angle < 0) angle += 360;
        else if (angle >= 360) angle -= 360;

        setRotation(angle);
    }

    @Override
    public void setRotation(float degrees) {
        angle = degrees;
        aimImage.setRotation(degrees);
        BulletSystem.inst.setAngle(degrees);
    }

    @Override
    public float getRotation() {
        return angle;
    }

    public float destAlpha = 0;

    public void hide() {
        destAlpha = 0;
        BulletSystem.inst.endFire();
        startFireTimer.clear();
    }

    Timer startFireTimer = new Timer();

    public void show() {
        destAlpha = 1;
        startFireTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                BulletSystem.inst.startFire();
            }
        }, 0.3f);
    }


}
