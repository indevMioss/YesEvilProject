package com.interdev.game.screens.game.attack.ultimate;


import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.interdev.game.screens.game.entities.Player;

public class Gun extends Group {

    private final LightningBullet lightningBullet;
    private Player player;

    enum WeaponType {
        LIGHTNING, BOMB, SHIELD
    }

    public Gun(Player player, World world) {

        this.player = player;
        lightningBullet = new LightningBullet(world, null); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        lightningBullet.setVisible(false);
        addActor(lightningBullet);
    }

    public void shoot(float angle, float powerScale, WeaponType weaponType) {
        switch (weaponType) {
            case LIGHTNING:
                lightningBullet.setEffectScale(powerScale);
                lightningBullet.launchAfterWorldStep(player.getX(), player.getY(), angle);

                break;
        }
    }


}



