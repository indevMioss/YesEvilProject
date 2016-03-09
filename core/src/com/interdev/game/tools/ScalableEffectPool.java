package com.interdev.game.tools;

import com.badlogic.gdx.utils.Pool;

public class ScalableEffectPool extends Pool<ScalableEffectPool.PooledEffect> {
    private final ScalableEffect effect;

    public ScalableEffectPool(ScalableEffect effect, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.effect = effect;
    }

    protected PooledEffect newObject () {
        return new PooledEffect(effect);
    }

    public PooledEffect obtain () {
        PooledEffect effect = super.obtain();
        effect.reset();
        return effect;
    }

    public class PooledEffect extends ScalableEffect {
        PooledEffect (ScalableEffect effect) {
            super(effect);
        }

        @Override
        public void reset () {
            super.reset();
        }

        public void free () {
            ScalableEffectPool.this.free(this);
        }
    }
}
