package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.ArrowMob;
import com.lecviz.utils.Easing;

/**
 * Draws an arrow from start to end point over time.
 */
public class DrawArrow extends Animation {

    public DrawArrow(MObject target, double duration) {
        super(target, duration, Easing.EASE_OUT);
    }

    @Override
    public void begin() {
        if (target instanceof ArrowMob a) {
            a.setDrawFraction(0);
            a.setOpacity(1);
        }
    }

    @Override
    public void interpolate(double t) {
        if (target instanceof ArrowMob a) {
            a.setDrawFraction(t);
        }
    }
}
