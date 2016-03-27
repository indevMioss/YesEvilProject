package com.interdev.game.screens.game.entities.demons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.entities.demons.bosses.SpineBoss;
import com.interdev.game.screens.game.entities.demons.bosses.Vis;
import com.interdev.game.screens.game.hud.directionsigns.DirectionSignFactory;
import com.interdev.game.tools.Utils;

import java.util.*;

public class DemonsSystem {

    public static DemonsSystem inst;
    private final SkeletonRenderer skeletonRenderer;

    public enum DemonType {
        SIMPLE_RED, ANGLER_GRAY, ANGLER_PURPLE, ANGLER_RED,
        BALL_GRAY, BALL_PURPLE, BALL_RED,
        FLY_BLUE, FLY_COLD, FLY_GREEN, FLY_RED,
        HORSE_BLUE, HORSE_GRAY, HORSE_PURPLE, HORSE_RED,
        CLOUD_GRAY, CLOUD_GREEN, CLOUD_RED
    }

    public enum BossType {
        VIS
    }

    public Array<Demon> demonsList = new Array<Demon>();
    public Array<SpineBoss> bossesList = new Array<SpineBoss>();

    private Pool<Monsters.SimpleRed> redSimplePool;

    private Pool<Monsters.AnglerGray> anglerGrayPool;

    private Pool<Monsters.AnglerPurple> anglerPurplePool;
    private Pool<Monsters.AnglerRed> anglerRedPool;

    private Pool<Monsters.BallGray> ballGrayPool;
    private Pool<Monsters.BallPurple> ballPurplePool;
    private Pool<Monsters.BallRed> ballRedPool;

    private Pool<Monsters.FlyBlue> flyBluePool;
    private Pool<Monsters.FlyCold> flyColdPool;
    private Pool<Monsters.FlyGreen> flyGreenPool;
    private Pool<Monsters.FlyRed> flyRedPool;

    private Pool<Monsters.HorseBlue> horseBluePool;
    private Pool<Monsters.HorseGray> horseGrayPool;
    private Pool<Monsters.HorsePurple> horsePurplePool;
    private Pool<Monsters.HorseRed> horseRedPool;

    private Pool<Monsters.CloudGray> cloudGrayPool;
    private Pool<Monsters.CloudGreen> cloudGreenPool;
    private Pool<Monsters.CloudRed> cloudRedPool;

