package com.lecviz.core;

import com.lecviz.utils.Easing;

import java.util.function.DoubleUnaryOperator;

/**
 * Base class for all animations.
 * An animation modifies one or more MObjects over a duration.
 */
public abstract class Animation {

    protected final MObject target;
    protected double duration;   // seconds
    protected DoubleUnaryOperator easing;
    protected double elapsed = 0;
    protected boolean finished = false;

    public Animation(MObject target, double duration) {
        this(target, duration, Easing.SMOOTH);
    }

    public Animation(MObject target, double duration, DoubleUnaryOperator easing) {
        this.target = target;
        this.duration = duration;
        this.easing = easing;
    }

    /**
     * Called once before the animation starts.
     */
    public void begin() {}

    /**
     * Called each frame. t is the eased progress (0..1).
     */
    public abstract void interpolate(double t);

    /**
     * Called once when the animation completes.
     */
    public void finish() {
        interpolate(1.0);
    }

    /**
     * Advance the animation by dt seconds.
     * Returns true if still running.
     */
    public boolean update(double dt) {
        if (finished) return false;
        elapsed += dt;
        double rawT = Math.min(elapsed / duration, 1.0);
        double easedT = easing.applyAsDouble(rawT);
        interpolate(easedT);

        if (rawT >= 1.0) {
            finished = true;
            finish();
            return false;
        }
        return true;
    }

    public MObject getTarget() { return target; }
    public double getDuration() { return duration; }
    public boolean isFinished() { return finished; }

    public Animation setEasing(DoubleUnaryOperator easing) {
        this.easing = easing;
        return this;
    }
}
