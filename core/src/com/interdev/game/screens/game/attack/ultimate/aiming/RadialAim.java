package com.interdev.game.screens.game.attack.ultimate.aiming;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.interdev.game.camera.OrthographicCameraWithVirtualViewport;
import com.interdev.game.camera.VirtualViewport;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.OneFloatChangeListener;

public class RadialAim implements Disposable {

    private boolean active = false;
    private final OrthographicCameraWithVirtualViewport camera;
    private final Stage stage;
    private final Lightning lightning;

    private float rotation = 0;

    private OneFloatChangeListener completionListener = null;

    public RadialAim(VirtualViewport hudVirtualViewport) {
        camera = new OrthographicCameraWithVirtualViewport(hudVirtualViewport);
        camera.updateViewport();

        stage = new Stage();
        stage.getViewport().setCamera(camera);
        stage.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        lightning = new Lightning();
        stage.addActor(lightning);
    }

    public void start(OneFloatChangeListener floatArgActionListener) {
        this.completionListener = floatArgActionListener;
        rotation = 0;
        active = true;
    }

    public void end() {
        active = false;
        if (completionListener == null) return;
        completionListener.onValueChange(rotation);
        rotate(-rotation);
    }

    public void update(float delta) {
        if (!active) return;
        camera.update();
        rotate(180f * delta);
        stage.act();
    }

    public void draw() {
        if (!active) return;
        stage.draw();
    }

    private void rotate(float degrees) {
        camera.rotate(degrees);
        rotation += degrees;
        if (rotation >= 360) rotation -= 360;
        if (rotation <= -360) rotation += 360;
    }

    public void onTouchDown() {
        if (!active) return;
        System.out.println("RADIAL AIM TOUCH DOWN " + rotation);
        end();
    }

    @Override
    public void dispose() {
        lightning.dispose();
    }


    private class Lightning extends Actor implements Disposable {
        private final ParticleEffect lightningEffect;

        public Lightning() {
            setBounds(0, 0, GameScreen.hudWidth, GameScreen.hudHeight);
            lightningEffect = new ParticleEffect();
            lightningEffect.load(Gdx.files.internal("effects/ultimate/lightning.p"), Gdx.files.internal("effects"));
            lightningEffect.setPosition(GameScreen.hudWidth / 2, GameScreen.hudHeight / 2);
            lightningEffect.start();
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            lightningEffect.update(delta * 2f);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            lightningEffect.draw(batch);
        }

        @Override
        public void dispose() {
            lightningEffect.dispose();
        }
    }
}














