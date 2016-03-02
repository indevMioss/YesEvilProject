package com.interdev.game.screens.game.entities.demons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.interdev.game.screens.game.entities.demons.AnimatedDemon;
import com.interdev.game.screens.game.entities.demons.Demon;
import com.interdev.game.screens.game.entities.demons.FlyDemon;
import com.interdev.game.screens.game.entities.demons.SpineDemon;

public class Monsters {

    public static class Values {
        public float DEFAULT_LIVES = 100f;
        public float DEFAULT_DAMAGE = 0.5f;
        public float DEFAULT_CHASING_IMPULSE = 3f;
        public float DEFAULT_MAX_SPEED = 3f;
        public float DEFAULT_BODY_SHAPE_RADIUS = 60;
        public float DEFAULT_MASS = 10f;
        public float DEFAULT_SCALE = 1f;
    }

    public static class SimpleRed extends AnimatedDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
            values.DEFAULT_BODY_SHAPE_RADIUS = 40;
        }

        public SimpleRed(TextureAtlas atlasWithAnimation, Pool<? extends Demon> myPool) {
            super(values, atlasWithAnimation.findRegions("evil"), myPool);
        }
    }

    public static class AnglerGray extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
            values.DEFAULT_BODY_SHAPE_RADIUS = 80;
        }

        public AnglerGray(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/angler_gray.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class AnglerPurple extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public AnglerPurple(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/angler_purple.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class AnglerRed extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public AnglerRed(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/angler_red.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }


    public static class BallGray extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_MAX_SPEED = 4;
            values.DEFAULT_BODY_SHAPE_RADIUS = 100;
            values.DEFAULT_DAMAGE = 0.3f;
            values.DEFAULT_CHASING_IMPULSE = 10f;
        }

        public BallGray(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_ball.json", atlasWithSkeleton, skeletonRenderer, myPool);
            byDefaultFacingRight = true;
        }
    }


    public static class BallPurple extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public BallPurple(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_ball_purple.json", atlasWithSkeleton, skeletonRenderer, myPool);
            byDefaultFacingRight = false;
        }
    }

    public static class BallRed extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public BallRed(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_ball_red.json", atlasWithSkeleton, skeletonRenderer, myPool);
            byDefaultFacingRight = false;
        }
    }

    public static class CloudGray extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public CloudGray(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/cloud_demon.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class CloudGreen extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public CloudGreen(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/cloud_demon_green.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class CloudRed extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public CloudRed(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/cloud_demon_red.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }


    public static class FlyBlue extends FlyDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public FlyBlue(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_fly_blue.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class FlyCold extends FlyDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public FlyCold(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_fly_cold.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class FlyGreen extends FlyDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public FlyGreen(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_fly_green.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class FlyRed extends FlyDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public FlyRed(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_fly_red.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }


    public static class HorseBlue extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public HorseBlue(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_horse_blue.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class HorseGray extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public HorseGray(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_horse.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class HorsePurple extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public HorsePurple(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_horse_purple.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }

    public static class HorseRed extends SpineDemon {
        private static Values values = new Values();

        static {
            values.DEFAULT_SCALE = 0.75f;
        }

        public HorseRed(TextureAtlas atlasWithSkeleton, SkeletonRenderer skeletonRenderer, Pool<? extends Demon> myPool) {
            super(values, "spine/monster_horse_red.json", atlasWithSkeleton, skeletonRenderer, myPool);
        }
    }


}
