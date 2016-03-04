package com.interdev.game.screens.game.trophy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.attack.BulletSystem;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.screens.game.hud.stamina.StaminaOrbits;
import com.interdev.game.sound.SoundSystem;
import com.interdev.game.tools.OffsetAnimation;
import com.interdev.game.tools.OneFloatChangeListener;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrophySystem extends Group {

    public static TrophySystem inst;

    public Map<Trophy.Type, TextureRegion> trophyTRMap = new HashMap<Trophy.Type, TextureRegion>();
    public Map<Trophy.Type, OffsetAnimation> trophyAnimMap = new HashMap<Trophy.Type, OffsetAnimation>();

    public Pool<AnimTrophy> animTrophyPool;
    private SpineTrophy moveSpeedBonus;
    private SpineTrophy attackSpeedBonus;
    private SpineTrophy bombBonus;
    private SpineTrophy resurrectBonus;
    private SpineTrophy slowAllBonus;

    public TrophySystem(final World world, TextureAtlas trophiesAtlas, TextureAtlas animAtlas) {

        trophyTRMap.put(Trophy.Type.RED_CRY, trophiesAtlas.findRegion("trophy_red_cry"));
        trophyTRMap.put(Trophy.Type.BLUE_CRY, trophiesAtlas.findRegion("trophy_blue_cry"));
        trophyTRMap.put(Trophy.Type.LIVE, trophiesAtlas.findRegion("live"));
        trophyTRMap.put(Trophy.Type.LIVES_BOTTLE, trophiesAtlas.findRegion("trophy_lives_bottle"));
        trophyTRMap.put(Trophy.Type.POWER_BOTTLE, trophiesAtlas.findRegion("trophy_power_bottle"));
        trophyTRMap.put(Trophy.Type.RESURRECT_BOTTLE, trophiesAtlas.findRegion("trophy_resurrect_bottle"));
        trophyTRMap.put(Trophy.Type.TIME_BOTTLE, trophiesAtlas.findRegion("trophy_time_bottle"));
        trophyTRMap.put(Trophy.Type.SHARP_SHIELD_BONUS, trophiesAtlas.findRegion("shield_bg"));
        trophyTRMap.put(Trophy.Type.SHIELD_BONUS, trophiesAtlas.findRegion("shield_bg"));

        OffsetAnimation liveAnimation = new OffsetAnimation(0.04f, animAtlas.findRegions("orb"), OffsetAnimation.PlayMode.LOOP);
        OffsetAnimation shieldAnimation = new OffsetAnimation(0.04f, animAtlas.findRegions("shield"), OffsetAnimation.PlayMode.LOOP);

        trophyAnimMap.put(Trophy.Type.LIVE, liveAnimation);
        trophyAnimMap.put(Trophy.Type.SHIELD_BONUS, shieldAnimation);
        trophyAnimMap.put(Trophy.Type.SHARP_SHIELD_BONUS, shieldAnimation);


        animTrophyPool = new Pool<AnimTrophy>() {
            @Override
            protected AnimTrophy newObject() {
                AnimTrophy trophy = new AnimTrophy(TrophySystem.this, animTrophyPool, world);
                addActor(trophy);
                return trophy;
            }
        };


        SkeletonRenderer skeletonRenderer = new SkeletonRenderer();

        moveSpeedBonus = new SpineTrophy(this, world, Gdx.files.internal("spine/bonuses/move_speed_bonus.json"),
                trophiesAtlas, skeletonRenderer);
        attackSpeedBonus = new SpineTrophy(this, world, Gdx.files.internal("spine/bonuses/attack_speed_bonus.json"),
                trophiesAtlas, skeletonRenderer);
        bombBonus = new SpineTrophy(this, world, Gdx.files.internal("spine/bonuses/bomb_bonus.json"),
                trophiesAtlas, skeletonRenderer);
        resurrectBonus = new SpineTrophy(this, world, Gdx.files.internal("spine/bonuses/resurrect_bonus.json"),
                trophiesAtlas, skeletonRenderer);
        slowAllBonus = new SpineTrophy(this, world, Gdx.files.internal("spine/bonuses/infinity_bonus.json"),
                trophiesAtlas, skeletonRenderer);

        moveSpeedBonus.setType(Trophy.Type.MOVE_SPEED_BONUS);
        attackSpeedBonus.setType(Trophy.Type.ATTACK_SPEED_BONUS);
        bombBonus.setType(Trophy.Type.BOMB_BONUS);
        resurrectBonus.setType(Trophy.Type.RESURRECT_BONUS);
        slowAllBonus.setType(Trophy.Type.SLOW_ALL_BONUS);

        addActor(moveSpeedBonus);
        addActor(attackSpeedBonus);
        addActor(bombBonus);
        addActor(resurrectBonus);
        addActor(slowAllBonus);


       // spawn(11, 8, null);
        inst = this;

        getCorrespondingTrophy(Trophy.Type.SHIELD_BONUS).go(15,5);
    }

    public void setLivesAddListener(OneFloatChangeListener livesAddListener) {
        this.livesAddListener = livesAddListener;
    }

    public void setRedCryChangeListener(OneFloatChangeListener redCryChangeListener) {
        this.redCryChangeListener = redCryChangeListener;
    }

    public void setBlueCryChangeListener(OneFloatChangeListener blueCryChangeListener) {
        this.blueCryChangeListener = blueCryChangeListener;
    }

    public OneFloatChangeListener redCryChangeListener;
    public OneFloatChangeListener blueCryChangeListener;
    public OneFloatChangeListener livesAddListener;


    private Trophy getCorrespondingTrophy(Trophy.Type type) {
        switch (type) {
            case MOVE_SPEED_BONUS:
                System.out.println("MOVE_SPEED_BONUS");
                return moveSpeedBonus;
            case ATTACK_SPEED_BONUS:
                System.out.println("ATTACK_SPEED_BONUS");
                return attackSpeedBonus;
            case BOMB_BONUS:
                System.out.println("BOMB_BONUS");
                return bombBonus;
            case RESURRECT_BONUS:
                System.out.println("RESURRECT_BONUS");
                return resurrectBonus;
            case SLOW_ALL_BONUS:
                System.out.println("SLOW_ALL_BONUS");
                return slowAllBonus;
        }
        return animTrophyPool.obtain().setType(type);
    }


    private List<Trophy.Type> trophyBundle = new ArrayList<Trophy.Type>();

    public void spawnAfterStep(final float x, final float y, final Demon demon) {
        GameScreen.addAfterWorldStepRunnable(new Runnable() {
            @Override
            public void run() {
                spawn(x, y, demon);
            }
        });
    }

    private void spawn(float x, float y, Demon demon) {
        trophyBundle.clear();
        for (Trophy.Type type : Trophy.Type.values()) {
            if (Utils.roll(0.01f)) trophyBundle.add(type);
        }

       // addToTrophyBundle(Trophy.Type.RED_CRY, Utils.randInt(0, 2));
       // addToTrophyBundle(Trophy.Type.BLUE_CRY, Utils.randInt(0, 2));
       // addToTrophyBundle(Trophy.Type.RESURRECT_BONUS, Utils.randInt(1, 2));

        /*
        if (demon instanceof AnglerGray) {
            if (Utils.roll(0.25f)) addToTrophyBundle(Trophy.Type.RED_CRY, Utils.randInt(0, 5));
            if (Utils.roll(0.1f)) addToTrophyBundle(Trophy.Type.RED_CRY, Utils.randInt(3, 5));
            if (Utils.roll(0.05f)) addToTrophyBundle(Trophy.Type.BLUE_CRY, Utils.randInt(1, 4));
            if (Utils.roll(0.1f)) addToTrophyBundle(Trophy.Type.LIVE, Utils.randInt(1, 3));
        } else if (demon instanceof AnglerRed) {
            if (Utils.roll(0.25f)) addToTrophyBundle(Trophy.Type.RED_CRY, Utils.randInt(0, 5));
            if (Utils.roll(0.1f)) addToTrophyBundle(Trophy.Type.RED_CRY, Utils.randInt(3, 5));
            if (Utils.roll(0.05f)) addToTrophyBundle(Trophy.Type.BLUE_CRY, Utils.randInt(1, 4));
            if (Utils.roll(0.1f)) addToTrophyBundle(Trophy.Type.LIVE, Utils.randInt(1, 3));

            if (Utils.roll(0.02f)) addToTrophyBundle(Trophy.Type.LIVES_BOTTLE, 1);
            if (Utils.roll(0.02f)) addToTrophyBundle(Trophy.Type.POWER_BOTTLE, 1);
        } else if (demon instanceof AnglerPurple) {
            if (Utils.roll(0.25f)) addToTrophyBundle(Trophy.Type.RED_CRY, Utils.randInt(0, 5));
            if (Utils.roll(0.1f)) addToTrophyBundle(Trophy.Type.RED_CRY, Utils.randInt(3, 5));
            if (Utils.roll(0.05f)) addToTrophyBundle(Trophy.Type.BLUE_CRY, Utils.randInt(1, 4));
            if (Utils.roll(0.1f)) addToTrophyBundle(Trophy.Type.LIVE, Utils.randInt(1, 3));

            if (Utils.roll(0.005f)) addToTrophyBundle(Trophy.Type.RESURRECT_BOTTLE, 1);
            if (Utils.roll(0.005f)) addToTrophyBundle(Trophy.Type.TIME_BOTTLE, 1);
        }
*/

        for (Trophy.Type trophyType : trophyBundle) {
            getCorrespondingTrophy(trophyType).go(x, y);
        }


    }

    private void addToTrophyBundle(Trophy.Type type, int amount) {
        for (int i = 0; i < amount; i++) {
            trophyBundle.add(type);
        }
    }


    public void onPickedUp(final Trophy.Type type) {
        switch (type) {
            case RED_CRY:
                SoundSystem.inst.playSound(SoundSystem.Sounds.RED_CRY_PICK);
                GameMain.redCrystals++;
                if (redCryChangeListener != null) redCryChangeListener.onValueChange(1);
                break;
            case BLUE_CRY:
                SoundSystem.inst.playSound(SoundSystem.Sounds.BLUE_CRY_PICK);
                GameMain.blueCrystals++;
                if (blueCryChangeListener != null) blueCryChangeListener.onValueChange(1);
                break;
            case LIVE:

                StaminaOrbits.inst.addStamina(1f);
                break;
            case LIVES_BOTTLE:

                GameMain.healthBottles++;
                break;
            case POWER_BOTTLE:

                GameMain.powerBottles++;
                break;
            case RESURRECT_BOTTLE:

                GameMain.resurrectionBottles++;
                break;
            case TIME_BOTTLE:

                GameMain.timeBottles++;
                break;
            case ATTACK_SPEED_BONUS:
                SoundSystem.inst.playSound(SoundSystem.Sounds.ATTACK_SPEED_BONUS);
                BulletSystem.timeFactor = 0.5f;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        BulletSystem.timeFactor = 1f;
                    }
                }, 7f);
                break;
            case RESURRECT_BONUS:
                SoundSystem.inst.playSound(SoundSystem.Sounds.RESURRECT_BONUS);
                Player.hasResurrection = true;
                break;
            case SLOW_ALL_BONUS:
                SoundSystem.inst.playSound(SoundSystem.Sounds.SLOW_ALL_BONUS);
                DemonsSystem.inst.slowAllTheDemons(0.25f, 10f);
                break;
            case MOVE_SPEED_BONUS:
                SoundSystem.inst.playSound(SoundSystem.Sounds.SPEED_BONUS);
                Player.hasMoveSpeedBonus = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Player.hasMoveSpeedBonus = false;
                    }
                }, 15f);
                break;
            case BOMB_BONUS:
                SoundSystem.inst.playSound(SoundSystem.Sounds.BOMB);
                GameScreen.inst.doEffect(GameScreen.Effect.FLASH);
                DemonsSystem.inst.blowAllTheDemons();
                break;
            case SHIELD_BONUS:
                SoundSystem.inst.playSound(SoundSystem.Sounds.SHIELD_PICK_UP);
                Player.inst.activateShield();
                break;
            case SHARP_SHIELD_BONUS:
                SoundSystem.inst.playSound(SoundSystem.Sounds.SHARP_SHIELD_PICK_UP);
                Player.inst.activateSharpShield();
        }
    }
}











