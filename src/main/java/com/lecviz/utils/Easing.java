package com.lecviz.utils;

import java.util.function.DoubleUnaryOperator;

/**
 * Standard easing functions for animations.
 * t ranges from 0..1, output ranges 0..1.
 */
public final class Easing {

    public static final DoubleUnaryOperator LINEAR      = t -> t;
    public static final DoubleUnaryOperator EASE_IN     = t -> t * t * t;
    public static final DoubleUnaryOperator EASE_OUT    = t -> 1 - Math.pow(1 - t, 3);
    public static final DoubleUnaryOperator EASE_IN_OUT = t ->
            t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    public static final DoubleUnaryOperator SMOOTH      = t ->
            3 * t * t - 2 * t * t * t;  // smoothstep
    public static final DoubleUnaryOperator ELASTIC     = t -> {
        if (t == 0 || t == 1) return t;
        return Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * (2 * Math.PI / 3)) + 1;
    };
    public static final DoubleUnaryOperator BOUNCE      = t -> {
        double n1 = 7.5625, d1 = 2.75;
        if (t < 1 / d1) return n1 * t * t;
        else if (t < 2 / d1) return n1 * (t -= 1.5 / d1) * t + 0.75;
        else if (t < 2.5 / d1) return n1 * (t -= 2.25 / d1) * t + 0.9375;
        else return n1 * (t -= 2.625 / d1) * t + 0.984375;
    };

    // "there and back" — useful for emphasis pulses
    public static DoubleUnaryOperator thereAndBack(DoubleUnaryOperator base) {
        return t -> t < 0.5
                ? base.applyAsDouble(2 * t)
                : base.applyAsDouble(2 * (1 - t));
    }

    private Easing() {}
}