    public DemonsSystem(final TextureAtlas atlas,
                        final TextureAtlas anglerAtlas,
                        final TextureAtlas ballAtlas,
                        final TextureAtlas flyAtlas,
                        final TextureAtlas horseAtlas,
                        final TextureAtlas cloudAtlas) {

        inst = this;

        skeletonRenderer = new SkeletonRenderer();

        redSimplePool = new Pool<Monsters.SimpleRed>() {
            @Override
            protected Monsters.SimpleRed newObject() {
                return new Monsters.SimpleRed(atlas, redSimplePool);
            }
        };

        anglerGrayPool = new Pool<Monsters.AnglerGray>() {
            @Override
            protected Monsters.AnglerGray newObject() {
                return new Monsters.AnglerGray(anglerAtlas, skeletonRenderer, anglerGrayPool);
            }
        };

        anglerRedPool = new Pool<Monsters.AnglerRed>() {
            @Override
            protected Monsters.AnglerRed newObject() {
                return new Monsters.AnglerRed(anglerAtlas, skeletonRenderer, anglerRedPool);
            }
        };

        anglerPurplePool = new Pool<Monsters.AnglerPurple>() {
            @Override
            protected Monsters.AnglerPurple newObject() {
                return new Monsters.AnglerPurple(anglerAtlas, skeletonRenderer, anglerPurplePool);
            }
        };

        ballGrayPool = new Pool<Monsters.BallGray>() {
            @Override
            protected Monsters.BallGray newObject() {
                return new Monsters.BallGray(ballAtlas, skeletonRenderer, ballGrayPool);
            }
        };

        ballPurplePool = new Pool<Monsters.BallPurple>() {
            @Override
            protected Monsters.BallPurple newObject() {
                return new Monsters.BallPurple(ballAtlas, skeletonRenderer, ballPurplePool);
            }
        };

        ballRedPool = new Pool<Monsters.BallRed>() {
            @Override
            protected Monsters.BallRed newObject() {
                return new Monsters.BallRed(ballAtlas, skeletonRenderer, ballRedPool);
            }
        };

        flyBluePool = new Pool<Monsters.FlyBlue>() {
            @Override
            protected Monsters.FlyBlue newObject() {
                return new Monsters.FlyBlue(flyAtlas, skeletonRenderer, flyBluePool);
            }
        };

        flyColdPool = new Pool<Monsters.FlyCold>() {
            @Override
            protected Monsters.FlyCold newObject() {
                return new Monsters.FlyCold(flyAtlas, skeletonRenderer, flyColdPool);
            }
        };

        flyGreenPool = new Pool<Monsters.FlyGreen>() {
            @Override
            protected Monsters.FlyGreen newObject() {
                return new Monsters.FlyGreen(flyAtlas, skeletonRenderer, flyGreenPool);
            }
        };

        flyRedPool = new Pool<Monsters.FlyRed>() {
            @Override
            protected Monsters.FlyRed newObject() {
                return new Monsters.FlyRed(flyAtlas, skeletonRenderer, flyRedPool);
            }
        };

        horseBluePool = new Pool<Monsters.HorseBlue>() {
            @Override
            protected Monsters.HorseBlue newObject() {
                return new Monsters.HorseBlue(horseAtlas, skeletonRenderer, horseBluePool);
            }
        };

        horseGrayPool = new Pool<Monsters.HorseGray>() {
            @Override
            protected Monsters.HorseGray newObject() {
                return new Monsters.HorseGray(horseAtlas, skeletonRenderer, horseGrayPool);
            }
        };

        horsePurplePool = new Pool<Monsters.HorsePurple>() {
            @Override
            protected Monsters.HorsePurple newObject() {
                return new Monsters.HorsePurple(horseAtlas, skeletonRenderer, horsePurplePool);
            }
        };

        horseRedPool = new Pool<Monsters.HorseRed>() {
            @Override
            protected Monsters.HorseRed newObject() {
                return new Monsters.HorseRed(horseAtlas, skeletonRenderer, horseRedPool);
            }
        };

        //////////

        cloudGrayPool = new Pool<Monsters.CloudGray>() {
            @Override
            protected Monsters.CloudGray newObject() {
                return new Monsters.CloudGray(cloudAtlas, skeletonRenderer, cloudGrayPool);
            }
        };

        cloudGreenPool = new Pool<Monsters.CloudGreen>() {
            @Override
            protected Monsters.CloudGreen newObject() {
                return new Monsters.CloudGreen(cloudAtlas, skeletonRenderer, cloudGreenPool);
            }
        };

        cloudRedPool = new Pool<Monsters.CloudRed>() {
            @Override
            protected Monsters.CloudRed newObject() {
                return new Monsters.CloudRed(cloudAtlas, skeletonRenderer, cloudRedPool);
            }
        };
    }


    public Demon createDemon(DemonType demonType, float size) {
        Demon demon = createDemon(demonType);
        demon.setScale(size);
        return demon;
    }

    public Demon createDemon(DemonType demonType) {
        Pool<? extends Demon> demonPool;
        demonPool = getAppropriatePool(demonType);
        Demon demon = demonPool.obtain();
        demonsList.add(demon);
        demon.go();
        return demon;
    }


    public SpineBoss createBoss(BossType bossType) {
        switch (bossType) {
            case VIS:
                Vis bossVIS = new Vis(skeletonRenderer);
                bossesList.add(bossVIS);
                return bossVIS;
        }
        return createBoss(BossType.VIS);
    }

