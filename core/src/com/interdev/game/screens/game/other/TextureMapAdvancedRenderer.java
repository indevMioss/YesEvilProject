package com.interdev.game.screens.game.other;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.interdev.game.GameMain;

public class TextureMapAdvancedRenderer extends OrthogonalTiledMapRenderer {

    public TextureMapAdvancedRenderer(TiledMap map) {
        super(map);
    }

    public TextureMapAdvancedRenderer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public TextureMapAdvancedRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public TextureMapAdvancedRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    @Override
    public void renderObject(MapObject object) {
        if (object instanceof TextureMapObject) {
            TextureMapObject textureObject = (TextureMapObject) object;
            batch.draw(textureObject.getTextureRegion().getTexture(),
                    textureObject.getX() / GameMain.PPM, textureObject.getY() / GameMain.PPM,
                    textureObject.getOriginX() / GameMain.PPM, textureObject.getOriginY() / GameMain.PPM,
                    textureObject.getTextureRegion().getTexture().getWidth() / GameMain.PPM, textureObject.getTextureRegion().getTexture().getHeight() / GameMain.PPM,
                    textureObject.getScaleX(), textureObject.getScaleY(), textureObject.getRotation(),
                    textureObject.getTextureRegion().getRegionX(), textureObject.getTextureRegion().getRegionY(),
                    textureObject.getTextureRegion().getRegionWidth(), textureObject.getTextureRegion().getRegionHeight(),
                    false, false);
        }
    }

    public void renderLayer(MapLayer layer) {
        if (layer instanceof TiledMapTileLayer) {
            renderTileLayer((TiledMapTileLayer) layer);
        }
        if (layer instanceof TiledMapImageLayer) {
            renderImageLayer((TiledMapImageLayer) layer);
        } else {
            renderObjects(layer);
        }
    }
}