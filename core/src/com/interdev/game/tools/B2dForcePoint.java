package com.interdev.game.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.interdev.game.screens.game.entities.demons.Demon;

import java.util.List;

public class B2dForcePoint {

    public static void blast(Array<? extends Demon> list, float pointX, float pointY, float maxRadius, float blastPower) {
        float dX, dY, distance;
        for (Demon demon : list) {
            dX = pointX - demon.getX();
            dY = pointY - demon.getY();
            distance = (float) Math.pow(dX * dX + dY * dY, 0.5f);
            if (distance > maxRadius) continue;
            applyBlastImpulse(demon.getBody(),
                    new Vector2(pointX, pointY),
                    new Vector2(demon.getX(), demon.getY()),
                    blastPower
            );
        }
    }

    public static void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        Vector2 blastDir = new Vector2(applyPoint.x - blastCenter.x, applyPoint.y - blastCenter.y);
        float distance = blastDir.len2();
        if (distance == 0) return;
        float invDistance = 1 / distance;
        float impulseMag = blastPower * invDistance;
        body.applyLinearImpulse(impulseMag * blastDir.x, impulseMag * blastDir.y, applyPoint.x, applyPoint.y, true);
    }
}
