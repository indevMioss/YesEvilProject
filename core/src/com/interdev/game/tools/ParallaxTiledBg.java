package com.interdev.game.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

public class ParallaxTiledBg implements Disposable {

    private List<ParallaxLayer> parallaxLayers = new ArrayList<ParallaxLayer>();
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private float viewportWidth;
    private float viewportHeight;

    public ParallaxTiledBg(OrthogonalTiledMapRenderer tiledMapRenderer, float viewportWidth, float viewportHeight) {
        this.tiledMapRenderer = tiledMapRenderer;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

    }

    public void addParallaxLayer(TiledMapTileLayer tiledMapTileLayer, float shiftFactor, float zoom) {
        parallaxLayers.add(new ParallaxLayer(tiledMapTileLayer, shiftFactor, zoom));
    }

    private float mainCamX, mainCamY;

    public void update(OrthographicCamera mainCamera) {
        float mainCamLastX = mainCamX;
        float mainCamLastY = mainCamY;

        mainCamX = mainCamera.position.x;
        mainCamY = mainCamera.position.y;

        if (mainCamLastX == 0 || mainCamLastY == 0) return;

        for (ParallaxLayer layer : parallaxLayers) {
            layer.camera.position.x += (mainCamX - mainCamLastX) * layer.shiftFactor;
            layer.camera.position.y += (mainCamY - mainCamLastY) * layer.shiftFactor;
            layer.camera.update();
        }
    }

    public void draw() {
        tiledMapRenderer.getBatch().begin();
        for (ParallaxLayer layer : parallaxLayers) {
            tiledMapRenderer.setView(layer.camera);
            tiledMapRenderer.renderTileLayer(layer.tiledMapTileLayer);
        }
        tiledMapRenderer.getBatch().end();
    }


    public class ParallaxLayer {
        public TiledMapTileLayer tiledMapTileLayer;
        public float shiftFactor;
        public OrthographicCamera camera;

        public ParallaxLayer(TiledMapTileLayer tiledMapTileLayer, float shiftFactor, float zoom) {
            this.tiledMapTileLayer = tiledMapTileLayer;
            this.shiftFactor = shiftFactor;
            camera = new OrthographicCamera();
            camera.zoom = zoom;
            updateCamera();
        }

        public void updateCamera() {
            camera.setToOrtho(false, viewportWidth, viewportHeight);
        }
    }

    @Override
    public void dispose() {
        tiledMapRenderer.dispose();
    }
}
