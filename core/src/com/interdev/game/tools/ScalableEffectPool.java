package com.interdev.game.tools;

import com.badlogic.gdx.utils.Pool;

public class ScalableEffectPool extends Pool<ScalableEffectPool.PooledEffect> {
    public final ScalableEffect effectPrototype;

    public ScalableEffectPool(ScalableEffect prototype, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.effectPrototype = prototype;
    }

    protected PooledEffect newObject() {
        return new PooledEffect(effectPrototype);
    }

    public PooledEffect obtain() {
        PooledEffect newEffect = super.obtain();
        newEffect.reset();
        return newEffect;
    }

    public class PooledEffect extends ScalableEffect {
        PooledEffect(ScalableEffect effect) {
            super(effect);
        }

        @Override
        public void reset() {
            super.reset();
        }

        public void free() {
            ScalableEffectPool.this.free(this);
        }
    }
}
