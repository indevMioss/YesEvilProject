package com.interdev.game.screens.game.hud.directionsigns;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.GoodStar;
import com.interdev.game.screens.game.entities.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DirectionSignFactory {

    public static DirectionSignFactory inst;
    private final Player player;
    private Stage hudStage;

    private List<DirectionSign> evilDirectionSigns = new ArrayList<DirectionSign>();

    public DirectionSignFactory(Player player, Stage hudStage) {
        inst = this;
        this.player = player;
        this.hudStage = hudStage;
    }

    public float getClosestDistPercent() { //for slow mo
        float min = 1f;
        for (DirectionSign directionSign : evilDirectionSigns) {
            min = Math.min(min, directionSign.targetDistPercent);
        }
        return min;
    }

    public DirectionSign createDirectionSign(Demon target) {
        Texture texture = new Texture("evil_direction_sign.png");
        DirectionSign directionSign = new DirectionSign(texture, player, target);
        evilDirectionSigns.add(directionSign);
        hudStage.addActor(directionSign);
        return directionSign;
    }

    public DirectionSign createDirectionSign(GoodStar target) {
        Texture texture = new Texture("good_direction_sign.png");
        DirectionSign directionSign = new DirectionSign(texture, player, target);
        hudStage.addActor(directionSign);
        return directionSign;
    }


    public void removeByTarget(Demon target) {
        Iterator<DirectionSign> iterator = evilDirectionSigns.iterator();
        while (iterator.hasNext()) {
            DirectionSign directionSign = iterator.next();
            if (directionSign.target.equals(target)) {
                directionSign.remove();
                iterator.remove();
            }
        }
    }
}
