package com.interdev.game.screens.game.entities.demons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.interdev.game.screens.game.GameScreen;
import com.interdev.game.screens.game.hud.directionsigns.DirectionSignFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DemonsSystem {

    public static DemonsSystem inst;

    public enum DemonType {
        SIMPLE_RED, ANGLER_GRAY, ANGLER_PURPLE, ANGLER_RED,
        BALL_GRAY, BALL_PURPLE, BALL_RED,
        FLY_BLUE, FLY_COLD, FLY_GREEN, FLY_RED,
        HORSE_BLUE, HORSE_GRAY, HORSE_PURPLE, HORSE_RED,
        CLOUD_GRAY, CLOUD_GREEN, CLOUD_RED,
        BOSS_VIT
    }

    public List<Demon> demonsList = new ArrayList<Demon>();

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

        final SkeletonRenderer skeletonRenderer = new SkeletonRenderer();

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

        switch (demonType) {

            case SIMPLE_RED:
                demonPool = redSimplePool;
                break;
            case ANGLER_GRAY:
                demonPool = anglerGrayPool;
                break;
            case ANGLER_RED:
                demonPool = anglerRedPool;
                break;
            case ANGLER_PURPLE:
                demonPool = anglerPurplePool;
                break;
            case BALL_GRAY:
                demonPool = ballGrayPool;
                break;
            case BALL_PURPLE:
                demonPool = ballPurplePool;
                break;
            case BALL_RED:
                demonPool = ballRedPool;
                break;
            case FLY_BLUE:
                demonPool = flyBluePool;
                break;
            case FLY_COLD:
                demonPool = flyColdPool;
                break;
            case FLY_GREEN:
                demonPool = flyGreenPool;
                break;
            case FLY_RED:
                demonPool = flyRedPool;
                break;
            case HORSE_BLUE:
                demonPool = horseBluePool;
                break;
            case HORSE_GRAY:
                demonPool = horseGrayPool;
                break;
            case HORSE_PURPLE:
                demonPool = horsePurplePool;
                break;
            case HORSE_RED:
                demonPool = horseRedPool;
                break;
            case CLOUD_GRAY:
                demonPool = cloudGrayPool;
                break;
            case CLOUD_GREEN:
                demonPool = cloudGreenPool;
                break;
            case CLOUD_RED:
                demonPool = cloudRedPool;
                break;

            //   case BOSS_VIT:
            //      System.out.println("BOSS_VIT BOSS_VIT BOSS_VIT");
            //     break;
            default:
                demonPool = redSimplePool;
        }

        Demon demon = demonPool.obtain();
        demonsList.add(demon);
        demon.go();
        return demon;
    }


    private float timeFactor = 1f;
    private float destTimeFactor = timeFactor;

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
        ArrayList<Demon> tempDemonsList = new ArrayList<Demon>(demonsList);
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
    }

    public void draw(SpriteBatch batch) {
        for (Demon demon : demonsList) {
            if (GameScreen.inFrustum(demon)) {
                demon.draw(batch, 1f);
            }
        }
    }

    public void removeDemon(Demon demon) {
        DirectionSignFactory.inst.removeByTarget(demon);
        demonsList.remove(demon);
    }


    public int getDemonsAmount() {
        return demonsList.size();
    }
}

