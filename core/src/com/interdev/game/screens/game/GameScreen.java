package com.interdev.game.screens.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.MotionBlur;
import com.bitfire.postprocessing.effects.Vignette;
import com.bitfire.utils.ShaderLoader;
import com.interdev.game.GameMain;
import com.interdev.game.camera.MultipleVirtualViewportBuilder;
import com.interdev.game.camera.OrthographicCameraWithVirtualViewport;
import com.interdev.game.camera.VirtualViewport;
import com.interdev.game.screens.game.attack.Aim;
import com.interdev.game.screens.game.attack.BulletParamsEnum;
import com.interdev.game.screens.game.attack.BulletSystem;
import com.interdev.game.screens.game.attack.ultimate.UltimateSystem;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.screens.game.hud.ControlsInput;
import com.interdev.game.screens.game.hud.ShieldField;
import com.interdev.game.screens.game.hud.directionsigns.DirectionSignFactory;
import com.interdev.game.screens.game.hud.gui.*;
import com.interdev.game.screens.game.hud.stamina.StaminaOrbits;
import com.interdev.game.screens.game.levels.LevelsSystem;
import com.interdev.game.screens.game.other.Box2DWorldCreator;
import com.interdev.game.screens.game.trophy.TrophySystem;
import com.interdev.game.sound.MusicSystem;
import com.interdev.game.sound.SoundSystem;
import com.interdev.game.tools.ParallaxTiledBg;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    public static GameScreen inst;

    public static final float DEFAULT_ZOOM = 0.75f;
    public static float zoom = DEFAULT_ZOOM;

    private static final float SLOWEST_TIME_FACTOR = 1f;
    private static final float START_SLOWMO_DIST = 0.2f;
    private static final float GRAVITY = -18f;

    public static float worldWidthPx;
    public static float worldHeightPx;

    public static float hudWidth, hudHeight;

    private MultipleVirtualViewportBuilder multipleVirtualViewportBuilder, hudMultipleVirtualViewportBuilder;
    private static OrthographicCameraWithVirtualViewport camera;
    private static OrthographicCameraWithVirtualViewport hudCamera;

    private TextureAtlas atlas, atlas2, trophiesAtlas, anglerAtlas,
            ballAtlas, flyAtlas, horseAtlas, cloudAtlas, shumpoAtlas;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private SoundSystem soundSystem;

    public static World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private SpriteBatch mainBatch;
    private ControlsInput controlsInput;
    private ParallaxTiledBg parallaxTiledBg;
    private TiledMapTileLayer platformsLayer;

    private Player player;
    // private GoodStar goodStar;

    private Stage hudStage;
    private DemonsSystem demonsSystem;

    private static List<Runnable> afterWorldStepList = new ArrayList<Runnable>();
    private EffectsSystem effectsSystem;
    private RestartUI restartUI;

    private MusicSystem musicSystem;
    private MusicText musicTextUI;
    private ShieldField shieldField;
    private DirectionSignFactory directionSignFactory;

    private UltimateSystem ultimateSystem;
    private PostProcessor postProcessor;
    private BulletSystem bulletSystem;
    private TrophySystem trophySystem;

    private Bloom bloom;
    private StaminaOrbits staminaOrbits;
    private LevelsSystem levelsSystem;
    private AmmoVisual ammoVisual;

    public GameScreen(SoundSystem soundSystem) {
        this.soundSystem = soundSystem;
    }

    @Override
    public void show() {
        inst = this;
        multipleVirtualViewportBuilder = new MultipleVirtualViewportBuilder(GameMain.VIRTUAL_WIDTH_MAX / GameMain.PPM,
                GameMain.VIRTUAL_HEIGHT_MIN / GameMain.PPM,
                GameMain.VIRTUAL_WIDTH_MAX / GameMain.PPM,
                GameMain.VIRTUAL_HEIGHT_MAX / GameMain.PPM);
        VirtualViewport virtualViewport = multipleVirtualViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth() / GameMain.PPM,
                Gdx.graphics.getHeight() / GameMain.PPM);
        camera = new OrthographicCameraWithVirtualViewport(virtualViewport);

        hudMultipleVirtualViewportBuilder = new MultipleVirtualViewportBuilder(GameMain.VIRTUAL_WIDTH_MAX,
                GameMain.VIRTUAL_HEIGHT_MIN,
                GameMain.VIRTUAL_WIDTH_MAX,
                GameMain.VIRTUAL_HEIGHT_MAX);
        VirtualViewport hudVirtualViewport = hudMultipleVirtualViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        hudWidth = hudVirtualViewport.getWidth();
        hudHeight = hudVirtualViewport.getHeight();

        hudCamera = new OrthographicCameraWithVirtualViewport(hudVirtualViewport);
        mainBatch = new SpriteBatch();

        tiledMap = new TmxMapLoader().load("maps/map1ver4.tmx");
        for (TiledMapTileSet tileSet : tiledMap.getTileSets()) {
            for (int i = 0; i < tileSet.size(); i++) {
                if (tileSet.getTile(i) == null) continue;
                Utils.applyLinearFilter(tileSet.getTile(i).getTextureRegion().getTexture());
            }
        }

        MapProperties prop = tiledMap.getProperties();
        worldWidthPx = prop.get("width", Integer.class) * prop.get("tilewidth", Integer.class);
        worldHeightPx = prop.get("height", Integer.class) * prop.get("tileheight", Integer.class);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / GameMain.PPM);

        parallaxTiledBg = new ParallaxTiledBg(tiledMapRenderer, virtualViewport.getWidth(), virtualViewport.getHeight());
        parallaxTiledBg.addParallaxLayer((TiledMapTileLayer) tiledMap.getLayers().get("Stars"), 0.1f, 1f);
        //parallaxTiledBg.addParallaxLayer((TiledMapTileLayer) tiledMap.getLayers().get("Planets"), 0.1f, 1f);
        parallaxTiledBg.addParallaxLayer((TiledMapTileLayer) tiledMap.getLayers().get("Clouds"), 0.08f, 0.5f);
        platformsLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Platforms");

        world = new World(new Vector2(0, GRAVITY), true);
        atlas = new TextureAtlas("atlases/atlas.txt");
        atlas2 = new TextureAtlas("atlases/atlas2.txt");
        trophiesAtlas = new TextureAtlas("atlases/trophies_atlas.txt");
        anglerAtlas = new TextureAtlas("spine/angler.atlas");
        ballAtlas = new TextureAtlas("spine/monster_ball.atlas");
        flyAtlas = new TextureAtlas("spine/monster_fly.atlas");
        horseAtlas = new TextureAtlas("spine/monster_horse.atlas");
        cloudAtlas = new TextureAtlas("spine/cloud_demon.atlas");
        shumpoAtlas = new TextureAtlas("atlases/shumpo.txt");

        Utils.applyLinearFilter(atlas);
        Utils.applyLinearFilter(atlas2);
        Utils.applyLinearFilter(trophiesAtlas);
        Utils.applyLinearFilter(anglerAtlas);
        Utils.applyLinearFilter(ballAtlas);
        Utils.applyLinearFilter(flyAtlas);
        Utils.applyLinearFilter(horseAtlas);
        Utils.applyLinearFilter(cloudAtlas);
        Utils.applyLinearFilter(shumpoAtlas);


        box2DDebugRenderer = new Box2DDebugRenderer();
        new Box2DWorldCreator(world, tiledMap.getLayers().get("Objects"));

        hudStage = new Stage();
        hudStage.getViewport().setCamera(hudCamera);


        Aim aim = new Aim();
        aim.setPosition(hudWidth / 2, hudHeight / 2);
        hudStage.addActor(aim);

        player = new Player(atlas.findRegions("spirit"), shumpoAtlas.findRegions("shumpo"));
        ammoVisual = new AmmoVisual();

        shieldField = new ShieldField(atlas.findRegions("shield"), player);
        effectsSystem = new EffectsSystem(player);
        //goodStar = new GoodStar(atlas.findRegions("goodstar"), world, effectsSystem);

        directionSignFactory = new DirectionSignFactory(player, hudStage);
        // directionSignFactory.createDirectionSign(goodStar);

        hudStage.addActor(shieldField);

        staminaOrbits = new StaminaOrbits(hudWidth * 0.12f, atlas.findRegions("orb"));
        staminaOrbits.addStamina(5.5f);

        Lives lives = new Lives();

        restartUI = new RestartUI(hudStage, this);

        musicSystem = new MusicSystem();
        musicTextUI = new MusicText(hudStage, musicSystem);

        ultimateSystem = new UltimateSystem(world, staminaOrbits, player, hudVirtualViewport,
                hudStage, mainBatch, atlas2.findRegions("particle"));

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(ultimateSystem);
        inputMultiplexer.addProcessor(hudStage);

        bulletSystem = new BulletSystem(world, player);
        bulletSystem.addLaunchPosChangeListener(aim.getAdjustPosListener());

        GUI gui = new GUI(ultimateSystem, aim, lives, staminaOrbits);
        gui.setPosition(0, 0);
        hudStage.addActor(gui);


        trophySystem = new TrophySystem(world, trophiesAtlas, atlas);
        trophySystem.setLivesAddListener(lives.getListenerForTrophyLives());
        trophySystem.setBlueCryChangeListener(gui.crystAlphaUI.getBlueCryChangeListener());
        trophySystem.setRedCryChangeListener(gui.crystAlphaUI.getRedCryChangeListener());

        demonsSystem = new DemonsSystem(atlas, anglerAtlas, ballAtlas, flyAtlas, horseAtlas, cloudAtlas);

        world.setContactListener(new WorldContactListener());

        controlsInput = new ControlsInput(player, gui.movePad, inputMultiplexer);
        Gdx.input.setInputProcessor(inputMultiplexer);


        //   musicSystem.play();


        ShaderLoader.BasePath = "resources/shaders/";
        postProcessor = new PostProcessor(false, true, (Gdx.app.getType() == Application.ApplicationType.Desktop));

        MotionBlur blur = new MotionBlur();
        blur.setEnabled(true);
        blur.setBlurOpacity(0.5f);


        bloom = new Bloom((int) (Gdx.graphics.getWidth() * 0.25f), (int) (Gdx.graphics.getHeight() * 0.25f));

        bloom.setSettings(new Bloom.Settings("sett1",
                3,
                1f,
                1.0f,
                1.0f,
                1.0f,
                1f));// fancy bloom

        Vignette vignette = new Vignette((int) (Gdx.graphics.getWidth() * 0.25f),
                (int) (Gdx.graphics.getHeight() * 0.25f), false);

        postProcessor.addEffect(vignette);
        postProcessor.addEffect(bloom);
        //  postProcessor.addEffect(blur);

        bloom.setEnabled(true);
        postProcessor.setEnabled(false);

        levelsSystem = new LevelsSystem();

        final InterlevelScene interlevelScene = new InterlevelScene();
        hudStage.addActor(interlevelScene);

        levelsSystem.start();
    }

    public static void addAfterWorldStepRunnable(Runnable runnable) {
        afterWorldStepList.add(runnable);
    }

    private void checkAfterWorldStepRunnableList() {
        Iterator<Runnable> iterator = afterWorldStepList.iterator();
        while (iterator.hasNext()) {
            Runnable runnable = iterator.next();
            runnable.run();
            iterator.remove();
        }
    }

    private static float timeFactor = 1f;

    private float calcTimeFactor(float closestDistPercent) {
        if (closestDistPercent < START_SLOWMO_DIST) {
            return Math.max(closestDistPercent / START_SLOWMO_DIST, SLOWEST_TIME_FACTOR);
        }
        return 1;
    }

    private void update(float delta) {
        delta *= timeFactor;
        handleKeyboardInput();
        handleTouchInput();
        if (!gameStopped && !ultimateSystem.isActive()) {
            world.step(delta, 1, 1);
            checkAfterWorldStepRunnableList();

            player.act(delta);
            //goodStar.act(delta);
            levelsSystem.update(delta);
            trophySystem.act(delta);
            demonsSystem.update(delta);
            bulletSystem.act(delta);
            staminaOrbits.act(delta);
            ammoVisual.act(delta);
            moveCamera();
        } else {
            player.passiveAct(delta);
        }
        camera.update();
        hudCamera.update();
        hudStage.act();

        ultimateSystem.update(delta);

        timeFactor = calcTimeFactor(directionSignFactory.getClosestDistPercent());
        restartUI.update(delta);
        musicTextUI.update(delta);
        parallaxTiledBg.update(camera);
        effectsSystem.update(delta);

    }

    private void moveCamera() {
        camera.position.x = player.getX();
        camera.position.y = player.getY();
        /*
        Vector2 deltaDist = new Vector2(player.getX() - camera.position.x, player.getY() - camera.position.y);
        float len = deltaDist.len();

        float camSpeed = 0.15f;
        final float maxCamOffset = 1f;
        camSpeed += len - maxCamOffset;

        if (camSpeed <= 0) {
            float camMoveSpdFactor = Utils.trimValue(0, 1, Interpolation.linear.apply(len / maxCamOffset));
            camSpeed = 0.15f * camMoveSpdFactor;
        }

        //camSpeed *= camMoveSpdFactor;
        System.out.println("old " + camera.position.x);
        camera.position.x = Utils.pull(camera.position.x, player.getX(), camSpeed);
        camera.position.y = Utils.pull(camera.position.y, player.getY(), camSpeed);
        System.out.println("new " + camera.position.x);
        */
    }


    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        postProcessor.capture();

        parallaxTiledBg.draw();
        mainBatch.setProjectionMatrix(camera.combined);
        mainBatch.begin();
        player.draw(mainBatch, 1f);
        mainBatch.end();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(platformsLayer);
        tiledMapRenderer.getBatch().end();

        mainBatch.begin();
        //goodStar.draw(mainBatch, 1f);
        trophySystem.draw(mainBatch, 1f);

        demonsSystem.draw(mainBatch);

        effectsSystem.draw(mainBatch, delta * 1f);
        bulletSystem.draw(mainBatch, delta);
        staminaOrbits.draw(mainBatch, 1f);
        ammoVisual.draw(mainBatch, 1f);
        mainBatch.end();

        hudStage.draw();
        ultimateSystem.draw();
        hudStage.getBatch().setColor(1f, 1f, 1f, 1f);
        postProcessor.render();
        //  fpsLogger.log();
        //  box2DDebugRenderer.render(world, camera.combined);
    }

    private FPSLogger fpsLogger = new FPSLogger();


    public static void setSlowmoFactor(float factor) {
        timeFactor = factor;
    }


    public static boolean inFrustum(Actor actor) {
        return true;// camera.frustum.boundsInFrustum(actor.getX(), actor.getY(), 0, actor.getWidth(), actor.getHeight(), 0);
    }

    public static boolean inFrustum(float x, float y) {
        return camera.frustum.pointInFrustum(x, y, 0);
    }

    public static boolean gameStopped = false;

    public void stopGame() {
        gameStopped = true;
        musicSystem.setVolume(MusicSystem.LOW_VOLUME);
        float volume = (LevelsSystem.levelsPassed * 15 + 5) / 100f; //the further you get - the louder you explode
        volume = Math.min(volume, 1f);
        SoundSystem.inst.playSound(SoundSystem.Sounds.DEAD, volume);
        EffectsSystem.inst.doEffect(player.getX(), player.getY(), EffectsSystem.Type.GG);
        restartUI.show();
        player.setColor(1f, 1f, 1f, 0f);
    }

    public void resetGame() {
        gameStopped = false;
        musicSystem.setVolume(MusicSystem.DEFAULT_VOLUME);

        restartUI.hide();
        player.resetPosition();
        player.setColor(1f, 1f, 1f, 1f);
    }

    private void handleTouchInput() {
        //controlsInput.checkInput();
    }

    private void handleKeyboardInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) player.jump();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.move(-1);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.move(1);


        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            bloom.setThreshold(bloom.getThreshold() - 0.05f);
            System.out.println("getThreshold = " + bloom.getThreshold());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            bloom.setThreshold(bloom.getThreshold() + 0.05f);
            System.out.println("getThreshold = " + bloom.getThreshold());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            bloom.setBaseIntesity(bloom.getBaseIntensity() - 0.05f);
            System.out.println("getBaseIntensity = " + bloom.getBaseIntensity());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            bloom.setBaseIntesity(bloom.getBaseIntensity() + 0.05f);
            System.out.println("getBaseIntensity = " + bloom.getBaseIntensity());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            bloom.setBaseSaturation(bloom.getBaseSaturation() - 0.05f);
            System.out.println("getBaseSaturation = " + bloom.getBaseSaturation());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            bloom.setBaseSaturation(bloom.getBaseSaturation() + 0.05f);
            System.out.println("getBaseSaturation = " + bloom.getBaseSaturation());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.V)) {
            bloom.setBloomIntesity(bloom.getBloomIntensity() - 0.05f);
            System.out.println("getBloomIntensity = " + bloom.getBloomIntensity());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            bloom.setBloomIntesity(bloom.getBloomIntensity() + 0.05f);
            System.out.println("getBloomIntensity = " + bloom.getBloomIntensity());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.B)) {
            bloom.setBloomSaturation(bloom.getBloomSaturation() - 0.05f);
            System.out.println("getBloomSaturation = " + bloom.getBloomSaturation());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.G)) {
            bloom.setBloomSaturation(bloom.getBloomSaturation() + 0.05f);
            System.out.println("getBloomSaturation = " + bloom.getBloomSaturation());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.N)) {
            bloom.setBlurAmount(bloom.getBlurAmount() - 0.05f);
            System.out.println("getBlurAmount = " + bloom.getBlurAmount());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            bloom.setBlurAmount(bloom.getBlurAmount() + 0.05f);
            System.out.println("getBlurAmount = " + bloom.getBlurAmount());
        }


    }

    public enum Effect {
        FLASH
    }

    public void doEffect(Effect effect) {
        switch (effect) {
            case FLASH:
                postProcessor.setEnabled(true);
                bloom.setEnabled(true);
                Timer.schedule(new Timer.Task() {
                    float destThreshold = -2f;
                    float destIntensity = 2.5f;

                    @Override
                    public void run() {

                        float newIntensity = bloom.getBloomIntensity();
                        newIntensity += (destIntensity == 0) ?
                                (newIntensity < 2.5) ? -0.005f : -0.02f
                                : +0.04f;

                        if (destIntensity != 0 && newIntensity >= destIntensity) {
                            newIntensity = destIntensity;
                            destIntensity = 0;
                        } else if (destIntensity == 0 && newIntensity <= 0) {
                            newIntensity = destIntensity;
                            bloom.setEnabled(false);
                            postProcessor.setEnabled(false);
                            cancel();
                        }

                        bloom.setThreshold(newIntensity / 4f * (destThreshold - 1) + 1);

                        bloom.setBloomIntesity(newIntensity);
                    }
                }, 0, 0.01f);
                break;
        }
    }


    public void changeZoom(float newZoom) {
        zoom = newZoom;
        camera.zoom = newZoom;
    }

    @Override
    public void resize(int width, int height) {
        VirtualViewport virtualViewport = multipleVirtualViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth() / GameMain.PPM,
                Gdx.graphics.getHeight() / GameMain.PPM);
        camera.setVirtualViewport(virtualViewport);
        camera.updateViewport();
        changeZoom(DEFAULT_ZOOM);
        camera.update();

        VirtualViewport hudVirtualViewport = hudMultipleVirtualViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setVirtualViewport(hudVirtualViewport);
        hudCamera.updateViewport();
        hudCamera.position.set(hudVirtualViewport.getWidth() / 2, hudVirtualViewport.getHeight() / 2, 0f);
        hudCamera.zoom = 1.0f;
        hudCamera.update();

        hudWidth = hudVirtualViewport.getWidth();
        hudHeight = hudVirtualViewport.getHeight();
        shieldField.resize();
        staminaOrbits.resize();
        //     directionSignFactory.resize();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        postProcessor.rebind();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        musicSystem.stop();
        musicSystem.dispose();

        atlas.dispose();
        atlas2.dispose();
        trophiesAtlas.dispose();
        anglerAtlas.dispose();
        ballAtlas.dispose();
        flyAtlas.dispose();
        horseAtlas.dispose();
        cloudAtlas.dispose();

        parallaxTiledBg.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        world.dispose();
        tiledMapRenderer.dispose();
        box2DDebugRenderer.dispose();
        mainBatch.dispose();
        postProcessor.dispose();
        afterWorldStepList = null;
        camera = null;
    }

}
