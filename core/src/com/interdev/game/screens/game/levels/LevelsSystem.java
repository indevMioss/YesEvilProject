package com.interdev.game.screens.game.levels;

import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.screens.game.hud.directionsigns.DirectionSignFactory;
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

    public LevelsSystem() {

        typeChancesMap.put(Type.KILL, 0.25f); //25
        typeChancesMap.put(Type.COLLECT, 0.5f); //25
        typeChancesMap.put(Type.SURVIVE, 0.75f); //25
        typeChancesMap.put(Type.BOSS, 0.9f);//15
        typeChancesMap.put(Type.REWARD, 1f);//10

        updateAllowedDemons();
        launchKillLevel();
    }


    private float spawnInterval;
    private float timePassed;

    public void update(float delta) {
        timePassed += delta;
        if (currentLevel == Type.KILL) {
            if (timePassed >= spawnInterval) {
                spawnInterval -= intervalDecrease;
                timePassed = 0;
                spawn();
            }
        }
    }

    private void spawn() {
        if (Utils.roll(0.9f)) {
            spawnOne();
        } else {
            spawnBigOne();
        }
        monstersToSpawn--;
        if (monstersToSpawn <= 0) currentLevel = Type.BREAK;
    }



    private void spawnOne() {
        DemonsSystem.DemonType type = allowedTypes.get(Utils.randInt(0, allowedTypes.size()));
        float size = Utils.getRand(0.25f, 0.5f);
        Demon demon = DemonsSystem.inst.createDemon(type, size);
        DirectionSignFactory.inst.createDirectionSign(demon);
    }

    private void spawnBigOne() {
        DemonsSystem.DemonType type = allowedTypes.get(Utils.randInt(0, allowedTypes.size()));
        float size = Utils.getRand(0.75f, 1f);
        Demon demon = DemonsSystem.inst.createDemon(type, size);
        demon.setAdditionalStr(size);
        DirectionSignFactory.inst.createDirectionSign(demon);
    }


    public void nextLevel() {
        levelsPassed++;

        updateAllowedDemons();

        launchKillLevel();
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

        if (levelsPassed >= 2) {
            allowedTypes.add(DemonsSystem.DemonType.ANGLER_PURPLE);
        }
        if (levelsPassed >= 3) {
            allowedTypes.add(DemonsSystem.DemonType.SIMPLE_RED);
        }
        if (levelsPassed >= 4) {
            allowedTypes.add(DemonsSystem.DemonType.ANGLER_RED);
        }

    }


    private int monstersToSpawn;
    private float intervalDecrease = 0.04f;

    private void launchKillLevel() {
        spawnInterval = 5f;
        monstersToSpawn = 15;
        intervalDecrease = 0.3f;
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
