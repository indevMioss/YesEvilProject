package com.interdev.game.screens.game.hud.stamina;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.entities.Player;
import com.interdev.game.tools.ActorParticleEffect;
import com.interdev.game.tools.ScalableParticleEffect;

import java.util.ArrayList;
import java.util.ListIterator;

public class StaminaOrbits extends Group {
    private static final int MAX_SLOTS_IN_ORBIT = 4;
    private static final float OFFSET_FROM_ORBIT = 0;
    private static final float ROTATION_SPEED = -25; //degrees/sec

    public static final Vector2 SCREEN_POS = new Vector2(0.5f, 0.51f);
    public static StaminaOrbits inst;

    private int orbsNow = 0;
    private float diameter;

    private ArrayList<ArrayList<Vector2>> listOfOrbits = new ArrayList<ArrayList<Vector2>>();
    private ArrayList<Orb> allOrbsList = new ArrayList<Orb>();

    public ActorParticleEffect lifeDrainEffect;


    public StaminaOrbits(float minDiameter, Array<? extends TextureRegion> orbRegions) {
        inst = this;
        this.diameter = minDiameter;
        for (int slots = 1; slots <= MAX_SLOTS_IN_ORBIT; slots++) {
            listOfOrbits.add(calcGemSlotsPoints(slots));
            Orb orb = new Orb(0, 0, orbRegions);
            addActor(orb);
            allOrbsList.add(orb);
        }
        lifeDrainEffect = new ActorParticleEffect("effects/life_drain3.p", "effects");
        addActor(lifeDrainEffect);
        resize();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(Player.inst.getX(), Player.inst.getY());
        float rotationDelta = ROTATION_SPEED * delta;
        setRotation(getRotation() + rotationDelta);

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public ArrayList<Vector2> calcGemSlotsPoints(float slotsNum) {
        ArrayList<Vector2> gemSlotsPointsList = new ArrayList<Vector2>();
        float deltaAngle = 360 / slotsNum;
        for (int i = 0; i < slotsNum; i++) {
            Vector2 point = new Vector2();
            point.x = (float) (-diameter / 2 + (diameter * (0.5f - OFFSET_FROM_ORBIT))
                    * Math.cos(Math.toRadians(deltaAngle * i))) + diameter / 2;
            point.y = (float) (-diameter / 2 + (diameter * (0.5f - OFFSET_FROM_ORBIT))
                    * Math.sin(Math.toRadians(deltaAngle * i))) + diameter / 2;
            gemSlotsPointsList.add(point);
        }
        return gemSlotsPointsList;
    }

    private void changeVisibleOrbsAmount(int delta) {
        if (orbsNow + delta < 0) return;
        if (orbsNow + delta > MAX_SLOTS_IN_ORBIT) return;
        orbsNow += delta;

        for (Orb orb : allOrbsList) {
            orb.setVisible(false);
        }
        for (int i = 0; i < orbsNow; i++) {
            Orb orb = allOrbsList.get(i);
            orb.setPosition(listOfOrbits.get(orbsNow - 1).get(i).x, listOfOrbits.get(orbsNow - 1).get(i).y);
            orb.setVisible(true);
        }
    }


    private Orb getFirstUnfilledOrb() {
        for (Orb orb : allOrbsList) if (orb.fullnessPercent < 1) return orb;
        return null;
    }

    private Orb getLastNotEmptyOrb() {
        ListIterator<Orb> iterator = allOrbsList.listIterator(allOrbsList.size());
        while (iterator.hasPrevious()) {
            Orb orb = iterator.previous();
            if (orb.fullnessPercent > 0) {
                return orb;
            }
        }
        return null;
    }

    public void addStamina(float stamina) {
        //  System.out.println(stamina);
        Orb orb = getFirstUnfilledOrb();
        if (orb == null) return;
        if (!orb.isVisible()) changeVisibleOrbsAmount(+1); //for the first one
        float rest = orb.fill(stamina);
        if (rest != 0) {
            changeVisibleOrbsAmount(+1);
            addStamina(rest);
        }
    }

    private Orb lastInteractedOrb;

    public void removeStamina(float negativeStamina) {
        if (negativeStamina > 0) negativeStamina *= -1;
        lastInteractedOrb = getLastNotEmptyOrb();
        if (lastInteractedOrb == null) return;
        lifeDrainEffect.setPosition(lastInteractedOrb.getX(), lastInteractedOrb.getY());
        lifeDrainEffect.effect.reset();
        lifeDrainEffect.effect.start();
        float rest = lastInteractedOrb.fill(negativeStamina);
        if (rest != 0) {
            changeVisibleOrbsAmount(-1);
            removeStamina(rest);
        }
    }

    public float getTotalStamina() {
        float totalAmount = 0;
        for (Orb orb : allOrbsList) {
            totalAmount += orb.fullnessPercent;
        }
        return totalAmount;
    }

    public void resize() {
        setScale(1 / GameMain.PPM);
        setPosition(Player.inst.getX(), Player.inst.getY());
    }

}

























