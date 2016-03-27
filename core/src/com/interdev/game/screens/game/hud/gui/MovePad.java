package com.interdev.game.screens.game.hud.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.tools.Utils;

public class MovePad extends Group {

    public final InputListener inputListener;
    private Image padImage;

    private Vector2 relPos = new Vector2();

    public float getRelPosX() {
        return relPos.x;
    }

    public float getRelPosY() {
        return relPos.y;
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
                //y = Math.max(0, y);
                float xSig = Math.signum(x);
                float ySig = Math.signum(y);
                Vector2 vec = new Vector2(Math.abs(x), Math.abs(y)).limit(radius);
                vec.scl(xSig, ySig);
                relPos.x = vec.x / radius; // [-1 : 1]
                relPos.y = vec.y / radius; // [-1 : 1]
                padImage.setX(vec.x + radius - padImage.getWidth() / 2);
                padImage.setY(vec.y + radius - padImage.getHeight() / 2);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                touching = false;
                float ang = (float) Math.atan2(relPos.y, relPos.x);
               // if (ang < 0) ang += 360;
                Player.inst.shumpoJump(ang, relPos.len());
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
        actTepmVec.set(0, 0);

        if (relPos.x != 0) {
            changed = true;
            int sign = (relPos.x > 0) ? -1 : 1;
            relPos.x += sign * PAD_RETURN_SPEED * delta;
            if ((sign == -1 && relPos.x < 0) || (sign == 1 && relPos.x > 0)) relPos.x = 0;
        }

        if (relPos.y != 0) {
            changed = true;
            int sign = (relPos.y > 0) ? -1 : 1;
            relPos.y += sign * PAD_RETURN_SPEED * delta;
            if ((sign == -1 && relPos.y < 0) || (sign == 1 && relPos.y > 0)) relPos.y = 0;
        }

        if (changed) {
            actTepmVec.set(relPos.x, relPos.y).limit(radius);

            padImage.setX(actTepmVec.x * radius + radius - padImage.getWidth() / 2);
            padImage.setY(actTepmVec.y * radius + radius - padImage.getHeight() / 2);
        }
    }


}




