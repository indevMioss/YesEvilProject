package com.interdev.game.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.interdev.game.GameMain;
import com.interdev.game.camera.MultipleVirtualViewportBuilder;
import com.interdev.game.camera.OrthographicCameraWithVirtualViewport;
import com.interdev.game.camera.VirtualViewport;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.tools.Utils;

public class MenuScreen implements Screen {

    private final GameMain gameMain;
    private MultipleVirtualViewportBuilder multipleVirtualViewportBuilder;
    private OrthographicCameraWithVirtualViewport camera;

    private Stage mainStage;

    private String[] textes = {"You are being chased by an evil demon!",
            "Try to pick up as many blue grains of goodness as you can.",
            "Use accelerometer to control and tap to jump.",
            "The demon feels your presence, and is getting angrier\n" +
                    "with every attempt you make to find\n" +
                    "hope in this sinister pit."};

    public MenuScreen(GameMain gameMain) {
        this.gameMain = gameMain;
    }

    @Override
    public void show() {

        multipleVirtualViewportBuilder = new MultipleVirtualViewportBuilder(GameMain.VIRTUAL_WIDTH_MAX,
                GameMain.VIRTUAL_HEIGHT_MIN,
                GameMain.VIRTUAL_WIDTH_MAX,
                GameMain.VIRTUAL_HEIGHT_MAX);

        VirtualViewport virtualViewport = multipleVirtualViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new OrthographicCameraWithVirtualViewport(virtualViewport);
        float width = virtualViewport.getWidth();
        float height = virtualViewport.getHeight();

        mainStage = new Stage();
        mainStage.getViewport().setCamera(camera);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.size = 54;
        BitmapFont font = generator.generateFont(parameter);
        Utils.applyLinearFilter(font.getRegion().getTexture());

        parameter.size = 64;
        BitmapFont biggerFont = generator.generateFont(parameter);
        Utils.applyLinearFilter(biggerFont.getRegion().getTexture());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        TextButton textButton = new TextButton(textes[0], textButtonStyle);
        textButton.setPosition(width / 2 - textButton.getWidth() / 2, height * 0.7f);
        mainStage.addActor(textButton);

        TextButton text2Button = new TextButton(textes[1], textButtonStyle);
        text2Button.setPosition(width / 2 - text2Button.getWidth()/2, height *0.6f);
        mainStage.addActor(text2Button);

        TextButton.TextButtonStyle biggerTextButtonStyle = new TextButton.TextButtonStyle();
        biggerTextButtonStyle.font = biggerFont;
        TextButton text3Button = new TextButton(textes[2], biggerTextButtonStyle);
        text3Button.setPosition(width / 2 - text3Button.getWidth()/2, height *0.48f);
        mainStage.addActor(text3Button);

        TextButton.TextButtonStyle redTextButtonStyle = new TextButton.TextButtonStyle(textButtonStyle);
        redTextButtonStyle.fontColor = Color.RED;
        TextButton text4Button = new TextButton(textes[3], redTextButtonStyle);
        text4Button.setPosition(width / 2 - text4Button.getWidth()/2, height *0.25f);
        mainStage.addActor(text4Button);


///////////////////////// ==>
        gameMain.setScreen(new GameScreen(gameMain.soundSystem));

        ///////////////////////// xxx
/*
        mainStage.addListener(new InputListener() {
            boolean pressedAlready = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!pressedAlready) {
                    pressedAlready = true;
                    motioGame.setScreen(new GameScreen(motioGame.soundSystem));
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        Gdx.input.setInputProcessor(mainStage);
        */
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainStage.act();
        mainStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        VirtualViewport virtualViewport = multipleVirtualViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setVirtualViewport(virtualViewport);
        camera.updateViewport();
        camera.position.set(virtualViewport.getWidth() / 2, virtualViewport.getHeight() / 2, 0f);
        camera.zoom = 1.0f;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