    private Pool<? extends Demon> getAppropriatePool(DemonType type) {
        switch (type) {
            case SIMPLE_RED:
                return redSimplePool;
            case ANGLER_GRAY:
                return anglerGrayPool;
            case ANGLER_RED:
                return anglerRedPool;
            case ANGLER_PURPLE:
                return anglerPurplePool;
            case BALL_GRAY:
                return ballGrayPool;
            case BALL_PURPLE:
                return ballPurplePool;
            case BALL_RED:
                return ballRedPool;
            case FLY_BLUE:
                return flyBluePool;
            case FLY_COLD:
                return flyColdPool;
            case FLY_GREEN:
                return flyGreenPool;
            case FLY_RED:
                return flyRedPool;
            case HORSE_BLUE:
                return horseBluePool;
            case HORSE_GRAY:
                return horseGrayPool;
            case HORSE_PURPLE:
                return horsePurplePool;
            case HORSE_RED:
                return horseRedPool;
            case CLOUD_GRAY:
                return cloudGrayPool;
            case CLOUD_GREEN:
                return cloudGreenPool;
            case CLOUD_RED:
                return cloudRedPool;
            default:
                return redSimplePool;
        }
    }


    private float timeFactor = 1f;
    private float destTimeFactor = timeFactor;

    public void splashDamage(float x, float y, float maxRadius, float maxDamage) {
        for (Demon demon : demonsList) {
            float sqDist = Utils.sqDist(demon.getX(), demon.getY(), x, y);
            if (sqDist > maxRadius * maxRadius) continue;
            float rate = sqDist / (maxRadius * maxRadius);
            demon.applyDamage(maxDamage * (1 - rate));
        }
    }

    public void slowAllTheDemons(float toTimeFactor, float forTime) {
        destTimeFactor = toTimeFactor;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                destTimeFactor = 1f;
            }
        }, forTime);
    }

    public void blowAllTheDemons() {
        Array<Demon> tempDemonsList = new Array<Demon>(demonsList);
        for (Demon demon : tempDemonsList) {
            demon.die();
        }
        tempDemonsList.clear();
    }

    public void deleteDemons() {
        Iterator<Demon> iterator = demonsList.iterator();
        while (iterator.hasNext()) {
            Demon demon = iterator.next();
            DirectionSignFactory.inst.removeByTarget(demon);
            GameScreen.world.destroyBody(demon.getBody());
            iterator.remove();
        }
    }

    public void speedUpDemons() {
        for (Demon demon : demonsList) {
            demon.speedUp();
        }
    }


    public void resetDemonsSpeed() {
        for (Demon demon : demonsList) {
            demon.resetSpeed();
        }
    }

    public void update(float delta) {
        if (timeFactor > destTimeFactor) {
            timeFactor -= 0.01f;
            timeFactor = Math.max(timeFactor, destTimeFactor);
        } else if (timeFactor < destTimeFactor) {
            timeFactor += 0.01f;
            timeFactor = Math.min(timeFactor, destTimeFactor);
        }
        for (Demon demon : demonsList) {
            demon.velocityTimeSlowFactor = timeFactor;
            demon.act(delta * timeFactor);
        }
        for (SpineBoss boss : bossesList) {
            boss.velocityTimeSlowFactor = timeFactor;
            boss.act(delta * timeFactor);
        }
    }

    public void draw(SpriteBatch batch) {
        for (Demon demon : demonsList) {
            if (GameScreen.inFrustum(demon)) {
                demon.draw(batch, 1f);
            }
        }
        for (SpineBoss boss : bossesList) {
            if (GameScreen.inFrustum(boss)) {
                boss.draw(batch, 1f);
            }
        }
    }

    public void removeDemon(Demon demon) {
        DirectionSignFactory.inst.removeByTarget(demon);
        demonsList.removeValue(demon, true);
    }


    public int getDemonsAmount() {
        return demonsList.size;
    }
}

