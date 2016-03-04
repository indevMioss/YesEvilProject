package com.interdev.game.screens.game.levels;

import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.screens.game.hud.directionsigns.DirectionSignFactory;
import com.interdev.game.screens.game.hud.gui.InterlevelScene;
import com.interdev.game.tools.ActionListener;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LevelsSystem {
    public static int levelsPassed = 0;

    enum Type {BREAK, KILL, COLLECT, REWARD, BOSS, SURVIVE}

    public Type currentLevel = Type.BREAK;

    private Map<Type, Float> typeChancesMap = new HashMap<Type, Float>();
    private Random random = new Random();

    private ArrayList<DemonsSystem.DemonType> allowedTypes = new ArrayList<DemonsSystem.DemonType>();
    private int monstersToSpawn;

    public LevelsSystem() {

        typeChancesMap.put(Type.KILL, 0.25f); //25
        typeChancesMap.put(Type.COLLECT, 0.5f); //25
        typeChancesMap.put(Type.SURVIVE, 0.75f); //25
        typeChancesMap.put(Type.BOSS, 0.9f);//15
        typeChancesMap.put(Type.REWARD, 1f);//10

        updateAllowedDemons();
    }


    public void start() {
        nextLevel();
    }

    public void nextLevel() {
        levelsPassed++;
        InterlevelScene.inst.setLevelText();
        InterlevelScene.inst.setSceneEndListener(new ActionListener() {
            @Override
            public void actionPerformed() {
                updateAllowedDemons();
                launchKillLevel();
            }
        });
        InterlevelScene.inst.show();
        return;
     /*   currentLevel = roll();
        switch (currentLevel) {
            case KILL:
                launchKillLevel();
                break;
            case COLLECT:
                launchCollectLevel();
                break;
            case SURVIVE:
                launchSurviveLevel();gb
                break;
            case BOSS:
                launchBossLevel();
                break;
            case REWARD:
                launchRewardLevel();
                break;
        } */
    }

    private float spawnInterval;
    private float timePassed;

    public void update(float delta) {
        timePassed += delta;
        if (currentLevel == Type.KILL) {
            if (monstersToSpawn <= 0 && DemonsSystem.inst.getDemonsAmount() == 0) {
                onLevelDone();
                return;
            }

            if (timePassed >= spawnInterval && monstersToSpawn > 0) {
                timePassed = 0;
                spawn();
            }
        }
    }

    private void onLevelDone() {
        currentLevel = Type.BREAK;
        System.out.println("onLevelDone");
        final float pauseTime = 5f;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                nextLevel();
            }
        }, pauseTime);
    }

    private void spawn() {
        if (true) {//Utils.roll(0.95f)) {
            spawnOne();
        } else {
            spawnBigOne();
        }
        monstersToSpawn--;
    }


    private void spawnOne() {
        DemonsSystem.DemonType type = allowedTypes.get(Utils.randInt(0, allowedTypes.size()));
        System.out.println(type.toString());
        float relSize = Utils.getRand(0f, 1f);
        System.out.println("size " + relSize);
        Demon demon = DemonsSystem.inst.createDemon(type);
        demon.setRelScale(relSize);
        DirectionSignFactory.inst.createDirectionSign(demon);
    }

    private void spawnBigOne() {
        DemonsSystem.DemonType type = allowedTypes.get(Utils.randInt(0, allowedTypes.size()));
        float size = Utils.getRand(1f, 1.5f);
        Demon demon = DemonsSystem.inst.createDemon(type, size);
        demon.setAdditionalStr(size);
        DirectionSignFactory.inst.createDirectionSign(demon);
    }


    private void updateAllowedDemons() {
        allowedTypes.clear();

        /*
        allowedTypes.add(DemonsSystem.DemonType.BALL_GRAY);
        allowedTypes.add(DemonsSystem.DemonType.BALL_PURPLE);
        allowedTypes.add(DemonsSystem.DemonType.BALL_RED);
        allowedTypes.add(DemonsSystem.DemonType.FLY_BLUE);
        allowedTypes.add(DemonsSystem.DemonType.FLY_COLD);
        allowedTypes.add(DemonsSystem.DemonType.FLY_GREEN);
        allowedTypes.add(DemonsSystem.DemonType.FLY_RED);
        allowedTypes.add(DemonsSystem.DemonType.HORSE_BLUE);
        allowedTypes.add(DemonsSystem.DemonType.HORSE_GRAY);
        allowedTypes.add(DemonsSystem.DemonType.HORSE_PURPLE);
        allowedTypes.add(DemonsSystem.DemonType.HORSE_RED);
        allowedTypes.add(DemonsSystem.DemonType.CLOUD_GRAY);
        allowedTypes.add(DemonsSystem.DemonType.CLOUD_GREEN);
        allowedTypes.add(DemonsSystem.DemonType.CLOUD_RED);
*/

         allowedTypes.add(DemonsSystem.DemonType.BALL_GRAY);
        //allowedTypes.add(DemonsSystem.DemonType.ANGLER_PURPLE);
       // allowedTypes.add(DemonsSystem.DemonType.SIMPLE_RED);
        //allowedTypes.add(DemonsSystem.DemonType.ANGLER_RED);

        if (levelsPassed >= 2) {
        }
        if (levelsPassed >= 3) {
        }
        if (levelsPassed >= 4) {
        }

    }

    private void launchKillLevel() {
        spawnInterval = 2;// - (levelsPassed * 0.25f);
        System.out.println("spawnInterval" + spawnInterval);
        monstersToSpawn = 1 * (levelsPassed);
        System.out.println("monstersToSpawn" + monstersToSpawn);
        currentLevel = Type.KILL;

    }

    private void launchCollectLevel() {

    }

    private void launchSurviveLevel() {

    }

    private void launchBossLevel() {

    }

    private void launchRewardLevel() {

    }


    private Type roll() {
        float rand = random.nextFloat();
        for (Map.Entry<Type, Float> entry : typeChancesMap.entrySet()) {
            if (rand <= entry.getValue()) return entry.getKey();
        }
        return Type.KILL;
    }

}
