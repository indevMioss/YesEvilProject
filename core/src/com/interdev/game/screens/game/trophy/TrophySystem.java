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

    public OneFloatChangeListener redCryChangeListener;
    public OneFloatChangeListener blueCryChangeListener;
    public OneFloatChangeListener livesAddListener;

    public Map<Trophy.Type, TextureRegion> trophyTRMap = new HashMap<Trophy.Type, TextureRegion>();
    public Map<Trophy.Type, OffsetAnimation> trophyAnimMap = new HashMap<Trophy.Type, OffsetAnimation>();

    public Pool<AnimTrophy> animTrophyPool;
    public Pool<EffectTrophy> effectTrophyPool;

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

        OffsetAnimation liveAnimation = new OffsetAnimation(0.04f, animAtlas.findRegions("orb"),
                OffsetAnimation.PlayMode.LOOP);
        OffsetAnimation shieldAnimation = new OffsetAnimation(0.04f, animAtlas.findRegions("shield"),
                OffsetAnimation.PlayMode.LOOP);

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

        effectTrophyPool = new Pool<EffectTrophy>() {
            @Override
            protected EffectTrophy newObject() {
                EffectTrophy trophy = new EffectTrophy(TrophySystem.this, effectTrophyPool, world);
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

        inst = this;

        getCorrespondingTrophy(Trophy.Type.AMMO_RICOCHET_BLUE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_RICOCHET_BLUE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_GREEN_FLY).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_GREEN_SHARP).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_SCATTER_YELLOW).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_MINI_FIRE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_MINI_FIRE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_RICOCHET_BLUE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_RICOCHET_BLUE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_GREEN_FLY).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_GREEN_SHARP).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_SCATTER_YELLOW).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_MINI_FIRE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_MINI_FIRE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_RICOCHET_BLUE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_RICOCHET_BLUE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_GREEN_FLY).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_GREEN_SHARP).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_SCATTER_YELLOW).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_MINI_FIRE).go(15, 5);
        getCorrespondingTrophy(Trophy.Type.AMMO_MINI_FIRE).go(15, 5);
    }

    private Trophy getCorrespondingTrophy(Trophy.Type type) {
        switch (type) {
            case RED_CRY:
            case BLUE_CRY:
            case LIVE:
            case LIVES_BOTTLE:
            case POWER_BOTTLE:
            case RESURRECT_BOTTLE:
            case TIME_BOTTLE:
            case SHIELD_BONUS:
            case SHARP_SHIELD_BONUS:
                return animTrophyPool.obtain().setType(type);

            case MOVE_SPEED_BONUS:
                return moveSpeedBonus;
            case ATTACK_SPEED_BONUS:
                return attackSpeedBonus;
            case BOMB_BONUS:
                return bombBonus;
            case RESURRECT_BONUS:
                return resurrectBonus;
            case SLOW_ALL_BONUS:
                return slowAllBonus;

            case AMMO_GREEN_FLY:
            case AMMO_GREEN_SHARP:
            case AMMO_MINI_FIRE:
            case AMMO_RICOCHET_BLUE:
            case AMMO_SCATTER_YELLOW:
                return effectTrophyPool.obtain().setType(type);
        }
        return animTrophyPool.obtain().setType(Trophy.Type.RED_CRY);
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

            case AMMO_GREEN_FLY:
            case AMMO_GREEN_SHARP:
            case AMMO_MINI_FIRE:
            case AMMO_RICOCHET_BLUE:
            case AMMO_SCATTER_YELLOW:
                BulletSystem.inst.addAmmo(type);
                break;
        }
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
}











