package com.interdev.game.screens.game;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.entities.demons.DemonsSystem;
import com.interdev.game.screens.game.hud.gui.Lives;
import com.interdev.game.screens.game.hud.stamina.StaminaOrbits;
import com.interdev.game.screens.game.other.LabeledReference;
import com.interdev.game.screens.game.other.LabeledReferenceList;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.GoodStar;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.screens.game.trophy.Trophy;
import com.interdev.game.sound.SoundSystem;
import com.interdev.game.tools.B2dForcePoint;

public class WorldContactListener implements ContactListener {
    public static final short BIT_CATEGORY_PLAYER = 0x0002;
    public static final short BIT_CATEGORY_GOODS = 0x0004;
    public static final short BIT_CATEGORY_DEMONS = 0x0008;
    public static final short BIT_CATEGORY_PLAYER_SHIELDS = 0x0010;
    public static final short BIT_CATEGORY_BULLETS = 0x0020;
    public static final short BIT_CATEGORY_PLATFORM = 0x0040;

    // 0x0040;
    // 0x0080;

    public enum ContactLabels {
        PLAYER,
        PLAYER_FEET,
        DEMON,
        GOODSTAR,
        SHIELD,
        BULLET,
        TROPHY,
        WALL
    }


    private Array<Demon> inContactWithDemonsArray = new Array<Demon>();
    private LabeledReferenceList userDatas = new LabeledReferenceList();


    public WorldContactListener() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                check();
            }
        }, 0, 0.25f);
    }

    private void check() {
        for (Demon demon : inContactWithDemonsArray) {
            if (demon.readyToHitAgain) {
                onDemonContact(demon);
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (GameScreen.gameStopped) return;
        userDatas.clear();

        for (Fixture fixture : new Fixture[]{contact.getFixtureA(), contact.getFixtureB()}) {
            if (fixture != null && fixture.getUserData() != null) {
                if (fixture.getUserData() instanceof LabeledReference)
                    userDatas.add((LabeledReference) fixture.getUserData());
            }
        }

        if (userDatas.contains(ContactLabels.GOODSTAR) && (
                (userDatas.contains(ContactLabels.PLAYER) || userDatas.contains(ContactLabels.PLAYER_FEET)))) {
            onGoodStarContact();
        }

        if (userDatas.contains(ContactLabels.DEMON) &&
                (userDatas.contains(ContactLabels.PLAYER) || userDatas.contains(ContactLabels.PLAYER_FEET))) {
            Demon demon = userDatas.getFirst(Demon.class);
            if (demon != null) {
                inContactWithDemonsArray.add(demon);
                onDemonContact(demon);
            }
        }

        if (userDatas.contains(ContactLabels.BULLET) && userDatas.contains(ContactLabels.DEMON)) {
            Demon demon = userDatas.getFirst(Demon.class);
            Bullet bullet = userDatas.getFirst(Bullet.class);
            if (demon != null && bullet != null) demon.takeDamageFrom(bullet);
        }

        if (userDatas.contains(ContactLabels.PLAYER_FEET)) {
            onFeetContact();
        }

        if ((userDatas.contains(ContactLabels.PLAYER) || userDatas.contains(ContactLabels.PLAYER_FEET))
                && userDatas.contains(ContactLabels.TROPHY)) {
            Trophy trophy = userDatas.getFirst(Trophy.class);
            if (trophy != null) {
                trophy.pickUp();
            }
        }

        if (userDatas.contains(ContactLabels.WALL) &&
                (userDatas.contains(ContactLabels.PLAYER) || userDatas.contains(ContactLabels.PLAYER_FEET))) {
            Player.inst.hitInWall = true;
            System.out.println("STOP ");
        }
    }


    private LabeledReferenceList endUserDatas = new LabeledReferenceList();

    @Override
    public void endContact(Contact contact) {
        if (GameScreen.gameStopped) return;
        endUserDatas.clear();

        for (Fixture fixture : new Fixture[]{contact.getFixtureA(), contact.getFixtureB()}) {
            if (fixture != null && fixture.getUserData() != null) {
                if (fixture.getUserData() instanceof LabeledReference)
                    endUserDatas.add((LabeledReference) fixture.getUserData());
            }
        }
        Demon demon = endUserDatas.getFirst(Demon.class);
        if (demon == null) return;
        inContactWithDemonsArray.removeValue(demon, true);
    }

    private void onFeetContact() {
        Player.inst.restoreJumps();
    }

    private void onGoodStarContact() {
        /*
        if (!GoodStar.inst.willBeRemoved) {
            if (goodStar.type == GoodStar.Type.SHIELD) {
                Player.hasShield = true;
                SoundSystem.inst.playSound(SoundSystem.Sounds.SHIELD_PICK_UP);
            } else if (goodStar.type == GoodStar.Type.SHARP_SHIELD) {
                Player.inst.activateSharpShield();
                SoundSystem.inst.playSound(SoundSystem.Sounds.SHIELD_PICK_UP);
            }

            SoundSystem.inst.playSound(SoundSystem.Sounds.GOOD_STAR);
            goodStar.resetPosition();

            Player.inst.restoreJumps();
            staminaOrbits.addStamina(goodStar.containsStamina);
        }
        */
    }


    private void onDemonContact(final Demon demon) {
        if (demon.isVisible()) {
            if (Player.hasSharpShield) {
                demon.die();
                SoundSystem.inst.playSound(SoundSystem.Sounds.SHIELD_BREAK);
                return;
            } else if (Player.hasShield) {
                //Player.hasShield = false;
                //B2dForcePoint.blast(DemonsSystem.inst.demonsList, Player.inst.getX(), Player.inst.getY(), 200, 100);
                //SoundSystem.inst.playSound(SoundSystem.Sounds.SHIELD_BREAK);
                return;
            }
            if (!demon.readyToHitAgain) {
                return;
            }
            if (StaminaOrbits.inst.getTotalStamina() > demon.getDamageVal()) {
                SoundSystem.inst.playSound(SoundSystem.Sounds.HIT_PLAYER);
                StaminaOrbits.inst.removeStamina(demon.getDamageVal());
                demon.punchFromPlayer(Player.PUNCH_IMPULSE / 3f);
                // B2dForcePoint.blast(DemonsSystem.inst.demonsList, Player.inst.getX(), Player.inst.getY(), 200, 100);
                return;
            }

            if (Player.hasResurrection) {
                SoundSystem.inst.playSound(SoundSystem.Sounds.RESURRECTED);
                EffectsSystem.inst.doEffect(Player.inst.getX(), Player.inst.getY(), EffectsSystem.Type.RESURRECTION);
                Player.hasResurrection = false;
                StaminaOrbits.inst.addStamina(99f);
                return;
            }

            // GG
            inContactWithDemonsArray.clear();
            GameScreen.inst.stopGame();
/*
            // RESET
            goodStar.resetPosition();
            demonFactory.resetDemonsPositions();
            if (demonFactory.getDemonsAmount() > 1) {
                gameScreen.addAfterWorldStepRunnable(new Runnable() {
                    @Override
                    public void run() {
                        demonFactory.deleteDemons();
                        directionSignFactory.createDirectionSign(demonFactory.createDemon(DemonFactory.DemonType.SIMPLE_RED));
                    }
                });
            }
            demonFactory.resetDemonsSpeed();
                    */

        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


}
