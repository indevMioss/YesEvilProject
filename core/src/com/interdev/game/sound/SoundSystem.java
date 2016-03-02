package com.interdev.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.interdev.game.screens.game.attack.Bullet;
import com.interdev.game.screens.game.attack.bullets.*;
import com.interdev.game.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class SoundSystem implements Disposable {
    public static SoundSystem inst;

    private boolean muteSounds = false;

    private List<Sound> soundList = new ArrayList<Sound>();

    private Sound jumpSound;
    private Sound goodStarSound;
    private Sound evilStarSound;
    private Sound deadSound;

    private Sound kickSound;
    private Sound breakingGlassSound;
    private Sound mysticSound;

    private Sound bombSound1;
    private Sound bombSound2;
    private Sound bombSound3;

    private Sound lightSound;
    private Sound bubbleSound;
    private Sound windSound;
    private Sound attackSpeedSound;
    private Sound slowAllSound;
    private Sound sharpShieldSound;


    private final Sound[] shootSpiritSounds;
    private final Sound[] blowSpiritSounds;
    private final Sound[] hitPlayerSounds;

    private final Sound[] blueCryPickSounds;
    private final Sound[] redCryPickSounds;


    public enum Sounds {
        JUMP,
        GOOD_STAR,
        EVIL_CRAWL,
        DEAD,
        SHIELD_BREAK,
        HIT_PLAYER,

        RED_CRY_PICK,
        BLUE_CRY_PICK,

        SHOOT_SPIRIT,
        BLOW_SPIRIT,

        SHIELD_PICK_UP,
        SHARP_SHIELD_PICK_UP,
        BOMB,
        RESURRECT_BONUS,
        SPEED_BONUS,
        ATTACK_SPEED_BONUS,
        SLOW_ALL_BONUS,

        RESURRECTED


    }

    public SoundSystem() {
        inst = this;

        jumpSound = newSound("sounds/jump.mp3");
        goodStarSound = newSound("sounds/good_star.mp3");
        evilStarSound = newSound("sounds/evil_star.mp3");
        deadSound = newSound("sounds/dead.mp3");

        kickSound = newSound("sounds/kick.mp3");
        breakingGlassSound = newSound("sounds/glass_break.mp3");
        mysticSound = newSound("sounds/mystic.mp3");

        shootSpiritSounds = new Sound[]{
                newSound("sounds/temp/ss1.mp3"),
                newSound("sounds/temp/ss2.mp3"),
                newSound("sounds/temp/ss3.mp3"),
                newSound("sounds/temp/ss4.mp3")
        };

        blowSpiritSounds = new Sound[]{
                newSound("sounds/temp/sb1.mp3"),
                newSound("sounds/temp/sb2.mp3"),
                newSound("sounds/temp/sb3.mp3"),
        };

        hitPlayerSounds = new Sound[]{
                newSound("sounds/hit1.mp3"),
                newSound("sounds/hit2.mp3"),
                newSound("sounds/hit3.mp3"),
        };

        redCryPickSounds = new Sound[]{
                newSound("sounds/red_cry_pick_1.mp3"),
                newSound("sounds/red_cry_pick_2.mp3"),
                newSound("sounds/red_cry_pick_3.mp3"),
        };

        blueCryPickSounds= new Sound[]{
                newSound("sounds/blue_cry_pick_1.mp3"),
                newSound("sounds/blue_cry_pick_2.mp3"),
                newSound("sounds/blue_cry_pick_3.mp3"),
        };

        bombSound1 = newSound("sounds/boom-kick.mp3");
        bombSound2 = newSound("sounds/boom-reverb.mp3");
        bombSound3 = newSound("sounds/bang-explosion.mp3");

        lightSound = newSound("sounds/light.mp3");
        windSound = newSound("sounds/wind.mp3");
        bubbleSound = newSound("sounds/bubble.mp3");

        attackSpeedSound = newSound("sounds/deconstruction.mp3");
        slowAllSound = newSound("sounds/space-blaster.mp3");
        sharpShieldSound = newSound("sounds/dub_recharge.mp3");


    }

    private Sound newSound(String path) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        soundList.add(sound);
        return sound;
    }


    public void playSound(Sounds sound) {
        playSound(sound, -1);
    }

    public void playSound(Sounds sound, float volume) { // -1 : default
        if (volume < 0) volume = 1f;

        if (!muteSounds) {
            switch (sound) {
                case JUMP:
                    jumpSound.play(0.3f * volume);
                    break;
                case GOOD_STAR:
                    goodStarSound.play(0.5f * volume);
                    break;
                case EVIL_CRAWL:
                    evilStarSound.play(0.4f * volume);
                    break;
                case DEAD:
                    deadSound.play(0.5f * volume);
                    break;
                case SHIELD_BREAK:
                    kickSound.play(0.4f * volume);
                    breakingGlassSound.play(0.5f * volume);
                    break;
                case SHIELD_PICK_UP:
                    bubbleSound.play(volume);
                    break;
                case HIT_PLAYER:
                    hitPlayerSounds[Utils.randInt(0, hitPlayerSounds.length)].play(1f * volume);
                    break;
                case RED_CRY_PICK:
                    redCryPickSounds[Utils.randInt(0, redCryPickSounds.length)].play(1f * volume);
                    break;
                case BLUE_CRY_PICK:
                    blueCryPickSounds[Utils.randInt(0, blueCryPickSounds.length)].play(1f * volume);
                    break;
                case SHOOT_SPIRIT:
                    shootSpiritSounds[Utils.randInt(0, shootSpiritSounds.length)].play(1.5f * volume);
                    break;
                case BLOW_SPIRIT:
                    blowSpiritSounds[Utils.randInt(0, blowSpiritSounds.length)].play(1 * volume);
                    break;
                case SHARP_SHIELD_PICK_UP:
                    sharpShieldSound.play(volume);
                    break;
                case BOMB:
                    bombSound1.play(volume);
                    bombSound2.play(volume);
                    bombSound3.play(volume);
                    break;
                case RESURRECT_BONUS:
                    lightSound.play(volume);
                    break;
                case ATTACK_SPEED_BONUS:
                    attackSpeedSound.play(volume);
                    break;
                case SPEED_BONUS:
                    windSound.play(volume);
                    break;
                case SLOW_ALL_BONUS:
                    slowAllSound.play(volume);
                    break;
                case RESURRECTED:
                    mysticSound.play();
                    break;
            }
        }
    }

    public void playBulletBlow(Bullet bullet) {
        playBulletBlow(bullet, 1f);
    }

    public void playBulletBlow(Bullet bullet, float volume) {
        if (bullet instanceof Spirit) blowSpiritSounds[Utils.randInt(0, blowSpiritSounds.length)].play(volume);
        if (bullet instanceof GreenFly) blowSpiritSounds[Utils.randInt(0, blowSpiritSounds.length)].play(volume);
        if (bullet instanceof GreenSharp) blowSpiritSounds[Utils.randInt(0, blowSpiritSounds.length)].play(volume);
        if (bullet instanceof MiniFire) blowSpiritSounds[Utils.randInt(0, blowSpiritSounds.length)].play(volume);
        if (bullet instanceof RicochetBullet) blowSpiritSounds[Utils.randInt(0, blowSpiritSounds.length)].play(volume);
        if (bullet instanceof ScatterYellow) blowSpiritSounds[Utils.randInt(0, blowSpiritSounds.length)].play(volume);
    }


    public void playBulletShoot(Bullet bullet) {
        playBulletShoot(bullet, 1f);
    }

    public void playBulletShoot(Bullet bullet, float volume) {
        if (bullet instanceof Spirit) shootSpiritSounds[Utils.randInt(0, shootSpiritSounds.length)].play(volume);
        if (bullet instanceof GreenFly) shootSpiritSounds[Utils.randInt(0, shootSpiritSounds.length)].play(volume);
        if (bullet instanceof GreenSharp) shootSpiritSounds[Utils.randInt(0, shootSpiritSounds.length)].play(volume);
        if (bullet instanceof MiniFire) shootSpiritSounds[Utils.randInt(0, shootSpiritSounds.length)].play(volume);
        if (bullet instanceof RicochetBullet) shootSpiritSounds[Utils.randInt(0, shootSpiritSounds.length)].play(volume);
        if (bullet instanceof ScatterYellow) shootSpiritSounds[Utils.randInt(0, shootSpiritSounds.length)].play(volume);
    }

    public void setMuteSounds(boolean mute) {
        muteSounds = mute;
    }


    @Override
    public void dispose() {
        for (Sound sound : soundList) {
            sound.dispose();
        }
        soundList.clear();
    }
}
