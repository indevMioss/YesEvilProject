package com.interdev.game.screens.game.attack.ultimate;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.interdev.game.camera.VirtualViewport;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.attack.ultimate.aiming.Forcemeter;
import com.interdev.game.screens.game.attack.ultimate.aiming.RadialAim;
import com.interdev.game.screens.game.hud.stamina.StaminaOrbits;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.tools.OneFloatChangeListener;

public class UltimateSystem implements InputProcessor {

    private boolean active;

    private Forcemeter forcemeter;
    private RadialAim radialAim;
    private Gun gun;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private StaminaOrbits staminaOrbits;
    private Stage hudStage;
    private SpriteBatch mainBatch;

    public UltimateSystem(World world,
                          StaminaOrbits staminaOrbits,
                          Player player,
                          VirtualViewport vp,
                          Stage hudStage,
                          SpriteBatch mainBatch,
                          Array<TextureAtlas.AtlasRegion> particleFrames) {
        this.staminaOrbits = staminaOrbits;
        this.hudStage = hudStage;
        this.mainBatch = mainBatch;
        forcemeter = new Forcemeter(particleFrames);
        radialAim = new RadialAim(vp);
        gun = new Gun(player, world);

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setColor(0, 0, 0, 0.5f);

      //  gun.shoot(0, 1, Gun.WeaponType.LIGHTNING);

    }

    public void update(float delta) {
        gun.act(delta);

        forcemeter.act(delta);
        radialAim.update(delta);
    }

    public void draw() {
        mainBatch.begin();
        gun.draw(mainBatch, 1f);
        mainBatch.end();

        if (!isActive()) return;
        drawDarkRect();

        hudStage.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        hudStage.getBatch().begin();
        forcemeter.draw(hudStage.getBatch(), 1f);
        hudStage.getBatch().end();
        hudStage.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        radialAim.draw();
    }

    private void drawDarkRect() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, GameScreen.hudWidth, GameScreen.hudHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean isActive() {
        return active;
    }

    public void start() {
        if (staminaOrbits.getTotalStamina() < 3F) return;
        staminaOrbits.removeStamina(3F);

        active = true;
        forcemeter.start(new OneFloatChangeListener() {
            @Override
            public void onValueChange(final float power) {
                radialAim.start(new OneFloatChangeListener() {
                    @Override
                    public void onValueChange(float angle) {
                        System.out.println(angle);
                        gun.shoot(angle, power, Gun.WeaponType.LIGHTNING);
                        active = false;
                    }
                });
            }
        });
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isActive()) {
            forcemeter.onTouchDown();
            radialAim.onTouchDown();
            return true;
        }
        return false;
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


}
