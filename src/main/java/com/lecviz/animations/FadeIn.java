package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;

public class FadeIn extends Animation {

    public FadeIn(MObject target, double duration) {
        super(target, duration);
    }

    @Override
    public void begin() {
        target.setOpacity(0);
    }

    @Override
    public void interpolate(double t) {
        target.setOpacity(t);
    }
}
