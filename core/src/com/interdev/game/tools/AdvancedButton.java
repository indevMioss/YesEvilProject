package com.interdev.game.tools;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Timer;

public class AdvancedButton extends ImageButton {

    private Timer shineTimer;
    private Image shineImage;

    public AdvancedButton(Texture textureUp, Texture textureDown) {
        super(new Image(textureUp).getDrawable(), new Image(textureDown).getDrawable());
        Utils.applyLinearFilter(textureUp, textureDown);
    }

    public AdvancedButton(Texture textureUp, Texture textureDown, Texture shineTexture) {
        this(textureUp, textureDown);
        Utils.applyLinearFilter(shineTexture);
        shineImage = new Image(shineTexture);
        shineImage.setPosition(getWidth() / 2 - shineImage.getWidth() / 2, getHeight() / 2 - shineImage.getHeight() / 2);
        shineImage.setColor(1f, 1f, 1f, 0);
        addActor(shineImage);
        // shineImage.setZIndex(0);

        shineTimer = new Timer();
        shineTimer.scheduleTask(new Timer.Task() {
            int signum = 1;

            @Override
            public void run() {
                float alpha = shineImage.getColor().a;
                alpha += signum * 0.05f;
                alpha = Utils.trimValue(0, 1, alpha);
                shineImage.setColor(1f, 1f, 1f, alpha);

                if ((signum == -1 && alpha == 0) || (signum == 1 && alpha == 1)) signum *= -1;
            }
        }, 0, 0.06f);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (shineImage != null) {
            if (visible) shineTimer.start();
            else shineTimer.stop();

        }
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        if (shineImage != null) {
            shineImage.setVisible(isDisabled);
            if (isDisabled) shineTimer.stop();
            else shineTimer.start();

        }
    }

    public AdvancedButton(Texture textureUp, Texture textureDown, Texture textureDisabled, Texture shineTexture) {
        this(textureUp, textureDown, shineTexture);
        Utils.applyLinearFilter(textureDisabled);
        getStyle().imageDisabled = new Image(textureDisabled).getDrawable();

    }
}
