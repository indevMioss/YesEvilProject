package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.interdev.game.tools.Utils;

public class MovePad extends Group {

    public final InputListener inputListener;
    private Image padImage;

    private float relPosX = 0f;
    private float relPosY = 0f;

    public float getRelPosX() {
        return relPosX;
    }

    public float getRelPosY() {
        return relPosY;
    }



    private boolean touching = false;
    private float radius;

    public MovePad(final float radius) {
        this.radius = radius;
        setSize(radius * 2, radius * 2);
        Texture texture = new Texture("gamepad.png");
        Utils.applyLinearFilter(texture);

        padImage = new Image(texture);
        padImage.setColor(1f, 1f, 1f, 0.33f);
        padImage.setPosition(getWidth() / 2 - padImage.getWidth() / 2, getHeight() / 2 - padImage.getHeight() / 2);
        addActor(padImage);

        inputListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                touching = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                x = event.getStageX() - MovePad.this.getX();
                y = event.getStageY() - MovePad.this.getY();

                x -= radius;
                y -= radius;
                y = Math.max(0, y);
                float xSig = Math.signum(x);
                float ySig = Math.signum(y);
                Vector2 vec = new Vector2(Math.abs(x), Math.abs(y)).limit(radius);
                vec.scl(xSig,ySig);
                relPosX = vec.x / radius; // [-1 : 1]
                relPosY = vec.y / radius; // [-1 : 1]
                padImage.setX(vec.x + radius - padImage.getWidth() / 2);
                padImage.setY(vec.y + radius - padImage.getHeight() / 2);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                touching = false;
            }
        };
        addListener(inputListener);

    }

    private static final float PAD_RETURN_SPEED = 0.5f * 6; // N half distances / sec


    private Vector2 actTepmVec = new Vector2(0, 0);

    @Override
    public void act(float delta) {
        super.act(delta);
        if (touching) return;
        boolean changed = false;
        actTepmVec.set(0,0);

        if (relPosX != 0) {
            changed = true;
            int sign = (relPosX > 0) ? -1 : 1;
            relPosX += sign * PAD_RETURN_SPEED * delta;
            if ((sign == -1 && relPosX < 0) || (sign == 1 && relPosX > 0)) relPosX = 0;
        }

        if (relPosY != 0) {
            changed = true;
            int sign = (relPosY > 0) ? -1 : 1;
            relPosY += sign * PAD_RETURN_SPEED * delta;
            if ((sign == -1 && relPosY < 0) || (sign == 1 && relPosY > 0)) relPosY = 0;
        }

        if (changed) {
            actTepmVec.set(relPosX, relPosY).limit(radius);

            padImage.setX(actTepmVec.x * radius + radius - padImage.getWidth() / 2);
            padImage.setY(actTepmVec.y * radius + radius - padImage.getHeight() / 2);
        }
    }


}




