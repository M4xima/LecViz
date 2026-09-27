package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;

public class FadeOut extends Animation {

    public FadeOut(MObject target, double duration) {
        super(target, duration);
    }

    @Override
    public void begin() {
        target.setOpacity(1);
    }

    @Override
    public void interpolate(double t) {
        target.setOpacity(1 - t);
    }
}
