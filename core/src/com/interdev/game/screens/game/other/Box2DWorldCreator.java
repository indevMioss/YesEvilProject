package com.interdev.game.screens.game.other;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.interdev.game.GameMain;
import com.interdev.game.screens.game.WorldContactListener;

public class Box2DWorldCreator {
    public static Box2DWorldCreator inst;


    public Box2DWorldCreator(World world, MapLayer tiledMapLayer) {
        inst = this;

        BodyDef bodyDef = new BodyDef();
        PolygonShape polygonShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;
        for (MapObject object : tiledMapLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            float x = rect.getX() + rect.getWidth() / 2;
            float y = rect.getY() + rect.getHeight() / 2;
            bodyDef.position.set(x / GameMain.PPM, y / GameMain.PPM);
            body = world.createBody(bodyDef);
            float width = rect.getWidth() / 2;
            float height = rect.getHeight() / 2;
            polygonShape.setAsBox(width / GameMain.PPM, height / GameMain.PPM);
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = WorldContactListener.BIT_CATEGORY_PLATFORM;
            body.createFixture(fixtureDef).setUserData(new LabeledReference(WorldContactListener.ContactLabels.WALL, body));
        }
    }
}
