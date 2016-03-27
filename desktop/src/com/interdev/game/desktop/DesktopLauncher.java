package com.interdev.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.interdev.game.GameMain;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.height = 720;
        config.width = 1280;

  //     config.height = 1080;
  //     config.width = 1920;

    //    config.width = 960;
    //    config.height = 560;

    //    config.width = 480;
    //    config.height = 240;

        config.width = 1600;
        config.height = 900;

//           config.x = 2561;
//           config.y = 0;

        new LwjglApplication(new GameMain(), config);
    }
}
