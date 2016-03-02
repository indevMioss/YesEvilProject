package com.interdev.game.screens.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.interdev.game.screens.game.hud.gui.MovePad;
import com.interdev.game.screens.game.entities.Player;

public class ControlsInput extends InputAdapter {

    public static boolean using_accelerometer = false;

    private float orientationFactor = 1f;
    private Player player;
    private MovePad movePad;

    public ControlsInput(Player player, MovePad movePad, InputMultiplexer inputMultiplexer) {
        this.player = player;
        this.movePad = movePad;
        inputMultiplexer.addProcessor(this);
    }

    private int orientCheckIntervalCounter = 0;

    public void checkInput() {
        if (using_accelerometer) {
            player.move(getAccelerometerFactor());
            orientCheckIntervalCounter++;
            if (orientCheckIntervalCounter >= 30) {
                orientCheckIntervalCounter = 0;
                updateOrientationFactor();
            }
        } else {
            player.move(movePad.getRelPosX());
            if (player.floatingMode) {
                if (movePad.getRelPosY() != 0)
                    player.floatUp(movePad.getRelPosY());
            } else {
                if (movePad.getRelPosY() >= 0.5f) {
                    player.jump();
                }
            }

        }
    }

    public void updateOrientationFactor() {
        if (Gdx.input.getRotation() == 90) {
            orientationFactor = 1f;
        } else {
            orientationFactor = -1f;
        }
    }

    private float getAccelerometerFactor() {
        float accelY = Gdx.input.getAccelerometerY() / 10;
        accelY *= orientationFactor;
        return accelY;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (using_accelerometer) {
            player.jump();
            return true;
        } else return false;
    }

}



/*
        private final Player player;

    private TextButton scoreTextButton, maxScoreTextButton, moneyTextButton;



    public ControlsUI(Stage stage, Player player, InputMultiplexer inputMultiplexer) {
        this.player = player;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;

        parameter.size = 64;
        BitmapFont font = generator.generateFont(parameter);
        Utils.applyLinearFilter(font.getRegion().getTexture());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        maxScoreTextButton = new TextButton("", textButtonStyle);
        scoreTextButton = new TextButton("", textButtonStyle);
        moneyTextButton = new TextButton("0", textButtonStyle);

        stage.addActor(moneyTextButton);
        resize();

        inputMultiplexer.addProcessor(this);

    }

    private String scoreLabel = "Score: ";
    private StringBuilder scoreStringBuilder = new StringBuilder().append(scoreLabel);

    private String maxScoreLabel = "Max: ";
    private StringBuilder maxScoreStringBuilder = new StringBuilder().append(maxScoreLabel);

    private void updateScoresText() {
        scoreStringBuilder.setLength(scoreLabel.length());
        maxScoreStringBuilder.setLength(maxScoreLabel.length());

        scoreStringBuilder.append(String.valueOf(MotioGame.getScore()));
        maxScoreStringBuilder.append(String.valueOf(MotioGame.getMaxScore()));

        scoreTextButton.setText(scoreStringBuilder.toString());
        maxScoreTextButton.setText(maxScoreStringBuilder.toString());
    }

    public void resize() {
        // updateScoresText();
        //   maxScoreTextButton.setPosition(GameScreen.hudWidth * 0.085f, GameScreen.hudHeight - maxScoreTextButton.getHeight() * 1.1f);
        //    scoreTextButton.setPosition(GameScreen.hudWidth * 0.085f, maxScoreTextButton.getY() - scoreTextButton.getHeight());
        moneyTextButton.setPosition(GameScreen.hudWidth * 0.085f, GameScreen.hudHeight - moneyTextButton.getHeight() * 1.1f);
    }


    private int meanMoney = 0;
    private final static float transferValueTime = 0.015f;
    private float uTimePassed = 0;

    public void update(float delta) {
        if (meanMoney >= GameScreen.moneyThisRound) return;
        uTimePassed += delta;
        if (uTimePassed >= transferValueTime) {
            uTimePassed = 0;
            meanMoney++;
            moneyTextButton.setText(String.valueOf(meanMoney));
        }
    }
*/
