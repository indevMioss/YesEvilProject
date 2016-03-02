package com.interdev.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.interdev.game.screens.menu.MenuScreen;
import com.interdev.game.sound.SoundSystem;

public class GameMain extends Game {
    public static int maxLevel = 0;

    public static int redCrystals = 0;
    public static int blueCrystals = 0;

    public static int healthBottles;
    public static int powerBottles;
    public static int resurrectionBottles;
    public static int timeBottles;

    public static final int VIRTUAL_WIDTH_MAX = 1920;
    public static final int VIRTUAL_HEIGHT_MIN = 1080;
    public static final int VIRTUAL_HEIGHT_MAX = 1200;
    public static final float PPM = 100;
    public static float VIRTUAL_WIDTH_TO_REAL;

    private ParseCom parseCom = null;
    public SoundSystem soundSystem;



    public GameMain() {
    }

    public GameMain(ParseCom parseCom) {
        this.parseCom = parseCom;
    }

    @Override
    public void create() {
        VIRTUAL_WIDTH_TO_REAL = (float) VIRTUAL_WIDTH_MAX / (float) Gdx.graphics.getWidth();

        loadAndApplyPrefs();

        healthBottles = 12;
        powerBottles = 7;
        resurrectionBottles = 3;
        timeBottles = 1;

        inrementStatsIfDidnt();

        soundSystem = new SoundSystem();
        soundSystem.setMuteSounds(false);

        setScreen(new MenuScreen(this));
    }

    public void loadAndApplyPrefs() {
        Preferences prefs = Gdx.app.getPreferences("settings");

        maxLevel = prefs.getInteger("maxLevel", 0);

        redCrystals = prefs.getInteger("redCrystals", 0);
        blueCrystals = prefs.getInteger("blueCrystals", 0);

        healthBottles = prefs.getInteger("healthBottles", 0);
        powerBottles = prefs.getInteger("powerBottles", 0);
        resurrectionBottles = prefs.getInteger("resurrectionBottles", 0);
        timeBottles = prefs.getInteger("timeBottles", 0);
    }

    public void savePrefs() {
        Preferences prefs = Gdx.app.getPreferences("settings");
        prefs.putInteger("maxLevel", maxLevel);

        prefs.putInteger("redCrystals", redCrystals);
        prefs.putInteger("blueCrystals", blueCrystals);

        prefs.putInteger("healthBottles", healthBottles);
        prefs.putInteger("powerBottles", powerBottles);
        prefs.putInteger("resurrectionBottles", resurrectionBottles);
        prefs.putInteger("timeBottles", timeBottles);




        prefs.flush();
    }


    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        savePrefs();
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        soundSystem.dispose();
    }

    private void inrementStatsIfDidnt() {
        if (parseCom == null) return;
        Preferences prefs = Gdx.app.getPreferences("settings");
        boolean incrementedAlready = prefs.getBoolean(("incrementedStats"), false);
        if (!incrementedAlready) {
            System.out.println("---incrementedStats == false");
            parseCom.incrementStats();
            prefs.putBoolean("incrementedStats", true);
            prefs.flush();
        }
    }
}
