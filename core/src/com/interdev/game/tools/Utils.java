package com.interdev.game.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;

import java.util.Random;

public class Utils {
    private static Random random = new Random();

    public static void applyLinearFilter(Texture... textures) {
        for (Texture texture : textures) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    public static void applyLinearFilter(TextureAtlas textureAtlas) {
        for (Texture texture : textureAtlas.getTextures()) {
            applyLinearFilter(texture);
        }
    }

    public static boolean roll(float percentWin) {
        return random.nextFloat() <= percentWin;
    }

    public static float getRand(float from, float to) {
        return from + random.nextFloat() * (to - from);
    }

    public static int randInt(int from, int to) {
        return (int) (from + random.nextFloat() * (to - from));
    }


    public static float randomizeVal(float value, float factor) { //(10, 0.5) returns [7.5 - 12.5]
        return (random.nextBoolean()) ?
                value * (1 + random.nextFloat() * factor / 2) :
                value * (1 - random.nextFloat() * factor / 2);
    }

    public static float randIncreaseVal(float value, float factor) { //(10, 0.5) returns [10 - 15]
        return value * (1 + random.nextFloat() * factor);
    }

    public static float randDecreaseVal(float value, float factor) { //(10, 0.5) returns [5 - 10]
        return value * (1 - random.nextFloat() * factor);
    }

    public static float trimValue(float min, float max, float val) {
        return Math.min(Math.max(val, min), max);
    }

    public static int trimValue(int min, int max, int val) {
        return Math.min(Math.max(val, min), max);
    }

    public static float pull(float val, float to, float speed) {
        if (val < to) {
            val += speed;
            val = Math.min(val, to);
        } else if (val > to) {
            val -= speed;
            val = Math.max(val, to);
        }
        return val;
    }


    public static Vector2 getNewWorldEdgePos() { // returns position of a random point on a world's random edge
        Random rand = new Random();
        float newPositionX, newPositionY;
        float offset = GameScreen.worldHeightPx * 0.2f;
        if (rand.nextBoolean()) {
            newPositionX = rand.nextBoolean() ? 0 - offset : GameScreen.worldWidthPx + offset;
            newPositionY = rand.nextInt((int) GameScreen.worldHeightPx);
        } else {
            newPositionY = GameScreen.worldHeightPx + offset;
//          newPositionY = rand.nextBoolean() ? 0 - offset : GameScreen.worldHeightPx + offset;
            newPositionX = rand.nextInt((int) GameScreen.worldWidthPx);
        }
        return new Vector2(newPositionX / GameMain.PPM, newPositionY / GameMain.PPM);
    }

    public static void dynamicSchedule(Timer.Task task, float interval, float intervalDifference) {
        dynamicSchedule(task, interval, intervalDifference, 0f);
    }

    public static void dynamicSchedule(final Timer.Task task, final float interval, final float intervalDifference, final int executions) {
        if (intervalDifference < 0 && executions <= 0) return;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                task.run();
                dynamicSchedule(task, interval + intervalDifference, intervalDifference, executions - 1);
            }
        }, interval);
    }

    public static void dynamicSchedule(final Timer.Task task, final float interval, final float intervalDifference, final float stopThreshold) {
        if (intervalDifference < 0 && interval < stopThreshold || intervalDifference > 0 && interval > stopThreshold)
            return;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                task.run();
                dynamicSchedule(task, interval + intervalDifference, intervalDifference, stopThreshold);
            }
        }, interval);
    }


    public static void graduallyChangeAlpha(final Actor actor, float forTime, final float destAlpha) {
        graduallyChangeAlpha(actor, forTime, destAlpha, 0);
    }


    public static void graduallyChangeAlpha(final Actor actor, float forTime, final float destAlpha, final float delay) {
        graduallyChangeAlpha(actor, forTime, destAlpha, delay, 0.01f);
    }


    public static void graduallyChangeAlpha(final Actor actor, float forTime, final float destAlpha, final float delay, final float interval) {
        final float deltaAlpha = destAlpha - actor.getColor().a;
        final float alphaIncrement = deltaAlpha * interval / forTime;
        if (deltaAlpha == 0) return;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float blackAlpha = actor.getColor().a;
                blackAlpha += alphaIncrement;
                if ((alphaIncrement > 0 && blackAlpha >= destAlpha) ||
                        (alphaIncrement < 0 && blackAlpha <= destAlpha)) {
                    cancel();
                }
                actor.setColor(1f, 1f, 1f, blackAlpha);
            }
        }, delay, interval);
    }


}
